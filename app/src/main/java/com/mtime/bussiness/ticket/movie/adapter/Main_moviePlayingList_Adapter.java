package com.mtime.bussiness.ticket.movie.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.bussiness.ticket.movie.bean.MovieBean;
import com.mtime.common.utils.ConvertHelper;
import com.mtime.common.utils.TextUtil;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.ticket.StatisticTicket;
import com.mtime.util.ImageLoader;
import com.mtime.util.ImageURLManager;
import com.mtime.util.JumpUtil;
import com.mtime.util.MtimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 正在热映列表适配器
 *
 * @author wangdaban
 */
public class Main_moviePlayingList_Adapter extends RecyclerView.Adapter<Main_moviePlayingList_Adapter.HolderView> {

    private final Context mContext;
    private final int serverYear;
    private final int serverMonth;
    private final int serverDate;
    private final ArrayList<MovieBean> filmList = new ArrayList<>();
    private final LayoutInflater layoutInflater;
    private long mStartTime;
    private String referBean;

    // 图片加载器
    private ImageLoader volleyImageLoader = new ImageLoader();

    public void setStartTime(long mStartTime) {
        this.mStartTime = mStartTime;
    }

    public void setReferBean(String referBean) {
        this.referBean = referBean;
    }

    public Main_moviePlayingList_Adapter(Context context, ArrayList<MovieBean> filmList) {
        this.mContext = context;
        Calendar cal = Calendar.getInstance();
        cal.setTime(MTimeUtils.getLastDiffServerDate());
        serverYear = cal.get(Calendar.YEAR);
        serverMonth = cal.get(Calendar.MONTH) + 1;
        serverDate = cal.get(Calendar.DATE);
        if (filmList != null) {
            this.filmList.addAll(filmList);
        }
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public HolderView onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HolderView(layoutInflater.inflate(R.layout.adapter_filmlist_on, parent, false));
    }

    @Override
    public void onBindViewHolder(HolderView holder, final int position) {
        final MovieBean info = filmList.get(position);
        info.setPosition(position);
        if (info == null) {
            return;
        }
        holder.button.setTag(info);
        holder.button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final MovieBean tagInfo = (MovieBean) (v.getTag());

//                mContext.setPageLabel(StatisticTicket.PN_ON_SHOW_LIST);
//                StatisticPageBean bean1 = mContext.assemble(StatisticConstant.CLOSE, null, null, null, null, null, null);
//                if (!TextUtils.isEmpty(referBean)) {
//                    bean1.refer = referBean;
//                }
//                StatisticManager.getInstance().submit(bean1);

//                mContext.setPageLabel(StatisticTicket.PN_ON_SHOW_LIST);
//                Map<String, String> params = new HashMap<>();
//                params.put("duration", String.valueOf(System.currentTimeMillis() - mStartTime));
//                StatisticPageBean bean3 = mContext.assemble(StatisticConstant.TIMING, null, null, null, null, null, params);
//                if (!TextUtils.isEmpty(referBean)) {
//                    bean3.refer = referBean;
//                }
//                StatisticManager.getInstance().submit(bean3);

//                mContext.setPageLabel(StatisticTicket.PN_ON_SHOW_LIST);
//                Map<String, String> businessParam = new HashMap<String, String>();
//                businessParam.put(StatisticConstant.MOVIE_ID, String.valueOf(tagInfo.getId()));
//                StatisticPageBean bean = mContext.assemble(StatisticTicket.TICKET_MOVIE_LIST, String.valueOf(info.getPosition()), "ticketIcon", null, null, null, businessParam);
//                if (!TextUtils.isEmpty(referBean)) {
//                    bean.refer = referBean;
//                }
//                StatisticManager.getInstance().submit(bean);

                JumpUtil.startMovieShowtimeActivity(mContext, null,String.valueOf(tagInfo.getId()),tagInfo.getName(),tagInfo.isTicket(), null, 0);

            }
        });
        if (!TextUtils.isEmpty(info.getImageUrl())) {
            if (info.isFilter() && App.getInstance().FILTER_SET) {
                // 屏蔽海报：高斯模糊
                // 注：在recyclerview里用ImageView加载不出高斯模糊的图片，显示出来的是默认图，先处理成用View加载
                volleyImageLoader.displayBlurImg(mContext,
                        info.getImageUrl(), holder.vFilterPhoto,
                        App.getInstance().FILTER_COVER_BLUR_RADIUS,
                        App.getInstance().FILTER_COVER_BLUR_SAMPLING,
                        R.drawable.default_image, R.drawable.default_image, null);
                holder.moviePic.setVisibility(View.GONE);
                holder.llFilterCover.setVisibility(View.VISIBLE);
                holder.vFilterPhoto.setVisibility(View.VISIBLE);
            } else {
                // 海报
                volleyImageLoader.displayImage(info.getImageUrl(), holder.moviePic,
                        R.drawable.default_image, R.drawable.default_image,
                        ImageURLManager.ImageStyle.STANDARD, null);
                holder.moviePic.setVisibility(View.VISIBLE);
                holder.llFilterCover.setVisibility(View.GONE);
                holder.vFilterPhoto.setVisibility(View.GONE);
            }
        } else {
            holder.moviePic.setVisibility(View.VISIBLE);
            holder.llFilterCover.setVisibility(View.GONE);
            holder.vFilterPhoto.setVisibility(View.GONE);
            holder.moviePic.setImageResource(R.drawable.default_image);
        }

        // set name
        if (!TextUtils.isEmpty(info.getName())) {
            holder.movieName.setText(info.getName());
        }
        if (info.isTicket()) {
            if (!TextUtils.isEmpty(info.getShowdate())) {
                String dateStr = ConvertHelper.toDateFormat(info.getShowdate(), "yyyyMMdd", "yyyy年M月d日");
                int year = 0;
                int month = 0;
                int day = 0;
                try {
                    year = Integer.parseInt(MtimeUtils.getSplitStr(dateStr)[0]);
                    month = Integer.parseInt(MtimeUtils.getSplitStr(dateStr)[1]);
                    day = Integer.parseInt(MtimeUtils.getSplitStr(dateStr)[2]);
                } catch (Exception e) {
                }
                boolean isIncoming = false;
                try {
                    if (!(Math.abs(year - serverYear) > 1)) {// 防止数据异常,正常预售应该不会跨2年以上
                        if (year - serverYear == 1) {// 上映年份大于当前时间说明该影片还未上映
                            isIncoming = true;
                        } else if (month - serverMonth >= 1// 上映月份大于当前月份说明该影片还未上映
                                && year == serverYear// 可以不要此判断的
                                ) {// 上映年份大于当前时间说明该影片还未上映
                            isIncoming = true;
                        } else if (day - serverDate >= 1// 上映日期大于当前日期的说明该影片还未上映
                                && year == serverYear// 可以不要此判断的
                                && month == serverMonth) {
                            isIncoming = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!isIncoming) {
                    holder.button.setText(mContext.getResources().getString(R.string.str_buy_ticket));
                    holder.button.setBackgroundResource(R.drawable.bg_stroke_f97d3f_frame);
                    holder.button.setTextColor(ContextCompat.getColor(mContext, R.color.color_f97d3f));
                } else {
                    holder.button.setText("预售");
                    holder.button.setBackgroundResource(R.drawable.bg_stroke_659d0e_frame);
                    holder.button.setTextColor(ContextCompat.getColor(mContext, R.color.color_659d0e));
                }
            } else {
                // 没有没有排片日期，则显示查影讯
                holder.button.setText(mContext.getResources().getString(R.string.st_see_movie_showtime));
                holder.button.setBackgroundResource(R.drawable.bg_stroke_659d0e_frame);
                holder.button.setTextColor(ContextCompat.getColor(mContext, R.color.color_659d0e));
            }

        } else {
            holder.button.setText(mContext.getResources().getString(R.string.st_see_movie_showtime));
            holder.button.setBackgroundResource(R.drawable.bg_stroke_659d0e_frame);
            holder.button.setTextColor(ContextCompat.getColor(mContext, R.color.color_659d0e));
        }
        if (info.getRatingScore() > 0) {
            holder.score.setVisibility(View.VISIBLE);
            holder.tvScore.setVisibility(View.VISIBLE);
            holder.score.setText(String.valueOf((Math.round(info.getRatingScore() * 10) / 10.0)));
            // ToolsUtils.drawScoreView(mContext, holder.score, 18, 18, 0, 0, false);
        } else {
            // 需要占位，否则版本号下面的底线显示不出来
            holder.score.setVisibility(View.INVISIBLE);
            holder.tvScore.setVisibility(View.INVISIBLE);
        }
        if (!TextUtils.isEmpty(info.getCommonSpecial())) {
            holder.movietype.setVisibility(View.GONE);
            holder.descLl.setVisibility(View.VISIBLE);
            holder.desc.setVisibility(View.VISIBLE);
            String str = TextUtil.replace(info.getCommonSpecial(), "\n", "");
            holder.desc.setText(str);
        } else {
            holder.descLl.setVisibility(View.GONE);
            holder.movietype.setVisibility(View.VISIBLE);
            final StringBuffer buf = new StringBuffer();
            final String type = info.getMovieType();
            final String[] array = type.split("/");

            if ((array != null) && (array.length > 0)) {
                buf.append("-");
                if (array.length > 3) {
                    buf.append(array[0]).append("/");
                    buf.append(array[1]).append("/");
                    buf.append(array[2]).append("/");
                } else {
                    for (final String str : array) {
                        if (!str.equals("")) {
                            buf.append(str);
                            buf.append("/");
                        }
                    }

                }
                if (buf.length() >= 1) {// - or /
                    buf.delete(buf.length() - 1, buf.length());
                }
            }
            holder.movietype.setText(info.getWantedCount() + "人想看" + buf.toString());
            changeWantSeenNumColor(holder.movietype);
        }

        if (info.isNew()) {
            holder.tagnew.setVisibility(View.VISIBLE);
        } else {
            holder.tagnew.setVisibility(View.GONE);
        }
        if (info.isIMAX()) {
            holder.tagimax.setVisibility(View.VISIBLE);
        } else {
            holder.tagimax.setVisibility(View.GONE);
        }
        if (info.isIs3D()) {
            holder.tag3d.setVisibility(View.VISIBLE);
        } else {
            holder.tag3d.setVisibility(View.GONE);
        }

        if (info.isIMAX3D()) {
            holder.tag3d.setVisibility(View.VISIBLE);
            holder.tagimax.setVisibility(View.VISIBLE);
        }

        //获取演员
        setTextValue(holder.actors, info.getActors());


        if (info.isHasTrailer()) {
            holder.img_video.setVisibility(View.VISIBLE);
        } else {
            holder.img_video.setVisibility(View.GONE);
        }

        holder.img_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToVideoList(v, info);
            }
        });


        holder.ll_whole_item.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

//                mContext.setPageLabel(StatisticTicket.PN_ON_SHOW_LIST);
//                StatisticPageBean bean1 = mContext.assemble(StatisticConstant.CLOSE, null, null, null, null, null, null);
//                if (!TextUtils.isEmpty(referBean)) {
//                    bean1.refer = referBean;
//                }
//                StatisticManager.getInstance().submit(bean1);

//                mContext.setPageLabel(StatisticTicket.PN_ON_SHOW_LIST);
//                Map<String, String> params = new HashMap<>();
//                params.put("duration", String.valueOf(System.currentTimeMillis() - mStartTime));
//                StatisticPageBean bean3 = mContext.assemble(StatisticConstant.TIMING, null, null, null, null, null, params);
//                if (!TextUtils.isEmpty(referBean)) {
//                    bean3.refer = referBean;
//                }
//                StatisticManager.getInstance().submit(bean3);

                String value = String.valueOf((filmList.get(position)).getId());
//                mContext.setPageLabel(StatisticTicket.PN_ON_SHOW_LIST);
//                Map<String, String> businessParam = new HashMap<>();
//                businessParam.put(StatisticConstant.MOVIE_ID, value);
//                StatisticPageBean bean = mContext.assemble(StatisticTicket.TICKET_MOVIE_LIST, String.valueOf(position), "movieInf", null, null, null, businessParam);
//                if (!TextUtils.isEmpty(referBean)) {
//                    bean.refer = referBean;
//                }
//                StatisticManager.getInstance().submit(bean);
                JumpUtil.startMovieInfoActivity(mContext, null,value, 0);
            }
        });

    }


    private void jumpToVideoList(View view, MovieBean info) {
        String value = String.valueOf(info.getId());
        if (info.isHasTrailer()) {
            JumpUtil.startVideoListActivity(mContext, null, value);
        } else {
            JumpUtil.startMovieInfoActivity(mContext, null, value, 0);
        }
    }


    @Override
    public int getItemCount() {
        return filmList.size();
    }

    public static class HolderView extends RecyclerView.ViewHolder {

        TextView movieName, score, tvScore; // 影片名字
        ImageView moviePic;
        ImageView tag3d, tagimax, tagnew, img_video; // 影片图标
        TextView desc, movietype;
        LinearLayout descLl, ll_whole_item;
        TextView button;
        TextView actors;
        View vFilterPhoto;
        View llFilterCover;

        public HolderView(View convertView) {
            super(convertView);
            ll_whole_item = convertView.findViewById(R.id.movie_list_item_ll_bg);
            movieName = convertView.findViewById(R.id.movie_list_item_moviename);
            desc = convertView.findViewById(R.id.movie_list_item_desc);
            movietype = convertView.findViewById(R.id.movie_list_item_movietype);
            movietype.setVisibility(View.GONE);
            moviePic = convertView.findViewById(R.id.movie_list_item_iv_photo);
            score = convertView.findViewById(R.id.movie_list_item_score_view);
            tvScore = convertView.findViewById(R.id.movie_list_item_score);
            button = convertView.findViewById(R.id.button_pay);
            descLl = convertView.findViewById(R.id.movie_list_item_desc_ll);
            tag3d = convertView.findViewById(R.id.movie_list_item_tag_3d);
            tagimax = convertView.findViewById(R.id.movie_list_item_tag_imax);
            tagnew = convertView.findViewById(R.id.movie_list_item_tag_new);
            actors = convertView.findViewById(R.id.tv_actors);
            img_video = itemView.findViewById(R.id.img_video);
            vFilterPhoto = convertView.findViewById(R.id.v_filter_photo);
            llFilterCover = convertView.findViewById(R.id.ll_filter_cover);
        }
    }

    private void changeWantSeenNumColor(TextView tv) {
        String wantSeeNum = tv.getText().toString();
        if (!TextUtils.isEmpty(wantSeeNum) && wantSeeNum.indexOf("人想看") > 0) {
            SpannableString ss = new SpannableString(wantSeeNum);
            int color = 0xFFFF8600;
            ss.setSpan(new ForegroundColorSpan(color), 0, wantSeeNum.indexOf("人想看"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(ss);
        }
    }

    public void addAll(Collection<? extends MovieBean> collection) {
        if (collection != null) {
            this.filmList.addAll(collection);
            this.notifyDataSetChanged();
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

}
