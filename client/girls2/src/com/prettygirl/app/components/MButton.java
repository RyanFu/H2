package com.prettygirl.app.components;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;

import com.pretty.girl.app.R;

public class MButton extends Button {

	public MButton(Context context) {
		super(context);
	}

	public MButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private void changeDrawableSize(Drawable mDrawable, int width, int height) {
		if (mDrawable != null) {
			Rect mRect = mDrawable.copyBounds();
			mRect.left = 0;
			mRect.top = 0;
			mRect.right = mRect.left + width;
			mRect.bottom = mRect.top + height;
			mDrawable.setBounds(mRect);
		}
	}

	@Override
	public void setCompoundDrawables(Drawable left, Drawable top,
			Drawable right, Drawable bottom) {
		int mqDrawableTopWidth = getResources().getDimensionPixelSize(
				R.dimen.thumb_up_icon_drawableWidth);
		int mqDrawableTopHeight = getResources().getDimensionPixelSize(
				R.dimen.thumb_up_icon_drawableHeight);
		changeDrawableSize(left, mqDrawableTopWidth, mqDrawableTopHeight);
		changeDrawableSize(top, mqDrawableTopWidth, mqDrawableTopHeight);
		changeDrawableSize(right, mqDrawableTopWidth, mqDrawableTopHeight);
		changeDrawableSize(bottom, mqDrawableTopWidth, mqDrawableTopHeight);
		super.setCompoundDrawables(left, top, right, bottom);
	}

}
