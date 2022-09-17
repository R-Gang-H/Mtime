package com.kotlin.android.widget.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineHeightSpan;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-06-14
 */
public class CommunityQuoteSpan implements LeadingMarginSpan, LineHeightSpan {

    public CommunityQuoteSpan(Context context, int resId, int pad) {
        Drawable d = context.getResources().getDrawable(resId);
        ini(d, pad);
    }

    public CommunityQuoteSpan(Drawable d, int pad) {
        ini(d, pad);
    }

    private void ini(Drawable b, int pad) {
        mDrawable = b;
        mPad = pad;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        int margin = mDrawable.getIntrinsicWidth() + mPad;
        return first ? margin : 0;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
                                  int top, int baseline, int bottom,
                                  CharSequence text, int start, int end,
                                  boolean first, Layout layout) {
        if (!first) return;
        int dw = mDrawable.getIntrinsicWidth();
        int dh = mDrawable.getIntrinsicHeight();
        // 在 第一行的 中间 画 图标
        int y = top + (bottom - top) / 2 - dh / 2;
        mDrawable.setBounds(x, y, x + dw, y + dh);
        mDrawable.draw(c);
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end,
                             int istartv, int v, Paint.FontMetricsInt fm) {
//        if (end == ((Spanned) text).getSpanEnd(this)) {
//            int ht = mDrawable.getIntrinsicHeight();
//
//            int need = ht - (v + fm.descent - fm.ascent - istartv);
//            if (need > 0)
//                fm.descent += need;
//
//            need = ht - (v + fm.bottom - fm.top - istartv);
//            if (need > 0)
//                fm.bottom += need;
//        }
    }

    private Drawable mDrawable;
    private int mPad;
}
