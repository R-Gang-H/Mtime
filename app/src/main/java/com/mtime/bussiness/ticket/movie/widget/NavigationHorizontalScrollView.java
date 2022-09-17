package com.mtime.bussiness.ticket.movie.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.constant.FrameConstant;

/**
 * 电影公司详情页_顶部tab
 */

public class NavigationHorizontalScrollView extends HorizontalScrollView implements OnClickListener {
    
    public NavigationHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    public NavigationHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public NavigationHorizontalScrollView(Context context) {
        super(context);
        init();
    }
    
    private LinearLayout      mFrameLayout;
    private BaseAdapter       mBaseAdapter;
    private SparseArray<View> mSparseArray;
    private int               oldPosition;
    private TextView          backView;
    private ImageView         preImage, nextImage;
    
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        resetImageView();
    }
    
    private void resetImageView() {
        if (preImage != null)
            preImage.setVisibility(View.GONE);
        if (nextImage != null)
            nextImage.setVisibility(View.GONE);
        
        /* 计算水平方向滚动条的滑块的偏移值。 */
        // int ScrollOffset = computeHorizontalScrollOffset();
        /* 滚动条长度 */
        // int ScrollExtent = computeHorizontalScrollExtent();
        /* 滚动条当前位置 */
        // int curScrollLoc = ScrollOffset + ScrollExtent;
        /* scrollView 的可滚动水平范围是所有子视图的宽度总合。 */
        // int ScrollRange = computeHorizontalScrollRange();
        
        /* 如果当前位置 在ScrollExtent 和 ScrollRange 之间,左右两边的View都显示 */
        // if (curScrollLoc > ScrollExtent && curScrollLoc < ScrollRange) {
        // if (preImage != null)
        // preImage.setVisibility(View.VISIBLE);
        // if (nextImage != null)
        // nextImage.setVisibility(View.VISIBLE);
        // return;
        // } else {
        // if (preImage != null)
        // preImage.setVisibility(View.INVISIBLE);
        // if (nextImage != null)
        // nextImage.setVisibility(View.INVISIBLE);
        // }
        // /* 如果滚动到最左边 */
        // if (curScrollLoc == ScrollExtent) {
        // if (preImage != null)
        // preImage.setVisibility(View.INVISIBLE);
        // return;
        // }
        // /* 如果滚动到最右边 */
        // if (curScrollLoc >= ScrollRange) {
        // if (nextImage != null)
        // nextImage.setVisibility(View.INVISIBLE);
        // return;
        // }
        
    }
    
    private void init() {
        mFrameLayout = new LinearLayout(getContext());
        mFrameLayout.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        mFrameLayout.setOrientation(LinearLayout.VERTICAL);
        addView(mFrameLayout);
        this.setBackgroundColor(0xfff6f6f6);
        mSparseArray = new SparseArray<View>();
    }
    
    private void buildItemView() {
        if (mBaseAdapter == null)
            return;
        LinearLayout linearLayout = new LinearLayout(getContext());
        for (int i = 0; i < mBaseAdapter.getCount(); i++) {
            View view = mBaseAdapter.getView(i, mSparseArray.get(i), this);
            view.setOnClickListener(this);
            mSparseArray.put(i, view);
            linearLayout.addView(mSparseArray.get(i));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        // backView = (TextView) LayoutInflater.from(getContext()).inflate(
        // R.layout.navigation_item, null);
        backView = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((MScreenUtils.getScreenWidth() - 100) / 6,
        // 100,
                LinearLayout.LayoutParams.WRAP_CONTENT
        // 2
        );
        backView.setLayoutParams(lp);
        
        // backView.setMaxWidth((FrameConstant.SCREEN_WIDTH-100)
        // / 5);
        // backView.setMinWidth((FrameConstant.SCREEN_WIDTH-100)
        // / 5);
        // backView.setWidth((FrameConstant.SCREEN_WIDTH-100)
        // / 5);
        backView.setMinHeight(5);
        backView.setMaxHeight(5);
        // backView.setBackgroundResource(R.drawable.filter_on);
        backView.setBackgroundResource(R.color.color_movie_main_0075C4);
        backView.setPadding(0, 0, 0, 0);
        
        mFrameLayout.addView(linearLayout);
        mFrameLayout.addView(backView, layoutParams);
    }
    
    public void setAdapter(BaseAdapter baseAdapter) {
        if (baseAdapter == null)
            return;
        mBaseAdapter = baseAdapter;
        mBaseAdapter.registerDataSetObserver(new DataSetObserver() {
            
            @Override
            public void onChanged() {
                oldPosition = 0;
                buildItemView();
                setTextHightLightColor(oldPosition);
                super.onChanged();
            }
        });
        mBaseAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void onClick(View v) {
        if (preImage != null) {
            if (v.getId() == preImage.getId()) {
                fling(-800);
                return;
            }
        }
        if (nextImage != null) {
            if (v.getId() == nextImage.getId()) {
                fling(800);
                return;
            }
        }
        if (onItemClickListener != null) {
            int position = mSparseArray.indexOfValue(v);
            startAnimation(position);
            oldPosition = position;
            onItemClickListener.click(position);
            setTextHightLightColor(position);
        }
    }
    
    public void selectItem(int index) {
        if (mSparseArray != null && index > -1 && index < mSparseArray.size()) {
            int position = index;
            startAnimation(position);
            oldPosition = position;
            onItemClickListener.click(position);
            setTextHightLightColor(position);
        }
    }
    
    private void setTextHightLightColor(int index) {
        for (int i = 0; i < mSparseArray.size(); i++) {
            View v = mSparseArray.get(i);
            if (v instanceof TextView) {
                TextView text = (TextView) v;
                if (i == index) {
                    text.setTextColor(0xff0075c4);
                }
                else {
                    text.setTextColor(0xff333333);
                }
            }
        }
    }
    
    private void startAnimation(int position) {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(buildScaleAnimation(oldPosition, position));
        animationSet.addAnimation(buildTranslateAnimation(oldPosition, position));
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        /* 移动后不复原,不返回动画前的状态位置 */
        animationSet.setFillAfter(true);
        animationSet.setDuration(500);
        backView.startAnimation(animationSet);
        invalidate();
    }
    
    private Animation buildScaleAnimation(int oldPosition, int position) {
        float oldWidth = getItemView(oldPosition).getWidth();
        float newWidth = getItemView(position).getWidth();
        float fromX = oldWidth / backView.getWidth();
        float toX = newWidth / backView.getWidth();
        ScaleAnimation animation = new ScaleAnimation(fromX, toX, 1f, 1f, Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE,
                0.0f);
        return animation;
    }
    
    private Animation buildTranslateAnimation(int oldPosition, int position) {
        TranslateAnimation animation = new TranslateAnimation(getItemView(oldPosition).getLeft(), getItemView(position)
                .getLeft(), 0, 0);
        return animation;
    }
    
    private View getItemView(int position) {
        return mSparseArray.get(position);
    }
    
    public interface OnItemClickListener {
        void click(int position);
    }
    
    private OnItemClickListener onItemClickListener;
    
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    
    public void setImageView(ImageView preImage, ImageView nextImage) {
        this.preImage = preImage;
        this.nextImage = nextImage;
        resetImageView();
        if (preImage != null) {
            preImage.setOnClickListener(this);
        }
        if (nextImage != null) {
            nextImage.setOnClickListener(this);
        }
    }
    
}
