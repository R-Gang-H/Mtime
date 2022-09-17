package com.mtime.frame.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kk.taurus.uiframe.d.BaseTitleBarParams;
import com.kk.taurus.uiframe.i.ITitleBar;
import com.kk.taurus.uiframe.v.BaseTitleBarHolder;
import com.mtime.R;

/**
 * Created by Taurus on 2017/10/9.
 */

public class DefaultTitleBarHolder extends BaseTitleBarHolder {

    public static final int TITLE_BAR_EVENT_LEFT_BUTTON_CLICK = -3001;
    public static final int TITLE_BAR_EVENT_RIGHT_BUTTON_CLICK = -3002;
    public static final int TITLE_BAR_EVENT_TEXT_BUTTON_CLICK = -3003;

    private TextView mTitle, mSubTitle, mTextButton;
    private ImageButton mLeftButton,mRightButton;

    public static final int TITLE_MODE_TOP_TITLE = 0;
    public static final int TITLE_MODE_ALL_TITLE = 2;

    public static final int RIGHT_BUTTON_MODE_TEXT = 0;
    public static final int RIGHT_BUTTON_MODE_IMAGE_01 = 1;
    public static final int RIGHT_BUTTON_MODE_IMAGE_02 = 2;

    private int rightButtonMode = RIGHT_BUTTON_MODE_TEXT;

    private int titleMode = TITLE_MODE_TOP_TITLE;

    public DefaultTitleBarHolder(Context context) {
        super(context);
    }

    @Override
    public BaseTitleBarParams getTitleBarParams() {
        BaseTitleBarParams params = new BaseTitleBarParams();
        params.titleBarHeight = (int) getDimension(R.dimen.title_bar_height);
        return params;
    }

    @Override
    public void onCreate() {
        setContentView(R.layout.layout_default_title_bar);

        mTitle = getViewById(R.id.top_title);
        mSubTitle = getViewById(R.id.bottom_title);
        mLeftButton = getViewById(R.id.btn_left);
        mRightButton = getViewById(R.id.btn_right);
        mTextButton = getViewById(R.id.tv_text_button);

        mLeftButton.setVisibility(View.GONE);
        mRightButton.setVisibility(View.GONE);

        mTextButton.setOnClickListener(this);
        mLeftButton.setOnClickListener(this);
        mRightButton.setOnClickListener(this);
        getViewById(R.id.back).setOnClickListener(this);

        updateTitleMode(titleMode);
        updateRightButtonMode(RIGHT_BUTTON_MODE_TEXT);
    }

    public void setTitleMode(int titleMode){
        this.titleMode = titleMode;
        updateTitleMode(titleMode);
    }

    private void updateTitleMode(int titleMode){
        switch (titleMode){
            case TITLE_MODE_TOP_TITLE:
                mTitle.setVisibility(View.VISIBLE);
                mSubTitle.setVisibility(View.GONE);
                break;
            case TITLE_MODE_ALL_TITLE:
                mTitle.setVisibility(View.VISIBLE);
                mSubTitle.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setRightButtonMode(int rightButtonMode) {
        this.rightButtonMode = rightButtonMode;
        updateRightButtonMode(rightButtonMode);
    }

    private void updateRightButtonMode(int rightButtonMode){
        switch (rightButtonMode){
            case RIGHT_BUTTON_MODE_TEXT:
                mTextButton.setVisibility(View.VISIBLE);
                mLeftButton.setVisibility(View.GONE);
                mRightButton.setVisibility(View.GONE);
                break;
            case RIGHT_BUTTON_MODE_IMAGE_01:
                mTextButton.setVisibility(View.GONE);
                mLeftButton.setVisibility(View.VISIBLE);
                mRightButton.setVisibility(View.GONE);
                break;
            case RIGHT_BUTTON_MODE_IMAGE_02:
                mTextButton.setVisibility(View.GONE);
                mLeftButton.setVisibility(View.GONE);
                mRightButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setTitle(CharSequence title){
        mTitle.setText(title);
    }

    public void setAlpha(float alpha){
        mRootView.setAlpha(alpha);
    }

    public void setTitleBarBackgroundColor(int color){
        mRootView.setBackgroundColor(color);
    }

    public void setSubTitle(CharSequence subTitle){
        mSubTitle.setText(subTitle);
    }

    public void setRightButton1(int resId){
        mLeftButton.setImageResource(resId);
    }

    public void setRightButton2(int resId){
        mRightButton.setImageResource(resId);
    }

    public void setTextButtonText(String text){
        mTextButton.setText(text);
    }

    public void setTextButtonText(int resId){
        mTextButton.setText(resId);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_left:
                onHolderEvent(TITLE_BAR_EVENT_LEFT_BUTTON_CLICK,null);
                break;

            case R.id.btn_right:
                onHolderEvent(TITLE_BAR_EVENT_RIGHT_BUTTON_CLICK,null);
                break;

            case R.id.tv_text_button:
                onHolderEvent(TITLE_BAR_EVENT_TEXT_BUTTON_CLICK,null);
                break;

            case R.id.back:
                onHolderEvent(ITitleBar.TITLE_BAR_EVENT_NAVIGATION_CLICK,null);
                break;
        }
    }
}
