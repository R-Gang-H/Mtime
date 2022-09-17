package com.mtime.bussiness.ticket.movie.details.adapter.binder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.kotlin.android.app.data.constant.CommConstant;
import com.kotlin.android.app.data.entity.common.CommBizCodeResult;
import com.kotlin.android.mtime.ktx.KtxMtimeKt;
import com.kotlin.android.router.ext.ProviderExtKt;

import com.kotlin.android.app.router.provider.publish.IPublishProvider;
import com.kotlin.android.app.router.provider.review.IReviewProvider;
import com.kotlin.android.app.router.provider.ugc.IUgcProvider;
import com.mtime.R;
import com.kotlin.android.user.UserManager;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.StatisticEnum;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsHotReviewsBean;
import com.mtime.bussiness.ticket.movie.details.holder.MovieDetailsHolder;
import com.mtime.bussiness.video.api.PraiseCommentApi;
import com.mtime.constant.FrameConstant;
import com.mtime.frame.App;
import com.mtime.frame.BaseStatisticHelper;
import com.mtime.mtmovie.widgets.ScoreView;
import com.mtime.statistic.large.MapBuild;
import com.mtime.util.JumpUtil;
import com.mtime.util.MtimeUtils;

import java.util.List;

import static com.kotlin.android.app.data.annotation.AnnotationExtKt.CONTENT_TYPE_FILM_COMMENT;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-30
 *
 * 影片详情-短评
 * */
public class MovieDetailsShortReviewBinder extends MovieDetailsBaseBinder<MovieDetailsHotReviewsBean.ShortReviewList> {

    private static final float ITEM_PADDING_LEFT_RIGHT_DP = 30f;
    private static final float HEAD_WIDTH_DP = 24f;
    private static final float NICK_NAME_MARGIN_LEFT_DP = 5f;
    private static final float TIME_MARGIN_LEFT_DP = 5f;
    private static final float SCORE_MARGIN_LEFT_DP = 10f;

    private PraiseCommentApi mPraiseCommentApi = new PraiseCommentApi();

    public MovieDetailsShortReviewBinder(MovieDetailsHolder.OnJumpPageCallback callback, BaseStatisticHelper helper) {
        super(callback, helper);
    }

    @NonNull
    @Override
    protected BaseViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new BaseViewHolder(inflater.inflate(R.layout.layout_movie_details_short_review_list, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull BaseViewHolder holder, @NonNull MovieDetailsHotReviewsBean.ShortReviewList item) {
        holder.setText(R.id.movie_details_short_review_list_all_tv, App.get().getString(R.string.movie_details_short_review_all, item.total));
        holder.getView(R.id.movie_details_short_review_list_all_tv).setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 埋点上报
                        MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
                        String refer = mBaseStatisticHelper.assemble1(
                                "shortReviews", null,
                                "all", null,
                                "", null, mapBuild.build()).submit();

                        // 短评列表
                        mOnJumpPageCallback.onJumpPageCallback(MovieDetailsHolder.PAGE_REVIEW);
//                        JumpUtil.startMovieShortCommentActivity(v.getContext(), refer,
//                                String.valueOf(item.movieId), item.movieName,
//                                null, null, item.total);
                        IReviewProvider provider = ProviderExtKt.getProvider(IReviewProvider.class);
                        provider.startMovieShortCommentList(String.valueOf(item.movieId), item.movieName);
                    }
                });

        holder.getView(R.id.movie_details_short_review_list_btn_short_review_iv).setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 写影评
                        if (!UserManager.Companion.getInstance().isLogin()) {
                            JumpUtil.startLoginActivity(v.getContext(), null);
                            return;
                        }

                        // 埋点上报
                        MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
                        String refer = mBaseStatisticHelper.assemble1(
                                "shortReviews", null,
                                "writeShortReview", null,
                                "", null, mapBuild.build()).submit();

                        // 跳转到写影评
                        mOnJumpPageCallback.onJumpPageCallback(MovieDetailsHolder.PAGE_REVIEW);
                        IPublishProvider provider = ProviderExtKt.getProvider(IPublishProvider.class);
                        if (null != provider) {
                            provider.startEditorActivity(
                                    CONTENT_TYPE_FILM_COMMENT,
                                    null,
                                    null,
                                    item.movieId,
                                    item.movieName,
                                    null,
                                    null,
                                    false,
                                    false
                            );
                        }
                    }
                });

        RecyclerView recyclerView = holder.getView(R.id.movie_details_short_review_list_rv);
        if (null == recyclerView.getAdapter()) {
            recyclerView.setNestedScrollingEnabled(false);
            ListAdapter listAdapter = new ListAdapter(item.list);
            listAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    MovieDetailsHotReviewsBean.ShortReview review = listAdapter.getItem(position);
                    if (null != review) {
                        // 埋点上报
                        MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam())
                                .put("shortReviewID", String.valueOf(review.commentId));
                        String refer = mBaseStatisticHelper.assemble1(
                                "shortReviews", null,
                                "showShortReviews", null,
                                "content", null, mapBuild.build()).submit();

                        // 短评详情页
                        mOnJumpPageCallback.onJumpPageCallback(MovieDetailsHolder.PAGE_REVIEW);
//                        JumpUtil.startTwitterActivity(view.getContext(), refer, review.commentId,
//                                TwitterActivity.TWITTERTYPE_MOIVE, "短影评", 0);

                        IUgcProvider ugcProvider = ProviderExtKt.getProvider(IUgcProvider.class);
                        if (ugcProvider != null) {
                            ugcProvider.launchDetail(review.commentId, CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT,0L,false);
                        }
                    }
                }
            });
            listAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    switch (view.getId()) {
                        case R.id.item_movie_details_short_review_thumb_up_tv:
                            // 点赞
                            if (!UserManager.Companion.getInstance().isLogin()) {
                                JumpUtil.startLoginActivity(view.getContext(), null);
                                return;
                            }

                            MovieDetailsHotReviewsBean.ShortReview review = listAdapter.getItem(position);
                            if (null != review) {
                                // 埋点上报
                                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam())
                                        .put("shortReviewID", String.valueOf(review.commentId))
                                        .put("thumbsUpState", review.isPraise ? StatisticEnum.EnumThumbsUpState.CANCEL.getValue() : StatisticEnum.EnumThumbsUpState.THUMBS_UP.getValue())
                                        .put("thumbsUpCount", String.valueOf(review.praiseCount));
                                String refer = mBaseStatisticHelper.assemble1(
                                        "shortReviews", null,
                                        "showShortReviews", null,
                                        "thumbsUp", null, mapBuild.build()).submit();

                                // 点赞or取消赞
                                view.setClickable(false);
                                final boolean isCancel = review.isPraise;
                                mPraiseCommentApi.postEditPraise("",
                                        String.valueOf(review.commentId),
                                        String.valueOf(CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT),
                                        isCancel,
                                        new NetworkManager.NetworkListener<CommBizCodeResult>() {
                                    @Override
                                    public void onSuccess(CommBizCodeResult result, String showMsg) {
                                        review.isPraise = !review.isPraise ;
                                        review.praiseCount = isCancel ? (review.praiseCount - 1) : (review.praiseCount + 1);
                                        adapter.notifyItemChanged(position);
                                        view.setClickable(true);
                                    }
                                    @Override
                                    public void onFailure(NetworkException<CommBizCodeResult> exception, String showMsg) {
                                        MToastUtils.showLongToast(R.string.st_actor_info_support_failed);
                                        view.setClickable(true);
                                    }
                                });
                            }
                            break;
                        default:
                            break;
                    }
                }
            });
            recyclerView.setAdapter(listAdapter);
        } else {
            ListAdapter listAdapter = (ListAdapter) recyclerView.getAdapter();
            listAdapter.setNewData(item.list);
            listAdapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends BaseQuickAdapter<MovieDetailsHotReviewsBean.ShortReview, BaseViewHolder> {

        public ListAdapter(@Nullable List<MovieDetailsHotReviewsBean.ShortReview> data) {
            super(R.layout.item_movie_details_short_review, data);
            addChildClickViewIds(R.id.item_movie_details_short_review_thumb_up_tv);
        }

        @Override
        protected void convert(BaseViewHolder helper, MovieDetailsHotReviewsBean.ShortReview item) {
            ImageView iv = helper.getView(R.id.item_movie_details_short_review_profile_iv);
            ImageHelper.with(ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                    .override(MScreenUtils.dp2px(24), MScreenUtils.dp2px(24))
                    .cropCircle()
                    .placeholder(R.drawable.head_h54)
                    .load(item.headImg)
                    .view(iv)
                    .showload();

            ScoreView scoreView = helper.getView(R.id.item_movie_details_short_review_score_tv);
            View thumb = helper.getView(R.id.item_movie_details_short_review_thumb_up_tv);
            thumb.setSelected(item.isPraise);
            scoreView.setScore(item.rating);
            helper.setText(R.id.item_movie_details_short_review_name_tv, item.nickname)
                    .setText(R.id.item_movie_details_short_review_time_tv, "· " + KtxMtimeKt.formatPublishTime(Long.parseLong(String.valueOf(item.commentDate))))
                    .setText(R.id.item_movie_details_short_review_content_tv, item.content)
                    .setText(R.id.item_movie_details_short_review_thumb_up_tv, MtimeUtils.formatCount(item.praiseCount))
                    .setText(R.id.item_movie_details_short_review_reply_tv, MtimeUtils.formatCount(item.replyCount));

            TextView nicknameTv = helper.findView(R.id.item_movie_details_short_review_name_tv);
            TextView timeTv = helper.findView(R.id.item_movie_details_short_review_time_tv);
            TextView scoreTv = helper.findView(R.id.item_movie_details_short_review_score_tv);
            int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            // 发布时间宽  5dp为左间距
            timeTv.measure(spec, spec);
            int timeWidth = timeTv.getMeasuredWidth() + MScreenUtils.dp2px(TIME_MARGIN_LEFT_DP);
            // 评分宽  10dp评分左间距
            scoreTv.measure(spec, spec);
            int scoreWidth = scoreTv.getMeasuredWidth() + MScreenUtils.dp2px(SCORE_MARGIN_LEFT_DP);
            // 昵称最大宽度  30dp左右间距总和 24dp头像 5dp昵称左间距
            int nickNameMaxWidth = FrameConstant.SCREEN_WIDTH
                    - MScreenUtils.dp2px(ITEM_PADDING_LEFT_RIGHT_DP + HEAD_WIDTH_DP + NICK_NAME_MARGIN_LEFT_DP)
                    - timeWidth - scoreWidth;
            nicknameTv.setMaxWidth(nickNameMaxWidth);
        }
    }

}
