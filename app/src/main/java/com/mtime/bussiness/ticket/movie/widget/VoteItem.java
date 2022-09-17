package com.mtime.bussiness.ticket.movie.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.mtime.R;
import com.mtime.bussiness.ticket.movie.bean.VoteDataOptionBean;


/**
 * Created by guangshun on 16-6-23.
 * 投票的选项Item
 */
public class VoteItem extends RelativeLayout {
    private VoteDataOptionBean optionBean;

    public VoteItem(Context context) {
        super(context);
        init();
    }

    public void setOptionBean(VoteDataOptionBean optionBean) {
        this.optionBean = optionBean;
    }

    public VoteDataOptionBean getOptionBean() {
        return optionBean;
    }

    public VoteItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VoteItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.vote_item, this);
    }

    public int getItemHeight() {
        if (0 == getMeasuredHeight()) {
            measure(View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED));
        }
        return getMeasuredHeight();
    }
}
