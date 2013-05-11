package com.prettygirl.app.dialog;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pretty.lib.R;

public abstract class DialogActivity extends Activity implements OnClickListener {

    protected static final int BUTTON_POSITIVE = 0;

    protected static final int BUTTON_NEGATIVE = 1;

    protected static final int BUTTON_NEUTRAL = 2;

    private Button mButtonPositive;

    private Button mButtonNegative;

    private Button mButtonNeutral;

    // contains the dialog body
    private LinearLayout mBodyContainer;

    protected LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.DialogActivity);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_activity);
        setupViews();
    }

    private void setupViews() {
        mButtonPositive = (Button) findViewById(R.id.button_positive);
        mButtonPositive.setOnClickListener(this);
        mButtonNegative = (Button) findViewById(R.id.button_negative);
        mButtonNegative.setOnClickListener(this);
        mButtonNeutral = (Button) findViewById(R.id.button_neutral);
        mButtonNeutral.setOnClickListener(this);

        mBodyContainer = (LinearLayout) findViewById(R.id.contentPanel);

        mInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    protected void setListView(BaseAdapter adapter) {
        ListView listView = (ListView) mInflater.inflate(R.layout.list_activity_list, null);
        mBodyContainer.removeAllViews();
        mBodyContainer.addView(listView, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
        listView.setAdapter(adapter);
    }

    protected void setMessage(String text) {
        View v = mInflater.inflate(R.layout.list_activity_message, null);
        mBodyContainer.removeAllViews();
        mBodyContainer.addView(v, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
        TextView textView = (TextView) v.findViewById(R.id.message);
        textView.setText(text);
    }

    protected void setContantView(View contantView) {
        mBodyContainer.removeAllViews();
        mBodyContainer.addView(contantView, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
    }

    @Override
    public void setTitle(CharSequence title) {
        setTitle(0, (String) title);
    }

    protected void setTitle(int iconResId, String titleText) {
        if (iconResId > 0) {
            ImageView icon = (ImageView) findViewById(R.id.icon);
            icon.setImageResource(iconResId);
            icon.setVisibility(View.VISIBLE);
        }
        TextView textView = (TextView) findViewById(R.id.alertTitle);
        textView.setText(titleText);
    }

    // TODO not tested
    public void setCustomTitle(View customTitleView) {
        // View titleTemplate = findViewById(R.id.title_template);
        // titleTemplate.setVisibility(View.GONE);
        LinearLayout topPanel = (LinearLayout) findViewById(R.id.topPanel);
        topPanel.removeAllViews();
        topPanel.addView(customTitleView);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_positive) {
            onDialogButtonClick(BUTTON_POSITIVE);
        } else if (id == R.id.button_negative) {
            onDialogButtonClick(BUTTON_NEGATIVE);
        } else if (id == R.id.button_neutral) {
            onDialogButtonClick(BUTTON_NEUTRAL);
        }
    }

    /**
     * setup button you want to show,don't call this will show no button
     * activity
     *
     * @param whichButton
     * @param text one of these : {@value BUTTON_POSITIVE}{@value
     *            BUTTON_NEGATIVE} {@value BUTTON_NEUTRAL}
     */
    protected Button setupButton(int whichButton, CharSequence text) {
        switch (whichButton) {
        case BUTTON_POSITIVE:
            showButtonContainer();
            mButtonPositive.setVisibility(View.VISIBLE);
            mButtonPositive.setText(text);
            return mButtonPositive;
        case BUTTON_NEGATIVE:
            showButtonContainer();
            mButtonNegative.setVisibility(View.VISIBLE);
            mButtonNegative.setText(text);
            return mButtonNegative;
        case BUTTON_NEUTRAL:
            showButtonContainer();
            mButtonNeutral.setVisibility(View.VISIBLE);
            mButtonNeutral.setText(text);
            return mButtonNeutral;
        default:
            return null;
        }
    }

    private void showButtonContainer() {
        View view = findViewById(R.id.buttonPanel);
        view.setVisibility(View.VISIBLE);
    }

    /**
     * if you want receive button click msg, you should override this method
     *
     * @param which one of these : {@value BUTTON_POSITIVE}{@value
     *            BUTTON_NEGATIVE} {@value BUTTON_NEUTRAL}
     */
    protected void onDialogButtonClick(int which) {
    }

}
