package com.prettygirl.app.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;

import com.pretty.girl.app.R;
import com.prettygirl.app.utils.MiscUtil;
import com.prettygirl.app.utils.UMengKey;
import com.umeng.analytics.MobclickAgent;

public class ExitDialog extends Dialog implements android.view.View.OnClickListener {
    private Activity mActivity;

    public ExitDialog(Activity context) {
        super(context);
        mActivity = context;
        MobclickAgent.onEvent(context, UMengKey.SHOW_EXIT_DIALOG);
        init();
    }

    private void init() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setContentView(R.layout.dialog_alert_dialog);
        setContentView(R.layout.exit_dialog);
        findViewById(R.id.e_exit_dialog_more_girl).setOnClickListener(this);
        findViewById(R.id.e_exit_dialog_support).setOnClickListener(this);
        findViewById(R.id.e_exit_dialog_exit).setOnClickListener(this);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.e_exit_dialog_exit) {
            MobclickAgent.onEvent(mActivity, UMengKey.SHOW_EXIT_DIALOG_EXIT);
            mActivity.finish();
        } else if (id == R.id.e_exit_dialog_more_girl) {
            MobclickAgent.onEvent(mActivity, UMengKey.SHOW_EXIT_DIALOG_MORE_GIRL);
            MiscUtil.handleMoreAppEvent(mActivity);
        } else if (id == R.id.e_exit_dialog_support) {
            MobclickAgent.onEvent(mActivity, UMengKey.SHOW_EXIT_DIALOG_SUPPORT);
            MiscUtil.startGooglePlay(mActivity, null);
        }
        this.dismiss();
    }
}
