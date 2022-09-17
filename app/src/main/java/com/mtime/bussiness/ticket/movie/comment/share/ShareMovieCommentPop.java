//package com.mtime.bussiness.ticket.movie.comment.share;
//
//import android.graphics.drawable.Drawable;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.kotlin.android.image.ImageCallback;
//import com.mtime.R;
//import com.mtime.base.imageload.ImageHelper;
//import com.mtime.base.imageload.ImageLoadOptions;
//import com.mtime.base.imageload.ImageProxyUrl;
//import com.mtime.bussiness.ticket.movie.comment.bean.ShareMovieCommentBean;
//import com.mtime.util.ViewCapture;
//
//import org.jetbrains.annotations.Nullable;
//
//import java.io.File;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
///**
// * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
// * <p>
// * On 2019-06-25
// */
//public class ShareMovieCommentPop extends ViewCapture {
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
//    @BindView(R.id.movie_score_tv)
//    TextView movieScoreTv;
//    @BindView(R.id.movie_types_tv)
//    TextView movieTypesTv;
//    @BindView(R.id.movie_comment_content_tv)
//    TextView movieCommentContentTv;
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
//    @BindView(R.id.choose_image_btn)
//    View chooseImage;
//
//    ShareMovieCommentPop(ViewGroup parent) {
//        super(parent, R.layout.share_movie_comment_popup_layout);
//    }
//
//    @Override
//    protected void onViewCreated(View view) {
//        super.onViewCreated(view);
//        ButterKnife.bind(this, view);
//        chooseImage.setVisibility(View.GONE);
//    }
//
//    void showShare(ShareMovieCommentBean shareBean) {
//
//        movieCnNameTv.setText(shareBean.name);
//        if (TextUtils.isEmpty(shareBean.nameEn)) {
//            movieEnNameTv.setVisibility(View.GONE);
//        } else {
//            movieEnNameTv.setText(shareBean.nameEn);
//        }
//        movieTypesTv.setText(shareBean.type);
//
//        if (shareBean.sensitiveStatus || shareBean.fRating < 0) {
//            mtimePing.setVisibility(View.GONE);
//            movieScoreTv.setVisibility(View.GONE);
//        } else {
//            movieScoreTv.setText(String.format(" %s ", shareBean.rating));
//        }
//
//        movieCommentContentTv.setText(shareBean.userComment);
//
//        myMovieCommentScoreTv.setText(shareBean.userRating);
//
//        publishCommentTimeTv.setText(shareBean.commentDateTime);
//
//        joinMtimeDaysTv.setText(mContext.getResources().getString(R.string.movie_comment_share_join_mtime,
//                shareBean.joinCountShow));
//        markMovieCountTv.setText(mContext.getResources().getString(R.string.movie_comment_share_mark_movie,
//                shareBean.signCountShow));
//    }
//
//    void loadQr(File qrFile, String msg) {
//        ImageLoadOptions.Builder loader = ImageHelper.with(mContext, ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT);
//        loader = loader.load(qrFile);
//        shareQrMsg.setText(msg);
//        loader.view(shareQrIv).showload();
//    }
//
//    void loadUserInfo(String userName, String userImage) {
//        userNameTv.setText(userName);
//        if (userImage == null) {
//            userHeaderIv.setImageResource(R.drawable.common_icon_round_default_avatar);
//        } else {
////            ImageHelper.with(mContext, ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
////                    .load(userImage)
////                    .cropCircle()
////                    .error(R.drawable.common_icon_round_default_avatar)
////                    .placeholder(R.drawable.common_icon_round_default_avatar)
////                    .view(userHeaderIv)
////                    .showload();
//
//            CoilExtKt.loadImage(
//                    userHeaderIv,
//                    userImage,
//                    0,0,
//                    R.drawable.common_icon_round_default_avatar,
//                    false,
//                    true,
//                    true
//            );
//        }
//    }
//
//    void loadMovieStill(String imageUrl) {
////        ImageHelper.with(mContext, ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
////                .load(imageUrl)
////                .blur(25, 4)
////                .view(blurIv)
////                .showload();
//        CoilExtKt.loadBlurImage(
//                blurIv,
//                imageUrl,
//                0,
//                0,
//                0,
//                false,
//                25,
//                4,
//                true
//        );
//
////        ImageHelper.with(mContext, ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
////                .load(imageUrl)
////                .view(movieImgIv)
////                .showload();
//
//        CoilExtKt.loadImageCallback(
//                movieImgIv,
//                imageUrl,
//                0,
//                0,
//                false,
//                true,
//                new ImageCallback() {
//                    @Override
//                    public void onDrawable(@Nullable Drawable drawable) {
//                        movieImgIv.setImageDrawable(drawable);
//                        doCapture();
//                    }
//                }
//        );
//    }
//}
