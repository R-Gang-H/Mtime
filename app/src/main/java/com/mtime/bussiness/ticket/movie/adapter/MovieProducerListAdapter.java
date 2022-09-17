package com.mtime.bussiness.ticket.movie.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.bussiness.ticket.movie.bean.Company;
import com.mtime.bussiness.ticket.movie.bean.MovieProducerType;
import com.mtime.bussiness.ticket.movie.widget.MovieStarsListView;
import com.mtime.bussiness.ticket.movie.widget.MovieStarsListView.HeaderAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zhangcong on 15/10/19.
 */
public class MovieProducerListAdapter extends SimpleExpandableListAdapter implements HeaderAdapter {
    private final MovieStarsListView listView;
    private final BaseActivity context;
    private final List<MovieProducerType> types;
    private final HashMap<Integer, Integer> groupStatusMap = new HashMap<Integer, Integer>();

    public MovieProducerListAdapter(final BaseActivity context, final List<MovieProducerType> types,
                                    final MovieStarsListView listView, final List<? extends Map<String, ?>> groupData,
                                    final int expandedGroupLayout, final String[] groupFrom, final int[] groupTo,
                                    final List<? extends List<? extends Map<String, ?>>> childData, final int childLayout,
                                    final String[] childFrom, final int[] childTo) {
        super(context, groupData, expandedGroupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
        this.context = context;
        this.listView = listView;
        this.types = types;
    }

    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.v2_moviestars_group, parent, false);
        }

        return super.getGroupView(groupPosition, isExpanded, convertView, parent);
    }

    @Override
    public void configureHeader(View header, int groupPosition, int childPosition, int alpha) {
        final Map<String, String> groupData = (Map<String, String>) getGroup(groupPosition);
        ((TextView) header.findViewById(R.id.groupto)).setText(groupData.get("typeName"));
    }

    @Override
    public int getHeaderState(int groupPosition, int childPosition) {
        // 防止崩溃
        if (groupPosition == -1) {
            return 0;
        }
        final int childCount = getChildrenCount(groupPosition);
        if (childPosition == (childCount - 1)) {
            return HeaderAdapter.PINNED_HEADER_PUSHED_UP;
        } else if ((childPosition == -1) && !listView.isGroupExpanded(groupPosition)) {
            return HeaderAdapter.PINNED_HEADER_GONE;
        } else {
            return HeaderAdapter.PINNED_HEADER_VISIBLE;
        }
    }

    @Override
    public void setGroupClickStatus(int groupPosition, int status) {
        groupStatusMap.put(groupPosition, status);
    }

    @Override
    public int getGroupClickStatus(int groupPosition) {
        if (groupStatusMap.containsKey(groupPosition)) {
            return groupStatusMap.get(groupPosition);
        } else {
            return 0;
        }
    }

    public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild,
                             View convertView, final ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.producer_child, null);
            //计算屏幕宽度，动态显示
            DisplayMetrics dm = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            holder = new ViewHolder();
            holder.line = convertView.findViewById(R.id.gray_line);
            holder.nameView = convertView.findViewById(R.id.company_name);
            holder.nameView.setMaxWidth(width - getPixels(TypedValue.COMPLEX_UNIT_DIP, 35));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MovieProducerType type = types.get(groupPosition);
        final List<Company> clist = type.getCompanys();

        final Company company = clist.get(childPosition);
        if (!company.getLocationName().isEmpty()) {
            String location = " [" + company.getLocationName() + "]";
            holder.nameView.setText(company.getName() + location);
        } else {
            holder.nameView.setText(company.getName());
        }
        if (clist.size() - 1 == childPosition) {
            holder.line.setVisibility(View.GONE);
        } else {
            holder.line.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    //px转换
    public int getPixels(int Unit, float size) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) TypedValue.applyDimension(Unit, size, metrics);
    }

    private class ViewHolder {
        TextView nameView;
        View line;
    }
}
