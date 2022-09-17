package com.mtime.bussiness.ticket.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtime.bussiness.ticket.bean.SeatItem;

import java.util.List;

/**
 * Created by JiaJunHui on 2018/5/25.
 */
public class SeatHelper {

    private final int MAX_PER_ROW_NUM = 4;
    private final int MAX_FIRST_ROW_NUM = 1 * MAX_PER_ROW_NUM;
    private final int MAX_SECOND_ROW_NUM = 2 * MAX_PER_ROW_NUM;
    private final int MAX_SEAT_NUM = 8;

    private final Context mContext;
    private List<SeatItem> mSeatItems;

    private final SeatHelperParams mParams;
    private final LinearLayout mRowFirstLinear;
    private final LinearLayout mRowSecondLinear;
    private final LinearLayout mRowThirdLinear;

    public SeatHelper(Context context, SeatHelperParams params){
        this.mContext = context;
        this.mParams = params;
        this.mRowFirstLinear = params.getFirstLinear();
        this.mRowSecondLinear = params.getSecondLinear();
        this.mRowThirdLinear = params.getThirdLinear();
    }

    public void setSeatInfo(List<SeatItem> items, String hallName){
        mSeatItems = items;
        int row = 1;
        if(mSeatItems!=null){
            clearAllLinear();
            mRowSecondLinear.setVisibility(View.GONE);
            mRowThirdLinear.setVisibility(View.GONE);
            int size = mSeatItems.size();
            size = size>MAX_SEAT_NUM?MAX_SEAT_NUM:size;
            if(size < MAX_FIRST_ROW_NUM){
                row = 1;
            }else if(size < MAX_SECOND_ROW_NUM){
                row = 2;
            }else {
                row = 3;
            }
            SeatItem item;
            for(int i=0;i<size;i++){
                item = mSeatItems.get(i);
                if(i/MAX_FIRST_ROW_NUM < 1){
                    mRowFirstLinear.addView(generatorSeatItemView(item), generatorWrapLayoutParams());
                }else if(i/MAX_SECOND_ROW_NUM < 1){
                    mRowSecondLinear.setVisibility(View.VISIBLE);
                    mRowSecondLinear.addView(generatorSeatItemView(item), generatorWrapLayoutParams());
                }else if(i/MAX_SEAT_NUM < 1){
                    mRowThirdLinear.setVisibility(View.VISIBLE);
                    mRowThirdLinear.addView(generatorSeatItemView(item), generatorWrapLayoutParams());
                }
            }
        }
        if(!TextUtils.isEmpty(hallName)){
            LinearLayout mHallLinear;
            switch (row){
                case 1:
                    mHallLinear = mRowFirstLinear;
                    break;
                case 2:
                    mHallLinear = mRowSecondLinear;
                    break;
                case 3:
                    mHallLinear = mRowThirdLinear;
                    break;
                default:
                    mHallLinear = mRowFirstLinear;
                    break;
            }
            mHallLinear.setVisibility(View.VISIBLE);
            mHallLinear.addView(generatorHallNameView(hallName), generatorWrapLayoutParams());
        }
    }

    private LinearLayout.LayoutParams generatorWrapLayoutParams(){
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private View generatorSeatItemView(SeatItem item){
        View view = View.inflate(mContext, mParams.itemLayoutId, null);
        TextView textView = view.findViewById(mParams.getItemTextViewId());
        textView.setText(item.getSeatInfo());
        return view;
    }

    private View generatorHallNameView(String hallName){
        View view = View.inflate(mContext, mParams.getHallNameLayoutId(), null);
        TextView textView = view.findViewById(mParams.getHallNameTextViewId());
        textView.setText(hallName);
        return view;
    }

    private void clearAllLinear(){
        mRowFirstLinear.removeAllViews();
        mRowSecondLinear.removeAllViews();
        mRowThirdLinear.removeAllViews();
    }

    public static class SeatHelperParams{
        private LinearLayout firstLinear;
        private LinearLayout secondLinear;
        private LinearLayout thirdLinear;

        private int itemLayoutId;
        private int hallNameLayoutId;

        private int itemTextViewId;
        private int hallNameTextViewId;

        public LinearLayout getFirstLinear() {
            return firstLinear;
        }

        public SeatHelperParams setFirstLinear(LinearLayout firstLinear) {
            this.firstLinear = firstLinear;
            return this;
        }

        public LinearLayout getSecondLinear() {
            return secondLinear;
        }

        public SeatHelperParams setSecondLinear(LinearLayout secondLinear) {
            this.secondLinear = secondLinear;
            return this;
        }

        public LinearLayout getThirdLinear() {
            return thirdLinear;
        }

        public SeatHelperParams setThirdLinear(LinearLayout thirdLinear) {
            this.thirdLinear = thirdLinear;
            return this;
        }

        public int getItemLayoutId() {
            return itemLayoutId;
        }

        public SeatHelperParams setItemLayoutId(int itemLayoutId) {
            this.itemLayoutId = itemLayoutId;
            return this;
        }

        public int getHallNameLayoutId() {
            return hallNameLayoutId;
        }

        public SeatHelperParams setHallNameLayoutId(int hallNameLayoutId) {
            this.hallNameLayoutId = hallNameLayoutId;
            return this;
        }

        public int getItemTextViewId() {
            return itemTextViewId;
        }

        public SeatHelperParams setItemTextViewId(int itemTextViewId) {
            this.itemTextViewId = itemTextViewId;
            return this;
        }

        public int getHallNameTextViewId() {
            return hallNameTextViewId;
        }

        public SeatHelperParams setHallNameTextViewId(int hallNameTextViewId) {
            this.hallNameTextViewId = hallNameTextViewId;
            return this;
        }
    }

}
