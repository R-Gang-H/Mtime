package com.mtime.bussiness.ticket.cinema.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.mtime.base.utils.MTimeUtils;
import com.mtime.bussiness.ticket.cinema.CinemaShowtimeFragment;
import com.mtime.bussiness.ticket.cinema.bean.MovieTimeChildMainBean;
import com.mtime.common.utils.DateUtil;

import java.util.Date;
import java.util.List;

/**
 * 影院排片页-viewpager
 */
public class CinemaShowtimeViewPagerAdapter extends FragmentPagerAdapter {
    private final List<MovieTimeChildMainBean> showTimeList;
    private String movieid;
    private final String cinemaid;
    private final ViewPager viewPager;

    public CinemaShowtimeViewPagerAdapter(FragmentManager fm, List<MovieTimeChildMainBean> showTimeList, String cinemaid, ViewPager viewPager) {
        super(fm);
        this.showTimeList = showTimeList;
        this.cinemaid = cinemaid;
        this.viewPager = viewPager;
    }

    @Override
    public int getCount() {
        return showTimeList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String moviekey = showTimeList.get(position).getMoviekey();
        String[] strs = moviekey.split("_");
        String dateString = "";
        if (strs.length == 2) {
            movieid = strs[0];
            dateString = strs[1];
        }
        final long time = DateUtil.getDateToLong(DateUtil.sdf1, dateString);
        int gapCount = DateUtil.getGapCount(MTimeUtils.getLastDiffServerDate(),
                DateUtil.getDateFromStr(dateString));
        if (gapCount == 0) {
            dateString = DateUtil.sdf12.format(new Date(time));
            return String.format("今天%s", dateString);
        } else if (gapCount == 1) {
            dateString = DateUtil.sdf12.format(new Date(time));
            return String.format("明天%s", dateString);
        } else if (gapCount == 2) {
            dateString = DateUtil.sdf12.format(new Date(time));
            return String.format("后天%s", dateString);
        } else if (gapCount >= 3) {
            dateString = DateUtil.sdf17.format(new Date(time));
            return dateString.replace("星期","周");
        }

        return DateUtil.sdf11.format(new Date(time));
    }

    @Override
    public Fragment getItem(int position) {
        CinemaShowtimeFragment fragment = CinemaShowtimeFragment.newInstance(showTimeList.get(position).getList(), cinemaid, movieid);
        return fragment;
    }
}
