package com.mtime.adapter.render.base;

import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import com.mtime.common.utils.LogWriter;

/**
 * 封装ViewHoler基类
 * Created by yinguanping on 16/3/25.
 */
@Deprecated
public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = BaseRecyclerViewHolder.class.getSimpleName();
    private SparseArray<View> holder = null;

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * 通过id获取一个缓存的泛型view
     *
     * @param id
     * @param <T>
     * @return
     */
    public <T extends View> T obtainView(int id) {
        if (null == holder) {
            holder = new SparseArray<>();
        }
        View view = holder.get(id);
        if (null != view) {
            return (T) view;
        }
        view = itemView.findViewById(id);
        if (null == view) {
            LogWriter.e(TAG, "no view that id is " + id);
            return null;
        }
        holder.put(id, view);
        return (T) view;
    }

    /**
     * 获取一个缓存的view，并自动转型，即可实现自动转换为TextView等对象
     *
     * @param id
     * @param <T>
     * @return
     */
    public <T> T obtainView(int id, Class<T> viewClazz) {
        View view = obtainView(id);
        if (null == view) {
            return null;
        }
        return (T) view;
    }


}
