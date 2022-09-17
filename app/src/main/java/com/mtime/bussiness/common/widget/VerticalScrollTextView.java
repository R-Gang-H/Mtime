package com.mtime.bussiness.common.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/3/21
 */
public class VerticalScrollTextView extends TextSwitcher implements ViewSwitcher.ViewFactory {

    private static final int FLAG_START_AUTO_SCROLL = 0;
    private static final int FLAG_STOP_AUTO_SCROLL = 1;

    private static final int STATE_PAUSE = 2;
    private static final int STATE_SCROLL = 3;

    private float mTextSize = 16;
    private int mPadding = 5;
    private int textColor = Color.BLACK;

    private int mScrollState = STATE_PAUSE;

    /**
     * @param textSize  textsize
     * @param padding   padding
     * @param textColor textcolor
     */
    public void setText(float textSize, int padding, int textColor) {
        mTextSize = textSize;
        mPadding = padding;
        this.textColor = textColor;
    }

    private OnItemClickListener itemClickListener;
    private Context mContext;
    private int currentId = -1;
    private ArrayList<String> textList;
    private Handler handler;

    public VerticalScrollTextView(Context context) {
        this(context, null);
        init(context);
    }

    public VerticalScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        textList = new ArrayList<String>();
        setFactory(this);
    }

    public void setAnimTime(long animDuration) {
        post(new Runnable() {
            @Override
            public void run() {
                Animation in = new TranslateAnimation(0, 0, getMeasuredHeight(), 0);
                in.setDuration(animDuration);
                in.setInterpolator(new AccelerateInterpolator());
                Animation out = new TranslateAnimation(0, 0, 0, -getMeasuredHeight());
                out.setDuration(animDuration);
                out.setInterpolator(new AccelerateInterpolator());
                setInAnimation(in);
                setOutAnimation(out);
            }
        });
    }

    /**
     * set time.
     *
     * @param time
     */
    public void setTextStillTime(final long time) {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case FLAG_START_AUTO_SCROLL:
                        if (textList.size() > 0) {
                            currentId++;
                            setText(textList.get(currentId % textList.size()));
                        }
                        if (textList.size() == 1) {
                            stopAutoScroll();
                            break;
                        }
                        handler.sendEmptyMessageDelayed(FLAG_START_AUTO_SCROLL, time);
                        break;
                    case FLAG_STOP_AUTO_SCROLL:
                        handler.removeMessages(FLAG_START_AUTO_SCROLL);
                        break;
                }
            }
        };
    }

    @Override
    public void setText(CharSequence text) {
        TextView nextView = (TextView) getNextView();
        if(null != nextView) {
            nextView.setPadding(mPadding, mPadding, mPadding, mPadding);
            nextView.setTextColor(textColor);
            nextView.setTextSize(mTextSize);
        }
        super.setText(text);
    }

    /**
     * set Data list.
     *
     * @param titles
     */
    public void setTextList(ArrayList<String> titles) {
        textList.clear();
        textList.addAll(titles);
        currentId = -1;
    }

    /**
     * start auto scroll
     */
    public void startAutoScroll() {
        if (mScrollState != STATE_SCROLL) {
            mScrollState = STATE_SCROLL;
            handler.sendEmptyMessage(FLAG_START_AUTO_SCROLL);
        }
    }

    /**
     * stop auto scroll
     */
    public void stopAutoScroll() {
        if (mScrollState != STATE_PAUSE) {
            mScrollState = STATE_PAUSE;
            handler.sendEmptyMessage(FLAG_STOP_AUTO_SCROLL);
        }
    }

    @Override
    public View makeView() {
        TextView t = new TextView(mContext);
//        t.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        t.setLines(1);
        t.setEllipsize(TextUtils.TruncateAt.END);

        t.setClickable(true);
        t.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null && textList.size() > 0 && currentId != -1) {
                    itemClickListener.onItemClick(currentId % textList.size());
                }
            }
        });
        return t;
    }

    /**
     * set onclick listener
     *
     * @param itemClickListener listener
     */
    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     * item click listener
     */
    public interface OnItemClickListener {
        /**
         * callback
         *
         * @param position position
         */
        void onItemClick(int position);
    }

    public boolean isScroll() {
        return mScrollState == STATE_SCROLL;
    }

    public boolean isPause() {
        return mScrollState == STATE_PAUSE;
    }

    //memory leancks.
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (textList.size() > 1) {
            stopAutoScroll();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (textList.size() > 1) {
            startAutoScroll();
        }
    }
}
