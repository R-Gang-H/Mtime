package com.mtime.bussiness.mine.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.widgets.BaseTitleView;

public class TitleOfLoginView extends BaseTitleView {

    private final View rootView;
    private final TextView title;
    private final View logo;
    private final TextView skip;
    private final Activity context;
    
    public TitleOfLoginView(final Activity context, final View root, final StructType type, final String label, final ITitleViewLActListener listener) {
        this.context = context;
        this.rootView = root.findViewById(R.id.background);
        
        title = root.findViewById(R.id.title);
        if (!TextUtils.isEmpty(label)) {
            title.setText(label);
        }
        
        this.logo = root.findViewById(R.id.logo);
        this.skip = root.findViewById(R.id.skip);
        switch(type) {
            case TYPE_LOGIN_SHOW_LOGO_ONLY:
                title.setVisibility(View.INVISIBLE);
                skip.setVisibility(View.INVISIBLE);
                break;
            case TYPE_LOGIN_SHOW_LOGO_SKIP:
                title.setVisibility(View.INVISIBLE);
                break;
            case TYPE_LOGIN_SHOW_TITLE_ONLY:
                logo.setVisibility(View.INVISIBLE);
                skip.setVisibility(View.INVISIBLE);
                break;
            case TYPE_LOGIN_SHOW_TITLE_SKIP:
                logo.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
        
        View back = root.findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_BACK, null);
                }
                
            }});
        
        skip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_SKIP, null);
                }
                
            }});
        
    }
    
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void setAlpha(float alpha) {
        if (android.os.Build.VERSION.SDK_INT < 11) {
            return;
        }
        
        if (this.rootView.getAlpha() == alpha) {
            return;
        }
        
        float a = alpha < MIN_ALPHA ? 0 : alpha;
        this.rootView.setAlpha(a > 1 ? 1 : a);
    }
    
    public void setLabel(final String label) {
        if (!TextUtils.isEmpty(label)) {
            this.title.setVisibility(View.VISIBLE);
            this.logo.setVisibility(View.INVISIBLE);
            this.title.setText(label);
        } else {
            this.title.setVisibility(View.INVISIBLE);
            this.logo.setVisibility(View.VISIBLE);
        }
    }
    
    /** 设置右边文字 */
    public void setRightText(String text, boolean clickable) {
        if (clickable) {
            if (text != null) {
                this.skip.setVisibility(View.VISIBLE);
                this.skip.setTextColor(ContextCompat.getColor(context,R.color.color_8798AF));
                this.skip.setText(text);
                this.skip.setClickable(true);
            }
        }
        else {
            this.skip.setVisibility(View.VISIBLE);
            this.skip.setTextColor(ContextCompat.getColor(context,R.color.color_999999));
            this.skip.setClickable(false);
            if (text != null) {
                this.skip.setText(text);
            }
        }
    }
    
    
    public void hideSkip() {
        if (null != this.skip) {
            skip.setVisibility(View.INVISIBLE);
        }
    }
}
