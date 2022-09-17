package com.mtime.bussiness.ticket.movie.details.adapter.binder;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsDataBank;
import com.mtime.bussiness.ticket.movie.details.holder.MovieDetailsHolder;
import com.mtime.frame.BaseStatisticHelper;
import com.mtime.statistic.large.MapBuild;
import com.mtime.util.JumpUtil;


/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-06-03
 *
 * 影片详情-扩展资料
 */
public class MovieDetailsExtendInfoBinder extends MovieDetailsBaseBinder<MovieDetailsDataBank> {
    public MovieDetailsExtendInfoBinder(MovieDetailsHolder.OnJumpPageCallback callback, BaseStatisticHelper helper) {
        super(callback, helper);
    }

    @NonNull
    @Override
    protected BaseViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new BaseViewHolder(inflater.inflate(R.layout.layout_movie_details_extend_info, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull BaseViewHolder holder, @NonNull MovieDetailsDataBank item) {
        holder.setGone(R.id.movie_details_extend_media_comment_fl, true)  // !item.isMediaReview
                .setText(R.id.movie_details_extend_media_comment_all_tv, holder.itemView.getResources().getString(R.string.movie_details_extend_media_comment_all, item.mediaReviewCount))
                .setGone(R.id.movie_details_extend_behind_make_fl, !item.isBehind)
                .setGone(R.id.movie_details_extend_make_release_fl, !item.isCompany)
                .setText(R.id.movie_details_extend_make_release_all_tv, holder.itemView.getResources().getString(R.string.movie_details_extend_make_release_all, item.companyCount))
                .setGone(R.id.movie_details_extend_more_info_fl, !item.isCompany);

        holder.getView(R.id.movie_details_extend_media_comment_fl).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 埋点上报
                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
                String refer = mBaseStatisticHelper.assemble1(
                        "otherInformation", null,
                        "mediaEvaluation", null,
                        null, null, mapBuild.build()).submit();

                //媒体评论列表
                JumpUtil.startMediaReviewActivity(v.getContext(), refer, String.valueOf(item.movieId));
            }
        });
        holder.getView(R.id.movie_details_extend_behind_make_fl).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 埋点上报
                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
                String refer = mBaseStatisticHelper.assemble1(
                        "otherInformation", null,
                        "behind", null,
                        null, null, mapBuild.build()).submit();

                //幕后制作
                JumpUtil.startCommonWebActivity(
                        v.getContext(), item.behindUrl, "behindStory",
                        null, true, true,
                        true, false, refer);
            }
        });
        holder.getView(R.id.movie_details_extend_make_release_fl).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 埋点上报
                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
                String refer = mBaseStatisticHelper.assemble1(
                        "otherInformation", null,
                        "productionRelease", null,
                        null, null, mapBuild.build()).submit();

                //制作发行
                JumpUtil.startProducerListActivity(v.getContext(), refer, String.valueOf(item.movieId));
            }
        });
        holder.getView(R.id.movie_details_extend_more_info_fl).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 埋点上报
                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
                String refer = mBaseStatisticHelper.assemble1(
                        "otherInformation", null,
                        "more", null,
                        null, null, mapBuild.build()).submit();

                //更多资料
                JumpUtil.startMovieMoreInfoActivity(v.getContext(), refer, String.valueOf(item.movieId));
            }
        });
    }
}
