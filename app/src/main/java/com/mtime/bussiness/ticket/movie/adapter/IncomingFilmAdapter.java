package com.mtime.bussiness.ticket.movie.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArrayMap;
import androidx.core.content.ContextCompat;
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
import com.mtime.frame.App;
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
 * 即将上映列表
 * Created by lsmtime on 17/4/5.
 */

public class IncomingFilmAdapter extends RecyclerView.Adapter<IncomingFilmAdapter.ViewHolder> {
    private final LayoutInflater layoutInflater;
    private final AppCompatActivity context;
    private final ArrayList<IncomingFilmBean.MoviecomingsBean> filmList = new ArrayList<>();
    private final ArrayList<Integer> wantMoviesList = new ArrayList<>();
    private AttentionFilmAdapter attentionFilmAdapter;
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



    public IncomingFilmAdapter(AppCompatActivity context, ArrayList<IncomingFilmBean.MoviecomingsBean> filmList) {
        this.context = context;
        if (filmList != null) {
            this.filmList.addAll(filmList);
        }
        layoutInflater = LayoutInflater.from(context);
    }

    public void setAttentionFilmAdapter(AttentionFilmAdapter attentionFilmAdapter) {
        this.attentionFilmAdapter = attentionFilmAdapter;
    }

    public void setWantMoviesList(ArrayList<Integer> wantMoviesList) {
        if (wantMoviesList != null) {
            this.wantMoviesList.clear();
            this.wantMoviesList.addAll(wantMoviesList);
            notifyDataSetChanged();
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.adapter_incoming_film, parent, false));
    }

    public IncomingFilmBean.MoviecomingsBean getItem(int postion) {
        if (postion >= 0 && postion < filmList.size()) {
            return filmList.get(postion);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        IncomingFilmBean.MoviecomingsBean moviecomingsBean = filmList.get(position);
        if (moviecomingsBean == null) {
            return;
        }

        setTextValue(holder.filmTitleTv, moviecomingsBean.getTitle());
        setTextValue(holder.wantSeeNumTv, String.valueOf(moviecomingsBean.getWantedCount()));
        if (!TextUtils.isEmpty(moviecomingsBean.getType())) {
            setTextValue(holder.filmTypeTv, String.format("人想看 - %1$s", moviecomingsBean.getType()));
        } else {
            setTextValue(holder.filmTypeTv, "人想看");
        }
        setTextValue(holder.actorsTv, moviecomingsBean.getActors());

        if (moviecomingsBean.isIsTicket()) {
            holder.wantSeeBtnLayout.setBackgroundResource(R.drawable.bg_stroke_659d0e_frame);
            holder.iconWantSee.setVisibility(View.GONE);
            holder.wantSeeBtn.setText("预售");
            holder.wantSeeBtn.setTextColor(ContextCompat.getColor(context, R.color.color_659d0e));
        } else {
            holder.wantSeeBtnLayout.setBackgroundResource(R.drawable.bg_stroke_f97d3f_frame);
            holder.wantSeeBtn.setTextColor(ContextCompat.getColor(context, R.color.color_f97d3f));
            if (wantMoviesList.contains(moviecomingsBean.getMovieId())) {
                holder.iconWantSee.setVisibility(View.GONE);
                holder.wantSeeBtn.setText("已想看");
            } else {
                holder.iconWantSee.setVisibility(View.VISIBLE);
                holder.wantSeeBtn.setText("想看");
            }
        }

        if (moviecomingsBean.isHeader()) {
            holder.dateTv.setVisibility(View.VISIBLE);
            setTextValue(holder.dateTv, moviecomingsBean.getDateString());
            holder.spliteLine.setVisibility(View.GONE);
        } else {
            holder.spliteLine.setVisibility(View.VISIBLE);
            holder.dateTv.setVisibility(View.GONE);
        }
        moviecomingsBean.setPosition(position);
        if (moviecomingsBean.isIsVideo()) {
            holder.videoBtn.setVisibility(View.VISIBLE);
        } else {
            holder.videoBtn.setVisibility(View.GONE);
        }

//        holder.videoBtn.setTag(R.string.app_name, moviecomingsBean);
//        holder.videoBtn.setTag(R.string.appbar_scrolling_view_behavior, position);
//        holder.videoBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                jumpToVideoList(v, moviecomingsBean, position);
//            }
//        });

//        holder.filmImg.setTag(R.string.app_name, moviecomingsBean.getImage());
        if (moviecomingsBean.isFilter() && App.getInstance().FILTER_SET) {
            // 屏蔽海报：高斯模糊
            // 注：在recyclerview里用ImageView加载不出高斯模糊的图片，显示出来的是默认图，先处理成用View加载
            volleyImageLoader.displayBlurImg(context,
                    moviecomingsBean.getImgUrl(), holder.vFilterPhoto,
                    App.getInstance().FILTER_COVER_BLUR_RADIUS,
                    App.getInstance().FILTER_COVER_BLUR_SAMPLING,
                    R.drawable.default_image, R.drawable.default_image, null);
            holder.filmImg.setVisibility(View.GONE);
            holder.llFilterCover.setVisibility(View.VISIBLE);
            holder.vFilterPhoto.setVisibility(View.VISIBLE);
        } else {
            volleyImageLoader.displayNetworkImage(volleyImageLoader,
                    moviecomingsBean.getImgUrl(), holder.filmImg,
                    R.drawable.default_image, R.drawable.default_image,
                    Utils.dip2px(context, 60), Utils.dip2px(context, 90),
                    ImageURLManager.SCALE_TO_FIT, null);
            holder.filmImg.setVisibility(View.VISIBLE);
            holder.llFilterCover.setVisibility(View.GONE);
            holder.vFilterPhoto.setVisibility(View.GONE);
        }


//            holder.filmImg.setTag(R.string.app_name, moviecomingsBean);
//            holder.filmImg.setTag(R.string.appbar_scrolling_view_behavior, position);
        holder.filmImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moviecomingsBean.isIsVideo()) {
                    jumpToVideoList(v, moviecomingsBean, position);
                } else {
                    holder.itemView.performClick();
                }
            }
        });

//        holder.wantSeeBtnLayout.setTag(R.string.app_name, moviecomingsBean);
//        holder.wantSeeBtnLayout.setTag(R.string.appbar_scrolling_view_behavior, position);
        holder.wantSeeBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final IncomingFilmBean.MoviecomingsBean incomingTag = moviecomingsBean;
//                IncomingFilmBean.MoviecomingsBean incomingTag = (IncomingFilmBean.MoviecomingsBean) v.getTag(R.string.app_name);
//                int position = (int) v.getTag(R.string.appbar_scrolling_view_behavior);

                int movieId = incomingTag.getMovieId();
//                Intent intent = new Intent();
                if (incomingTag == null) {
                    return;
                }
                if (incomingTag.isIsTicket()) {//跳转到影片排片页面

//                    context.setPageLabel(StatisticTicket.PN_FUTURE_SCHEDULE);
//                    StatisticPageBean bean1 = context.assemble(StatisticConstant.CLOSE, null, null, null, null, null, null);
//                    if (!TextUtils.isEmpty(referBean)) {
//                        bean1.refer = referBean;
//                    }
//                    StatisticManager.getInstance().submit(bean1);

//                    context.setPageLabel(StatisticTicket.PN_FUTURE_SCHEDULE);
//                    Map<String, String> params = new HashMap<>();
//                    params.put("duration", String.valueOf(System.currentTimeMillis() - mStartTime));
//                    StatisticPageBean bean3 = context.assemble(StatisticConstant.TIMING, null, null, null, null, null, params);
//                    if (!TextUtils.isEmpty(referBean)) {
//                        bean3.refer = referBean;
//                    }
//                    StatisticManager.getInstance().submit(bean3);

//                    context.setPageLabel(StatisticTicket.PN_FUTURE_SCHEDULE);
//                    Map<String, String> businessParam = new HashMap<String, String>();
//                    businessParam.put(StatisticConstant.MOVIE_ID, String.valueOf(movieId));
//                    StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_LIST, String.valueOf(incomingTag.getPosition()), "preSale", null, null, null, businessParam);
//                    if (!TextUtils.isEmpty(referBean)) {
//                        bean.refer = referBean;
//                    }
//                    StatisticManager.getInstance().submit(bean);

                    JumpUtil.startMovieShowtimeActivity(context, null, String.valueOf(movieId), incomingTag.getTitle(), true, null, 0);

                } else {
//                    context.setPageLabel(StatisticTicket.PN_FUTURE_SCHEDULE);
//                    Map<String, String> businessParam = new HashMap<String, String>();
//                    businessParam.put(StatisticConstant.MOVIE_ID, String.valueOf(movieId));
//                    StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_LIST, String.valueOf(position), "like", null, null, null, businessParam);
//                    StatisticManager.getInstance().submit(bean);

                    if (UserManager.Companion.getInstance().isLogin()) {
                        if (wantMoviesList.contains(movieId)) {//取消想看
                            handleWantSee(movieId, 2, position, incomingTag);
                        } else {//想看
                            handleWantSee(movieId, 1, position, incomingTag);
                        }
                    } else {
//                        context.startActivity(LoginActivity.class, intent);
                        UserLoginKt.gotoLoginPage(context, null, null);
                        PrefsManager.get(context).putBoolean("incoming_wantids", true);
                    }

                }
            }
        });
//        holder.itemView.setTag(R.string.app_name, moviecomingsBean);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                final IncomingFilmBean.MoviecomingsBean incomingTag = moviecomingsBean;
                String value = String.valueOf(incomingTag.getMovieId());
//                context.setPageLabel(StatisticTicket.PN_FUTURE_SCHEDULE);
//                Map<String, String> businessParam = new HashMap<String, String>();
//                businessParam.put(StatisticConstant.MOVIE_ID, value);
//                StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_LIST, String.valueOf(position), "movieInf", null, null, null, businessParam);
//                if (!TextUtils.isEmpty(referBean)) {
//                    bean.refer = referBean;
//                }
//                StatisticManager.getInstance().submit(bean);

                JumpUtil.startMovieInfoActivity(context, null, value, 0);
            }
        });

    }

    private void jumpToVideoList(View view, IncomingFilmBean.MoviecomingsBean incomingTag, int position) {
//        IncomingFilmBean.MoviecomingsBean incomingTag = (IncomingFilmBean.MoviecomingsBean) view.getTag(R.string.app_name);
//        int position = (int) view.getTag(R.string.appbar_scrolling_view_behavior);
        if (incomingTag == null) {
            return;
        }
        String value = String.valueOf(incomingTag.getMovieId());
        if (incomingTag.isIsVideo()) {


//            context.setPageLabel(StatisticTicket.PN_FUTURE_SCHEDULE);
//            StatisticPageBean bean1 = context.assemble(StatisticConstant.CLOSE, null, null, null, null, null, null);
//            if (!TextUtils.isEmpty(referBean)) {
//                bean1.refer = referBean;
//            }
//            StatisticManager.getInstance().submit(bean1);

//            context.setPageLabel(StatisticTicket.PN_FUTURE_SCHEDULE);
//            Map<String, String> params = new HashMap<>();
//            params.put("duration", String.valueOf(System.currentTimeMillis() - mStartTime));
//            StatisticPageBean bean3 = context.assemble(StatisticConstant.TIMING, null, null, null, null, null, params);
//            if (!TextUtils.isEmpty(referBean)) {
//                bean3.refer = referBean;
//            }
//            StatisticManager.getInstance().submit(bean3);

//            context.setPageLabel(StatisticTicket.PN_FUTURE_SCHEDULE);
//            Map<String, String> businessParam = new HashMap<String, String>();
//            businessParam.put(StatisticConstant.MOVIE_ID, value);
//            StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_LIST, String.valueOf(position), "tailer", null, null, null, businessParam);
//            if (!TextUtils.isEmpty(referBean)) {
//                bean.refer = referBean;
//            }
//            StatisticManager.getInstance().submit(bean);

            JumpUtil.startVideoListActivity(context, null, String.valueOf(incomingTag.getMovieId()));
        } else {

//            context.setPageLabel(StatisticTicket.PN_FUTURE_SCHEDULE);
//            StatisticPageBean bean1 = context.assemble(StatisticConstant.CLOSE, null, null, null, null, null, null);
//            if (!TextUtils.isEmpty(referBean)) {
//                bean1.refer = referBean;
//            }
//            StatisticManager.getInstance().submit(bean1);

//            context.setPageLabel(StatisticTicket.PN_FUTURE_SCHEDULE);
//            Map<String, String> params = new HashMap<>();
//            params.put("duration", String.valueOf(System.currentTimeMillis() - mStartTime));
//            StatisticPageBean bean3 = context.assemble(StatisticConstant.TIMING, null, null, null, null, null, params);
//            if (!TextUtils.isEmpty(referBean)) {
//                bean3.refer = referBean;
//            }
//            StatisticManager.getInstance().submit(bean3);

//            context.setPageLabel(StatisticTicket.PN_FUTURE_SCHEDULE);
//            Map<String, String> businessParam = new HashMap<String, String>();
//            businessParam.put(StatisticConstant.MOVIE_ID, value);
//            StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_LIST, String.valueOf(position), "movieInf", null, null, null, businessParam);
//            if (!TextUtils.isEmpty(referBean)) {
//                bean.refer = referBean;
//            }
//            StatisticManager.getInstance().submit(bean);

            JumpUtil.startMovieInfoActivity(context, null, value, 0);
        }


    }

    /**
     * 想看取消想看
     *
     * @param movieId
     * @param flag
     */
    private void handleWantSee(final int movieId, final int flag, final int positin, final IncomingFilmBean.MoviecomingsBean incomingMovieBean) {
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
        if (attentionFilmAdapter != null) {
            attentionFilmAdapter.setWantMoviesList(wantMoviesList);
        }

        mCommonApi.setWantToSee(movieId, flag, new NetworkManager.NetworkListener<MovieWantSeenResultBean>() {
            @Override
            public void onSuccess(MovieWantSeenResultBean result, String showMsg) {
                sendRemindTicket(movieId, flag, positin, incomingMovieBean);
            }

            @Override
            public void onFailure(NetworkException<MovieWantSeenResultBean> exception, String showMsg) {
                MToastUtils.showShortToast(showMsg);
            }
        });
    }

    /**
     * 添加或取消上映提醒
     *
     * @param movieId
     * @param flag
     */
    private void sendRemindTicket(final int movieId, int flag, int position, final IncomingFilmBean.MoviecomingsBean incomingMovieBean) {
        String pushtoken = ToolsUtils.getToken(context.getApplicationContext());
        String jpushid = ToolsUtils.getJPushId(context.getApplicationContext());
//        context.setPageLabel(StatisticTicket.PN_FUTURE_SCHEDULE);
//        Map<String, String> businessParam = new HashMap<String, String>();
//        businessParam.put(StatisticConstant.MOVIE_ID, String.valueOf(movieId));
//        StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_LIST, String.valueOf(position), "reminder", null, null, null, businessParam);
//        StatisticManager.getInstance().submit(bean);
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
//                        final Intent intent = new Intent("com.mtime.REMINDER_MOVIE");
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
//                        final Intent intent = new Intent("com.mtime.REMINDER_MOVIE");
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

    /**
     * 更新该影片想看状态
     *
     * @param movieId
     * @param isadd
     */
    public void modifyRemindIds(String movieId, boolean isadd) {
        if (TextUtils.isDigitsOnly(movieId)) {
            int filmId = Integer.parseInt(movieId);
            if (wantMoviesList.contains(filmId)) {
                if (isadd) {

                } else {
                    for (int i = 0; i < wantMoviesList.size(); i++) {
                        int wantMovieId = wantMoviesList.get(i);
                        if (wantMovieId == filmId) {
                            wantMoviesList.remove(i);
                            notifyDataSetChanged();
                            break;
                        }
                    }
                }
            } else {
                if (isadd) {
                    wantMoviesList.add(filmId);
                    notifyDataSetChanged();
                }
            }
        }
        attentionFilmAdapter.setWantMoviesList(wantMoviesList);
    }

    @Override
    public int getItemCount() {
        return filmList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateTv;
        private final NetworkImageView filmImg;
        private final ImageView videoBtn;
        private final TextView filmTitleTv;
        private final TextView wantSeeNumTv;
        private final TextView filmTypeTv;
        private final TextView actorsTv;
        private final View wantSeeBtnLayout;
        private final View iconWantSee;
        private final TextView wantSeeBtn;
        private final View spliteLine;
        private final View vFilterPhoto;
        private final View llFilterCover;

        public ViewHolder(View itemView) {
            super(itemView);
            dateTv = itemView.findViewById(R.id.date_tv);
            filmImg = itemView.findViewById(R.id.film_img);
            videoBtn = itemView.findViewById(R.id.video_btn);
            filmTitleTv = itemView.findViewById(R.id.film_title);
            wantSeeNumTv = itemView.findViewById(R.id.want_see_count_tv);
            filmTypeTv = itemView.findViewById(R.id.film_type);
            actorsTv = itemView.findViewById(R.id.actors);
            wantSeeBtnLayout = itemView.findViewById(R.id.want_btn_layout);
            iconWantSee = itemView.findViewById(R.id.want_see_icon);
            wantSeeBtn = itemView.findViewById(R.id.want_see_btn);
            spliteLine = itemView.findViewById(R.id.splite_line);
            vFilterPhoto = itemView.findViewById(R.id.v_filter_photo);
            llFilterCover = itemView.findViewById(R.id.ll_filter_cover);
        }
    }
}
