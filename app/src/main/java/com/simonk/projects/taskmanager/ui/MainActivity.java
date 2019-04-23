package com.simonk.projects.taskmanager.ui;

import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.simonk.projects.taskmanager.entity.ProcessInfo;
import com.simonk.projects.taskmanager.repository.ProcessRepository;
import com.simonk.projects.taskmanager.ui.process.ChangeDetailsDialog;
import com.simonk.projects.taskmanager.ui.process.CleanedDialog;
import com.simonk.projects.taskmanager.ui.process.ProcessFragment;
import com.simonk.projects.taskmanager.ui.process.viewmodels.ProcessViewModel;
import com.simonk.projects.taskmanager.ui.terminal.TerminalFragment;
import com.simonk.projects.taskmanager.ui.util.ObjectListAdapter;
import com.simonk.projects.taskmanager.R;
import com.simonk.projects.taskmanager.databinding.ActivityMainBinding;
import com.simonk.projects.taskmanager.util.MemoryUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BindingActivity {

    @Override
    public ActivityMainBinding getBinding() {
        return (ActivityMainBinding) super.getBinding();
    }

    @Override
    public ViewDataBinding initBinding() {
        return DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_main, null, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewPager viewPager = getBinding().viewPager;
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);

        TabLayout tabLayout = getBinding().tabLayout;
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getBinding().coordinator);
        adapter.addFragment(ProcessFragment.newInstance(),
                "Apps");
        adapter.addFragment(ProcessFragment.newInstance(),
                "BlackList");
        adapter.addFragment(TerminalFragment.newInstance(),
                "Terminal");
        viewPager.setAdapter(adapter);
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private View mRootView;

        public ViewPagerAdapter(FragmentManager manager, View root) {
            super(manager);
            mRootView = root;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.setPrimaryItem(container, position, object);
            if (mRootView.getBackground() instanceof ColorDrawable) {
                int installedColor = ((ColorDrawable) mRootView.getBackground()).getColor();
                int newColor = installedColor;
                if (position == 2) {
                    newColor =  Color.parseColor("#455A64");
                } else {
                    newColor = mRootView.getResources().getColor(R.color.colorPrimary);

                }

                if (installedColor != newColor) {
                    ValueAnimator valueAnimator = ValueAnimator.ofArgb(installedColor, newColor);
                    valueAnimator.setDuration(300);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mRootView.setBackground(new ColorDrawable((Integer) animation.getAnimatedValue()));
                        }
                    });
                    valueAnimator.start();
                }
            }
        }
    }
}
