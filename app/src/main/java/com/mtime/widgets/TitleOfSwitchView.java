package com.mtime.widgets;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.bussiness.ticket.MovieAndCinemaSwitchView.IMovieAndCinemaSwitchViewListener;

public class TitleOfSwitchView extends BaseTitleView {
    
    private final View     root;
    private final View     viewAlpha;
    // 判断当前是哪个部分被选中
    private boolean  isLeftSelected = true;
    private final TextView leftTextView;
    private final TextView rightTextView;
    
    @SuppressLint("CutPasteId")
    public TitleOfSwitchView(final BaseActivity context, final View root,
            final IMovieAndCinemaSwitchViewListener listener, final ITitleViewLActListener titleListener,
            final String leftString, final String rightString) {
        this.root = root;
        this.viewAlpha = root.findViewById(R.id.background);
        
        View back = root.findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                context.finish();
                
                if (null != titleListener) {
                    titleListener.onEvent(ActionType.TYPE_BACK, null);
                }
                
            }
        });
        leftTextView = root.findViewById(R.id.left_label);
        rightTextView = root.findViewById(R.id.right_label);
        if (leftString != null && !"".equals(leftString)) {
            leftTextView.setText(leftString);
        }
        if (rightString != null && !"".equals(rightString)) {
            rightTextView.setText(rightString);
        }
        leftTextView.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (isLeftSelected) {
                    return;
                }
                isLeftSelected = true;
                leftTextView.setBackgroundResource(R.drawable.bg_common_switch_item_shape);
                rightTextView.setBackgroundResource(0);
//                rightTextView.setBackgroundResource(R.drawable.title_bar_switch_view_right_normal);
                leftTextView.setTextColor(ContextCompat.getColor(context,R.color.color_1D2736));
                rightTextView.setTextColor(ContextCompat.getColor(context,R.color.color_8798AF));
                
                if (null != listener) {
                    listener.onEvent(true);
                }
                
            }
        });
        rightTextView.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (!isLeftSelected) {
                    return;
                }
                isLeftSelected = false;
//                leftTextView.setBackgroundResource(R.drawable.title_bar_switch_view_left_normal);
                rightTextView.setBackgroundResource(R.drawable.bg_common_switch_item_shape);
                leftTextView.setBackgroundResource(0);
                leftTextView.setTextColor(ContextCompat.getColor(context,R.color.color_8798AF));
                rightTextView.setTextColor(ContextCompat.getColor(context,R.color.color_1D2736));
                
                if (null != listener) {
                    listener.onEvent(false);
                }
            }
            
        });
        
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void setAlpha(float alpha) {
        if (null != viewAlpha && android.os.Build.VERSION.SDK_INT >= 11) {
            float a = alpha < MIN_ALPHA ? 0 : alpha;
            this.viewAlpha.setAlpha(a > 1 ? 1 : a);
        }
    }
    
    public void setVisibile(int visibility) {
        this.root.setVisibility(visibility);
    }
    
    public View getRootView() {
        return this.root;
    }
    
}
