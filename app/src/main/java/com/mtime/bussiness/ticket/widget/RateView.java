package com.mtime.bussiness.ticket.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mtime.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 最大支持10个。
 * 
 * @author LEE
 * 
 */
public class RateView extends LinearLayout implements OnClickListener {
    
    public interface IRateViewListener {
        void onEvent(final int rateValue);
    }
    
    private final int         MAX_CHILDRENS = 10;
    
    private List<ImageView>   childrens;
    private int               resNormalId;
    private int               resSelectedId;
    private int               currentSelected;
    private int               lastSelected;
    private int               left;
    private int               unitWidth     = 0;
    
    private IRateViewListener listener;
    
    public RateView(Context context) {
        super(context);
    }
    
    public RateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (0 == unitWidth) {
                    unitWidth = (this.getWidth() - left) / childrens.size();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getX() <= this.left) {
                    currentSelected = 0;
                }
                else {
                    currentSelected = (int) Math.ceil(event.getX() / this.unitWidth);
                }
                // 确保点击评分之后至少不低于1分，from MovieRateView line 324
                currentSelected = currentSelected == 0 ? 1 : currentSelected;
                this.select();
                break;
        }
        
        return super.onTouchEvent(event);
    }
    
    public void setListener(final IRateViewListener listener) {
        this.listener = listener;
    }
    
    public void addResources(final int childrensNum, final int resNormalId, final int resSelectedId) {
        this.init();
        
        int num = childrensNum > MAX_CHILDRENS ? MAX_CHILDRENS : childrensNum;
        this.childrens = new ArrayList<ImageView>(num);
        this.resNormalId = resNormalId;
        this.resSelectedId = resSelectedId;
        
        int left = this.getResources().getDimensionPixelSize(R.dimen.rate_view_margin_left) / 2;
        int top = this.getResources().getDimensionPixelSize(R.dimen.rate_view_margin_top);
        int right = this.getResources().getDimensionPixelSize(R.dimen.rate_view_margin_right) / 2;
        int bottom = this.getResources().getDimensionPixelSize(R.dimen.rate_view_margin_bottom);
        this.left = left;
        
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(left, top, right, bottom);
        
        for (int i = 0; i < childrensNum; i++) {
            addView(i + 1, params);
        }
        
    }
    
    public void addResources(final int childrensNum, final int resNormalId, final int resSelectedId,
            final LayoutParams paramsLinearLayout) {
        int num = childrensNum > MAX_CHILDRENS ? MAX_CHILDRENS : childrensNum;
        this.childrens = new ArrayList<ImageView>(num);
        this.resNormalId = resNormalId;
        this.resSelectedId = resSelectedId;
        
        for (int i = 0; i < childrensNum; i++) {
            addView(i + 1, paramsLinearLayout);
        }
        
        this.left = paramsLinearLayout.leftMargin;
    }
    
    public void setValue(final int rateValue) {
        this.lastSelected = currentSelected;
        this.currentSelected = rateValue >= this.childrens.size() ? this.childrens.size() : rateValue;
        this.select();
        if (listener != null) {
            listener.onEvent(rateValue);
        }
    }
    
    public void setValue(final int rateValue, boolean isNeedListener) {
        this.lastSelected = currentSelected;
        this.currentSelected = rateValue >= this.childrens.size() ? this.childrens.size() : rateValue;
        this.select(isNeedListener);
        if (isNeedListener && listener != null) {
            listener.onEvent(rateValue);
        }
        
    }
    
    public void setValue(final double rateValue, boolean isNeedListener) {
        int value = (int) rateValue;
        this.lastSelected = currentSelected;
        this.currentSelected = value >= this.childrens.size() ? this.childrens.size() : value;
        this.select(isNeedListener);
    }
    
    public int getSelected() {
        return this.currentSelected;
    }
    
    private void addView(int id, LayoutParams params) {
        
        ImageView view = new ImageView(this.getContext());
        view.setBackgroundResource(resNormalId);
        view.setId(id);
        this.addView(view, params);
        this.childrens.add(view);
    }
    private void select(boolean isNeedListener) {
        if (null == this.childrens || lastSelected == currentSelected) {
            return;
        }
        currentSelected = currentSelected > childrens.size() ? childrens.size() : currentSelected;
        if (lastSelected > currentSelected) {
            for (int i = currentSelected; i < lastSelected; i++) {
                childrens.get(i).setBackgroundResource(resNormalId);
            }
        }
        else {
            for (int i = lastSelected; i < currentSelected; i++) {
                childrens.get(i).setBackgroundResource(resSelectedId);
            }
        }
        
        lastSelected = currentSelected;
        this.invalidate();
        if (isNeedListener&&null != listener) {
            listener.onEvent(currentSelected);
        }
    }
    private void select() {
        if (null == this.childrens || lastSelected == currentSelected) {
            return;
        }
        currentSelected = currentSelected > childrens.size() ? childrens.size() : currentSelected;
        if (lastSelected > currentSelected) {
            for (int i = currentSelected; i < lastSelected; i++) {
                childrens.get(i).setBackgroundResource(resNormalId);
            }
        }
        else {
            for (int i = lastSelected; i < currentSelected; i++) {
                childrens.get(i).setBackgroundResource(resSelectedId);
            }
        }
        
        lastSelected = currentSelected;
        this.invalidate();
        if (null != listener) {
            listener.onEvent(currentSelected);
        }
    }
    
    private void init() {
        this.setOnClickListener(this);
        currentSelected = 0;
        this.setOrientation(HORIZONTAL);
    }
    
    @Override
    public void onClick(View arg0) {
    }
    
}
