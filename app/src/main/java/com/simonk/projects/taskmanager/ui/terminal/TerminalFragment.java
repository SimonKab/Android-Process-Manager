package com.simonk.projects.taskmanager.ui.terminal;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.simonk.projects.taskmanager.R;
import com.simonk.projects.taskmanager.database.entity.TerminalSnapshotEntity;
import com.simonk.projects.taskmanager.databinding.FragmentTerminalBinding;
import com.simonk.projects.taskmanager.terminal.StringTerminalListener;
import com.simonk.projects.taskmanager.ui.terminal.viewmodels.TerminalViewModel;

import java.util.List;

public class TerminalFragment extends Fragment {

    private ViewGroup mRootView;
    private Button mStopButton;
    private Button mClearButton;

    private TerminalViewModel mViewModel;

    public static TerminalFragment newInstance() {
        TerminalFragment fragment = new TerminalFragment();
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View root = bindingView(inflater, container);

        mStopButton.setOnClickListener((v) -> {
            mViewModel.stopTerminalRequest();
        });

        mClearButton.setOnClickListener((v) -> {
            mViewModel.clearTerminalSnapshots();
        });

        mViewModel = ViewModelProviders.of(this).get(TerminalViewModel.class);
        mViewModel.getTerminalSnapshots().observe(this, this::updateUi);

        return root;
    }

    private View bindingView(LayoutInflater inflater, ViewGroup container) {
        FragmentTerminalBinding binding
                = DataBindingUtil.inflate(inflater, R.layout.fragment_terminal, container, false);

        mRootView = binding.terminalRoot;
        mStopButton = binding.terminalStopButton;
        mClearButton = binding.terminalClearButton;

        return binding.getRoot();
    }

    private void updateUi(List<TerminalSnapshotEntity> requestList) {
        mRootView.removeAllViews();
        for (TerminalSnapshotEntity snapshot : requestList) {
            EditText editText = addEditTextLayout(mRootView);
            editText.setText(snapshot.request);
            editText.setEnabled(false);
            TextView textView = addResponseTextView();
            textView.setText(snapshot.response);
        }
        addEditTextLayout(mRootView);
    }

    private class TerminalTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String change = s.subSequence(start, start + count).toString();
            if (change.equals("\n")) {
                makeTerminalRequest(s.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            if (text.contains("\n")) {
                s.clear();
                s.append(text.replace("\n", ""));
            }
        }
    }

    private void makeTerminalRequest(String request) {
        TextView responseTextView = addResponseTextView();
        mViewModel.makeNewTerminalRequest(request, new StringTerminalListener() {
            @Override
            public void onInput(String input) {
                responseTextView.setText(input);
            }

            @Override
            public void onError(String input) {

            }

            @Override
            public String onOutputString() {
                return null;
            }

            @Override
            public void onInputStarted() {
                responseTextView.setText("");
            }

            @Override
            public void onInputFinished(Exception exception) {

            }

            @Override
            public void onErrorFinished() {

            }

            @Override
            public void onFinished() {
                finishRequest();
            }
        });
    }

    private void finishRequest() {
        mViewModel.addTerminalSnapshot(new TerminalSnapshotEntity(
                findLastEditText(mRootView).getText().toString(),
                findLastTextView(mRootView).getText().toString()
        ));
        blockAllEditTexts(mRootView);
        addEditTextLayout(mRootView);
    }

    private void blockAllEditTexts(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                blockAllEditTexts((ViewGroup) child);
            }
            if (child instanceof EditText) {
                child.setEnabled(false);
            }
        }
    }

    private EditText findLastEditText(ViewGroup parent) {
        for (int i = parent.getChildCount()-1; i >= 0; i--) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                EditText editText = findLastEditText((ViewGroup) child);
                if (editText != null) {
                    return editText;
                }
            }
            if (child instanceof EditText) {
                return (EditText)child;
            }
        }
        return null;
    }

    private TextView findLastTextView(ViewGroup parent) {
        for (int i = parent.getChildCount()-1; i >= 0; i--) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                TextView textView = findLastTextView((ViewGroup) child);
                if (textView != null) {
                    return textView;
                }
            }
            if (child instanceof TextView) {
                return (TextView)child;
            }
        }
        return null;
    }

    private TextView addResponseTextView() {
        int margin16 = (int) (16 * getResources().getDisplayMetrics().density);

        TextView textView = new TextView(requireContext());
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.leftMargin = margin16;
        textViewParams.rightMargin = margin16;
        textViewParams.bottomMargin = margin16;

        textView.setTextColor(Color.parseColor("#FFFFFF"));

        mRootView.addView(textView, textViewParams);
        return textView;
    }

    private EditText addEditTextLayout(ViewGroup parent) {
        int margin16 = (int) (16 * getResources().getDisplayMetrics().density);
        int margin8 = (int) (8 * getResources().getDisplayMetrics().density);

        EditText terminalEditText = new EditText(requireContext());
        terminalEditText.addTextChangedListener(new TerminalTextWatcher());
        LinearLayout.LayoutParams terminalEditTextParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        terminalEditTextParams.leftMargin = margin16;
        terminalEditTextParams.topMargin = margin8;
        terminalEditTextParams.rightMargin = margin16;
        terminalEditText.setLayoutParams(terminalEditTextParams);

        terminalEditText.setBackground(null);
        terminalEditText.setTextColor(Color.parseColor("#FFFFFF"));

        terminalEditText.requestFocus();

        SignTextView signTextView = new SignTextView(requireContext());

        View devider = new View(requireContext());
        ViewGroup.MarginLayoutParams deviderParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, (int) (getResources().getDisplayMetrics().density));
        devider.setLayoutParams(deviderParams);
        devider.setBackground(new ColorDrawable(Color.parseColor("#37474f")));

        LinearLayout editTextLayout = new LinearLayout(requireContext());
        editTextLayout.setOrientation(LinearLayout.HORIZONTAL);
        ViewGroup.MarginLayoutParams editTextLayoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editTextLayout.setLayoutParams(editTextLayoutParams);

        editTextLayout.addView(signTextView);
        editTextLayout.addView(terminalEditText);

        parent.addView(devider);
        parent.addView(editTextLayout);

        return terminalEditText;
    }

    private static class SignTextView extends AppCompatTextView {
        public SignTextView(Context context) {
            this(context, null);
        }

        public SignTextView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public SignTextView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init() {
            int margin = (int) (8 * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = margin;
            params.gravity = Gravity.CENTER_VERTICAL;

            setLayoutParams(params);

            setText(">>");
            setTextColor(Color.parseColor("#009688"));
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
    }

}
