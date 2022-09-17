package com.mtime.bussiness.ticket.movie.details.adapter.binder;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.kotlin.android.mtime.ktx.KtxMtimeKt;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.base.views.ForegroundImageView;
import com.mtime.bussiness.common.widget.CommonItemTitleView;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsArticle;
import com.mtime.bussiness.ticket.movie.details.holder.MovieDetailsHolder;
import com.mtime.frame.BaseStatisticHelper;
import com.mtime.statistic.large.MapBuild;
import com.mtime.util.JumpUtil;



/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-30
 *
 * 影片详情-公共的文章(时光对话/时光原创)
 */
public class MovieDetailsArticleBinder extends MovieDetailsBaseBinder<MovieDetailsArticle> {
    public MovieDetailsArticleBinder(MovieDetailsHolder.OnJumpPageCallback callback, BaseStatisticHelper helper) {
        super(callback, helper);
    }

    @NonNull
    @Override
    protected BaseViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new BaseViewHolder(inflater.inflate(R.layout.layout_movie_details_common_article, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull BaseViewHolder holder, @NonNull MovieDetailsArticle item) {
        CommonItemTitleView titleView = holder.getView(R.id.movie_details_common_article_title_view);
        titleView.setTitleText(item.titleResId);
        boolean isMtimeDialogue = item.titleResId == R.string.movie_details_mtime_dialogue_title;

        // 去掉"全部"跳转
//        titleView.setOnAllBtnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 埋点上报
//                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
//                String refer;
//
//                // 跳转到文章列表
//                if (isMtimeDialogue) {
//                    // 埋点上报
//                    refer = mBaseStatisticHelper.assemble1(
//                            "mtimeDia", null,
//                            "all", null,
//                            null, null, mapBuild.build()).submit();
//
//                    // 时光对话列表
//                    JumpUtil.startMovieOriginalListActivity(v.getContext(), refer,
//                            String.valueOf(item.movieId), MovieOriginalListActivity.NEWS_TYPE_CONVERSATION);
//                } else {
//                    // 埋点上报
//                    refer = mBaseStatisticHelper.assemble1(
//                            "mtimeOriginal", null,
//                            "all", null,
//                            null, null, mapBuild.build()).submit();
//
//                    // 时光原创列表
//                    JumpUtil.startMovieOriginalListActivity(v.getContext(), refer,
//                            String.valueOf(item.movieId), MovieOriginalListActivity.NEWS_TYPE_ORIGINAL);
//                }
//            }
//        });

        ForegroundImageView img = holder.getView(R.id.movie_details_common_article_img_iv);
        img.setForegroundResource(item.isImgVideo() ? R.drawable.common_icon_play_small : R.color.transparent);
        ImageHelper.with(ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                .override(MScreenUtils.dp2px(110), MScreenUtils.dp2px(70))
                .roundedCorners(4, 0)
                .placeholder(R.drawable.default_image)
                .load(item.getImgUrl())
                .view(img)
                .showload();
        holder.setText(R.id.movie_details_common_article_title_tv, item.title)
                .setText(R.id.movie_details_common_article_content_tv, item.bodyText)
                .setText(R.id.movie_details_common_article_time_tv, KtxMtimeKt.formatPublishTime(Long.parseLong(String.valueOf(item.publishTime))));
        holder.getView(R.id.movie_details_common_article_cl).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 埋点上报
                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam())
                        .put("articlesID", String.valueOf(item.relatedId));
                String refer;
                if (isMtimeDialogue) {
                    refer = mBaseStatisticHelper.assemble1(
                            "mtimeDia", null,
                            "showMtimeDia", null,
                            null, null, mapBuild.build()).submit();
                } else {
                    refer = mBaseStatisticHelper.assemble1(
                            "mtimeOriginal", null,
                            "showMtimeOriginal", null,
                            null, null, mapBuild.build()).submit();
                }

                // contentType 文章类型（改版后没了，api只返回-1）2021-1-22 by wwl
                //文章详情
                JumpUtil.startArticleActivity(v.getContext(), refer, String.valueOf(item.relatedId), null, null);

//                switch (item.contentType) {
//                    case HomeFeedItemBean.CONTENT_TYPE_RANK:
//                    case HomeFeedItemBean.CONTENT_TYPE_ARTICLES:
//                        //文章详情
//                        JumpUtil.startArticleActivity(v.getContext(), refer, item.relatedId + "", null, null);
//                        break;
//                    case HomeFeedItemBean.CONTENT_TYPE_VIDEO:
//                        //视频
//                        JumpUtil.startMediaVideoDetailActivity(v.getContext(), item.relatedId + "", item.videoId, item.videoSourceType, null, null, refer);
//                        break;
//                }

            }
        });
    }
}
