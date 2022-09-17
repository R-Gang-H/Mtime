package com.mtime.bussiness.ticket.cinema.widget;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtime.R;

/**
 * Created by LEE on 9/18/16.
 */
public class AppraiseOfCinema extends RelativeLayout {
    public interface OnAppraiseViewClickListener {
        void onEvent(final int type);
    }

    private final int EVENT_COMMENTS = 0;
    private final int EVENT_APPRAISE = 1;

    private OnAppraiseViewClickListener listener;
    private TextView appraise;

    public AppraiseOfCinema(Context context) {
        super(context);

        init(context);
    }

    public AppraiseOfCinema(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public AppraiseOfCinema(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        View root = LayoutInflater.from(context).inflate(R.layout.appraiseofcinema, null);
        View comment = root.findViewById(R.id.comments);
        comment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(EVENT_COMMENTS);
                }
            }
        });

        appraise = root.findViewById(R.id.appraise);
        appraise.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(EVENT_APPRAISE);
                }
            }
        });

        this.addView(root);
    }

    public void setListener(final OnAppraiseViewClickListener listener) {
        this.listener = listener;
    }

    public void setAppraise(final String appraise) {
        if (TextUtils.isEmpty(appraise)) {
            return;
        }

        // 更新评价内容
        final String prefix = "我评 ";
        SpannableStringBuilder spanString = new SpannableStringBuilder(prefix);
        spanString.append(appraise);
        spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_f15353))
                , prefix.length(), spanString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        this.appraise.setText(spanString);
    }
}
