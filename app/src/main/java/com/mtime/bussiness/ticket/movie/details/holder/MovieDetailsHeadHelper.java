package com.mtime.bussiness.ticket.movie.details.holder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kotlin.android.image.coil.ext.CoilCompat;
import com.kotlin.android.ktx.ext.dimension.DimensionExtKt;
import com.kotlin.android.router.ext.ProviderExtKt;

import com.kotlin.android.app.router.provider.publish.IPublishProvider;
import com.kotlin.android.app.router.provider.review.IReviewProvider;
import com.mtime.R;
import com.kotlin.android.user.UserManager;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.StatisticEnum;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.base.views.ForegroundImageView;
import com.mtime.bussiness.common.bean.CommResultBean;
import com.mtime.bussiness.common.bean.MovieLatestReviewBean;
import com.mtime.bussiness.ticket.movie.details.adapter.MovieDetailsSubScoreItemAdapter;
import com.mtime.bussiness.ticket.movie.details.api.MovieDetailsApi;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsBasic;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsOnlinePlay;
import com.mtime.bussiness.common.bean.MovieWantSeenResultBean;
import com.mtime.bussiness.ticket.movie.fragment.TicketMoviesInComingFragment;
import com.mtime.event.EventManager;
import com.mtime.frame.BaseStatisticHelper;
import com.mtime.mtmovie.widgets.CircleImageView;
import com.mtime.mtmovie.widgets.PosterFilterView;
import com.mtime.mtmovie.widgets.ScoreView;
import com.mtime.statistic.large.MapBuild;
import com.mtime.util.JumpUtil;
import com.mtime.util.MtimeUtils;
import com.mtime.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


import static com.kotlin.android.app.data.annotation.AnnotationExtKt.CONTENT_TYPE_FILM_COMMENT;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-21
 * <p>
 * ????????????-??????UI???????????????????????????
 */
public class MovieDetailsHeadHelper {

    // ?????????????????????
    @BindView(R.id.activity_movie_details_head_poster_bg_iv)
    ForegroundImageView mBgIv;
    @BindView(R.id.activity_movie_details_head_poster_iv)
    PosterFilterView mPosterIv;
    @BindViews({R.id.activity_movie_details_head_screen_type1_tv,
            R.id.activity_movie_details_head_screen_type2_tv,
            R.id.activity_movie_details_head_screen_type3_tv})
    List<TextView> mScreenTypeTvs;

    // ????????????
    @BindView(R.id.activity_movie_details_head_name_tv)
    TextView mNameTv;
    @BindView(R.id.activity_movie_details_head_en_name_tv)
    TextView mEnNameTv;
    @BindView(R.id.activity_movie_details_head_types_tv)
    TextView mTypesTv;
    @BindView(R.id.activity_movie_details_head_quote_ll)
    View mQueteLayout;
    @BindView(R.id.activity_movie_details_head_quote_tv)
    TextView mQueteTv;
    @BindView(R.id.activity_movie_details_head_easter_ll)
    View mEasterLayout;
    @BindView(R.id.activity_movie_details_head_easter_tv)
    TextView mEasterTv;

    // ???????????????
    @BindView(R.id.activity_movie_details_head_score_tv)
    ScoreView mScoreTv;
    @BindView(R.id.activity_movie_details_head_score_title_tv)
    TextView mScoreTitleTv;
    @BindView(R.id.activity_movie_details_head_no_score_tv)
    TextView mNoScoreTv;
    @BindView(R.id.activity_movie_details_head_no_sub_score_ll)
    ViewGroup mNoSubScoreLayout;
    @BindView(R.id.activity_movie_details_head_no_sub_score_wantsee_num_tv)
    TextView mNoSubScoreWantSeeNumTv;
    @BindView(R.id.activity_movie_details_head_sub_score_cl)
    ViewGroup mSubScoreLayout;
    @BindView(R.id.activity_movie_details_head_sub_score_pingfen_num_tv)
    TextView mSubScorePingfenNumTv;
    @BindView(R.id.activity_movie_details_head_sub_score_wantsee_num_tv)
    TextView mSubScoreWantSeeNumTv;
    @BindView(R.id.activity_movie_details_head_sub_score_list_rv)
    RecyclerView mSubScoreListRv;
    @BindView(R.id.activity_movie_details_head_sub_score_empty_tv)
    View mSubScoreEmptyTv;
    @BindView(R.id.activity_movie_details_head_sub_score_arrow_iv)
    ImageView mSubScoreArrowIv;
    @BindView(R.id.activity_movie_details_head_sub_score_line_view)
    View mSubScoreLineView;


    // ??????????????? ??????
    @BindView(R.id.activity_movie_details_head_want_btn_fl)
    View mWantBtnView;
    @BindView(R.id.activity_movie_details_head_seen_btn_fl)
    View mSeenBtnView;

    // ??????????????????
    @BindView(R.id.activity_movie_details_head_seen_score_review_cl)
    View mSeenScoreReviewLayout;
    @BindView(R.id.activity_movie_details_head_seen_score_review_profile_iv)
    ImageView mSeenScoreReviewProfileIv;
    @BindView(R.id.activity_movie_details_head_seen_score_review_name_tv)
    TextView mSeenScoreReviewUserNameTv;
    @BindView(R.id.activity_movie_details_head_seen_score_review_score_tv)
    ScoreView mSeenScoreReviewScoreTv;
    @BindView(R.id.activity_movie_details_head_seen_score_review_share_iv)
    ImageView mSeenScoreReviewShareIv;
    @BindView(R.id.activity_movie_details_head_seen_score_review_arrow_iv)
    ImageView mSeenScoreReviewArrowIv;

    // ????????????
    @BindView(R.id.activity_movie_details_head_online_play_line_view)
    View mOnlinePlayLineView;
    @BindView(R.id.activity_movie_details_head_online_play_fl)
    FrameLayout mOnlinePlayLayout;
    @BindView(R.id.activity_movie_details_head_online_play_icons_fl)
    FrameLayout mOnlinePlayIconsLayout;

    private final MovieDetailsApi mApi = new MovieDetailsApi();
    private final Unbinder mUnbinder;
    private MovieDetailsBasic mBasic;
    private MovieLatestReviewBean mLatestReview;

    private final MovieDetailsHolder.OnJumpPageCallback mOnJumpPageCallback;
    private final BaseStatisticHelper mBaseStatisticHelper;

    private final IReviewProvider mReviewProvider = ProviderExtKt.getProvider(IReviewProvider.class);

    MovieDetailsHeadHelper(View headView, MovieDetailsHolder.OnJumpPageCallback callback, BaseStatisticHelper helper) {
        mUnbinder = ButterKnife.bind(this, headView);
        mOnJumpPageCallback = callback;
        mOnlinePlayLineView.setVisibility(View.GONE);
        mOnlinePlayLayout.setVisibility(View.GONE);
        mBaseStatisticHelper = helper;
    }

    public void release() {
        mUnbinder.unbind();
        mApi.cancel();
    }

    @OnClick({R.id.activity_movie_details_head_poster_iv,
            R.id.activity_movie_details_head_want_btn_fl,
            R.id.activity_movie_details_head_seen_btn_fl,
            R.id.activity_movie_details_head_seen_score_review_cl,
            R.id.activity_movie_details_head_score_cl,
            R.id.activity_movie_details_head_sub_score_root_fl
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_movie_details_head_poster_iv: //????????????
                onPosterClick(view);
                break;

            case R.id.activity_movie_details_head_want_btn_fl: //????????????
                onWantSee(view);
                break;

            case R.id.activity_movie_details_head_seen_btn_fl: //????????????
                onHasSeen(view);
                break;

            case R.id.activity_movie_details_head_seen_score_review_cl: // ??????????????????
                if (null != mLatestReview) {
                    mOnJumpPageCallback.onJumpPageCallback(MovieDetailsHolder.PAGE_REVIEW);
                    if (mLatestReview.hasData()) {
                        onLatestReviewShare(view);
                    } else {
                        onJumpWriteMovieComment(view.getContext(), mBaseStatisticHelper.assemble().toString());
                    }
                }
                break;

            case R.id.activity_movie_details_head_score_cl: //????????????
            case R.id.activity_movie_details_head_sub_score_root_fl: {
                //?????????????????????
                if (null != mReviewProvider
                        && null != mBasic
                        && mBasic.isEReleased == 1
                        && mBasic.overallRating > 0
                        && mBasic.overallRating <= 10) {
                    mReviewProvider.startMovieRatingDetail(mBasic.movieId);
                }
                break;
            }
        }
    }

    private void onPosterClick(View view) {
        if (null != mBasic && mBasic.hasVideo()) {
            // ????????????
            MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
            String refer = mBaseStatisticHelper.assemble1(
                    "head", null,
                    "poster", null,
                    null, null, mapBuild.build()).submit();

            // ???????????????
            JumpUtil.startVideoListActivity(view.getContext(), refer, mBasic.movieId + "");
        }
    }

    private void onLatestReviewShare(View view) {
        // ????????????,???????????????????????????(????????????????????????&????????????&????????????????????????????????????????????????????????????Icon)
        if (null != mReviewProvider) {
            mReviewProvider.startReviewShare(mLatestReview.getCommentId(), true, true);
        }
    }

    private void onJumpWriteMovieComment(Context context, String refer) {
        // ??????????????????
        IPublishProvider provider = ProviderExtKt.getProvider(IPublishProvider.class);
        if (null != provider) {
            provider.startEditorActivity(
                    CONTENT_TYPE_FILM_COMMENT,
                    null,
                    null,
                    mBasic.movieId,
                    mBasic.name,
                    null,
                    null,
                    false,
                    false
            );
        }
    }

    // ????????????
    private void onWantSee(final View view) {
        if (!UserManager.Companion.getInstance().isLogin()) {
            JumpUtil.startLoginActivity(view.getContext(), null);
            return;
        }
        if (null == mBasic) {
            return;
        }

        int flag = mBasic.attitude == 1 ? 2 : 1; //1:?????? 2:????????????

        // ????????????
        MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam())
                .put(StatisticConstant.WISH_STATE, flag == 1 ? StatisticEnum.EnumWishState.WISH.getValue() : StatisticEnum.EnumWishState.CANCEL.getValue());
        mBaseStatisticHelper.assemble1(
                "head", null,
                "wish", null,
                null, null, mapBuild.build()).submit();

        UIUtil.showLoadingDialog(view.getContext());
        mApi.setWantToSee(mBasic.movieId, flag, new NetworkManager.NetworkListener<MovieWantSeenResultBean>() {
            @Override
            public void onSuccess(MovieWantSeenResultBean result, String showMsg) {
                UIUtil.dismissLoadingDialog();
                if (null != result && result.status == 1) {
                    MToastUtils.showShortToast(result.statusMsg);

                    boolean isWantSee = flag == 1;

                    pushWantSee(isWantSee);
                    //?????????????????????-??????????????????
                    TicketMoviesInComingFragment.addRemindMovieId = isWantSee ? String.valueOf(mBasic.movieId) : null;
                    TicketMoviesInComingFragment.removeRemindMovieId = isWantSee ? null : String.valueOf(mBasic.movieId);
                    if (view.getContext() instanceof Activity) {
                        //?????????????????????-?????????????????????????????????????????????
                        // TODO: 2020/10/20 ???????????????????????????????????????
//                        ((Activity)view.getContext()).setResult(WantSeeMovieActivity.RESULTCODE);
                    }

                    //todo ????????????????????????????????????????????????????????????????????????????????????????????????
                    // ?????????????????????????????????
                    EventManager.getInstance().sendMovieWantSeeChangedEvent(String.valueOf(mBasic.movieId), isWantSee);

                    mBasic.attitude = isWantSee ? 1 : -1;
                    mBasic.wantToSeeCount += isWantSee ? 1 : -1;
                    mBasic.wantToSeeNumberShow = result.wantToSeeNumberShow;
                    updateWantSeenUI();
                } else {
                    MToastUtils.showShortToast(showMsg);
                }
            }

            @Override
            public void onFailure(NetworkException<MovieWantSeenResultBean> exception, String showMsg) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast(showMsg);
            }
        });
    }

    // ??????---?????????????????????????????????
    private void pushWantSee(boolean isAdd) {
        mApi.addOrDeletePushRemindMovie(isAdd, mBasic.movieId, new NetworkManager.NetworkListener<CommResultBean>() {
            @Override
            public void onSuccess(CommResultBean result, String showMsg) {

            }

            @Override
            public void onFailure(NetworkException<CommResultBean> exception, String showMsg) {

            }
        });
    }

    // ????????????
    private void onHasSeen(View view) {
        if (!UserManager.Companion.getInstance().isLogin()) {
            JumpUtil.startLoginActivity(view.getContext(), null);
            return;
        }

        // ????????????
        MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
        String refer = mBaseStatisticHelper.assemble1(
                "head", null,
                "seen", null,
                null, null, mapBuild.build()).submit();

        if (mBasic != null) {
            mApi.setHasSeen(mBasic.movieId, new NetworkManager.NetworkListener<MovieWantSeenResultBean>() {
                @Override
                public void onSuccess(MovieWantSeenResultBean result, String showMsg) {
                    if (null != result) {
                        if (result.status == 1) {
                            updateWantSeenUI();
                        }
                        MToastUtils.showShortToast(result.statusMsg);
                    } else {
                        MToastUtils.showShortToast(showMsg);
                    }
                }

                @Override
                public void onFailure(NetworkException<MovieWantSeenResultBean> exception, String showMsg) {
                    MToastUtils.showShortToast(showMsg);
                }
            });
        }

        // ??????????????????
        onJumpWriteMovieComment(view.getContext(), refer);
    }

    void setBasicData(MovieDetailsBasic basic) {
        if (null != basic) {
            mBasic = basic;

            //??????????????????
            mPosterIv.setPosterFilter(basic.isFilter);
            //????????????icon
            if (basic.hasVideo()) {
                mPosterIv.setForegroundResource(R.drawable.common_icon_play_large);
            } else {
                mPosterIv.setForeground(null);
            }
            //????????????
            if (null == mPosterIv.getDrawable()) {
                ImageHelper.with(mPosterIv.getContext(), ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                        .override(MScreenUtils.dp2px(120), MScreenUtils.dp2px(178))
                        .load(basic.img)
                        .view(mPosterIv)
                        .roundedCorners(4, 0)
                        .showload();
            }
            //????????????????????????
            if (null == mBgIv.getDrawable()) {
                CoilCompat.INSTANCE.loadBlurImage(
                        mBgIv,
                        basic.img,
                        DimensionExtKt.getDp(120),
                        DimensionExtKt.getDp(178),
                        true,
                        R.drawable.default_image,
                        20F,
                        4F
                );
            }
            //????????????
            List<String> tx = new ArrayList<>();
            if (basic.isIMAX3D) {
                tx.add("IMAX 3D");
            }
            if (basic.isDMAX) {
                tx.add("????????????");
            }
            if (basic.isIMAX) {
                tx.add("IMAX");
            }
            if (basic.is3D) {
                tx.add("3D");
            }
            int txs = tx.size() > 3 ? 3 : tx.size();
            for (int i = 0; i < txs; i++) {
                mScreenTypeTvs.get(i).setText(tx.get(i));
            }

            //??????
            if (TextUtils.isEmpty(basic.name)) {
                //?????????????????????????????????????????????????????????????????????????????????
                mNameTv.setText(basic.nameEn);
                mNameTv.setVisibility(TextUtils.isEmpty(basic.nameEn) ? View.GONE : View.VISIBLE);
                mEnNameTv.setVisibility(View.GONE);
            } else {
                mNameTv.setText(basic.name);
                mEnNameTv.setText(basic.nameEn);
                mNameTv.setVisibility(TextUtils.isEmpty(basic.name) ? View.GONE : View.VISIBLE);
                mEnNameTv.setVisibility(TextUtils.isEmpty(basic.nameEn) ? View.GONE : View.VISIBLE);
            }
            //??????-??????-?????????????????????
            StringBuilder types = new StringBuilder();
            if (null != basic.type) {
                int size = basic.type.size() > 4 ? 4 : basic.type.size();
                for (int i = 0; i < size; i++) {
                    types.append(basic.type.get(i));
                    if (i != size - 1) {
                        types.append("/");
                    }
                }
            }
            if (!TextUtils.isEmpty(basic.mins)) {
                types.append(" ?? ");
                types.append(basic.mins);
            }
            boolean hasReleaseDate = !TextUtils.isEmpty(basic.releaseDate) && basic.releaseDate.length() == 8;
            boolean hasReleaseArea = !TextUtils.isEmpty(basic.releaseArea);
            if (hasReleaseDate || hasReleaseArea) {
                types.append(" ?? ");
                if (hasReleaseDate)
                    if (!TextUtils.isEmpty(basic.releaseDate) && basic.releaseDate.length() == 8) {
                        types.append(MTimeUtils.format("yyyy???M???d???", MTimeUtils.parse(basic.releaseDate)));
                    }
                if (hasReleaseArea) {
                    types.append(basic.releaseArea);
                    types.append("??????");
                }
            }

            mTypesTv.setText(types.toString());
            //?????????
            if (!TextUtils.isEmpty(basic.commentSpecial)) {
                mQueteTv.setText(basic.commentSpecial);
            } else {
                mQueteLayout.setVisibility(View.GONE);
            }
            //??????
            if (basic.isEggHunt) {
                mEasterTv.setText(TextUtils.isEmpty(basic.eggDesc) ? "?????????" : basic.eggDesc);
            } else {
                mEasterLayout.setVisibility(View.GONE);
            }

            //???????????????????????????????????????
            updateWantSeenUI();

            //??????
            if (basic.isEReleased == 1) {
                if (basic.overallRating > 0 && basic.overallRating <= 10) {
                    mScoreTv.setVisibility(View.VISIBLE);
                    mScoreTv.setScore(basic.overallRating);
                    mSubScoreArrowIv.setVisibility(View.VISIBLE);
                } else {
                    mNoScoreTv.setVisibility(View.VISIBLE);
                    mNoScoreTv.setText("????????????");
                    mScoreTv.setVisibility(View.INVISIBLE);
                    mSubScoreArrowIv.setVisibility(View.GONE);
                }
                mNoSubScoreLayout.setVisibility(View.GONE);
                mSubScoreLayout.setVisibility(View.VISIBLE);
                mSubScorePingfenNumTv.setText(MtimeUtils.formatCount(basic.ratingCount) + "?????????");
                mSubScoreWantSeeNumTv.setText(MtimeUtils.formatCount(basic.wantToSeeCount) + "?????????");
                mSubScorePingfenNumTv.setVisibility(basic.ratingCount > 0 ? View.VISIBLE : View.GONE);
                mSubScoreWantSeeNumTv.setVisibility(basic.wantToSeeCount > 0 ? View.VISIBLE : View.GONE);
                if (basic.ratingCount <= 0
                        && basic.wantToSeeCount <= 0
                        && mSubScoreArrowIv.getVisibility() == View.GONE
                ) {
                    mSubScoreLineView.setVisibility(View.GONE);
                } else {
                    mSubScoreLineView.setVisibility(View.VISIBLE);
                }
                if (!basic.hasSubRatings()) {
                    mSubScoreEmptyTv.setVisibility(View.VISIBLE);
                    mSubScoreListRv.setVisibility(View.GONE);
                } else {
                    mSubScoreEmptyTv.setVisibility(View.GONE);
                    mSubScoreListRv.setVisibility(View.VISIBLE);
                    mSubScoreListRv.setAdapter(new MovieDetailsSubScoreItemAdapter(basic.getSubItemRatings()));
                    mSubScoreListRv.suppressLayout(true); //??????ViewGroup??????RecyclerView??????ViewGroup?????????????????????
                }
            } else {
                mScoreTv.setVisibility(View.INVISIBLE);
                mNoScoreTv.setVisibility(View.VISIBLE);
                mNoScoreTv.setText("?????????");
                mSubScoreLayout.setVisibility(View.GONE);
                mNoSubScoreLayout.setVisibility(View.VISIBLE);
                mNoSubScoreWantSeeNumTv.setText(MtimeUtils.formatCount(basic.wantToSeeCount));
            }
        }
    }

    //???????????????????????????????????????
    private void updateWantSeenUI() {
        if (null == mBasic || null == mWantBtnView) {
            return;
        }
        if (mBasic.attitude == 0) {
            //???????????????
            mWantBtnView.setVisibility(View.GONE);
            mSeenBtnView.setVisibility(View.GONE);

            mSeenScoreReviewLayout.setVisibility(View.VISIBLE);

            updateLatestReviewUI();
        } else {
            mSeenScoreReviewLayout.setVisibility(View.GONE);
            mWantBtnView.setVisibility(View.VISIBLE);
            mSeenBtnView.setVisibility(View.VISIBLE);
            //???????????????????????????
            mWantBtnView.setSelected(mBasic.attitude == 1);
            if (mBasic.isEReleased == 1) {
                mSubScoreWantSeeNumTv.setText(MtimeUtils.formatCount(mBasic.wantToSeeCount) + "?????????");
                mSubScoreWantSeeNumTv.setVisibility(mBasic.wantToSeeCount > 0 ? View.VISIBLE : View.GONE);
                if (mBasic.ratingCount <= 0 && mBasic.wantToSeeCount <= 0) {
                    mSubScoreLineView.setVisibility(View.GONE);
                } else {
                    mSubScoreLineView.setVisibility(View.VISIBLE);
                }
            } else {
                mNoSubScoreLayout.setVisibility(View.VISIBLE);
                mNoSubScoreWantSeeNumTv.setText(MtimeUtils.formatCount(mBasic.wantToSeeCount));
            }
        }

    }

    /**
     * ????????????????????????
     */
    @SuppressLint("RtlHardcoded")
    void setOnlinePlayData(List<MovieDetailsOnlinePlay> playlist) {
        if (!CollectionUtils.isEmpty(playlist)) {
            mOnlinePlayLineView.setVisibility(View.VISIBLE);
            mOnlinePlayLayout.setVisibility(View.VISIBLE);

            mOnlinePlayIconsLayout.removeAllViews();
            int size = playlist.size() > 3 ? 3 : playlist.size();
            for (int i = 0; i < size; i++) {
                MovieDetailsOnlinePlay play = playlist.get(i);

                CircleImageView iv = new CircleImageView(mOnlinePlayIconsLayout.getContext());
                iv.setBorderColor(Color.parseColor("#F2F3F6"));
                iv.setBorderWidth(MScreenUtils.dp2px(1.5f));
                int ws = MScreenUtils.dp2px(27);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ws, ws);
                params.gravity = Gravity.RIGHT;
                params.rightMargin = i * MScreenUtils.dp2px(20);
                iv.setLayoutParams(params);
                mOnlinePlayIconsLayout.addView(iv);

                ImageHelper.with(ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                        .override(ws, ws)
                        .load(play.picUrl)
                        .view(iv)
                        .showload();
            }
        }
    }

    /**
     * ??????????????????????????????????????????
     */
    void setLatestReview(MovieLatestReviewBean review) {
        if (null != review) {
            mLatestReview = review;

            updateWantSeenUI();
        }
    }

    // ????????????????????????????????????UI
    private void updateLatestReviewUI() {
        if (null == mLatestReview)
            return;

        ImageHelper.with(ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                .load(mLatestReview.getUserImg())
                .cropCircle()
                .override(MScreenUtils.dp2px(24), MScreenUtils.dp2px(24))
                .view(mSeenScoreReviewProfileIv)
                .placeholder(R.drawable.default_user_head)
                .showload();

        mSeenScoreReviewUserNameTv.setText(mLatestReview.getUserName());
        mSeenScoreReviewScoreTv.setScore(mLatestReview.getRating());
        mSeenScoreReviewShareIv.setVisibility(mLatestReview.hasData() ? View.VISIBLE : View.GONE);
        mSeenScoreReviewArrowIv.setVisibility(mLatestReview.hasData() ? View.GONE : View.VISIBLE);
    }
}
