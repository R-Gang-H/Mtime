package com.mtime.bussiness.ticket.movie.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtime.R;

/**
 * Created by LEE on 9/18/16.
 */
public class AppraiseOfPerson extends RelativeLayout {
    public interface OnApprasiePersonListener {
        void onEvent(final int state);
    }


    private OnApprasiePersonListener listener;
    private final int EVENT_COMMENTS = 0;
    private final int EVENT_LIKE = 1;
    private final int EVENT_UNLIKE = 2;

    private TextView like;
    private TextView unlike;

    public AppraiseOfPerson(Context context) {
        super(context);

        init(context);
    }

    public AppraiseOfPerson(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public AppraiseOfPerson(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        View root = LayoutInflater.from(context).inflate(R.layout.appraiseofperson, null);
        final View comments = root.findViewById(R.id.appraise_tips);
        comments.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(EVENT_COMMENTS);
                }
            }
        });

        like = root.findViewById(R.id.like);
        like.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(EVENT_LIKE);
                }
            }
        });

        unlike = root.findViewById(R.id.unlike);
        unlike.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(EVENT_UNLIKE);
                }
            }
        });

        this.addView(root);
    }

    public void setListener(OnApprasiePersonListener listener) {
        this.listener = listener;
    }

    public void setState(final boolean liked, final boolean unliked) {
        // like
        Drawable likedrawable;
        int likecolor;
        if (liked) {
            likedrawable = ContextCompat.getDrawable(getContext(), R.drawable.actor_detail_like);
            likecolor = ContextCompat.getColor(getContext(), R.color.color_f15353);
        } else {
            likedrawable = ContextCompat.getDrawable(getContext(), R.drawable.actor_detail_like_base);
            likecolor = ContextCompat.getColor(getContext(), R.color.color_777777);
        }

        like.setTextColor(likecolor);
        likedrawable.setBounds(0, 0, likedrawable.getMinimumWidth(), likedrawable.getMinimumHeight());
        like.setCompoundDrawables(null, likedrawable, null, null);

        // unlike
        Drawable unlikedrawable;
        int unlikccolor;
        if (unliked) {
            unlikedrawable = ContextCompat.getDrawable(getContext(),R.drawable.actor_detail_unlike);
            unlikccolor = ContextCompat.getColor(getContext(), R.color.color_669e0e);
        } else {
            unlikedrawable = ContextCompat.getDrawable(getContext(),R.drawable.actor_detail_unlike_base);
            unlikccolor = ContextCompat.getColor(getContext(), R.color.color_777777);
        }

        unlike.setTextColor(unlikccolor);
        unlikedrawable.setBounds(0, 0, unlikedrawable.getMinimumWidth(), unlikedrawable.getMinimumHeight());
        unlike.setCompoundDrawables(null, unlikedrawable, null, null);
    }
}
