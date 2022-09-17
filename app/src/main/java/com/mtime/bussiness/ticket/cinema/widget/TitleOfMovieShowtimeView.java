package com.mtime.bussiness.ticket.cinema.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.widgets.BaseTitleView;

/**
 * Created by vivian.wei on 2017/12/2.
 * 影片排片页title
 */

public class TitleOfMovieShowtimeView  extends BaseTitleView {

    private final View mRootView;
    private final TextView mTitleTv;

    public TitleOfMovieShowtimeView(final Context context, final View root, final ITitleViewLActListener listener) {
        mRootView = root;
        mTitleTv = mRootView.findViewById(R.id.title_bar_movie_showtime_title_tv);

        View backLayout = mRootView.findViewById(R.id.title_bar_movie_showtime_back_rl);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_BACK, null);
                }
            }
        });

        ImageView searchIconIv = mRootView.findViewById(R.id.title_bar_movie_showtime_search_icon_iv);
        searchIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_SEARCH, null);
                }
            }
        });

    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void setAlpha(float alpha) {
        if (android.os.Build.VERSION.SDK_INT < 11 || null == mRootView) {
            return;
        }

        if (mRootView.getAlpha() == alpha) {
            return;
        }

        float a = alpha < MIN_ALPHA ? 0 : alpha;
        mRootView.setAlpha(a > 1 ? 1 : a);
    }

    public void setTitle(final String title) {
        if (null != mTitleTv) {
            mTitleTv.setText(title);
        }
    }

}
