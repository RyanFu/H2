package com.prettygirl.avgallery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.prettygirl.app.base.AdTitleBaseActivity;
import com.prettygirl.app.utils.AdUtils;
import com.prettygirl.app.utils.DialogToastUtils;
import com.prettygirl.app.utils.ServerUtils;
import com.prettygirl.avgallery.components.FixedTabsView;
import com.prettygirl.avgallery.components.ScrollerView;
import com.prettygirl.avgallery.components.TabsAdapter;
import com.prettygirl.avgallery.components.ViewPagerTabButton;
import com.prettygirl.avgallery.model.AVGirl;
import com.prettygirl.avgallery.util.UMengKey;
import com.prettygirl.avgallery1.R;
import com.umeng.analytics.MobclickAgent;

public class AvGalleryDetailActivity extends AdTitleBaseActivity implements OnClickListener {

    private AVGirl mGirl;
    private FixedTabsView mFixedTabsView;
    private ViewPager mViewPager;

    private String[] tabs = null;

    private View[] views = new View[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.av_gallery_detail_activity);
        tabs = getResources().getStringArray(R.array.av_detail_tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mFixedTabsView = (FixedTabsView) findViewById(R.id.fixedTabsView);
        mViewPager.setAdapter(mPageAdapter);
        mFixedTabsView.setAdapter(mTabsAdapter);
        mFixedTabsView.setViewPager(mViewPager);
        Intent intent = getIntent();
        Object o = intent.getParcelableExtra(AvGalleryMainActivity.EXT_KEY_GIRL);
        AVGirl girl = null;
        if (o == null || !(o instanceof AVGirl)) {
            DialogToastUtils.showMessage(this, "error opt");
            finish();
        } else {
            girl = (AVGirl) o;
        }
        mGirl = girl;
        setGoBackIconVisibility(View.VISIBLE);
        setTitle(girl.name);
        setAdViewClickListener(this);
        loadImages();
    }

    private void loadImages() {
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                String url = ServerUtils.getPicServerRoot(AvGalleryDetailActivity.this) + "/jp/" + mGirl.id + "/n";
                int r = 0;
                try {
                    String s = readUrl(url);
                    r = Integer.parseInt(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return r;
            }

            @Override
            protected void onPostExecute(Integer result) {
                if (result <= 0) {
                    return;
                }
                ScrollerView cScrollerView = (ScrollerView) findViewById(R.id.scrollerView);
                cScrollerView.bindActivity(AvGalleryDetailActivity.this);
                ArrayList<String> urls = new ArrayList<String>();
                for (int i = 0; i < result; i++) {
                    urls.add(ServerUtils.getPicServerRoot(AvGalleryDetailActivity.this) + "/jp/" + mGirl.id + "/" + i
                            + ".jpg");
                }
                cScrollerView.setImages(urls, false);
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.entry_point_ad_icon) {
            MobclickAgent.onEvent(this, UMengKey.ENTRY_POINT_ACTIVITY_AD);
            AdUtils.handleMoreAppEvent(this);
        }
    }

    TabsAdapter mTabsAdapter = new TabsAdapter() {

        @Override
        public View getView(int position) {
            if (tabs == null) {
                return null;
            } else {
                View result = View.inflate(getBaseContext(), R.layout.av_gallery_detail_tab, null);
                ViewPagerTabButton text = (ViewPagerTabButton) result.findViewById(R.id.av_detail_tab_text);
                text.setText(tabs[position]);
                return result;
            }
        }
        
    };

    PagerAdapter mPageAdapter = new PagerAdapter() {

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int index = position % views.length;
            if (views[index] == null) {
                views[index] = new WebView(AvGalleryDetailActivity.this);
            } else {
                container.removeView(views[index]);
            }
            ((WebView) views[index]).loadUrl("file:///android_asset/langs/" + AvApplication.getCurrentLang() + "/"
                    + mGirl.path);
            ((ViewPager) container).addView(views[index], 0);
            return views[index];
        }

        @Override
        public int getCount() {
            if (tabs == null) {
                return 0;
            } else {
                return tabs.length;
            }
        }
    };

    public final static String readUrl(String url) throws IOException {
        StringBuilder buf = new StringBuilder();
        URL urlToRead = new URL(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(urlToRead.openStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            buf.append(inputLine);
        }
        in.close();
        return buf.toString();
    }
}
