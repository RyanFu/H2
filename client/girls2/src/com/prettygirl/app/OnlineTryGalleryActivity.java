package com.prettygirl.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.pretty.girl.app.R;
import com.prettygirl.app.components.OnlineTryGalleryView;
import com.prettygirl.app.utils.DialogToastUtils;
import com.prettygirl.app.utils.ImageResourceUtils;
import com.prettygirl.app.utils.UMengKey;
import com.prettygirl.app.utils.UserInfoManager;
import com.umeng.analytics.MobclickAgent;

public class OnlineTryGalleryActivity extends BaseActivity implements OnPageChangeListener, OnClickListener {

    public static final int GO_DETAIL = 0x1;

    private OnlineTryGalleryView mGallery;

    private TextView mScoreView;

    private TextView mPageView;

    private int mLevel = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_try_gallery);
        initContent();
    }

    private void initContent() {
        String baseUrl = ImageResourceUtils.getServerUrl(this, 0);
        mGallery = (OnlineTryGalleryView) findViewById(R.id.gallery);
        mGallery.init(mLevel, baseUrl);
        mGallery.setOnPageChangeListener(this);

        UserInfoManager cUserInfoManager = UserInfoManager.getInstance();
        mScoreView = (TextView) findViewById(R.id.point);
        mScoreView.setText(Integer.toString(cUserInfoManager.getCurrentPoint()));
        mPageView = (TextView) findViewById(R.id.page);
        mPageView.setOnClickListener(this);
        if (mGallery.getAdapter() != null) {
            updateCurrentPageDisplay((int) (mGallery.getTotalSize()));
        }
        mScoreView.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        mGallery.resume();
        updateUserScore();
    }

    @Override
    public void onPointChange(int point, int previousPoint) {
        updateUserScore();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GO_DETAIL && data != null) {
            int item = data.getIntExtra(GalleryDetailActivity.EXT_INT_INDEX, 0);
            mGallery.setSelectedItem(item);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPageSelected(int pos) {
        if (mGallery == null || mGallery.getAdapter() == null) {
            return;
        }
        updateCurrentPageDisplay((int) (mGallery.getTotalSize() - pos));
    }

    private SpannableString formatText(String text, String target) {
        SpannableString result = new SpannableString(text);
        int index = text.lastIndexOf(target);
        //2.0f表示默认字体大小的两倍  
        result.setSpan(new RelativeSizeSpan(2.0f), index, result.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        result.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        result.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), index, result.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return result;
    }

    private void updateCurrentPageDisplay(int index) {
        String text = getResources().getString(R.string.current_page, index);
        SpannableString result = formatText(text, "" + index);
        mPageView.setText(result);
    }

    private void updateUserScore() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            int point = UserInfoManager.getInstance().getCurrentPoint();
            String text = getResources().getString(R.string.current_level_1, point);
            SpannableString result = formatText(text, "" + point);
            mScoreView.setText(result);
            //    mScoreView.setText(getResources().getString(R.string.current_level_1,
            //            UserInfoManager.getInstance().getCurrentPoint()));
        } else {
            mGallery.post(new Runnable() {
                @Override
                public void run() {
                    int point = UserInfoManager.getInstance().getCurrentPoint();
                    String text = getResources().getString(R.string.current_level_1, point);
                    SpannableString result = formatText(text, "" + point);
                    mScoreView.setText(result);
                    //            mScoreView.setText(getResources().getString(R.string.current_level_1,
                    //                    UserInfoManager.getInstance().getCurrentPoint()));
                }
            });
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.page) {
            MobclickAgent.onEvent(this, UMengKey.GALLERY_ACTIVITY_GOTO);
            final EditText cEditText = new EditText(this);
            cEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            DisplayMetrics cDisplayMetrics = getResources().getDisplayMetrics();
            cEditText.setWidth(cDisplayMetrics.widthPixels - 20);
            DialogToastUtils.showDialog(this, getString(R.string.e_gallery_dialog_goto_index), cEditText,
                    getString(R.string.e_gallery_detail_image_cancel), null, getString(R.string.ok),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MobclickAgent.onEvent(getBaseContext(), UMengKey.GALLERY_ACTIVITY_GOTO_OK);
                            String pageIndexString = cEditText.getText().toString();
                            if (TextUtils.isEmpty(pageIndexString)) {
                                return;
                            }
                            int pageIndex = -1;
                            try {
                                pageIndex = Integer.valueOf(pageIndexString);
                            } catch (NumberFormatException e) {
                            }
                            if (pageIndex > 0) {
                                mGallery.gotoPageByIndex(pageIndex);
                            }
                            DialogToastUtils.showMessage(getBaseContext(), pageIndexString);
                        }
                    });
        }
    }

}
