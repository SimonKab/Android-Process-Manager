package com.simonk.projects.taskmanager.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;

/**
 * Simple activity to support binding
 */
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

    /**
     * Child must implement this method to retrieve corresponding binding
     * @return
     */
    protected abstract ViewDataBinding initBinding();

    /**
     * Child can override this method to specify binding subclass it want
     * @return
     */
    public ViewDataBinding getBinding() {
        return mBinding;
    }

}
