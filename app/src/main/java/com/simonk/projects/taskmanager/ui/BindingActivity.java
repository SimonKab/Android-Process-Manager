package com.simonk.projects.taskmanager.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;

public abstract class BindingActivity extends AppCompatActivity {

    private ViewDataBinding mBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = initBinding();
        if (mBinding == null) {
            throw new IllegalStateException("Binding can not be null");
        }
        setContentView(mBinding.getRoot());
    }

    protected abstract ViewDataBinding initBinding();

    public ViewDataBinding getBinding() {
        return mBinding;
    }

}
