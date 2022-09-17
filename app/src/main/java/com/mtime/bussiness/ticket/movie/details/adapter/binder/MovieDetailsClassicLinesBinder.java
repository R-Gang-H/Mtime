package com.mtime.bussiness.ticket.movie.details.adapter.binder;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsDataBank;
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
 * 影片详情-经典台词
 */
public class MovieDetailsClassicLinesBinder extends MovieDetailsBaseBinder<MovieDetailsDataBank.ClassicLines> {
    public MovieDetailsClassicLinesBinder(MovieDetailsHolder.OnJumpPageCallback callback, BaseStatisticHelper helper) {
        super(callback, helper);
    }

    @NonNull
    @Override
    protected BaseViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new BaseViewHolder(inflater.inflate(R.layout.layout_movie_details_classics_lines, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull BaseViewHolder holder, @NonNull MovieDetailsDataBank.ClassicLines item) {
        CommonItemTitleView titleView = holder.getView(R.id.movie_details_classics_lines_title_view);
        titleView.setOnAllBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 埋点上报
                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
                String refer = mBaseStatisticHelper.assemble1(
                        "lines", null,
                        "all", null,
                        null, null, mapBuild.build()).submit();

                JumpUtil.startCommonWebActivity(v.getContext(), item.classicUrl, "quotes",
                        null, true, true, true, false, refer);
            }
        });

        holder.setText(R.id.movie_details_classics_lines_content_tv, item.classicLine);
        holder.getView(R.id.movie_details_classics_lines_content_tv).setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView textView = (TextView) v;
                    if (null != textView) {
                        int line = textView.getMaxLines();
                        if (line != 4) {
                            textView.setMaxLines(4);
                        } else {
                            textView.setMaxLines(Integer.MAX_VALUE);
                        }

                        // 埋点上报
                        MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
                        String refer = mBaseStatisticHelper.assemble1(
                                "lines", null,
                                "showLines", null,
                                null, null, mapBuild.build()).submit();
                    }
                }
            });
    }
}
