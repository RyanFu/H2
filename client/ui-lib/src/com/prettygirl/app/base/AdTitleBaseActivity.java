package com.prettygirl.app.base;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pretty.lib.R;

public class AdTitleBaseActivity extends BaseActivity {

    private TextView mTitleView;

    private FrameLayout mContainer;

    private OnClickListener mOnClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.custom_layout_with_ad_title);
        mTitleView = (TextView) findViewById(R.id.android_title);
        mContainer = (FrameLayout) findViewById(R.id.android_layout_container);
        findViewById(R.id.entry_point_ad_icon).setOnClickListener(mOnMainClickListener);
        findViewById(R.id.android_title_back).setOnClickListener(mOnMainClickListener);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitleView.setText(title);
    }

    public void setGoBackIconVisibility(int visibility) {
        findViewById(R.id.android_title_back).setVisibility(visibility);
        findViewById(R.id.android_title_back_div).setVisibility(visibility);
    }

    @Override
    public void setTitle(int titleId) {
        mTitleView.setText(titleId);
    }

    public void setAdViewClickListener(OnClickListener cOnClickListener) {
        mOnClickListener = cOnClickListener;
    }

    @Override
    public void setTitleColor(int textColor) {
        mTitleView.setTextColor(textColor);
    }

    @Override
    public void setContentView(int layoutResID) {
        View.inflate(this, layoutResID, mContainer);
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        mContainer.addView(view, params);
    }

    @Override
    public void setContentView(View view) {
        mContainer.addView(view);
    }

    private OnClickListener mOnMainClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.entry_point_ad_icon && mOnClickListener != null) {
                mOnClickListener.onClick(v);
            } else if (id == R.id.android_title_back) {
                finish();
            }
        }
    };
}
