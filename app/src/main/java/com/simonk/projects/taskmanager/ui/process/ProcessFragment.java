package com.simonk.projects.taskmanager.ui.process;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simonk.projects.taskmanager.R;
import com.simonk.projects.taskmanager.databinding.FragmentProcessBinding;
import com.simonk.projects.taskmanager.entity.ProcessInfo;
import com.simonk.projects.taskmanager.ui.process.viewmodels.ProcessViewModel;

import java.util.List;

public class ProcessFragment extends Fragment
        implements CleanedDialog.OnCleaned, ChangeDetailsDialog.OnChanged {

    private RecyclerView mAppsRecyclerView;
    private Button mClearAllButton;
    private TextView mAllMemory;
    private TextView mFreeMemory;
    private TextView mPercentMemory;

    private ProcessAdapter mProcessAdapter;

    private ProcessViewModel mViewModel;

    public static ProcessFragment newInstance() {
        ProcessFragment fragment = new ProcessFragment();
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View root = bindingView(inflater, container);

        mAppsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),
                RecyclerView.VERTICAL, false));
        mProcessAdapter = new ProcessAdapter();
        mAppsRecyclerView.setAdapter(mProcessAdapter);

        mClearAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CleanedDialog cleanedDialog = new CleanedDialog();
                cleanedDialog.setTargetFragment(ProcessFragment.this, 0);
                cleanedDialog.show(requireFragmentManager(), "CleanedDialog");
            }
        });

        mProcessAdapter.setItemClickListener(new ProcessAdapter.ProcessAdapterViewHolder.OnClickListener() {
            @Override
            public void onClick(View v, ProcessInfo info) {
                ViewGroup detailsView = (ViewGroup)
                        LayoutInflater.from(requireContext()).inflate(R.layout.process_list_item_details, null);
                if (((FrameLayout) v.findViewById(R.id.process_list_item_details)).getChildCount() == 0) {
                    ((TextView) detailsView.findViewById(R.id.priority)).setText(getString(R.string.priority, String.valueOf(info.getPriority())));
                    ((TextView) detailsView.findViewById(R.id.status)).setText(getString(R.string.enabled, String.valueOf(info.isStatus())));
                    ((TextView) detailsView.findViewById(R.id.uid)).setText(getString(R.string.uid, String.valueOf(info.isStatus())));
                    ((TextView) detailsView.findViewById(R.id.min_sdk)).setText(getString(R.string.target_sdk, String.valueOf(info.getMinSdk())));
                    ((TextView) detailsView.findViewById(R.id.description)).setText(getString(R.string.description, info.getDescription()));
                    ((FrameLayout) v.findViewById(R.id.process_list_item_details)).addView(detailsView);
                    ((Button) detailsView.findViewById(R.id.kill)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mViewModel.getRepository().killProcess(requireContext(), info);
                            updateUi();
                        }
                    });
                } else {
                    ((FrameLayout) v.findViewById(R.id.process_list_item_details)).removeAllViews();
                }
            }

            @Override
            public boolean onLongClick(ProcessInfo info) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(ChangeDetailsDialog.PROCESS_INFO_ARG, info);
                ChangeDetailsDialog changeDetailsDialog = new ChangeDetailsDialog();
                changeDetailsDialog.setArguments(bundle);
                changeDetailsDialog.show(requireFragmentManager(), "ChangeDetailsDialog");
                return false;
            }
        });

        mViewModel = ViewModelProviders.of(this).get(ProcessViewModel.class);
        mViewModel.getAllProcessInfo().observe(this, this::updateProcessAdapter);

        return root;
    }

    private View bindingView(LayoutInflater inflater, ViewGroup container) {
        FragmentProcessBinding binding
                = DataBindingUtil.inflate(inflater, R.layout.fragment_process, container, false);

        mAppsRecyclerView = binding.activityMainRecycler;
        mClearAllButton = binding.clearAll;
        mAllMemory = binding.allMemory;
        mPercentMemory = binding.percentMemory;
        mFreeMemory = binding.freeMemory;

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUi();
    }

    private void updateUi() {
        float allMemory = mViewModel.getTotalMemory();
        float availMemory = mViewModel.getAvailableMemory();
        mAllMemory.setText("" + allMemory + "G");
        mFreeMemory.setText("" + availMemory + "G");
        mPercentMemory.setText("" + (int)(availMemory * 100 / allMemory) + "%");

        mViewModel.forceUpdateProcessInfo();
    }

    private void updateProcessAdapter(List<ProcessInfo> processInfoList) {
        mProcessAdapter.resolveActionChange(() -> {
            mProcessAdapter.setItemsList(processInfoList);
        });
    }

    @Override
    public void onCleaned() {
        updateUi();
    }

    @Override
    public void onChanged(ProcessInfo info, int priority) {
        mViewModel.getRepository().changeProcessPriority(requireContext(), info, priority);
        updateUi();
    }

}
