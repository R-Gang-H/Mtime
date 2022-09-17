package com.mtime.bussiness.ticket;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mtime.R;

public class MovieAndCinemaSwitchView {
    
    public interface IMovieAndCinemaSwitchViewListener {
        void onEvent(final boolean leftOn);
    }
    
    // 判断当前是哪个部分被选中
    private boolean  isMoveViewSelected = true;
    private final TextView movieView;
    public TextView cinemaView;
    private final View     root;
    
    public MovieAndCinemaSwitchView(final Context context, final View parent, final IMovieAndCinemaSwitchViewListener listener,
                                    final int couponRemindType) {
        this.root = parent;
        movieView = parent.findViewById(R.id.movie_label);
        cinemaView = parent.findViewById(R.id.cinema_label);

        setCinemaViewOn(context, 2 == couponRemindType);

        movieView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (isMoveViewSelected) {
                    return;
                }

                setCinemaViewOn(v.getContext(), false);
                if (null != listener) {
                    listener.onEvent(true);
                }
            }
        });
        
        cinemaView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (!isMoveViewSelected) {
                    return;
                }

                setCinemaViewOn(v.getContext(), true);
                // listener
                if (null != listener) {
                    listener.onEvent(false);
                }
            }
        });
        
    }
    public void setCinemaViewOn(final Context context, boolean on){
        if(on){
            isMoveViewSelected = false;
            // 未选中
            movieView.setBackgroundResource(0);
            movieView.setTextColor(ContextCompat.getColor(context,R.color.color_8798AF));
            // 选中
            cinemaView.setBackgroundResource(R.drawable.bg_common_switch_item_shape);
            cinemaView.setTextColor(ContextCompat.getColor(context,R.color.color_ffffff));
        }else{
            isMoveViewSelected = true;
            // 选中
            movieView.setBackgroundResource(R.drawable.bg_common_switch_item_shape);
            movieView.setTextColor(ContextCompat.getColor(context,R.color.color_ffffff));
            // 未选中
            cinemaView.setBackgroundResource(0);
            cinemaView.setTextColor(ContextCompat.getColor(context,R.color.color_8798AF));
        }
    }
    public void setVisibility(int value) {
        if (null != root) {
            root.setVisibility(value);
        }
    }
    
}
