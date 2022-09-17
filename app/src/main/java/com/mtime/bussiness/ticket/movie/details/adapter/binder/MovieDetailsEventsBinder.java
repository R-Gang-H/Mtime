package com.mtime.bussiness.ticket.movie.details.adapter.binder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsEvents;
import com.mtime.bussiness.ticket.movie.details.holder.MovieDetailsHolder;
import com.mtime.bussiness.common.widget.CommonItemTitleView;
import com.mtime.frame.BaseStatisticHelper;
import com.mtime.statistic.large.MapBuild;
import com.mtime.util.JumpUtil;

import java.util.List;


/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-31
 *
 * 影片详情-你该了解的X件事(幕后揭秘)
 */
public class MovieDetailsEventsBinder extends MovieDetailsBaseBinder<MovieDetailsEvents> {
    public MovieDetailsEventsBinder(MovieDetailsHolder.OnJumpPageCallback callback, BaseStatisticHelper helper) {
        super(callback, helper);
    }

    @NonNull
    @Override
    protected BaseViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new BaseViewHolder(inflater.inflate(R.layout.layout_movie_details_events_list, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull BaseViewHolder holder, @NonNull MovieDetailsEvents item) {
        CommonItemTitleView titleView = holder.getView(R.id.movie_details_events_list_title_view);
        titleView.setTitleTextForHtml(R.string.movie_details_events_title, item.eventCount);
        titleView.setAllBtnViewVisibility(item.eventCount > 2);
        titleView.setOnAllBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 埋点上报
                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
                String refer = mBaseStatisticHelper.assemble1(
                        "anecdote", null,
                        "all", null,
                        null, null, mapBuild.build()).submit();

                //跳转到幕后解密
                JumpUtil.startMovieSecretActivity(v.getContext(), refer, String.valueOf(item.movieId));
            }
        });

        RecyclerView recyclerView = holder.getView(R.id.movie_details_events_list_rv);
        if (null == recyclerView.getAdapter()) {
            recyclerView.setNestedScrollingEnabled(false);
            ListAdapter listAdapter = new ListAdapter(item.list);
            listAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    TextView textView = view.findViewById(R.id.item_movie_details_event_content_tv);
                    if (null != textView) {
                        int line = textView.getMaxLines();
                        if (line != 3) {
                            textView.setMaxLines(3);
                        } else {
                            textView.setMaxLines(Integer.MAX_VALUE);
                        }

                        // 埋点上报
                        MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
                        String refer = mBaseStatisticHelper.assemble1(
                                "anecdote", null,
                                "showAnecdote", null,
                                null, null, mapBuild.build()).submit();
                    }
                }
            });
            recyclerView.setAdapter(listAdapter);
        }
    }

    private class ListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public ListAdapter(@Nullable List<String> data) {
            super(R.layout.item_movie_details_event, data);
        }

        @Override
        public int getItemCount() {
            return super.getItemCount() > 2 ? 2 : super.getItemCount();
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.item_movie_details_event_num_tv, (helper.getAdapterPosition() + 1) + ".")
                    .setText(R.id.item_movie_details_event_content_tv, item);
        }
    }
}
