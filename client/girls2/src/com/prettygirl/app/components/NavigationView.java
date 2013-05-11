package com.prettygirl.app.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.pretty.girl.app.R;

public class NavigationView extends ListView {

    private int mMaxlevel;
    private MNavigationAdapter mAdapter;

    public NavigationView(Context context) {
        super(context);
        init(context);
    }

    public NavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NavigationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mMaxlevel = getResources().getInteger(R.integer.e_navigation_max_level);
        setDivider(null);
        this.setSelector(R.drawable.navigation_list_selector);
        mAdapter = new MNavigationAdapter();
        setAdapter(mAdapter);
    }

    public void notifyDataChanged() {
        mAdapter.notifyDataSetChanged();
    }

    class MNavigationAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mMaxlevel;
        }

        @Override
        public Object getItem(int pos) {
            return pos;
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null || convertView instanceof NavigationViewItem) {
                convertView = new NavigationViewItem(parent.getContext());
            }
            NavigationViewItem cNavigationViewItem = (NavigationViewItem) convertView;
            cNavigationViewItem.setCurrentLevel(position);
            return cNavigationViewItem;
        }

    }

}
