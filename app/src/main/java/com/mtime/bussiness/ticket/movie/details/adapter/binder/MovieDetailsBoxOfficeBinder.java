package com.mtime.bussiness.ticket.movie.details.adapter.binder;

import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsBoxOffice;
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
 * 影片详情-票房
 */
public class MovieDetailsBoxOfficeBinder extends MovieDetailsBaseBinder<MovieDetailsBoxOffice> {
    public MovieDetailsBoxOfficeBinder(MovieDetailsHolder.OnJumpPageCallback callback, BaseStatisticHelper helper) {
        super(callback, helper);
    }

    @NonNull
    @Override
    protected BaseViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new BaseViewHolder(inflater.inflate(R.layout.layout_movie_details_box_office, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull BaseViewHolder holder, @NonNull MovieDetailsBoxOffice item) {
        CommonItemTitleView titleView = holder.getView(R.id.movie_details_box_office_title_view);
        titleView.setOnAllBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 埋点上报
                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
                String refer = mBaseStatisticHelper.assemble1(
                        "ranking", null,
                        "globalbox", null,
                        null, null, mapBuild.build()).submit();

                JumpUtil.startFindTopGlobalActivity(v.getContext(), refer, 1);
            }
        });

        holder.getView(R.id.movie_details_box_office_content_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JumpUtil.startFindTopGlobalActivity(v.getContext(), null, 1);
            }
        });

        if (item.ranking > 0 && !TextUtils.isEmpty(item.todayBoxDes)) {
            holder.setVisible(R.id.movie_details_box_office_content_ll, true)
                    .setText(R.id.movie_details_box_office_num1_tv, item.ranking != -1 ? String.valueOf(item.ranking) : "--")
                    .setText(R.id.movie_details_box_office_num2_tv, item.todayBoxDes)
                    .setText(R.id.movie_details_box_office_sub_title2_tv, item.todayBoxDesUnit)
                    .setText(R.id.movie_details_box_office_num3_tv, item.totalBoxDes)
                    .setText(R.id.movie_details_box_office_sub_title3_tv, item.totalBoxUnit);
        } else {
            holder.setGone(R.id.movie_details_box_office_content_ll, true);
            titleView.setTitleTextForHtml(R.string.movie_details_box_office_title_total, item.totalBoxUnit, item.totalBoxDes);
        }
    }
}
