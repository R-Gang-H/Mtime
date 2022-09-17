package com.mtime.bussiness.ticket.cinema;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mtime.R;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.bussiness.ticket.cinema.bean.ShowtimeJsonBean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 影院排片页-某个影院在某个影片的影讯列表
 */
public class CinemaShowtimeFragment extends Fragment {

    private static final String ARG_LIST = "list";
    private static final String MOVIDID = "movieid";
    private static final String CINEMAID = "cinemaid";
    private static final String VIEWPAGER = "viewpager";
    private List<ShowtimeJsonBean> list;
    private String movieid;
    private String cinemaid;
    private String newDate;// 当前时间

    public static CinemaShowtimeFragment newInstance(List<ShowtimeJsonBean> list, String movieid, String cinemaid) {
        CinemaShowtimeFragment f = new CinemaShowtimeFragment();
        Bundle b = new Bundle();
        b.putSerializable(ARG_LIST, (Serializable) list);
        b.putString(MOVIDID, movieid);
        b.putString(CINEMAID, cinemaid);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list = (List<ShowtimeJsonBean>) getArguments().getSerializable(ARG_LIST);
        movieid = getArguments().getString(MOVIDID);
        cinemaid = getArguments().getString(CINEMAID);
        Date serverDate = MTimeUtils.getLastDiffServerDate();
        final SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (serverDate == null) {
            serverDate = new Date();
        }
        String date = sFormat.format(serverDate);
        newDate = date.replace("-", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.cinema_showtime_fragment, container, false);
        return rootView;
    }
}