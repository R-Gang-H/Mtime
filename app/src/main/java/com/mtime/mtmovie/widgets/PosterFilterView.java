package com.mtime.mtmovie.widgets;

/**
 * Created by mtime on 15/8/10.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.mtime.R;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.base.views.ForegroundImageView;
import com.mtime.frame.App;

public class PosterFilterView extends ForegroundImageView {
    private Drawable mPosterImg;
    private Paint mPaint;
    private Paint mTxtPaint;
    private Paint mRoundRectPaint;
    private final RectF mRoundRectRectF = new RectF();
    private String mTxt;
    private int mTxtColor;
    private int mTxtSize;
    private boolean showPosterFilter;//显示过滤

    public void setPosterFilter(boolean posterFilter) {
        showPosterFilter = posterFilter;
    }

    public PosterFilterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public PosterFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PosterFilterView(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PosterFilterView);
            mPosterImg = a.getDrawable(R.styleable.PosterFilterView_poster_img);
            mTxt = a.getString(R.styleable.PosterFilterView_text_txt);
            mTxtColor = a.getColor(R.styleable.PosterFilterView_text_color, context.getResources().getColor(R.color.color_bbbbbb));
            mTxtSize = a.getDimensionPixelSize(R.styleable.PosterFilterView_text_size, MScreenUtils.sp2px(12));
            a.recycle();
        }

        if (null == mPosterImg) {
            mPosterImg = context.getResources().getDrawable(R.drawable.shield_poster);
        }
        if (TextUtils.isEmpty(mTxt)) {
            mTxt = context.getResources().getString(R.string.movie_cover_filter);
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mTxtPaint = new Paint();
        mTxtPaint.setAntiAlias(true);
        mTxtPaint.setStyle(Paint.Style.FILL);
        mTxtPaint.setTextSize(mTxtSize);

        mRoundRectPaint = new Paint();
        mRoundRectPaint.setAntiAlias(true);
        mRoundRectPaint.setStyle(Paint.Style.FILL);
        mRoundRectPaint.setColor(Color.parseColor("#E6282b2e"));

    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**画过滤*/
        if (showPosterFilter && App.getInstance().FILTER_SET) {
            mRoundRectRectF.set(0 + getPaddingLeft(), 0 + getPaddingTop(),
                    getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingBottom());
            canvas.drawRoundRect(mRoundRectRectF, MScreenUtils.dp2px(4), MScreenUtils.dp2px(4), mRoundRectPaint);
            Bitmap bitma = ((BitmapDrawable) mPosterImg).getBitmap();
            int left = this.getMeasuredWidth() / 2 - bitma.getWidth() / 2;
            int top = getMeasuredHeight() / 2 - (bitma.getHeight() + getShortHeight()) / 2;
            canvas.drawBitmap(bitma, left, top, mPaint);
            mTxtPaint.setColor(mTxtColor);
            canvas.drawText(mTxt, getMeasuredWidth() / 2 - mTxtPaint.measureText(mTxt) / 2, bitma.getHeight() + top + MScreenUtils.dp2px(15), mTxtPaint);
        }
    }
    
    private int getShortHeight() {
        return (int)(mTxtPaint.descent() - mTxtPaint.ascent());
    }
}

