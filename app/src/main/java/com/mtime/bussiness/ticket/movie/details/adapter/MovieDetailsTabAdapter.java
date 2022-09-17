package com.mtime.bussiness.ticket.movie.details.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsTab;
import com.mtime.frame.App;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-06-05
 */
public class MovieDetailsTabAdapter extends PagerAdapter {
    private List<MovieDetailsTab> mTabs = new ArrayList<>();

    public MovieDetailsTabAdapter(List<MovieDetailsTab> tabs) {
        mTabs = tabs;
    }

    private final SparseArray<TextView> views = new SparseArray<>();
    @Override
    public int getCount() {
        return mTabs.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        TextView textView = views.get(position);
        if (null == textView) {
            textView = new TextView(container.getContext());
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(params);
        }
        if (null != textView.getParent()) {
            ((ViewGroup)textView.getParent()).removeView(textView);
        }
        container.addView(textView);
        return textView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return App.getInstance().getResources().getString(mTabs.get(position).titleResid);
    }
}
