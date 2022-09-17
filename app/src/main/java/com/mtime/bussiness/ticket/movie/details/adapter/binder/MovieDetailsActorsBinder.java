package com.mtime.bussiness.ticket.movie.details.adapter.binder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
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
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsActors;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsBasic;
import com.mtime.bussiness.ticket.movie.details.holder.MovieDetailsHolder;
import com.mtime.bussiness.common.widget.CommonItemTitleView;
import com.mtime.frame.BaseStatisticHelper;
import com.mtime.statistic.large.MapBuild;
import com.mtime.util.JumpUtil;

import java.util.List;



/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-30
 *
 * 影片详情-演职人员
 */
public class MovieDetailsActorsBinder extends MovieDetailsBaseBinder<MovieDetailsActors> {

    public MovieDetailsActorsBinder(MovieDetailsHolder.OnJumpPageCallback callback, BaseStatisticHelper helper) {
        super(callback, helper);
    }

    @NonNull
    @Override
    protected BaseViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new BaseViewHolder(inflater.inflate(R.layout.layout_movie_details_actor_list, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull BaseViewHolder holder, @NonNull MovieDetailsActors item) {
        int listSize = item.getDatas().size();
        boolean showAll = listSize > 2;

        CommonItemTitleView titleView = holder.getView(R.id.movie_details_actor_list_title_view);
        titleView.setAllBtnViewVisibility(showAll);
        titleView.setOnAllBtnClickListener(v -> {
            // 埋点上报
            MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam())
                    .put("moreCount", String.valueOf(item.getCount()));
            String refer = mBaseStatisticHelper.assemble1(
                    "screenCredits", null,
                    "more", null,
                    null, null, mapBuild.build()).submit();

            // 跳转到演职人员列表
            JumpUtil.startActorListActivity(v.getContext(), refer, item.movieId + "");
        });

        // 手动增加一条全部
        if (listSize > 5 && item.getDatas().get(listSize - 1).actorId != allId) {
            MovieDetailsBasic.Actor all = new MovieDetailsBasic.Actor();
            all.actorId = allId;
            item.getDatas().add(all);
        }

        RecyclerView recyclerView = holder.getView(R.id.movie_details_actor_list_rv);
        if (null == recyclerView.getAdapter()) {
            recyclerView.setNestedScrollingEnabled(false);
            ListAdapter listAdapter = new ListAdapter(item.getDatas());
            listAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    MovieDetailsBasic.Actor actor = listAdapter.getItem(position);
                    if (null != actor) {
                        if (actor.actorId != allId) {
                            // 埋点上报
                            MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam())
                                    .put("personID", String.valueOf(actor.actorId));
                            String refer = mBaseStatisticHelper.assemble1(
                                    "screenCredits", null,
                                    "showScreenCredits", null,
                                    null, null, mapBuild.build()).submit();

                            // 跳转到演职人员详情
                            JumpUtil.startActorDetail(view.getContext(), refer, actor.actorId + "");
                        } else {
                            // 跳转到演职人员列表
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
                                "screenCredits", null,
                                "scroll", null,
                                null, null, mapBuild.build()).submit();
                    }
                }
            });
        }
    }

    private class ListAdapter extends BaseMultiItemQuickAdapter<MovieDetailsBasic.Actor, BaseViewHolder> {

        public ListAdapter(@Nullable List<MovieDetailsBasic.Actor> data) {
            super(data);
            addItemType(0, R.layout.item_movie_details_actor);
            addItemType(1, R.layout.item_common_all);
        }

        @Override
        protected void convert(BaseViewHolder helper, MovieDetailsBasic.Actor item) {
            if (item.actorId != allId) {
                ImageView img = helper.getView(R.id.item_movie_details_actor_poster_iv);
                ImageHelper.with(ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                        .override(MScreenUtils.dp2px(80), MScreenUtils.dp2px(120))
                        .roundedCorners(4, 0)
                        .load(item.img)
                        .view(img)
                        .placeholder(R.drawable.default_image)
                        .showload();
                helper.setText(R.id.item_movie_details_actor_name_tv, item.name)
                        .setText(R.id.item_movie_details_actor_en_name_tv, item.nameEn)
                        .setText(R.id.item_movie_details_actor_role_tv, (item.isDirector || TextUtils.isEmpty(item.roleName)) ? item.roleName : "饰 " + item.roleName);
            }
        }
    }
}
