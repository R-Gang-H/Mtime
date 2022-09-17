//package com.mtime.bussiness.ticket.movie.details.adapter.binder;
//
//import androidx.annotation.NonNull;
//import androidx.viewpager.widget.ViewPager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.chad.library.adapter.base.viewholder.BaseViewHolder;
//import com.mtime.R;
//import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsHotTopicsBean;
//import com.mtime.bussiness.ticket.movie.details.holder.MovieDetailsHolder;
//import com.mtime.bussiness.common.widget.CommonItemTitleView;
//import com.mtime.bussiness.community.topic.activity.TopicListActivity;
//import com.mtime.bussiness.community.topic.bean.HotTopicBean;
//import com.mtime.bussiness.community.topic.widget.HotTopicScrollView;
//import com.mtime.frame.BaseStatisticHelper;
//import com.mtime.statistic.large.MapBuild;
//import com.mtime.util.JumpUtil;
//
//
///**
// * @author ZhouSuQiang
// * @email zhousuqiang@126.com
// * @date 2019-05-31
// *
// * 影片详情-热门话题
// */
//public class MovieDetailsHotTopicsBinder extends MovieDetailsBaseBinder<MovieDetailsHotTopicsBean> implements ViewPager.OnPageChangeListener {
//    public MovieDetailsHotTopicsBinder(MovieDetailsHolder.OnJumpPageCallback callback, BaseStatisticHelper helper) {
//        super(callback, helper);
//    }
//
//    @NonNull
//    @Override
//    protected BaseViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
//        return new BaseViewHolder(inflater.inflate(R.layout.layout_movie_details_hot_topic, parent, false));
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull BaseViewHolder holder, @NonNull MovieDetailsHotTopicsBean item) {
//        CommonItemTitleView titleView = holder.getView(R.id.movie_details_hot_topic_title_view);
//        titleView.setAllBtnViewVisibility(item.countCount >= 2);
//        titleView.setOnAllBtnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 埋点上报
//                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
//                String refer = mBaseStatisticHelper.assemble1(
//                        "topic", null,
//                        "all", null,
//                        null, null, mapBuild.build()).submit();
//
//                // 跳转到话题列表页面
//                JumpUtil.startTopicListActivity(v.getContext(), refer, TopicListActivity.RELATED_OBJ_ID_MOVIE,
//                        String.valueOf(item.movieId), item.movieName);
//            }
//        });
//
//        HotTopicScrollView topicScrollView = holder.getView(R.id.movie_details_hot_topic_banner_view);
//        topicScrollView.setDatas(item.list);
//        topicScrollView.setOnHotTopicItemListener(new HotTopicScrollView.OnHotTopicItemListener() {
//            @Override
//            public void onHotTopicItemClick(int position, HotTopicBean itemBean) {
//                // 埋点上报
//                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam())
//                        .put("topicID", String.valueOf(itemBean.topicId))
//                        .put("topicType", String.valueOf(itemBean.topicType));
//                String refer = mBaseStatisticHelper.assemble1(
//                        "topic", null,
//                        "showTopic", null,
//                        null, null, mapBuild.build()).submit();
//
//                // 话题详情
//                JumpUtil.startTopicDetail(topicScrollView.getContext(), itemBean.topicId, itemBean.title, itemBean.topicType, refer);
//            }
//
//            @Override
//            public void onClickOpinionBtn(View view, int position, long topicId, int opId) {
//                // 点击PK按钮 回调只处理埋点上报
//            }
//        });
//        topicScrollView.getViewPager().addOnPageChangeListener(this);
//    }
//
//    @Override
//    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//    }
//
//    @Override
//    public void onPageSelected(int position) {
//        // 埋点上报
//        MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
//        String refer = mBaseStatisticHelper.assemble1(
//                "topic", null,
//                "scroll", null,
//                null, null, mapBuild.build()).submit();
//    }
//
//    @Override
//    public void onPageScrollStateChanged(int state) {
//
//    }
//}
