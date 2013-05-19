package com.prettygirl.superstar;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.prettygirl.app.utils.AdUtils;
import com.prettygirl.app.utils.ServerUtils;
import com.prettygirl.superstar.dialog.ExitDialog;
import com.prettygirl.superstar.model.SuperStar;
import com.prettygirl.superstar.util.StorageUtils;
import com.prettygirl.superstar.util.StorageUtils.ILoadListener;
import com.prettygirl.superstar.util.UMengKey;
import com.umeng.analytics.MobclickAgent;

public class SuperStarActivity extends MBaseActivity implements ILoadListener, OnClickListener {

    private ArrayList<SuperStar> mGirls;

    private View mProgressView;

    private View mFailedPanelView;

    private GridView mGridView;

    private SuperStarAdapter mSuperStarAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.super_gallery_main_activity);
        mProgressView = findViewById(R.id.loadingPanel);
        mFailedPanelView = findViewById(R.id.failedPanel);
        mGridView = (GridView) findViewById(R.id.av_gallery_grid_view);
        StorageUtils.loadGrils(this, this);
        mProgressView.setVisibility(View.VISIBLE);
        setTitle(R.string.app_name);
        setGoBackIconVisibility(View.GONE);
        setAdViewClickListener(this);
        mGridView.setAdapter(mSuperStarAdapter = new SuperStarAdapter());
        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(SuperStarActivity.this, GalleryDetailActivity.class);
                SuperStar girl = mGirls.get(position);
                intent.putExtra(GalleryDetailActivity.EXT_IMAGE_INDEX, girl.id);
                intent.putExtra(GalleryDetailActivity.EXT_IMAGE_NAME, girl.name);
                startActivity(intent);
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        tryShowOnlineConfigAd();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ExitDialog d = new ExitDialog(this);
            d.show();
        }
        return result;
    }

    class SuperStarAdapter extends BaseAdapter implements SectionIndexer {

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
                convertView = View.inflate(context, R.layout.super_gallery_main_grid_item, null);
            }
            Object obj = convertView.getTag();
            ViewHolder cViewHolder = null;
            if (obj == null || !(obj instanceof ViewHolder)) {
                cViewHolder = new ViewHolder(convertView);
                convertView.setTag(cViewHolder);
            } else {
                cViewHolder = (ViewHolder) obj;
            }
            SuperStar girl = mGirls.get(position);
            cViewHolder.imageView.setImageDrawable(null);
            ImageLoader.getInstance().cancelDisplayTask(cViewHolder.imageView);
            ImageLoader.getInstance().displayImage(
                    String.format("%s/girl/%s/i.jpg", ServerUtils.getPicServerRoot(SuperStarActivity.this), girl.id),
                    cViewHolder.imageView);
            cViewHolder.mNameView.setText(girl.name);
            cViewHolder.mSubView.setText(girl.subTitle);
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

    @Override
    public void startLoad() {
        mProgressView.setVisibility(View.VISIBLE);
        mGridView.setVisibility(View.GONE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadFinished(Status status, Object obj) {
        ArrayList<SuperStar> cGirls = null;
        if (obj == null || !(obj instanceof ArrayList<?>)) {
            cGirls = null;
        } else {
            try {
                cGirls = (ArrayList<SuperStar>) obj;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (status == Status.Failed || cGirls == null) {
            mProgressView.setVisibility(View.GONE);
            mGridView.setVisibility(View.GONE);
            mFailedPanelView.setVisibility(View.VISIBLE);
        } else {
            mProgressView.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);
            mFailedPanelView.setVisibility(View.GONE);
            mGirls = cGirls;
            mSuperStarAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.entry_point_ad_icon) {
            MobclickAgent.onEvent(this, UMengKey.ENTRY_POINT_ACTIVITY_AD);
            AdUtils.handleMoreAppEvent(this);
        }
    }

}
