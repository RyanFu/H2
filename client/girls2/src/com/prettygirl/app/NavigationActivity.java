package com.prettygirl.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.pretty.girl.app.R;
import com.prettygirl.app.components.NavigationView;
import com.prettygirl.app.components.SlideLayout;
import com.prettygirl.app.components.SlideLayout.SlideSideListener;
import com.prettygirl.app.dialog.ExitDialog;
import com.prettygirl.app.model.provider.FavoriteManager;
import com.prettygirl.app.model.provider.ThumbUpManager;
import com.prettygirl.app.utils.BitmapUtils;
import com.prettygirl.app.utils.DialogToastUtils;
import com.prettygirl.app.utils.MiscUtil;
import com.prettygirl.app.utils.UMengKey;
import com.prettygirl.app.utils.UserInfoManager;
import com.umeng.analytics.MobclickAgent;

public class NavigationActivity extends BaseActivity implements OnClickListener, SlideSideListener {

    private NavigationView mNavigationView;

    private SlideLayout mMainView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        SlideLayout mainView = new SlideLayout(this);
        View cSidebar = View.inflate(this, R.layout.e_navigation_slide_side_view, null);

        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.e_x_repeat_shader)).getBitmap();
        DisplayMetrics cDisplayMetrics = getResources().getDisplayMetrics();
        Drawable background = BitmapUtils.createTiledRepeatDrawable(bitmap, 0x23e3e3e3, cDisplayMetrics.widthPixels,
                cDisplayMetrics.heightPixels);

        cSidebar.setBackgroundDrawable(background);
        View cContent = View.inflate(this, R.layout.e_navigation, null);
        mainView.updateView(cSidebar, cContent);
        mainView.setSlideSideListener(this);
        mMainView = mainView;
        setContentView(mainView);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_girl_navigation_title);

        MiscUtil.setVisitCount(MiscUtil.getVisitCount(this) + 1, this);
        if (Application.DEFAULT_IS_OFFLINE == true) {
            Intent intent = new Intent();
            intent.setClass(this, GalleryActivity.class);
            intent.putExtra(GalleryActivity.EXT_TYPE, GalleryActivity.EXT_VALUE_OFFLINE);
            startActivity(intent);
            finish();
        }
        mNavigationView = (NavigationView) findViewById(R.id.navigationView);
        ((TextView) findViewById(R.id.slide_side_view_version_name)).setText(getResources().getString(
                R.string.navigation_rank_center_version_name, getVersionName()));

        findViewById(R.id.navigation_rank_center_latest).setOnClickListener(this);
        findViewById(R.id.navigation_rank_center_weeky).setOnClickListener(this);
        findViewById(R.id.navigation_rank_center_monthy).setOnClickListener(this);
        findViewById(R.id.navigation_rank_center_support).setOnClickListener(this);
        findViewById(R.id.navigation_rank_center_contact_us).setOnClickListener(this);

        //        findViewById(R.id.navigation_rank_center).setOnClickListener(this);
        findViewById(R.id.navigation_rank_center_latest1).setOnClickListener(this);
        findViewById(R.id.navigation_rank_center_weeky1).setOnClickListener(this);
        findViewById(R.id.navigation_rank_center_monthy1).setOnClickListener(this);
        findViewById(R.id.favorite_button).setOnClickListener(this);
        findViewById(R.id.more_button).setOnClickListener(this);
        findViewById(R.id.share_button).setOnClickListener(this);
        findViewById(R.id.navigation_title_share).setOnClickListener(this);
        findViewById(R.id.navigation_title_more).setOnClickListener(this);
        UserInfoManager.getInstance().onEvent(UserInfoManager.Event.START_APP);
    }

    private String getVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return getResources().getString(R.string.navigation_rank_center_verion_unknow);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mMainView.isSlideSideOpening()) {
                mMainView.closeSidebar();
                return false;
            } else {
                ExitDialog d = new ExitDialog(this);
                d.show();
            }
        }
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        String value = MobclickAgent
                .getConfigParams(this, UMengKey.ONLINE_CONFIG_KEY_GALLERY_DETAIL_VIEW_RANK_SHOWABLE);
        if (!TextUtils.isEmpty(value) && !Boolean.valueOf(value)) {
            //            findViewById(R.id.navigation_rank_center).setVisibility(View.GONE);
            findViewById(R.id.navigation_rank_center_latest1).setVisibility(View.GONE);
            findViewById(R.id.navigation_rank_center_weeky1).setVisibility(View.GONE);
            findViewById(R.id.navigation_rank_center_monthy1).setVisibility(View.GONE);
        }
        mNavigationView.notifyDataChanged();
    }

    @Override
    public void onLevelChange(int level, int previousLevel) {
        super.onLevelChange(level, previousLevel);
        mNavigationView.notifyDataChanged();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.favorite_button) {
            MobclickAgent.onEvent(this, UMengKey.NAVIGATION_ACTIVITY_FAVORITE);
            int size = FavoriteManager.getInstance().getFavoriteSize();
            if (size <= 0) {
                DialogToastUtils.showDialog(this, getString(R.string.favorite_button_text),
                        getString(R.string.favorite_button_text_null_msg),
                        getString(R.string.e_gallery_detail_image_cancel), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // NG 
                            }

                        });
            } else {
                Intent intent = new Intent();
                intent.setClass(this, GalleryActivity.class);
                intent.putExtra(GalleryActivity.EXT_TYPE, GalleryActivity.EXT_VALUE_FAVORITE);
                startActivity(intent);
            }
        } else if (id == R.id.more_button || id == R.id.navigation_title_more) {
            MobclickAgent.onEvent(this, UMengKey.NAVIGATION_ACTIVITY_MORE);
            MiscUtil.startGooglePlayByAuthor(view.getContext());
        } else if (id == R.id.share_button || id == R.id.navigation_title_share) {
            MobclickAgent.onEvent(this, UMengKey.NAVIGATION_ACTIVITY_SHARE);
            MiscUtil.shareApp(this);
            //        } else if (id == R.id.navigation_rank_center) {
            //            mMainView.toggleSidebar();
        } else if (id == R.id.navigation_rank_center_latest || id == R.id.navigation_rank_center_latest1) {
            MobclickAgent.onEvent(this, UMengKey.NAVIGATION_ACTIVITY_RANK_LATEST);
            mMainView.closeSidebar();
            Intent intent = new Intent();
            intent.setClass(this, RankActivity.class);
            intent.putExtra(RankActivity.EXT_METHOD, ThumbUpManager.RANK_LATEST);
            startActivity(intent);
        } else if (id == R.id.navigation_rank_center_weeky || id == R.id.navigation_rank_center_weeky1) {
            MobclickAgent.onEvent(this, UMengKey.NAVIGATION_ACTIVITY_RANK_WEEKY);
            mMainView.closeSidebar();
            Intent intent = new Intent();
            intent.setClass(this, RankActivity.class);
            intent.putExtra(RankActivity.EXT_METHOD, ThumbUpManager.RANK_WEEKY);
            startActivity(intent);
        } else if (id == R.id.navigation_rank_center_monthy || id == R.id.navigation_rank_center_monthy1) {
            MobclickAgent.onEvent(this, UMengKey.NAVIGATION_ACTIVITY_RANK_MONTHY);
            mMainView.closeSidebar();
            Intent intent = new Intent();
            intent.setClass(this, RankActivity.class);
            intent.putExtra(RankActivity.EXT_METHOD, ThumbUpManager.RANK_MONTHY);
            startActivity(intent);
        } else if (id == R.id.navigation_rank_center_support) {
            MobclickAgent.onEvent(this, UMengKey.NAVIGATION_ACTIVITY_RANK_SUPPORT);
            mMainView.closeSidebar();
            MiscUtil.startGooglePlay(this, null);
        } else if (id == R.id.navigation_rank_center_contact_us) {
            MobclickAgent.onEvent(this, UMengKey.NAVIGATION_ACTIVITY_RANK_CONTACT_US);
            mMainView.closeSidebar();
            Uri uri = Uri.parse("mailto:iplayboy2014@gmail.com");
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            startActivity(intent);
        }
    }

    @Override
    public void onSidebarOpened() {

    }

    @Override
    public void onSidebarClosed() {

    }

    @Override
    public boolean onContentTouchedWhenOpening() {
        mMainView.closeSidebar();
        return true;
    }
}
