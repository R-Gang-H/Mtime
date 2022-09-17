package com.mtime.bussiness.ticket.movie.details.adapter.binder;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsIntro;
import com.mtime.bussiness.ticket.movie.details.holder.MovieDetailsHolder;
import com.mtime.bussiness.ticket.movie.details.widget.MovieDetailsExpandableTextView;
import com.mtime.frame.BaseStatisticHelper;
import com.mtime.statistic.large.MapBuild;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-29
 *
 * 影片详情-简介部分
 */
public class MovieDetailsIntroBinder extends MovieDetailsBaseBinder<MovieDetailsIntro> implements MovieDetailsExpandableTextView.OnToggleExpansionStatusListener {

    public MovieDetailsIntroBinder(MovieDetailsHolder.OnJumpPageCallback callback, BaseStatisticHelper helper) {
        super(callback, helper);
    }

    @NonNull
    @Override
    protected BaseViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new BaseViewHolder(inflater.inflate(R.layout.layout_movie_details_intro, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull BaseViewHolder holder, @NonNull MovieDetailsIntro item) {
        MovieDetailsExpandableTextView textView = holder.getView(R.id.layout_movie_details_intro_expandable_tv);
        textView.setMaxLine(3);
        textView.setText(item.story);
        textView.setOnToggleExpansionStatusListener(this);
    }

    @Override
    public void onToggleExpansionStatusChanged(boolean isExpansion) {
        if (isExpansion) {
            // 埋点上报
            MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
            mBaseStatisticHelper.assemble1(
                    "plotSummary", null,
                    null, null,
                    null, null, mapBuild.build()).submit();
        }
    }
}
