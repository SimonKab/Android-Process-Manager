package com.simonk.projects.taskmanager.ui.blacklist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simonk.projects.taskmanager.R;
import com.simonk.projects.taskmanager.entity.AppInfo;
import com.simonk.projects.taskmanager.entity.ProcessInfo;
import com.simonk.projects.taskmanager.ui.process.ProcessAdapter;
import com.simonk.projects.taskmanager.ui.util.ObjectListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlackListAdapter extends ObjectListAdapter<BlackListAdapter.AdapterDataHolder, RecyclerView.ViewHolder> {

    private BlacklistAdapterViewHolder.OnClickListener mItemClickListener;

    private static final int BLACK_ITEM_TYPE = 1;
    private static final int ALL_ITEM_TYPE = 2;
    private static final int BLACK_LABEL_ITEM_TYPE = 3;
    private static final int ALL_LABEL_ITEM_TYPE = 4;

    public static class AdapterDataHolder {
        public AppInfo appInfo;
        public boolean isBlacklistLabel;
        public boolean isAllAppsLabel;

        AdapterDataHolder(AppInfo appInfo) {
            this.appInfo = appInfo;
        }
    }

    public void setAppInfoList(List<AppInfo> items) {
        Collections.sort(items, (o1, o2) ->
                Boolean.compare(o1.isInBlacklist(), o2.isInBlacklist())
        );
        List<AdapterDataHolder> adapterDataHolderList = new ArrayList<>();

        AdapterDataHolder blacklistLabel = new AdapterDataHolder(null);
        blacklistLabel.isBlacklistLabel = true;
        adapterDataHolderList.add(blacklistLabel);

        int i = 0;
        for (; i < items.size(); i++) {
            AppInfo appInfo = items.get(i);
            if (appInfo.isInBlacklist()) {
                adapterDataHolderList.add(new AdapterDataHolder(appInfo));
            } else {
                break;
            }
        }

        AdapterDataHolder allAppsLabel = new AdapterDataHolder(null);
        allAppsLabel.isAllAppsLabel = true;
        adapterDataHolderList.add(allAppsLabel);

        for (; i < items.size(); i++) {
            adapterDataHolderList.add(new AdapterDataHolder(items.get(i)));
        }

        setItemsList(adapterDataHolderList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == BLACK_ITEM_TYPE || viewType == ALL_ITEM_TYPE) {
            View v = inflater.inflate(R.layout.blacklist_item, parent, false);
            return new BlacklistAdapterViewHolder(v, mItemClickListener);
        }
        if (viewType == BLACK_LABEL_ITEM_TYPE) {
            TextView label = (TextView) inflater.inflate(R.layout.blacklist_item_label, parent, false);
            label.setText("Blacklist:");
            return new LabelViewHolder(label);
        }
        if (viewType == ALL_LABEL_ITEM_TYPE) {
            TextView label = (TextView) inflater.inflate(R.layout.blacklist_item_label, parent, false);
            label.setText("All apps:");
            return new LabelViewHolder(label);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == BLACK_ITEM_TYPE || holder.getItemViewType() == ALL_ITEM_TYPE) {
            ((BlacklistAdapterViewHolder)holder).bind(getItem(position).appInfo);
        }
    }

    @Override
    public int getItemViewType(int position) {
        AdapterDataHolder dataHolder = getItem(position);
        if (dataHolder.isAllAppsLabel) {
            return ALL_LABEL_ITEM_TYPE;
        }
        if (dataHolder.isBlacklistLabel) {
            return BLACK_LABEL_ITEM_TYPE;
        }
        if (dataHolder.appInfo.isInBlacklist()) {
            return BLACK_ITEM_TYPE;
        }
        return ALL_ITEM_TYPE;
    }

    public void setItemClickListener(BlacklistAdapterViewHolder.OnClickListener onClickListener) {
        mItemClickListener = onClickListener;
    }

    public static class BlacklistAdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView mName;
        private ImageView mImage;
        private TextView mPackage;

        private AppInfo mItem;

        public interface OnClickListener {
            void onClick(View v, AppInfo ppackage);
            boolean onLongClick(AppInfo info);
        }

        public BlacklistAdapterViewHolder(@NonNull View itemView, OnClickListener onClickListener) {
            super(itemView);
            mName = itemView.findViewById(R.id.blacklist_item_text);
            mImage = itemView.findViewById(R.id.blacklist_item_logo);
            mPackage = itemView.findViewById(R.id.blacklist_item_package);
            itemView.setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onClick(v, mItem);
                }
            });
            itemView.setOnLongClickListener(v -> {
                if (onClickListener != null) {
                    return onClickListener.onLongClick(mItem);
                }
                return false;
            });
        }

        public void bind(AppInfo info) {
            mItem = info;
            mName.setText(info.getText());
            mImage.setImageDrawable(info.getImage());
            mPackage.setText(info.getPpackage());
        }
    }

    public static class LabelViewHolder extends RecyclerView.ViewHolder {
        public LabelViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
