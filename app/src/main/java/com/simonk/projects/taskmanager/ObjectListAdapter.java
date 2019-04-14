package com.simonk.projects.taskmanager;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class ObjectListAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private static final String INITIAL_ITEMS_PARCELABLE_KEY = "INITIAL_ITEMS_PARCELABLE_KEY";
    private static final String INITIAL_ITEMS_SERIALIZABLE_KEY = "INITIAL_ITEMS_SERIALIZABLE_KEY";

    private static final String FILTERED_ITEMS_PARCELABLE_KEY = "FILTERED_ITEMS_PARCELABLE_KEY";
    private static final String FILTERED_ITEMS_SERIALIZABLE_KEY = "FILTERED_ITEMS_SERIALIZABLE_KEY";

    private static final String FILTER_REQUEST_CODE_KEY = "FILTER_CONDITION_KEY";
    private static final String FILTER_CONDITION_KEY = "FILTER_CONDITION_KEY";

    private List<T> mFilteredItems; //-------
    private List<T> mInitialItemsBeforeFiltering;
    private SparseArray<FilterCondition<T>> mFilterConditions;
    private Comparator<T> mSortComparator;

    public ObjectListAdapter() {
        mInitialItemsBeforeFiltering = new ArrayList<>();
        mFilterConditions = new SparseArray<>();
    }

    public interface FilterCondition<F> extends Serializable {
        boolean isMatch(F item);
    }

    private void refilter() {
        if (mFilterConditions.size() == 0) {
            cancelFiltering();
            return;
        }

        List<T> itemsToFilter = mInitialItemsBeforeFiltering;
        for(int i = 0; i < mFilterConditions.size(); i++) {
            itemsToFilter = processFiltering(itemsToFilter, mFilterConditions.valueAt(i));
        }
        mFilteredItems = itemsToFilter;
    }

    protected void filter(FilterCondition<T> filterCondition, int requestCode) {
        if (filterCondition == null) {
            return;
        }

        if (isFiltered(requestCode)) {
            mFilterConditions.delete(requestCode);
            refilter();
        }

        mFilteredItems = processFiltering(getItemsList(), filterCondition);
        mFilterConditions.append(requestCode, filterCondition);
    }

    protected void sort(Comparator<T> comparator) {
        mSortComparator = comparator;
        if (mInitialItemsBeforeFiltering != null) {
            Collections.sort(mInitialItemsBeforeFiltering, comparator);
        }
        if (mFilteredItems != null) {
            Collections.sort(mFilteredItems, comparator);
        }
    }

    private List<T> processFiltering(List<T> itemsToFilter, FilterCondition<T> filterCondition) {
        List<T> filteredItems = new ArrayList<>();

        for (T item : itemsToFilter) {
            if (filterCondition.isMatch(item)) {
                filteredItems.add(item);
            }
        }

        return filteredItems;
    }

    public void cancelFiltering() {
        mFilterConditions.clear();
        if (mFilteredItems != null) {
            mFilteredItems = null;
        }
    }

    protected void cancelFiltering(int requestCode) {
        mFilterConditions.delete(requestCode);
        if (mFilterConditions.size() == 0) {
            cancelFiltering();
        } else {
            refilter();
        }
    }

    protected boolean isFiltered(int requestCode) {
        return mFilterConditions.indexOfKey(requestCode) != -1;
    }

    public boolean isFiltered() {
        return mFilteredItems != null;
    }

    public void setItemsList(List<T> items) {
        mInitialItemsBeforeFiltering.clear();
        mInitialItemsBeforeFiltering.addAll(items);

        if (mSortComparator != null) {
            sort(mSortComparator);
        }

        refilter();
    }

    public List<T> getItemsList() {
        if (mFilteredItems == null) {
            return new ArrayList<>(mInitialItemsBeforeFiltering);
        }
        return new ArrayList<>(mFilteredItems);
    }

    public List<T> getItemsListBeforeFiltering() {
        return mInitialItemsBeforeFiltering;
    }

    @Override
    public int getItemCount() {
        return getItemsList() != null ? getItemsList().size() : 0;
    }

    public T getItem(int index) {
        if (index >= getItemsList().size()) {
            return null;
        }

        return getItemsList().get(index);
    }

    public void resolveActionChange(Runnable action) {
        List<T> before = getItemsList();
        action.run();
        resolveItemsListChange(before);
    }

    public void resolveItemsListChange(List<T> oldList) {
        List<T> newList = getItemsList();
        if (oldList == null || newList == null) {
            return;
        }

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtilImpl(oldList, newList), true);
        result.dispatchUpdatesTo(this);
    }

    protected boolean isItemsContentEquals(T first, T second) {
        return first.equals(second);
    }

    private class DiffUtilImpl extends DiffUtil.Callback {

        private List<T> mOldList;
        private List<T> mNewList;

        public DiffUtilImpl(List<T> oldList, List<T> newList) {
            mOldList = oldList;
            mNewList = newList;
        }

        @Override
        public int getOldListSize() {
            return mOldList.size();
        }

        @Override
        public int getNewListSize() {
            return mNewList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldList.get(oldItemPosition).equals(mNewList.get(newItemPosition));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return isItemsContentEquals(mOldList.get(oldItemPosition), mNewList.get(newItemPosition));
        }
    }

    @SuppressWarnings("unchecked")
    public Bundle packAllItems() {
        if (mInitialItemsBeforeFiltering.isEmpty()) {
            return null;
        }

        boolean itParcelable;
        boolean itSerializable;

        try {
            Parcelable parcelable = (Parcelable) mInitialItemsBeforeFiltering.get(0);
            itParcelable = true;
        } catch (ClassCastException e) {
            itParcelable = false;
        }

        try {
            Serializable serializable = (Serializable) mInitialItemsBeforeFiltering.get(0);
            itSerializable = true;
        } catch (ClassCastException e) {
            itSerializable = false;
        }

        Bundle itemsBox = new Bundle();

        if (mFilterConditions.size() != 0) {
            int[] requestCodes = new int[mFilterConditions.size()];
            FilterCondition<T>[] filterConditions = new FilterCondition[mFilterConditions.size()];
            for (int i = 0; i < mFilterConditions.size(); i++) {
                requestCodes[i] = mFilterConditions.keyAt(i);
                filterConditions[i] = mFilterConditions.valueAt(i);
            }
            itemsBox.putIntArray(FILTER_REQUEST_CODE_KEY, requestCodes);
            itemsBox.putSerializable(FILTER_CONDITION_KEY, filterConditions);
        }

        if (itParcelable) {
            List<Parcelable> initialParcelableList = (List<Parcelable>) mInitialItemsBeforeFiltering;
            Parcelable[] parcelables = initialParcelableList.toArray(new Parcelable[] {});
            itemsBox.putParcelableArray(INITIAL_ITEMS_PARCELABLE_KEY, parcelables);

            if (isFiltered()) {
                List<Parcelable> filteredParcelableList = (List<Parcelable>) mFilteredItems;
                parcelables = filteredParcelableList.toArray(new Parcelable[]{});
                itemsBox.putParcelableArray(FILTERED_ITEMS_PARCELABLE_KEY, parcelables);
            }

            return itemsBox;
        }
        if (itSerializable) {
            List<Serializable> initialSerializableList = (List<Serializable>) mInitialItemsBeforeFiltering;
            Serializable[] serializables = initialSerializableList.toArray(new Serializable[] {});
            itemsBox.putSerializable(INITIAL_ITEMS_SERIALIZABLE_KEY, serializables);

            if (mFilteredItems != null) {
                List<Serializable> filteredSerializableList = (List<Serializable>) mFilteredItems;
                serializables = filteredSerializableList.toArray(new Serializable[]{});
                itemsBox.putSerializable(FILTERED_ITEMS_SERIALIZABLE_KEY, serializables);
            }

            return itemsBox;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public void unpackAllItems(Bundle itemsBox) {
        if (itemsBox == null) {
            return;
        }

        int[] requestCodes = itemsBox.getIntArray(FILTER_REQUEST_CODE_KEY);
        FilterCondition<T>[] filterConditions = (FilterCondition<T>[]) itemsBox.getSerializable(FILTER_CONDITION_KEY);
        if (requestCodes != null && filterConditions != null) {
            for (int i = 0; i < requestCodes.length && i < filterConditions.length; i++) {
                mFilterConditions.append(requestCodes[i], filterConditions[i]);
            }
        }

        Parcelable[] initialParcelables = itemsBox.getParcelableArray(INITIAL_ITEMS_PARCELABLE_KEY);
        Parcelable[] filteredParcelables = itemsBox.getParcelableArray(FILTERED_ITEMS_PARCELABLE_KEY);
        if (initialParcelables != null) {
            if (filteredParcelables != null && mFilterConditions.size() != 0) {
                /* избегаем излишней фильтрации */
                List<T> fixedSizeInitialList = (List<T>) Arrays.asList(initialParcelables);
                List<T> fixedSizeFilteredList = (List<T>) Arrays.asList(filteredParcelables);
                mInitialItemsBeforeFiltering = new ArrayList<>(fixedSizeInitialList);
                mFilteredItems = new ArrayList<>(fixedSizeFilteredList);
            } else {
                setItemsList((List<T>) Arrays.asList(initialParcelables));
            }
        }

        Serializable[] initialSerializables =
                (Serializable[]) itemsBox.getSerializable(INITIAL_ITEMS_SERIALIZABLE_KEY);
        Serializable[] filteredSerializables =
                (Serializable[]) itemsBox.getSerializable(FILTERED_ITEMS_SERIALIZABLE_KEY);
        if (initialSerializables != null) {
            if (filteredSerializables != null && mFilterConditions.size() != 0) {
                /* избегаем излишней фильтрации */
                List<T> fixedSizeInitialList = (List<T>) Arrays.asList(initialSerializables);
                List<T> fixedSizeFilteredList = (List<T>) Arrays.asList(filteredSerializables);
                mInitialItemsBeforeFiltering = new ArrayList<>(fixedSizeInitialList);
                mFilteredItems = new ArrayList<>(fixedSizeFilteredList);
            } else {
                setItemsList((List<T>) Arrays.asList(initialSerializables));
            }
        }
    }
}
