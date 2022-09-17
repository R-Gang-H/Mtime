package com.mtime.base.recyclerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spanned;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 通用ViewHolder类;
 * 最好与CommonRecyclerAdapter配套使用;
 * 里面的set方法没有的可自行追加;
 */
public class CommonViewHolder extends RecyclerView.ViewHolder{
    private final SparseArray<View> mViews;
    private final Context mContext;
    
    private CommonViewHolder(View view) {
        super(view);
        this.mContext = view.getContext();
        this.mViews = new SparseArray<View>();
    }
    
    private CommonViewHolder(Context context, ViewGroup parent, int layoutId) {
        super(LayoutInflater.from(context).inflate(layoutId, parent, false));
        this.mContext = context;
        this.mViews = new SparseArray<View>();
    }
    
    /**
     * 拿到一个ViewHolder对象
     *
     * @param context
     * @param parent
     * @param layoutId
     * @return
     */
    public static CommonViewHolder get(Context context, ViewGroup parent, int layoutId) {
        return new CommonViewHolder(context, parent, layoutId);
    }
    
    public static CommonViewHolder get(View view) {
        return new CommonViewHolder(view);
    }
    
    public Context getContext() {
        return mContext;
    }
    
    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(@IdRes int viewId) {
        View view = mViews.get(viewId);
        if (view == null && null != itemView) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public CommonViewHolder setCompoundDrawables(@IdRes int viewId, Drawable left, Drawable top, Drawable right, Drawable bottom){
        TextView view = getView(viewId);
        if(null != view)
            view.setCompoundDrawables(left,top,right,bottom);
        return this;
    }

    public CommonViewHolder setCompoundDrawablesWithIntrinsicBounds(@IdRes int viewId, Drawable left, Drawable top, Drawable right, Drawable bottom){
        TextView view = getView(viewId);
        if(null != view)
            view.setCompoundDrawablesWithIntrinsicBounds(left,top,right,bottom);
        return this;
    }
    
    public void setTextColor(@IdRes int viewId, @ColorInt int color) {
        View view = getView(viewId);
        if(view instanceof TextView) {
            ((TextView)view).setTextColor(color);
        } else {
            throw new ClassCastException("View must be an extends TextView");
        }
    }
    
    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public CommonViewHolder setText(@IdRes int viewId, CharSequence text)
    {
        TextView view = getView(viewId);
        if(null != view)
            view.setText(text);
        return this;
    }

    public CommonViewHolder setText(@IdRes int viewId, Spanned spanned) {
        TextView view = getView(viewId);
        if(null != view)
            view.setText(spanned);
        return this;
    }

    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param stringId
     * @param formatArgs
     * @return
     */
    public CommonViewHolder setText(@IdRes int viewId, int stringId, Object... formatArgs)
    {
        TextView view = getView(viewId);
        if(null != view)
            view.setText(this.mContext.getString(stringId, formatArgs));
        return this;
    }

    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param stringId
     * @return
     */
    public CommonViewHolder setText(@IdRes int viewId, int stringId)
    {
        TextView view = getView(viewId);
        if(null != view)
            view.setText(stringId);
        return this;
    }
    /**
     * 为TextView设置字符串
     *
     * @param textViewId
     * @param singleLine
     * @return
     */
    public CommonViewHolder setSingleLine(int textViewId, boolean singleLine)
    {
        TextView view = getView(textViewId);
        if(null != view)
            view.setSingleLine(singleLine);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public CommonViewHolder setImageResource(@IdRes int viewId, int drawableId)
    {
        ImageView view = getView(viewId);
        if(null != view)
            view.setImageResource(drawableId);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param bm
     * @return
     */
    public CommonViewHolder setImageBitmap(@IdRes int viewId, Bitmap bm)
    {
        ImageView view = getView(viewId);
        if(null != view)
            view.setImageBitmap(bm);
        return this;
    }
    
    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param bm
     * @return
     */
    public CommonViewHolder setImageDrawable(@IdRes int viewId, Drawable bm)
    {
        ImageView view = getView(viewId);
        if(null != view)
            view.setImageDrawable(bm);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public CommonViewHolder setBackgroundResource(@IdRes int viewId, int drawableId)
    {
        View view = getView(viewId);
        if(null != view)
            view.setBackgroundResource(drawableId);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawable
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public CommonViewHolder setBackground(@IdRes int viewId, Drawable drawable)
    {
        View view = getView(viewId);
        if(null != view)
            view.setBackground(drawable);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param bm
     * @return
     */
    public CommonViewHolder setBackground(@IdRes int viewId, Bitmap bm) {
        return this.setBackground(viewId, new BitmapDrawable(this.mContext.getResources(), bm));
    }
    
    /**
     * 设置View GONE
     * @param viewId
     * @return
     */
    public CommonViewHolder setGone(@IdRes int viewId) {
        return setVisibility(viewId, View.GONE);
    }
    
    /**
     * 设置View VISIBLE
     * @param viewId
     * @return
     */
    public CommonViewHolder setVisible(@IdRes int viewId) {
        return setVisibility(viewId, View.VISIBLE);
    }
    
    /**
     * 设置View INVISIBLE
     * @param viewId
     * @return
     */
    public CommonViewHolder setInvisible(@IdRes int viewId) {
        return setVisibility(viewId, View.INVISIBLE);
    }

    /**
     * 设置View的Visibility
     * @param viewId
     * @param visibility
     * @return
     */
    public CommonViewHolder setVisibility(@IdRes int viewId, int visibility) {
        View view = getView(viewId);
        if(null != view)
            view.setVisibility(visibility);
        return this;
    }
    
    public CommonViewHolder setEnabled(@IdRes int viewId, boolean enabled) {
        View view = getView(viewId);
        if(null != view)
            view.setEnabled(enabled);
        return this;
    }

    /**
     * 设置焦点监听
     * @param viewId
     * @param listener
     * @return
     */
    public CommonViewHolder setOnFocusChangeListener(@IdRes int viewId, View.OnFocusChangeListener listener) {
        View view = getView(viewId);
        if(null != view)
            view.setOnFocusChangeListener(listener);
        return this;
    }

    /**
     * 设置点击监听
     * @param viewId
     * @param listener
     * @return
     */
    public CommonViewHolder setOnClickListener(@IdRes int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        if(null != view)
            view.setOnClickListener(listener);
        return this;
    }


}
