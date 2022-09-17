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
import com.mtime.beans.Photo;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsBasic;
import com.mtime.bussiness.ticket.movie.details.holder.MovieDetailsHolder;
import com.mtime.bussiness.common.widget.CommonItemTitleView;
import com.mtime.bussiness.ticket.stills.MovieStillsActivity;
import com.mtime.frame.BaseStatisticHelper;
import com.mtime.statistic.large.MapBuild;
import com.mtime.util.JumpUtil;

import java.util.ArrayList;
import java.util.List;



/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-30
 * <p>
 * 影片详情-剧照
 */
public class MovieDetailsPicturesBinder extends MovieDetailsBaseBinder<MovieDetailsBasic.StageImg> {
    public MovieDetailsPicturesBinder(MovieDetailsHolder.OnJumpPageCallback callback, BaseStatisticHelper helper) {
        super(callback, helper);
    }

    @NonNull
    @Override
    protected BaseViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new BaseViewHolder(inflater.inflate(R.layout.layout_movie_details_picture_list, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull BaseViewHolder holder, @NonNull MovieDetailsBasic.StageImg item) {
        int listSize = item.list.size();
        boolean showAll = listSize > 2;

        CommonItemTitleView titleView = holder.getView(R.id.movie_details_picture_list_title_view);
        titleView.setAllBtnViewVisibility(showAll);
        titleView.setOnAllBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 埋点上报
                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam())
                        .put("moreCount", String.valueOf(item.count));
                String refer = mBaseStatisticHelper.assemble1(
                        "multiMedia", null,
                        "stills", null,
                        "more", null, mapBuild.build()).submit();

                // 剧照列表
                JumpUtil.startPhotoListActivity(v.getContext(), refer, MovieStillsActivity.TARGET_TYPE_MOVIE, String.valueOf(item.movieId), item.name, null);
            }
        });

        // 手动增加一条全部
        if (listSize > 5 && item.list.get(listSize - 1).imgId != allId) {
            MovieDetailsBasic.StageImg.Img all = new MovieDetailsBasic.StageImg.Img();
            all.imgId = allId;
            item.list.add(all);
        }

        RecyclerView recyclerView = holder.getView(R.id.movie_details_picture_list_rv);
        if (null == recyclerView.getAdapter()) {
            recyclerView.setNestedScrollingEnabled(false);
            ListAdapter listAdapter = new ListAdapter(item.list);
            listAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    MovieDetailsBasic.StageImg.Img img1 = (MovieDetailsBasic.StageImg.Img) adapter.getItem(position);
                    if (null == img1)
                        return;

                    if (img1.imgId == allId) {
                        // 剧照列表
                        titleView.performAllBtnClick();
                        return;
                    }

                    // 埋点上报
                    MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam())
                            .put("posterID", String.valueOf(img1.imgId));
                    String refer = mBaseStatisticHelper.assemble1(
                            "multiMedia", null,
                            "stills", null,
                            "showStills", null, mapBuild.build()).submit();

                    // 剧照单独页面
                    ArrayList<Photo> photoList = new ArrayList<>();
                    for (MovieDetailsBasic.StageImg.Img img : item.list) {
                        Photo photo = new Photo();
                        photo.setId(img.imgId + "");
                        photo.setImage(img.imgUrl);
                        photo.setType(1);
                        photoList.add(photo);
                    }
                    JumpUtil.startPhotoDetailActivity(view.getContext(), MovieStillsActivity.TARGET_TYPE_MOVIE, photoList, position);
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
                                "stills", null,
                                "scroll", null, mapBuild.build()).submit();
                    }
                }
            });
        }
    }

    private class ListAdapter extends BaseMultiItemQuickAdapter<MovieDetailsBasic.StageImg.Img, BaseViewHolder> {

        public ListAdapter(@Nullable List<MovieDetailsBasic.StageImg.Img> data) {
            super(data);

            addItemType(0, R.layout.item_movie_details_picture);
            addItemType(1, R.layout.item_common_all);
        }

        @Override
        protected void convert(BaseViewHolder helper, MovieDetailsBasic.StageImg.Img item) {
            if (item.imgId != allId) {
                ImageView iv = helper.getView(R.id.item_movie_details_picture_img_iv);
                ImageHelper.with(ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                        .override(MScreenUtils.dp2px(108), MScreenUtils.dp2px(108))
                        .roundedCorners(4, 0)
                        .placeholder(R.drawable.default_image)
                        .load(item.imgUrl)
                        .view(iv)
                        .showload();
            }
        }
    }
}
