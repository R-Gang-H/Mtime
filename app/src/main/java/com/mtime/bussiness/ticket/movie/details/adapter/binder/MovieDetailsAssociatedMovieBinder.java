package com.mtime.bussiness.ticket.movie.details.adapter.binder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsExtendBean;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsRelatedMovie;
import com.mtime.bussiness.ticket.movie.details.holder.MovieDetailsHolder;
import com.mtime.bussiness.common.widget.CommonItemTitleView;
import com.mtime.frame.BaseStatisticHelper;
import com.mtime.mtmovie.widgets.PosterFilterView;
import com.mtime.mtmovie.widgets.ScoreView;
import com.mtime.statistic.large.MapBuild;
import com.mtime.util.JumpUtil;

import java.util.List;



/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-06-03
 *
 * 影片详情-关联电影
 */
public class MovieDetailsAssociatedMovieBinder extends MovieDetailsBaseBinder<MovieDetailsExtendBean.AssociatedMovies> {

    public MovieDetailsAssociatedMovieBinder(MovieDetailsHolder.OnJumpPageCallback callback, BaseStatisticHelper helper) {
        super(callback, helper);
    }

    @NonNull
    @Override
    protected BaseViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new BaseViewHolder(inflater.inflate(R.layout.layout_movie_details_associated_movie_list, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull BaseViewHolder holder, @NonNull MovieDetailsExtendBean.AssociatedMovies item) {
        int listSize = item.getCount();
        boolean showAll = listSize >= 3;

        CommonItemTitleView titleView = holder.getView(R.id.movie_details_associated_movie_title_view);
        titleView.setAllBtnViewVisibility(showAll);
        titleView.setOnAllBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 埋点上报
                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam())
                        .put("moreCount", String.valueOf(item.getCount()));
                String refer = mBaseStatisticHelper.assemble1(
                        "relatedMovie", null,
                        "all", null,
                        null, null, mapBuild.build()).submit();

                //关联电影列表
                JumpUtil.startRelatedMovieActivity(v.getContext(), refer, String.valueOf(item.movieId));
            }
        });

        // 手动增加一条全部
        if (listSize > 5 && item.list.get(listSize - 1).movieID != allId) {
            MovieDetailsRelatedMovie.Movie all = new MovieDetailsRelatedMovie.Movie();
            all.movieID = allId;
            item.list.add(all);
        }

        RecyclerView recyclerView = holder.getView(R.id.movie_details_associated_movie_rv);
        if (null == recyclerView.getAdapter()) {
            recyclerView.setNestedScrollingEnabled(false);
            ListAdatper listAdatper = new ListAdatper(item.list);
            listAdatper.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    MovieDetailsRelatedMovie.Movie movie = (MovieDetailsRelatedMovie.Movie) adapter.getItem(position);
                    if (null != movie) {
                        if (movie.movieID != allId) {
                            // 埋点上报
                            MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam())
                                    .put("targetMovieID", String.valueOf(movie.movieID));
                            String refer = mBaseStatisticHelper.assemble1(
                                    "relatedMovie", null,
                                    "showRelatedMovies", String.valueOf(position + 1),
                                    null, null, mapBuild.build()).submit();

                            //影片详情
                            JumpUtil.startMovieInfoActivity(view.getContext(), refer, String.valueOf(movie.movieID), 0);
                        } else {
                            //关联电影列表
                            titleView.performAllBtnClick();
                        }
                    }
                }
            });
            recyclerView.setAdapter(listAdatper);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        // 埋点上报
                        MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
                        String refer = mBaseStatisticHelper.assemble1(
                                "relatedMovie", null,
                                "scroll", null,
                                null, null, mapBuild.build()).submit();
                    }
                }
            });
        }
    }

    private class ListAdatper extends BaseMultiItemQuickAdapter<MovieDetailsRelatedMovie.Movie, BaseViewHolder> {

        public ListAdatper(@Nullable List<MovieDetailsRelatedMovie.Movie> data) {
            super(data);

            addItemType(0, R.layout.item_movie_details_associated_movie);
            addItemType(1, R.layout.item_common_all);
        }

        @Override
        protected void convert(BaseViewHolder helper, MovieDetailsRelatedMovie.Movie item) {
            if (item.movieID != allId) {
                PosterFilterView posterView = helper.setText(R.id.item_movie_details_associated_movie_title_tv, item.typeName)
                        .getView(R.id.movie_details_movie_item_poster_iv);
                posterView.setPosterFilter(item.isFilter);
                ImageHelper.with(ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                        .override(MScreenUtils.dp2px(100), MScreenUtils.dp2px(150))
                        .roundedCorners(4, 0)
                        .placeholder(R.drawable.default_image)
                        .load(item.img)
                        .view(posterView)
                        .showload();

                ScoreView scoreView = helper.setGone(R.id.movie_details_movie_item_score_layout,
                        TextUtils.isEmpty(item.rating) && TextUtils.equals("0.0", item.rating) && TextUtils.equals("0", item.rating))
                        .setText(R.id.movie_details_movie_item_name_tv, item.title)
                        .getView(R.id.movie_details_movie_item_score_tv);
                scoreView.setScore(item.rating);
            }
        }
    }
}
