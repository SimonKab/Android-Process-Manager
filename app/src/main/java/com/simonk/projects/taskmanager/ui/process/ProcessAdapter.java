package com.simonk.projects.taskmanager.ui.process;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simonk.projects.taskmanager.R;
import com.simonk.projects.taskmanager.entity.ProcessInfo;
import com.simonk.projects.taskmanager.ui.util.ObjectListAdapter;

public class ProcessAdapter extends ObjectListAdapter<ProcessInfo, ProcessAdapter.ProcessAdapterViewHolder> {

    private ProcessAdapterViewHolder.OnClickListener mItemClickListener;

    @NonNull
    @Override
    public ProcessAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v = inflater.inflate(R.layout.process_list_item, viewGroup, false);

        return new ProcessAdapterViewHolder(v, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProcessAdapterViewHolder processAdapterViewHolder, int i) {
        processAdapterViewHolder.bind(getItem(i));
    }

    public void setItemClickListener(ProcessAdapterViewHolder.OnClickListener onClickListener) {
        mItemClickListener = onClickListener;
    }

    public static class ProcessAdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView mName;
        private ImageView mImage;
        private TextView mPackage;

        private ProcessInfo mItem;

        public interface OnClickListener {
            void onClick(View v, ProcessInfo ppackage);
            boolean onLongClick(ProcessInfo info);
        }

        public ProcessAdapterViewHolder(@NonNull View itemView, OnClickListener onClickListener) {
            super(itemView);
            mName = itemView.findViewById(R.id.process_list_item_text);
            mImage = itemView.findViewById(R.id.process_list_item_logo);
            mPackage = itemView.findViewById(R.id.process_list_item_package);
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

        public void bind(ProcessInfo info) {
            mItem = info;
            mName.setText(info.getText());
            mImage.setImageDrawable(info.getImage());
            mPackage.setText(info.getPpackage());
        }
    }

}