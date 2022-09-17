package com.mtime.bussiness.ticket.widget;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-06-26
 */
public class TabSizeTextView extends AppCompatTextView {

    private int mSelectedTextSize = 14;
    private int mDefaultTextSize = 12;

    private boolean mSelectedBold = false;

    public TabSizeTextView(Context context) {
        super(context);
    }

    public TabSizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabSizeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mSelectedTextSize);
            if (mSelectedBold) {
                setTypeface(Typeface.DEFAULT_BOLD);
            }
        } else {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mDefaultTextSize);
            if (mSelectedBold) {
                setTypeface(Typeface.DEFAULT);
            }
        }
    }

    public void setSelectedBold(boolean selectedBold) {
        mSelectedBold = selectedBold;
    }

    public void setSelectedTextSize(int selectedTextSize) {
        mSelectedTextSize = selectedTextSize;
    }

    public void setDefaultTextSize(int defaultTextSize) {
        mDefaultTextSize = defaultTextSize;
    }
}
