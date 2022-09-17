package com.mtime.bussiness.ticket.movie.details.adapter.binder;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsBasic;
import com.mtime.bussiness.ticket.movie.details.holder.MovieDetailsHolder;
import com.mtime.bussiness.common.widget.CommonItemTitleView;
import com.mtime.frame.BaseStatisticHelper;
import com.mtime.statistic.large.MapBuild;
import com.mtime.util.JumpUtil;


/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-06-03
 *
 * 影片详情-奖项
 */
public class MovieDetailsAwardBinder extends MovieDetailsBaseBinder<MovieDetailsBasic.Award> {
    public MovieDetailsAwardBinder(MovieDetailsHolder.OnJumpPageCallback callback, BaseStatisticHelper helper) {
        super(callback, helper);
    }

    @NonNull
    @Override
    protected BaseViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new BaseViewHolder(inflater.inflate(R.layout.layout_movie_details_awards, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull BaseViewHolder holder, @NonNull MovieDetailsBasic.Award item) {
        CommonItemTitleView titleView = holder.getView(R.id.movie_details_awards_title_view);
        if (item.totalWinAward > 0 && item.totalNominateAward > 0) {
            titleView.setTitleTextForHtml(R.string.movie_details_awards_title, item.totalWinAward, item.totalNominateAward);
        } else if (item.totalWinAward > 0) {
            titleView.setTitleTextForHtml(R.string.movie_details_awards_title2, item.totalWinAward);
        } else if (item.totalNominateAward > 0) {
            titleView.setTitleTextForHtml(R.string.movie_details_awards_title3, item.totalNominateAward);
        }

        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 埋点上报
                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
                String refer = mBaseStatisticHelper.assemble1(
                        "honor", null,
                        "", null,
                        null, null, mapBuild.build()).submit();

                // 跳转到奖项列表
                JumpUtil.startMovieHonorsActivity(item, v.getContext(), refer, String.valueOf(item.movieId));
            }
        });
    }
}
