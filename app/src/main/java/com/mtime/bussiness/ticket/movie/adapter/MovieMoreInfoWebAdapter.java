package com.mtime.bussiness.ticket.movie.adapter;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.bussiness.ticket.movie.activity.MovieMoreInfoActivity;
import com.mtime.bussiness.ticket.movie.bean.MoreInfoWebBean;
import com.mtime.frame.BaseActivity;

import java.util.List;

/**
 * 用于{@link MovieMoreInfoActivity}
 */
public class MovieMoreInfoWebAdapter extends BaseAdapter {
    private final List<MoreInfoWebBean> webs;
    private final BaseActivity context;

    public MovieMoreInfoWebAdapter(final BaseActivity context, final List<MoreInfoWebBean> webs) {
        this.context = context;
        this.webs = webs;
    }

    @Override
    public int getCount() {
        return webs.size();
    }

    @Override
    public Object getItem(int i) {
        return webs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final Holder holder;
        if (view == null) {
            holder = new Holder();
            view = context.getLayoutInflater().inflate(R.layout.producer_child, viewGroup, false);
            //计算屏幕宽度，动态显示
            DisplayMetrics dm = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            holder.textWeb = view.findViewById(R.id.company_name);
            holder.textWeb.setMaxWidth(width - getPixels(TypedValue.COMPLEX_UNIT_DIP, 35));
            holder.line = view.findViewById(R.id.gray_line);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        holder.textWeb.setText(webs.get(position).getText());

        if (position == getCount() - 1) {
            holder.line.setVisibility(View.GONE);
        } else {
            holder.line.setVisibility(View.VISIBLE);
        }

        return view;
    }

    //px转换
    public int getPixels(int Unit, float size) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) TypedValue.applyDimension(Unit, size, metrics);
    }

    private class Holder {
        TextView textWeb;
        View line;
    }
}
