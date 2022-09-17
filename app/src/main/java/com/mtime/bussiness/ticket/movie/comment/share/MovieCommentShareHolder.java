//package com.mtime.bussiness.ticket.movie.comment.share;
//
//import android.animation.Animator;
//import android.animation.AnimatorListenerAdapter;
//import android.animation.ValueAnimator;
//import android.content.Context;
//import android.graphics.Bitmap;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.core.widget.NestedScrollView;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CompoundButton;
//import android.widget.ImageView;
//import android.widget.Switch;
//import android.widget.TextView;
//
//import com.kk.taurus.uiframe.v.ContentHolder;
//import com.mtime.R;
//import com.mtime.base.dialog.BaseDialogFragment;
//import com.mtime.base.imageload.ImageHelper;
//import com.mtime.base.imageload.ImageLoadOptions;
//import com.mtime.base.imageload.ImageProxyUrl;
//import com.mtime.base.utils.MScreenUtils;
//import com.mtime.base.utils.MToastUtils;
//import com.mtime.bussiness.ticket.movie.comment.bean.MovieStillBean;
//import com.mtime.bussiness.ticket.movie.comment.bean.ShareMovieCommentBean;
//import com.mtime.util.FileUtils;
//import com.mtime.util.MtimeUtils;
//import com.mtime.util.QRCUtils;
//import com.mtime.util.ViewCapture;
//
//import java.io.File;
//import java.util.ArrayList;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//
///**
// * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
// * <p>
// * On 2019-06-24
// */
//class MovieCommentShareHolder extends ContentHolder<Void>
//        implements CompoundButton.OnCheckedChangeListener, ViewCapture.CaptureCallback<ShareMovieCommentPop>,
//        ValueAnimator.AnimatorUpdateListener, SelectMovieStillDialog.OnStillChanged, BaseDialogFragment.DismissListener {
//
//    @BindView(R.id.blur_iv)
//    ImageView blurIv;
//    @BindView(R.id.movie_cn_name_tv)
//    TextView movieCnNameTv;
//    @BindView(R.id.movie_en_name_tv)
//    TextView movieEnNameTv;
//    @BindView(R.id.movie_img_iv)
//    ImageView movieImgIv;
//
//
//    @BindView(R.id.movie_types_tv)
//    TextView movieTypesTv;
//    @BindView(R.id.movie_comment_content_tv)
//    TextView movieCommentContentTv;
//
//    @BindView(R.id.movie_score_tv)
//    TextView movieScoreTv;
//    @BindView(R.id.mtime_movie_score)
//    View mtimePing;
//
//    @BindView(R.id.user_header_iv)
//    ImageView userHeaderIv;
//    @BindView(R.id.user_name_tv)
//    TextView userNameTv;
//    @BindView(R.id.my_movie_comment_score_tv)
//    TextView myMovieCommentScoreTv;
//    @BindView(R.id.publish_comment_time_tv)
//    TextView publishCommentTimeTv;
//    @BindView(R.id.join_mtime_days_tv)
//    TextView joinMtimeDaysTv;
//    @BindView(R.id.mark_movie_count_tv)
//    TextView markMovieCountTv;
//    @BindView(R.id.share_qr_iv)
//    ImageView shareQrIv;
//    @BindView(R.id.share_qr_msg)
//    TextView shareQrMsg;
//
//    @BindView(R.id.anonymous_switch)
//    Switch anonymousSwitch;
//
//    @BindView(R.id.scroll_view)
//    NestedScrollView scrollView;
//    @BindView(R.id.share_operation_panel)
//    ConstraintLayout shareOperationPanel;
//
//    private final int dp240 = MScreenUtils.dp2px(240);
//
//    private boolean mLongComment;
//    private File mMovieDetailQr;
//    private File mMovieLongCommentQr;
//    private ShareMovieCommentBean mShareBean;
//
//    private ShareMovieCommentPop mSharePop;
//    private int imageUse = 0;
//    private ValueAnimator mCloseAnimator;
//    private ValueAnimator mOpenAnimator;
//
//    private final ArrayList<MovieStillBean> movieStills = new ArrayList<>();
//    private String mSelectedMovieStill;
//    private DirectShare mShare;
//
//    interface Callback {
//        void onStillChanged(String url, int pos);
//
//        void onStillScrolled();
//
//        void sharePic(int channel);
//
//        void savePic();
//
//        void onPicConfirm();
//
//        void onNnonyStateChanged(boolean isChecked);
//    }
//
//    private final Callback mCallback;
//
//    MovieCommentShareHolder(Context context, Callback callback) {
//        super(context);
//        mCallback = callback;
//    }
//
//    @Override
//    public void onCreate() {
//    }
//
//    @Override
//    public void onHolderCreated() {
//        super.onHolderCreated();
//        setContentView(R.layout.acitvity_share_movie_comment);
//        ButterKnife.bind(this, mRootView);
//        View decorView = getActivity().getWindow().getDecorView();
//        mSharePop = new ShareMovieCommentPop((ViewGroup) decorView);
//        mSharePop.setCaptureCallback(this);
//        mLongComment = getIntent().getBooleanExtra("longComment", false);
//
//        anonymousSwitch.setOnCheckedChangeListener(this);
//        mShare = new DirectShare(getActivity());
//    }
//
//    @OnClick({
//            R.id.share_weixin_friend,
//            R.id.share_weichat_world,
//            R.id.share_weibo_sina,
//            R.id.share_qq_friend,
//            R.id.share_menu_save_to_local,
//            R.id.choose_image_btn,
//            R.id.close,
//    })
//    public void onViewClicked(View view) {
//        imageUse = view.getId();
//        switch (imageUse) {
//            case R.id.share_weixin_friend:
//            case R.id.share_weichat_world:
//            case R.id.share_weibo_sina:
//            case R.id.share_qq_friend:
//            case R.id.share_menu_save_to_local:
//                mSharePop.capture();
//                break;
//            case R.id.choose_image_btn:
//                closeSharePanel();
//                showChooseImageDialog();
//                break;
//            case R.id.close:
//                onHolderEvent(1, null);
//                break;
//        }
//    }
//
//    private void showChooseImageDialog() {
//        SelectMovieStillDialog dialog = new SelectMovieStillDialog();
//        dialog.setOnStillChanged(this);
//        dialog.setStills(movieStills, mSelectedMovieStill);
//        dialog.show(getActivity().getSupportFragmentManager());
//        dialog.setOnDismissListener(this);
//    }
//
//    @Override
//    public void onStillChanged(String url, int pos) {
//        mSelectedMovieStill = url;
//        loadMovieStill(url);
//        mCallback.onStillChanged(url, pos);
//    }
//
//    @Override
//    public void onStillScrolled() {
//        mCallback.onStillScrolled();
//    }
//
//    @Override
//    public void onDismiss() {
//        openSharePanel();
//        mCallback.onPicConfirm();
//    }
//
//    private void closeSharePanel() {
//        mCloseAnimator = ValueAnimator.ofInt(0, dp240);
//        mCloseAnimator.addUpdateListener(this);
//        mCloseAnimator.addListener(mAnimListener);
//        mCloseAnimator.setDuration(300);
//        mCloseAnimator.start();
//    }
//
//    private void openSharePanel() {
//        mOpenAnimator = ValueAnimator.ofInt(dp240, 0);
//        mOpenAnimator.addUpdateListener(this);
//        mOpenAnimator.addListener(mAnimListener);
//        mOpenAnimator.setDuration(300);
//        mOpenAnimator.start();
//    }
//
//    private final Animator.AnimatorListener mAnimListener = new AnimatorListenerAdapter() {
//        @Override
//        public void onAnimationCancel(Animator animation) {
//            onAnimationEnd(animation);
//        }
//
//        @Override
//        public void onAnimationEnd(Animator animation) {
//            super.onAnimationEnd(animation);
//            mCloseAnimator.removeAllUpdateListeners();
//            animation.removeAllListeners();
//        }
//    };
//
//    @Override
//    public void onAnimationUpdate(ValueAnimator animation) {
//        int value = (int) animation.getAnimatedValue();
//        shareOperationPanel.setTranslationY(value);
//
//        scrollView.setPadding(scrollView.getPaddingLeft(),
//                scrollView.getPaddingTop(), scrollView.getPaddingRight(),
//                dp240 - value);
//    }
//
//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        loadUserInfo(mShareBean);
//        loadQr(mShareBean);
//        mCallback.onNnonyStateChanged(isChecked);
//    }
//
//    void showShare(ShareMovieCommentBean shareBean) {
//        mShareBean = shareBean;
//
//        int size = MScreenUtils.dp2px(55);
//
//        long time = System.currentTimeMillis();
//
//        mMovieDetailQr = new File(getActivity().getCacheDir(), time + "movieDetailQr.jpg");
//        mMovieLongCommentQr = new File(getActivity().getCacheDir(), time + "movieLongCommentQr.jpg");
//
//        int blackColor = getResource().getColor(R.color.color_8798AF);
//        int whiteColor = getResource().getColor(R.color.color_f2f3f6);
//
//        QRCUtils.createQRImage(shareBean.movieDetailURL, size, size, blackColor, whiteColor, mMovieDetailQr);
//        QRCUtils.createQRImage(shareBean.longCommentURL, size, size, blackColor, whiteColor, mMovieLongCommentQr);
//
//        movieStills.addAll(shareBean.movieStills);
//
//        onStillChanged(movieStills.get(0).stillUrl, 0);
//
//        movieCnNameTv.setText(shareBean.name);
//        if (TextUtils.isEmpty(shareBean.nameEn)) {
//            movieEnNameTv.setVisibility(View.GONE);
//        } else {
//            movieEnNameTv.setText(shareBean.nameEn);
//        }
//        movieTypesTv.setText(shareBean.type);
//        if (shareBean.sensitiveStatus || shareBean.fRating < 0) {
//            mtimePing.setVisibility(View.GONE);
//            movieScoreTv.setVisibility(View.GONE);
//        } else {
//            movieScoreTv.setText(String.format(" %s ", shareBean.rating));
//        }
//
//        TextUtils.ellipsize(shareBean.userComment, movieCommentContentTv.getPaint(), 0, TextUtils.TruncateAt.END);
//
//        if (shareBean.userComment != null && shareBean.userComment.length() > 1000) {
//            shareBean.userComment = shareBean.userComment.substring(0, 997) + "...";
//        }
//
//        movieCommentContentTv.setText(shareBean.userComment);
//
//        loadUserInfo(shareBean);
//
//        myMovieCommentScoreTv.setText(shareBean.userRating);
//
//        publishCommentTimeTv.setText(shareBean.commentDateTime);
//
//        joinMtimeDaysTv.setText(getString(R.string.movie_comment_share_join_mtime, shareBean.joinCountShow));
//        markMovieCountTv.setText(getString(R.string.movie_comment_share_mark_movie, shareBean.signCountShow));
//
//        loadQr(shareBean);
//        mSharePop.showShare(shareBean);
//    }
//
//    private void loadMovieStill(String imageUrl) {
//        ImageHelper.with(getActivity(), ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
//                .load(imageUrl)
//                .blur(25, 4)
//                .view(blurIv)
//                .showload();
//
//        ImageHelper.with(getActivity(), ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
//                .load(imageUrl)
//                .view(movieImgIv)
//                .showload();
//        mSharePop.loadMovieStill(imageUrl);
//
//    }
//
//    private void loadQr(ShareMovieCommentBean shareBean) {
//        ImageLoadOptions.Builder loader = ImageHelper.with(getActivity(), ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT);
//        if (mLongComment && !anonymousSwitch.isChecked()) {
//            loader = loader.load(mMovieLongCommentQr);
//            shareQrMsg.setText(shareBean.longCommentRcmdMsg);
//            mSharePop.loadQr(mMovieLongCommentQr, shareBean.longCommentRcmdMsg);
//        } else {
//            loader = loader.load(mMovieDetailQr);
//            shareQrMsg.setText(shareBean.movieDetailRcmdMsg);
//            mSharePop.loadQr(mMovieDetailQr, shareBean.movieDetailRcmdMsg);
//        }
//        loader.view(shareQrIv).showload();
//    }
//
//    private void loadUserInfo(ShareMovieCommentBean shareBean) {
//        if (anonymousSwitch.isChecked()) {
//            userHeaderIv.setImageResource(R.drawable.common_icon_round_default_avatar);
//            userNameTv.setText("我");
//            mSharePop.loadUserInfo("我", null);
//        } else {
//            ImageHelper.with(getActivity(), ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
//                    .load(shareBean.userImage)
//                    .cropCircle()
//                    .error(R.drawable.common_icon_round_default_avatar)
//                    .placeholder(R.drawable.common_icon_round_default_avatar)
//                    .view(userHeaderIv)
//                    .showload();
//            userNameTv.setText(shareBean.userName);
//            mSharePop.loadUserInfo(shareBean.userName, shareBean.userImage);
//        }
//    }
//
//    private Bitmap captureBitmap;
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (captureBitmap != null) {
//            captureBitmap.recycle();
//        }
//        if (mCloseAnimator != null) {
//            mCloseAnimator.cancel();
//        }
//        if (mOpenAnimator != null) {
//            mOpenAnimator.cancel();
//        }
//    }
//
//    @Override
//    public void onCaptured(ShareMovieCommentPop capture, Bitmap b) {
//        if (R.id.share_menu_save_to_local == imageUse) {
//            mCallback.savePic();
//            String path = MtimeUtils.saveBitmapToInternal(mContext, b);
//            b.recycle();
//            if (FileUtils.savePhotoToLocal(getActivity(), path)) {
//                MToastUtils.showLongToast(R.string.daily_recommend_pic_saved);
//            } else {
//                MToastUtils.showLongToast(R.string.daily_recommend_pic_save_failed);
//            }
//            return;
//        }
//        captureBitmap = b;
//        switch (imageUse) {
//            case R.id.share_weixin_friend:
//                mShare.shareChannel(DirectShare.CHANNEL_WECHAT, b);
//                mCallback.sharePic(DirectShare.CHANNEL_WECHAT);
//                break;
//            case R.id.share_weichat_world:
//                mShare.shareChannel(DirectShare.CHANNEL_MOMENTS, b);
//                mCallback.sharePic(DirectShare.CHANNEL_MOMENTS);
//                break;
//            case R.id.share_weibo_sina:
//                mShare.shareChannel(DirectShare.CHANNEL_SINA, b);
//                mCallback.sharePic(DirectShare.CHANNEL_SINA);
//                break;
//            case R.id.share_qq_friend:
//                mShare.shareChannel(DirectShare.CHANNEL_QQ, b);
//                mCallback.sharePic(DirectShare.CHANNEL_QQ);
//                break;
//        }
//    }
//}
