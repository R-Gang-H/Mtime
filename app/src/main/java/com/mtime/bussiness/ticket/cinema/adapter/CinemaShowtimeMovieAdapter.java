package com.mtime.bussiness.ticket.cinema.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.kotlin.android.image.coil.ext.CoilCompat;
import com.kotlin.android.ktx.ext.core.Direction;
import com.kotlin.android.ktx.ext.core.ViewExtKt;
import com.kotlin.android.ktx.ext.dimension.DimensionExtKt;
import com.mtime.frame.App;
import com.mtime.R;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.ticket.cinema.bean.CinemaShowtimeUPHalfMovieBean;
import com.mtime.mtmovie.widgets.ScoreView;

import java.util.ArrayList;
import java.util.List;

import static com.mtime.R.id.rl_cinema_movies;

public class CinemaShowtimeMovieAdapter extends RecyclerView.Adapter<CinemaShowtimeMovieAdapter.ViewHolder> {

    private static final float IMAGE_WIDTH = 70f;

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final List<CinemaShowtimeUPHalfMovieBean> beans = new ArrayList<CinemaShowtimeUPHalfMovieBean>();
    private final int width;
    private final int height;

    public CinemaShowtimeMovieAdapter(Context c, List<CinemaShowtimeUPHalfMovieBean> beans) {
        this.mContext = c;
        this.beans.clear();
        this.beans.addAll(beans);
        this.mInflater = LayoutInflater.from(c);
        width = MScreenUtils.dp2px(IMAGE_WIDTH);
        height = width * 3 / 2;
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View convertView = mInflater.inflate(R.layout.cinema_showtime_gallery_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(convertView);

        holder.rl_cinema_movies = convertView.findViewById(rl_cinema_movies);
        holder.imageView = convertView.findViewById(R.id.cinema_showtime_gallery_item_iv);
        holder.vFilterPhoto = convertView.findViewById(R.id.v_filter_photo);
        holder.llFilterCover = convertView.findViewById(R.id.ll_filter_cover);
        holder.scroe = convertView.findViewById(R.id.cinema_showtime_gallery_item_score);

        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
        params1.width = width;
        params1.height = height;
        holder.imageView.setLayoutParams(params1);
        // 恐怖海报
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) holder.vFilterPhoto.getLayoutParams();
        params2.width = width;
        params2.height = height;
        holder.vFilterPhoto.setLayoutParams(params2);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.position = position;
        final CinemaShowtimeUPHalfMovieBean movieBean = beans.get(position);
        if (movieBean.isBorder()) {
            holder.rl_cinema_movies.setVisibility(View.GONE);
        } else {
            holder.rl_cinema_movies.setVisibility(View.VISIBLE);
            if (null == movieBean.getImg()) {
                movieBean.setImg("");
            }
            if (movieBean.isFilter() && App.getInstance().FILTER_SET) {
                holder.imageView.setVisibility(View.INVISIBLE); // 需要占位，否则评分位置会乱
                holder.llFilterCover.setVisibility(View.VISIBLE);
                holder.vFilterPhoto.setVisibility(View.VISIBLE);
                // 屏蔽海报：高斯模糊
                CoilCompat.INSTANCE.loadBlurImage(
                        holder.vFilterPhoto,
                        movieBean.getImg(),
                        0,
                        0,
                        false,
                        R.drawable.default_image,
                        App.getInstance().FILTER_COVER_BLUR_RADIUS,
                        App.getInstance().FILTER_COVER_BLUR_SAMPLING
                );
            } else {
                holder.imageView.setVisibility(View.VISIBLE);
                holder.llFilterCover.setVisibility(View.GONE);
                holder.vFilterPhoto.setVisibility(View.GONE);

                CoilCompat.INSTANCE.loadGifImage(
                        holder.imageView,
                        movieBean.getImg(),
                        DimensionExtKt.getDp(width),
                        DimensionExtKt.getDp(height),
                        true,
                        false,
                        false,
                        R.drawable.default_image,
                        null,
                        0F
                );
            }
            ViewExtKt.setBackground(
                    holder.scroe,
                    R.color.score_color,
                    null,
                    android.R.color.transparent,
                    null, null, null, null, null,
                    0, 0, 0,
                    DimensionExtKt.getDpF(5),
                    Direction.RIGHT_BOTTOM,
                    GradientDrawable.Orientation.TOP_BOTTOM
                    );
            holder.scroe.setScore(movieBean.getRatingFinal());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

        public ImageView imageView;
        public ScoreView scroe;
        public int position;
        public RelativeLayout rl_cinema_movies;
        public ImageView vFilterPhoto;
        public View llFilterCover;
    }
}
