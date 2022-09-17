package com.mtime.bussiness.ticket.movie.details.adapter.binder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.common.utils.VideoUtils;
import com.mtime.bussiness.ticket.movie.details.holder.MovieDetailsHolder;
import com.mtime.bussiness.common.widget.CommonItemTitleView;
import com.mtime.bussiness.video.bean.CategoryVideosBean;
import com.mtime.frame.BaseStatisticHelper;
import com.mtime.statistic.large.MapBuild;
import com.mtime.util.JumpUtil;

import java.util.List;



/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-30
 *
 * 影片详情-预告片列表（数据来原于视频联播页的推荐接口）
 */
public class MovieDetailsTrailersBinder extends MovieDetailsBaseBinder<CategoryVideosBean> {
    private long mMovieId;

    public MovieDetailsTrailersBinder(MovieDetailsHolder.OnJumpPageCallback callback, BaseStatisticHelper helper) {
        super(callback, helper);
    }

    public void setMovieId(long movieId) {
        mMovieId = movieId;
    }

    @NonNull
    @Override
    protected BaseViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new BaseViewHolder(inflater.inflate(R.layout.layout_movie_details_trailer_list, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull BaseViewHolder holder, @NonNull CategoryVideosBean item) {
        int listSize = item.getVideoList().size();
        boolean showAll = listSize > 1;
        CommonItemTitleView titleView = holder.getView(R.id.movie_details_trailer_list_title_view);
        titleView.setAllBtnViewVisibility(showAll);
        titleView.setOnAllBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 埋点上报
                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam())
                        .put("moreCount", String.valueOf(item.getListCount()));
                String refer = mBaseStatisticHelper.assemble1(
                        "multiMedia", null,
                        "video", null,
                        "more", null, mapBuild.build()).submit();

                // 全部 视频连播页
                JumpUtil.startVideoListActivity(v.getContext(), refer, mMovieId + "");
            }
        });

        // 手动增加一条全部
        if (listSize > 5 && item.getVideoList().get(listSize - 1).getvId() != allId) {
            CategoryVideosBean.RecommendVideoItem all = new CategoryVideosBean.RecommendVideoItem();
            all.setvId(allId);
            item.getVideoList().add(all);
        }

        RecyclerView recyclerView = holder.getView(R.id.movie_details_trailer_list_rv);
        if (null == recyclerView.getAdapter()) {
            recyclerView.setNestedScrollingEnabled(false);
            ListAdapter listAdapter = new ListAdapter(item.getVideoList());
            listAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    CategoryVideosBean.RecommendVideoItem video = listAdapter.getItem(position);
                    if (null != video) {
                        if (video.getvId() != allId) {
                            // 埋点上报
                            MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam())
                                    .put("vID", String.valueOf(video.getvId()));
                            String refer = mBaseStatisticHelper.assemble1(
                                    "multiMedia", null,
                                    "video", null,
                                    "showVideo", null, mapBuild.build()).submit();

                            // 半屏播放页
                            JumpUtil.startPrevueVideoPlayerActivity(
                                    view.getContext(), String.valueOf(video.getvId()),
                                    video.getVideoSource(), false, refer);
                        } else {
                            // 全部 视频连播页
                            titleView.performAllBtnClick();
                        }
                    }
                }
            });
            recyclerView.setAdapter(listAdapter);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        // 埋点上报
                        MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
                        mBaseStatisticHelper.assemble1(
                                "multiMedia", null,
                                "video", null,
                                "scroll", null, mapBuild.build()).submit();
                    }
                }
            });
        }
    }

    private class ListAdapter extends BaseMultiItemQuickAdapter<CategoryVideosBean.RecommendVideoItem, BaseViewHolder> {

        public ListAdapter(@Nullable List<CategoryVideosBean.RecommendVideoItem> data) {
            super(data);

            addItemType(0, R.layout.item_movie_details_trailer);
            addItemType(1, R.layout.item_common_all);
        }

        @Override
        protected void convert(BaseViewHolder helper, CategoryVideosBean.RecommendVideoItem item) {
            if (item.getvId() != allId) {
                ImageView iv = helper.getView(R.id.item_movie_details_trailer_img_iv);
                ImageHelper.with(ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                        .override(MScreenUtils.dp2px(167), MScreenUtils.dp2px(108))
                        .roundedCorners(4, 0)
                        .placeholder(R.drawable.default_image)
                        .load(item.getImage())
                        .view(iv)
                        .showload();
                helper.setText(R.id.item_movie_details_trailer_name_tv, item.getTitle())
                        .setText(R.id.item_movie_details_trailer_time_tv, VideoUtils.getTime(VideoUtils.FORMAT_TYPE_02, item.getLength()));
            }
        }
    }
}
