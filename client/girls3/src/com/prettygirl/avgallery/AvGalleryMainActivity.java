package com.prettygirl.avgallery;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.prettygirl.app.utils.ServerUtils;
import com.prettygirl.avgallery.components.FixedTabsView;
import com.prettygirl.avgallery.components.TabsAdapter;
import com.prettygirl.avgallery.components.ViewPagerTabButton;
import com.prettygirl.avgallery.dialog.ExitDialog;
import com.prettygirl.avgallery.model.AVGirl;
import com.prettygirl.avgallery1.R;

public class AvGalleryMainActivity extends MBaseActivity {

    public static final String EXT_KEY_GIRL = "ext_key_girl";

    private String mLang;

    private ArrayList<AVGirl> mGirls;

    private FixedTabsView mFixedTabsView;

    private ViewPager mViewPager;

    private String[] tabs;

    private View[] views = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.av_gallery_main_activity);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.av_gallery_main_cust_title_with_tab);

        mLang = AvApplication.getCurrentLang();
        mGirls = ((AvApplication) getApplicationContext()).getGirlList(mLang);

        tabs = getResources().getStringArray(R.array.av_detail_tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mFixedTabsView = (FixedTabsView) findViewById(R.id.fixedTabsView);
        mViewPager.setAdapter(mPageAdapter);
        mFixedTabsView.setAdapter(mTabsAdapter);
        mFixedTabsView.setViewPager(mViewPager);
        mFixedTabsView.setOnPageChangeListener(mOnPageChangeListener);

        //        GridView mGridView = (GridView) findViewById(R.id.av_gallery_grid_view);
        //        //        mGridView.setFastScrollEnabled(true);
        //        mGridView.setAdapter(new AVAdapter());
        //        mGridView.setOnItemClickListener(new OnItemClickListener() {
        //
        //            @Override
        //            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //                Intent intent = new Intent();
        //                intent.setClass(parent.getContext(), AvGalleryDetailActivity.class);
        //                intent.putExtra(EXT_KEY_GIRL, mGirls.get(position));
        //                startActivity(intent);
        //            }
        //        });
    }

    OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // NG
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // NG
        }

        @Override
        public void onPageSelected(int pIndex) {
//            if ("news".equals(tabs[pIndex])) {
//                int index = pIndex % tabs.length;
//                views[index].findViewById(R.id.av_gallery_news).setVisibility(View.VISIBLE);
//                views[index].findViewById(R.id.av_gallery_grid_view).setVisibility(View.GONE);
//                WebView webView = ((WebView) views[index].findViewById(R.id.av_gallery_main_news));
//                webView.loadUrl(ServerUtils.getPicServerRoot(AvGalleryMainActivity.this) + "/jp/wordpress");
//            }
        }

    };

    @Override
    public void onResume() {
        super.onResume();
        tryShowOnlineConfigAd();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mViewPager.getCurrentItem() != 0) {
                mViewPager.setCurrentItem(0);
                return false;
            } else {
                ExitDialog d = new ExitDialog(this);
                d.show();
            }
        }
        return result;
    }

    class AVAdapter extends BaseAdapter implements SectionIndexer {

        private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        @Override
        public int getCount() {
            if (mGirls == null) {
                return 0;
            }
            return mGirls.size();
        }

        @Override
        public Object getItem(int position) {
            if (mGirls == null) {
                return null;
            }
            return mGirls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Context context = parent.getContext();
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.av_gallery_main_grid_item, null);
            }
            Object obj = convertView.getTag();
            ViewHolder cViewHolder = null;
            if (obj == null || !(obj instanceof ViewHolder)) {
                cViewHolder = new ViewHolder(convertView);
                convertView.setTag(cViewHolder);
            } else {
                cViewHolder = (ViewHolder) obj;
            }
            AVGirl girl = mGirls.get(position);
            ImageLoader.getInstance().cancelDisplayTask(cViewHolder.imageView);
            ImageLoader.getInstance().displayImage("assets://icons/" + girl.id + ".jpg", cViewHolder.imageView);
            cViewHolder.mNameView.setText(girl.name);
            cViewHolder.mSubView.setText(girl.subTilte);
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView mNameView;
            TextView mSubView;

            ViewHolder(View view) {
                this.imageView = (ImageView) view.findViewById(R.id.av_gallery_av_image);
                this.mNameView = (TextView) view.findViewById(R.id.av_gallery_av_name);
                this.mSubView = (TextView) view.findViewById(R.id.av_gallery_av_sub_title);
            }
        }

        @Override
        public Object[] getSections() {
            String[] sections = new String[mSections.length()];
            for (int i = 0; i < mSections.length(); i++)
                sections[i] = String.valueOf(mSections.charAt(i));
            return sections;
        }

        @Override
        public int getPositionForSection(int section) {
            return 0;
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
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
                text.setText(getResources().getIdentifier("av_personal_" + tabs[position], "string", getPackageName()));
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
            int index = position % tabs.length;
            if (views == null) {
                views = new View[tabs.length];
            }
            if (views[index] == null) {
                views[index] = View.inflate(AvGalleryMainActivity.this, R.layout.av_gallery_main_activity_tabs_content,
                        null);
            } else {
                container.removeView(views[index]);
            }
            if ("info".equals(tabs[position])) {
                views[index].findViewById(R.id.av_gallery_news).setVisibility(View.GONE);
                views[index].findViewById(R.id.av_gallery_grid_view).setVisibility(View.VISIBLE);
                GridView mGridView = (GridView) views[index].findViewById(R.id.av_gallery_grid_view);
                mGridView.setAdapter(new AVAdapter());
                mGridView.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent();
                        intent.setClass(parent.getContext(), AvGalleryDetailActivity.class);
                        intent.putExtra(EXT_KEY_GIRL, mGirls.get(position));
                        startActivity(intent);
                    }
                });
            } else if ("news".equals(tabs[position])) {
                views[index].findViewById(R.id.av_gallery_news).setVisibility(View.VISIBLE);
                views[index].findViewById(R.id.av_gallery_grid_view).setVisibility(View.GONE);
                final View errView = views[index].findViewById(R.id.av_gallery_main_news_err);
                final View loadingView = views[index].findViewById(R.id.loadingPanel);
                final View mainView = views[index].findViewById(R.id.av_gallery_main_news_main);
                final WebView webView = ((WebView) mainView.findViewById(R.id.av_gallery_main_news));
                mainView.findViewById(R.id.av_gallery_main_news_back).setVisibility(View.GONE);
                mainView.findViewById(R.id.av_gallery_main_news_back).setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (webView.canGoBack()) {
                            webView.goBack();
                        }
                    }

                });
                errView.findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        errView.setVisibility(View.GONE);
                        loadingView.setVisibility(View.VISIBLE);
                        webView.reload();
                        // webView.loadUrl(ServerUtils.getPicServerRoot(AvGalleryMainActivity.this) + "/jp/wordpress");
                    }
                });
                errView.findViewById(R.id.button2).setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                        startActivity(intent);
                    }
                });
                webView.getSettings().setSupportMultipleWindows(false);
                webView.getSettings().setBlockNetworkImage(false);
                // webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                // webView.getSettings().setJavaScriptEnabled(false);
                webView.setWebChromeClient(new WebChromeClient() {

                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);
                        if (newProgress >= 100) {
                            mainView.setVisibility(View.VISIBLE);
                            loadingView.setVisibility(View.GONE);
                        }
                    }

                });
                webView.setWebViewClient(new WebViewClient() {

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        if(webView.canGoBack()) {
                            mainView.findViewById(R.id.av_gallery_main_news_back).setVisibility(View.VISIBLE);
                        } else {
                            mainView.findViewById(R.id.av_gallery_main_news_back).setVisibility(View.GONE);
                        }
                        loadingView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        super.onReceivedError(view, errorCode, description, failingUrl);
                        errView.setVisibility(View.VISIBLE);
                    }

                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        errView.setVisibility(View.GONE);
                        loadingView.setVisibility(View.VISIBLE);
                        view.loadUrl(url);
                        return true;
                    }
                });
                errView.setVisibility(View.GONE);
                loadingView.setVisibility(View.VISIBLE);
                webView.loadUrl(ServerUtils.getPicServerRoot(AvGalleryMainActivity.this) + "/jp/wordpress");
            }
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

}
