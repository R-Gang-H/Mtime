package com.mtime.bussiness.ticket.movie.adapter;

import android.content.Intent;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.kotlin.android.user.UserManager;
import com.kotlin.android.user.login.UserLoginKt;
import com.mtime.R;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.MToastUtils;
import com.mtime.beans.SuccessBean;
import com.mtime.bussiness.common.api.CommonApi;
import com.mtime.bussiness.common.bean.MovieWantSeenResultBean;
import com.mtime.bussiness.ticket.movie.bean.IncomingFilmBean;
import com.mtime.common.utils.PrefsManager;
import com.mtime.common.utils.Utils;
import com.mtime.frame.BaseActivity;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.ticket.StatisticTicket;
import com.mtime.util.HttpUtil;
import com.mtime.util.ImageLoader;
import com.mtime.util.ImageURLManager;
import com.mtime.util.JumpUtil;
import com.mtime.util.ToolsUtils;
import com.mtime.widgets.NetworkImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * 最受关注电影
 * Created by lsmtime on 17/4/5.
 */

public class AttentionFilmAdapter extends RecyclerView.Adapter<AttentionFilmAdapter.ViewHolder> {
    private final LayoutInflater layoutInflater;
    private final AppCompatActivity context;
    private final ArrayList<IncomingFilmBean.MoviecomingsBean> filmList = new ArrayList<>();
    private final ArrayList<Integer> wantMoviesList = new ArrayList<>();
    private IncomingFilmAdapter incomingFilmAdapter;
    private int tagIndex;//即将上映推荐标签位置索引

    private long mStartTime;
    private String referBean;
    private CommonApi mCommonApi = new CommonApi();

    // 图片加载器
    private ImageLoader volleyImageLoader = new ImageLoader();

    public void setStartTime(long mStartTime) {
        this.mStartTime = mStartTime;
    }

    public void setReferBean(String referBean) {
        this.referBean = referBean;
    }

    public AttentionFilmAdapter(AppCompatActivity context, ArrayList<IncomingFilmBean.MoviecomingsBean> filmList) {
        this.context = context;
        if (filmList != null) {
            this.filmList.addAll(filmList);
        }
        layoutInflater = LayoutInflater.from(context);
    }

    public void setWantMoviesList(ArrayList<Integer> wantMoviesList) {
        if (wantMoviesList != null) {
            this.wantMoviesList.clear();
            this.wantMoviesList.addAll(wantMoviesList);
            notifyDataSetChanged();
        }
    }

    public void setIncomingFilmAdapter(IncomingFilmAdapter incomingFilmAdapter) {
        this.incomingFilmAdapter = incomingFilmAdapter;
    }

    @Override
    public AttentionFilmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.adapter_attention_film, parent, false));
    }

    @Override
    public void onBindViewHolder(AttentionFilmAdapter.ViewHolder holder, final int position) {
        IncomingFilmBean.MoviecomingsBean moviesBean = filmList.get(position);
        if (moviesBean == null) {
            return;
        }
        setTextValue(holder.titleTv, moviesBean.getTitle());
        setTextValue(holder.dateTv, moviesBean.getReleaseDateStr());
        setTextValue(holder.wantSeeNumTv, String.format("%1$d人想看", moviesBean.getWantedCount()));
        if (wantMoviesList.contains(moviesBean.getMovieId())) {
            holder.wantSeeBtn.setImageResource(R.drawable.icon_wanted_see);
        } else {
            holder.wantSeeBtn.setImageResource(R.drawable.icon_want_see);
        }
        holder.filmImg.setTag(R.string.app_name, moviesBean.getImgUrl());
        volleyImageLoader.displayNetworkImage(volleyImageLoader, ((String) holder.filmImg.getTag(R.string.app_name)), holder.filmImg, R.drawable.default_image, R.drawable.default_image, Utils.dip2px(context, 100), Utils.dip2px(context, 201), ImageURLManager.SCALE_TO_FIT, null);
        holder.filmImg.setTag(R.string.appbar_scrolling_view_behavior, moviesBean.getMovieId());
        holder.filmImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent();
                int id = ((int) v.getTag(R.string.appbar_scrolling_view_behavior));
                String value = String.valueOf(id);
                //subClickData(position, false, value);//埋点

//                context.setPageLabel(StatisticTicket.PN_FUTURE_SCHEDULE);
//                StatisticPageBean bean1 = context.assemble(StatisticConstant.CLOSE, null, null, null, null, null, null);
//                if (!TextUtils.isEmpty(referBean)) {
//                    bean1.refer = referBean;
//                }
//                StatisticManager.getInstance().submit(bean1);

//                context.setPageLabel(StatisticTicket.PN_FUTURE_SCHEDULE);
//                Map<String, String> params = new HashMap<>();
//                params.put("duration", String.valueOf(System.currentTimeMillis() - mStartTime));
//                StatisticPageBean bean3 = context.assemble(StatisticConstant.TIMING, null, null, null, null, null, params);
//                if (!TextUtils.isEmpty(referBean)) {
//                    bean3.refer = referBean;
//                }
//                StatisticManager.getInstance().submit(bean3);

//                String firstRegin = "";
//                String secondRegin = "movieInf";
//                if (tagIndex == 0) {
//                    firstRegin = StatisticTicket.TICKET_HOT;
//                } else {
//                    firstRegin = StatisticTicket.TICKET_RECOMMEND;
//                }
//                context.setPageLabel(StatisticTicket.PN_FUTURE_SCHEDULE);
//                Map<String, String> businessParam = new HashMap<String, String>();
//                businessParam.put(StatisticConstant.MOVIE_ID, value);
//                StatisticPageBean bean = context.assemble(firstRegin, String.valueOf(position), "movieList", null, secondRegin, null, businessParam);
//                if (!TextUtils.isEmpty(referBean)) {
//                    bean.refer = referBean;
//                }
//                StatisticManager.getInstance().submit(bean);
                JumpUtil.startMovieInfoActivity(context, null, value, 0);
            }
        });
        holder.wantSeeBtn.setTag(R.string.app_name, moviesBean);
        holder.wantSeeBtn.setTag(R.string.appbar_scrolling_view_behavior, position);
        holder.wantSeeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IncomingFilmBean.MoviecomingsBean moviecomingsBean = (IncomingFilmBean.MoviecomingsBean) v.getTag(R.string.app_name);
                int position = (int) v.getTag(R.string.appbar_scrolling_view_behavior);
                subClickData(position, true, String.valueOf(moviecomingsBean.getMovieId()));//埋点统计
                if (UserManager.Companion.getInstance().isLogin()) {
                    if (wantMoviesList.contains(moviecomingsBean.getMovieId())) {//取消想看
                        handleWantSee(moviecomingsBean.getMovieId(), 2, moviecomingsBean);
                    } else {//想看
                        handleWantSee(moviecomingsBean.getMovieId(), 1, moviecomingsBean);
                    }
                } else {
//                    Intent intent = new Intent();
//                    context.startActivity(LoginActivity.class, intent);
                    UserLoginKt.gotoLoginPage(context, null, null);
                    PrefsManager.get(context).putBoolean("incoming_wantids", true);
                }


            }
        });

    }

    /**
     * 设置推荐位对应位置索引
     *
     * @param tagIndex
     */
    public void setTagIndex(int tagIndex) {
        this.tagIndex = tagIndex;
    }

    /**
     * 埋点统计
     *
     * @param position
     * @param isLike
     * @param movieId
     */
    private void subClickData(int position, boolean isLike, String movieId) {
//        String firstRegin = "";
//        String secondRegin = "";
//        if (tagIndex == 0) {
//            firstRegin = StatisticTicket.TICKET_HOT;
//        } else {
//            firstRegin = StatisticTicket.TICKET_RECOMMEND;
//        }
//        if (isLike) {
//            secondRegin = "like";
//        } else {
//            secondRegin = "movieInf";
//        }
//        context.setPageLabel(StatisticTicket.PN_FUTURE_SCHEDULE);
//        Map<String, String> businessParam = new HashMap<String, String>();
//        businessParam.put(StatisticConstant.MOVIE_ID, movieId);
//        StatisticPageBean bean = context.assemble(firstRegin, String.valueOf(position), "movieList", null, secondRegin, null, businessParam);
//        StatisticManager.getInstance().submit(bean);
    }

    /**
     * 想看取消想看
     *
     * @param movieId
     * @param flag
     */
    private void handleWantSee(final int movieId, final int flag, final IncomingFilmBean.MoviecomingsBean moviecomingsBean) {
        if (flag == 1) {//添加为想看
            if (!wantMoviesList.contains(movieId)) {
                wantMoviesList.add(movieId);
            }
            notifyDataSetChanged();
        } else {//取消想看
            for (int i = 0; i < wantMoviesList.size(); i++) {
                int wantMovieId = wantMoviesList.get(i);
                if (movieId == wantMovieId) {
                    wantMoviesList.remove(i);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
        if (incomingFilmAdapter != null) {
            incomingFilmAdapter.setWantMoviesList(wantMoviesList);
        }

        mCommonApi.setWantToSee(movieId, flag, new NetworkManager.NetworkListener<MovieWantSeenResultBean>() {
            @Override
            public void onSuccess(MovieWantSeenResultBean result, String showMsg) {
                sendRemindTicket(movieId, flag, moviecomingsBean);
            }

            @Override
            public void onFailure(NetworkException<MovieWantSeenResultBean> exception, String showMsg) {
                MToastUtils.showShortToast(showMsg);
            }
        });
    }

    /**
     * 添加或取消上映提醒
     */
    private void sendRemindTicket(final int movieId, int flag, final IncomingFilmBean.MoviecomingsBean incomingMovieBean) {
        String pushtoken = ToolsUtils.getToken(context.getApplicationContext());
        String jpushid = ToolsUtils.getJPushId(context.getApplicationContext());

        if (!TextUtils.isEmpty(pushtoken) || !TextUtils.isEmpty(jpushid)) {
            Map<String, String> parameterList = new ArrayMap<String, String>(3);
            parameterList.put("movieId", String.valueOf(movieId));
            parameterList.put("deviceToken", pushtoken);
            parameterList.put("jPushRegID", jpushid);
            if (flag == 1) {
                HttpUtil.post(ConstantUrl.ADD_REMIND_MOVIE, parameterList, SuccessBean.class, new RequestCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        SuccessBean bean = (SuccessBean) o;
                        if (!Boolean.parseBoolean(bean.getSuccess())) {
                            return;
                        }

                        //(2021.10.21提示功能去掉)
//                        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//                        final Intent intent = new Intent(AlarmReceiver.ALARM_REMINDER);
//                        intent.putExtra("MovieTitle", incomingMovieBean.getTitle());
//                        intent.putExtra("MovieID", String.valueOf(movieId));
//                        final PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                        int rYear = incomingMovieBean.getReleaseYear();
//                        int rMonth = incomingMovieBean.getReleaseMonth();
//                        int rDay = incomingMovieBean.getReleaseDay();
//                        if (rDay != 0 && rMonth != 0 && rYear != 0) {
//                            String dateString = String.format("%1$d年%2$2d月%3$d日", rYear, rMonth, rDay);
//                            try {
//                                Date parse = DateUtil.sdf8.parse(dateString);
//                                am.set(AlarmManager.RTC_WAKEUP, parse.getTime(), pi);
//                                if (!ReleaseReminder.getInstance().contains(String.valueOf(movieId))) {
//                                    ReleaseReminder.getInstance().add(new RemindBean(String.valueOf(movieId), incomingMovieBean.getReleaseDateStr(), incomingMovieBean.getTitle(), parse.getTime()));
//                                }
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                        }

                    }

                    @Override
                    public void onFail(Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                HttpUtil.post(ConstantUrl.DELETE_REMIND_MOVIE, parameterList, SuccessBean.class, new RequestCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        SuccessBean bean = (SuccessBean) o;
                        if (!Boolean.parseBoolean(bean.getSuccess())) {
                            return;
                        }

                        //(2021.10.21提示功能去掉)
//                        ReleaseReminder.getInstance().remove(String.valueOf(movieId));
//                        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//                        final Intent intent = new Intent(AlarmReceiver.ALARM_REMINDER);
//                        intent.putExtra("MovieTitle", incomingMovieBean.getTitle());
//                        intent.putExtra("MovieID", String.valueOf(movieId));
//                        final PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                        am.cancel(pi);
                    }

                    @Override
                    public void onFail(Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
    private void setTextValue(TextView tv, String value) {
        if (tv != null) {
            if (!TextUtils.isEmpty(value)) {
                tv.setText(value);
            } else {
                tv.setText("");
            }
        }
    }

    @Override
    public int getItemCount() {
        return filmList.size();
    }

    public void clear() {
        this.filmList.clear();
        this.notifyDataSetChanged();
    }

    public void addAll(Collection<? extends IncomingFilmBean.MoviecomingsBean> collection) {
        if (collection != null) {
            this.filmList.addAll(collection);
            this.notifyDataSetChanged();
        }

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView dateTv;
        private final NetworkImageView filmImg;
        private final TextView titleTv;
        private final TextView wantSeeNumTv;
        private final ImageView wantSeeBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            dateTv = itemView.findViewById(R.id.releas_date);
            filmImg = itemView.findViewById(R.id.film_img);
            titleTv = itemView.findViewById(R.id.film_title);
            wantSeeNumTv = itemView.findViewById(R.id.want_see_num);
            wantSeeBtn = itemView.findViewById(R.id.icon_wantseen);
        }
    }
}
