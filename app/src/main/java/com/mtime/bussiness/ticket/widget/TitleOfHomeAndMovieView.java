package com.mtime.bussiness.ticket.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.bussiness.ticket.MovieAndCinemaSwitchView;
import com.mtime.bussiness.ticket.MovieAndCinemaSwitchView.IMovieAndCinemaSwitchViewListener;
import com.mtime.util.MtimeUtils;
import com.mtime.widgets.BaseTitleView;

public class TitleOfHomeAndMovieView extends BaseTitleView {

    private final View root;
    private final TextView viewCity;
    private final View viewAlpha;
    private boolean localSearch;
    public MovieAndCinemaSwitchView viewSwitch;

    @SuppressLint("CutPasteId")
    public TitleOfHomeAndMovieView(final Context context, final View root, String cityname,
                                   final StructType type, final IMovieAndCinemaSwitchViewListener listener,
                                   final ITitleViewLActListener titleListener, final ILogXListener logXListener) {

        localSearch = false;
        this.root = root;
        this.viewAlpha = root.findViewById(R.id.background);
        // show logo or show the switch part
        View viewLogo = root.findViewById(R.id.logo);
        View switchView = root.findViewById(R.id.move_cinema_switched_view);
        viewSwitch = new MovieAndCinemaSwitchView(context, switchView, listener, 0);

        // city selected part
        viewCity = root.findViewById(R.id.city_select);
        MtimeUtils.changeCitySize(viewCity);

        if (!TextUtils.isEmpty(cityname)) {
            if (cityname.length() > 4) {
                cityname = cityname.substring(0, 3) + "...";
            }
            viewCity.setText(cityname);
        } else {
            viewCity.setText(R.string.st_beijing);
        }

        viewCity.setClickable(true);
        viewCity.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != logXListener) {
                    logXListener.onEvent(ILogXListener.ActionType.TYPE_CITY_CLICK);
                }
                //context.startActivityForResult(CityChangeActivity.class, 0);
            }
        });

        // search part
        ImageButton viewSearch = root.findViewById(R.id.search);
        viewSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != logXListener) {
                    logXListener.onEvent(ILogXListener.ActionType.TYPE_SEARCH_CLICK);
                }
                if (StructType.TYPE_HOME_SHOW_MOVINGS == type && localSearch && null != titleListener) {
                    titleListener.onEvent(ActionType.TYPE_SEARCH, null);
                } else {
                    if (null != titleListener) {
                        titleListener.onEvent(ActionType.TYPE_SEARCH, null);
                    }
                    // search with type?
                    //context.startActivityForResult(SearchActivity.class, 0);

                }
            }
        });

        if (StructType.TYPE_HOME_SHOW_LOGO == type) {
            viewLogo.setVisibility(View.VISIBLE);
            viewSwitch.setVisibility(View.INVISIBLE);
        } else {
            viewLogo.setVisibility(View.INVISIBLE);
            viewSwitch.setVisibility(View.VISIBLE);
        }

    }

    /**
     * update city name,
     * <p>
     * must call this funciton in UI thread.
     *
     * @param cityname new city name
     */
    public void update(String cityname) {
        if (null != viewCity && !TextUtils.isEmpty(cityname)) {
            if (cityname.length() > 4) {
                cityname = cityname.substring(0, 3) + "...";
            }
            viewCity.setText(cityname);
            MtimeUtils.changeCitySize(viewCity);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void setAlpha(float alpha) {
        if (null != viewAlpha && android.os.Build.VERSION.SDK_INT >= 11) {
            float a = alpha < MIN_ALPHA ? 0 : alpha;
            viewAlpha.setAlpha(a > 1 ? 1 : a);
        }
    }

    public void setVisibile(int visibility) {
        this.root.setVisibility(visibility);
    }

    public void setSearchType(final boolean localSearch) {
        this.localSearch = localSearch;
    }

    public boolean getSearchType() {
        return this.localSearch;
    }

    public View getRootView() {
        return this.root;
    }

    public void setCinemaViewOn(final Context context, boolean on) {
        viewSwitch.setCinemaViewOn(context, on);
    }

    public interface ILogXListener {//用于统计的监听

        enum ActionType {
            TYPE_CITY_CLICK,//点击了Title部分的切换城市
            TYPE_SEARCH_CLICK,//点击了Title部分的搜索按钮
        }

        void onEvent(final ActionType type);
    }
}
