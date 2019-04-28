package com.simonk.projects.taskmanager.ui.blacklist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simonk.projects.taskmanager.R;
import com.simonk.projects.taskmanager.databinding.FragmentBlacklistBinding;
import com.simonk.projects.taskmanager.databinding.FragmentProcessBinding;
import com.simonk.projects.taskmanager.entity.AppInfo;
import com.simonk.projects.taskmanager.ui.blacklist.viewmodels.BlacklistViewModel;
import com.simonk.projects.taskmanager.ui.process.ProcessFragment;

import java.util.List;

public class BlacklistFragment extends Fragment {

    private RecyclerView mBlacklistRecyclerView;

    private BlackListAdapter mBlackListAdapter;

    private BlacklistViewModel mViewModel;

    public static BlacklistFragment newInstance() {
        BlacklistFragment fragment = new BlacklistFragment();
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View root = bindingView(inflater, container);

        mViewModel = ViewModelProviders.of(this).get(BlacklistViewModel.class);

        mBlacklistRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),
                RecyclerView.VERTICAL, false));
        mBlackListAdapter = new BlackListAdapter();
        mBlacklistRecyclerView.setAdapter(mBlackListAdapter);

        mViewModel.getAllAppsInfo().observe(this, this::updateAllAppsInfo);

        return root;
    }

    private View bindingView(LayoutInflater inflater, ViewGroup container) {
        FragmentBlacklistBinding binding
                = DataBindingUtil.inflate(inflater, R.layout.fragment_blacklist, container, false);

        mBlacklistRecyclerView = binding.blacklistApps;

        return binding.getRoot();
    }

    private void updateAllAppsInfo(List<AppInfo> allAppsInfoList) {
        mBlackListAdapter.resolveActionChange(() -> {
            mBlackListAdapter.setAppInfoList(allAppsInfoList);
        });
    }
}