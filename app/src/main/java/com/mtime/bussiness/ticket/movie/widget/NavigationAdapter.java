package com.mtime.bussiness.ticket.movie.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtime.base.utils.MScreenUtils;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.constant.FrameConstant;
import com.mtime.bussiness.mine.bean.NavigationItem;

import java.util.List;

/**
 * 公司详情页面用到{@link com.mtime.bussiness.mine.activity.CompanyDetailActivity}
 */
public class NavigationAdapter extends BaseAdapter {
	List<NavigationItem> dataList;
	BaseActivity context;

	public NavigationAdapter(BaseActivity c, List<NavigationItem> data) {
		context = c;
		dataList = data;
	}

	
    public int getCount() {
		if (dataList != null) {
			return dataList.size();
		}
		return 0;
	}

	
    public Object getItem(int arg0) {
		if (dataList != null) {
			return dataList.get(arg0);
		}
		return null;
	}

	
    public long getItemId(int arg0) {
		return arg0;
	}

	
    public View getView(int position, View convertView, ViewGroup arg2) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.navigation_item, null);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					MScreenUtils.getScreenWidth()/dataList.size(), LinearLayout.LayoutParams.WRAP_CONTENT);
			convertView.setLayoutParams(lp);
		}
		((TextView) convertView).setText(dataList.get(position).getTitle());
		return convertView;
	}

}
