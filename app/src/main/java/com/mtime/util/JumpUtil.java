package com.mtime.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.kotlin.android.app.data.constant.CommConstant;
import com.kotlin.android.film.JavaOpenSeatActivity;
import com.kotlin.android.router.ext.ProviderExtKt;

import com.kotlin.android.app.router.provider.mine.IMineProvider;
import com.kotlin.android.app.router.provider.ugc.IUgcProvider;
import com.kotlin.android.app.router.provider.video.IVideoProvider;
import com.kotlin.android.user.UserManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.beans.ImageBean;
import com.mtime.beans.Photo;
import com.mtime.bussiness.common.CommonWebActivity;
import com.mtime.bussiness.common.MovieCommentViewActivity;
import com.mtime.bussiness.common.PictureActivity;
import com.mtime.bussiness.common.PictureSelectActivity;
import com.mtime.bussiness.daily.recommend.HistoryRecommendActivity;
import com.mtime.bussiness.daily.recommend.RecommendActivity;
import com.mtime.bussiness.daily.recommend.bean.DailyRecommendBean;
import com.mtime.bussiness.information.NewsCommentListActivity;
import com.mtime.bussiness.information.NewsPhotoDetailActivity;
import com.mtime.bussiness.location.CityChangeActivity;
import com.mtime.bussiness.location.MapViewActivity;
import com.mtime.bussiness.main.GuideViewActivity;
import com.mtime.bussiness.mine.activity.CompanyDetailActivity;
import com.mtime.bussiness.mine.activity.MtimeCoinListActivity;
import com.mtime.bussiness.mine.activity.SettingNoticeManageActivity;
import com.mtime.bussiness.mine.login.activity.BindPhoneWithLoginActivity;
import com.mtime.bussiness.mine.login.activity.LoginActivity;
import com.mtime.bussiness.mine.login.activity.RetrievePasswordActivity;
import com.mtime.bussiness.mine.profile.activity.BindPhoneActivity;
import com.mtime.bussiness.mine.profile.activity.ChangePasswordActivity;
import com.mtime.bussiness.mine.profile.activity.ChangePasswordLoginActivity;
import com.mtime.bussiness.mine.profile.activity.EditInfoActivity;
import com.mtime.bussiness.mine.profile.activity.ProfileActivity;
import com.mtime.bussiness.mine.profile.activity.SecuritycodeActivity;
import com.mtime.bussiness.mine.profile.activity.SetPasswordActivity;
import com.mtime.bussiness.ticket.cinema.activity.ActivitiesInstructionsActivity;
import com.mtime.bussiness.ticket.cinema.activity.CinemaViewActivity;
import com.mtime.bussiness.ticket.cinema.activity.NewCinemaShowtimeActivity;
import com.mtime.bussiness.ticket.cinema.activity.TwitterCinemaActivity;
import com.mtime.bussiness.ticket.movie.activity.ActorExperienceActivity;
import com.mtime.bussiness.ticket.movie.activity.ActorFilmographyActivity;
import com.mtime.bussiness.ticket.movie.activity.ActorHonorsActivity;
import com.mtime.bussiness.ticket.movie.activity.ActorViewActivity;
import com.mtime.bussiness.ticket.movie.activity.CommentImageDetailActivity;
import com.mtime.bussiness.ticket.movie.activity.MovieMoreInfoActivity;
import com.mtime.bussiness.ticket.movie.activity.MovieShortCommentsActivity;
import com.mtime.bussiness.ticket.movie.activity.MovieShowtimeActivity;
import com.mtime.bussiness.ticket.movie.activity.ProducerListActivity;
import com.mtime.bussiness.ticket.movie.activity.SeatSelectActivity;
import com.mtime.bussiness.ticket.movie.activity.TwitterActivity;
import com.mtime.bussiness.ticket.movie.boxoffice.MovieGlobalBoxOfficeActivity;
import com.mtime.bussiness.ticket.movie.details.MovieActorListActivity;
import com.mtime.bussiness.ticket.movie.details.MovieDetailsActivity;
import com.mtime.bussiness.ticket.movie.details.MovieEventActivity;
import com.mtime.bussiness.ticket.movie.details.MovieHonorListActivity;
import com.mtime.bussiness.ticket.movie.details.MovieLongCommentListActivity;
import com.mtime.bussiness.ticket.movie.details.MovieMediaReviewActivity;
import com.mtime.bussiness.ticket.movie.details.MovieRelatedMovieActivity;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsBasic;
import com.mtime.bussiness.ticket.stills.MovieStillsActivity;
import com.mtime.bussiness.ticket.stills.MovieStillsDetailActivity;
import com.mtime.bussiness.video.activity.CategoryVideoListActivity;
import com.mtime.payment.MemberCardListActivity;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinguanping on 2017/5/27. activity ??????
 * <p>
 * ????????? if (isNewTask) { intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); isNewTask = false; }
 * ???????????????????????????????????????????????????????????????????????????push???????????????????????????activity
 */

public class JumpUtil {

    private static final String PHONE_NUMBER = "tel:%s";
    private static final String PHONE_GRANT_DENIED = "????????????????????????";


    /**
     * ??????????????????
     *
     * @param context
     */
    public static void actionCall(final Context context, final String phoneNumber) {
        try {
            Acp.getInstance(context).request(
                    new AcpOptions.Builder()
                            .setPermissions(Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE).build(),
                    new AcpListener() {
                        @Override
                        public void onGranted() {
                            Acp.getInstance(context).onDestroy();
                            Uri uri = Uri.parse(String.format(PHONE_NUMBER, phoneNumber));
                            Intent intent = new Intent();
                            if (context instanceof Activity) {
                            } else {
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            }
                            intent.setData(uri);
                            intent.setAction(Intent.ACTION_CALL);// ??????????????????
                            context.startActivity(intent);
                        }

                        @Override
                        public void onDenied(List<String> permissions) {
                            Acp.getInstance(context).onDestroy();
                            MToastUtils.showShortToast(PHONE_GRANT_DENIED);
                        }
                    });
        } catch (Exception e) {

        }
    }

    /**
     * ?????????????????????
     *
     * @param context
     * @param refer
     * @param articleId
     * @param rcmdedId
     * @param rcmdedType
     */
    public static void startArticleActivity(Context context, String refer, String articleId, String rcmdedId, String rcmdedType) {
       startActicleActivity(articleId);
    }

    private static void startActicleActivity(String articleId){
        if (!TextUtils.isEmpty(articleId) && TextUtils.isDigitsOnly(articleId)) {
            IUgcProvider ugcProvider = ProviderExtKt.getProvider(IUgcProvider.class);
            if (ugcProvider != null) {
                ugcProvider.launchDetail(Long.parseLong(articleId),CommConstant.PRAISE_OBJ_TYPE_ARTICLE,0L,false);
            }
        }
    }

    public static void startArticleActivity(Context context, String refer, String articleId, String informationType, String rcmdedId, String rcmdedType) {
//        InformationDetailActivity.launch(context, refer, articleId, informationType, rcmdedId, rcmdedType);
        startActicleActivity(articleId);
    }

    /**
     * ????????????
     */
    public static void jumpHistoryRecommend(Context context, String refre) {
        HistoryRecommendActivity.launch(context, refre);
    }

    /**
     * ????????????????????????
     */
    public static void jumpDailyRecommend(Context c, String refre) {
        RecommendActivity.launchFromHome(c, refre);
    }

    /**
     * ??????????????????????????????
     */
    public static void jumpDailyRecommendFromHistory(Context c, DailyRecommendBean bean, String refre) {
        RecommendActivity.launchFromRecmdHistory(c, bean, refre);
    }


    /**
     * ???????????????
     *
     * @param context
     */
    public static void startMovieInfoActivity(Context context, String refer, String movieId, int requestCode) {
        MovieDetailsActivity.launch(context, refer, movieId, requestCode);
    }

    /**
     * ??????????????????
     *
     * @param context
     * @param couponRemindType ??????????????????0-??????,????????? 2-??????
     */
    public static void startMyVoucherListActivity(Context context, String refer, int couponRemindType) {
        if (!UserManager.Companion.getInstance().isLogin()) {
            startLoginActivity(context, refer);
            return;
        }
        // 2020/11/3 ???????????????????????????????????????
        //MyVoucherListActivity.launch(context, refer, couponRemindType);
        ((IMineProvider)ProviderExtKt.getProvider(IMineProvider.class)).startMyWalletActivity(null, null);
    }


    /**
     * ???????????????
     *
     * @param context
     */
    public static void startProfileActivity(Context context, String refer) {
        if (!UserManager.Companion.getInstance().isLogin()) {
            startLoginActivity(context, refer);
            return;
        }
        ProfileActivity.launch(context, refer);
    }


    /**
     * ?????????????????????/??????????????????
     *
     * @param context
     */
    public static void startMovieCommentViewActivity(Context context, String refer, String movieId) {
        MovieCommentViewActivity.launch(context, refer, movieId);
    }

    /**
     * ????????????_??????????????????
     *
     * @param context
     */
    public static void startMtimeCoinListActivity(Context context, String refer) {
        if (!UserManager.Companion.getInstance().isLogin()) {
            startLoginActivity(context, refer);
            return;
        }
        MtimeCoinListActivity.launch(context, refer);
    }

    /**
     * ?????????
     *
     * @param context
     */
    public static void startLoginActivity(Context context, String refer) {
        LoginActivity.launch(context, refer);
    }

    public static void startLoginActivity(Context context, String refer, int requestCode) {
        LoginActivity.launch(context, refer, requestCode);
    }

    public static void startLoginActivity(Context context, String refer, double mTotalPrice, String mIntroduction, String movieName, String cinemaName, String mSeatId, int selectedSeatCount, double serviceFee, String subOrderID, String mTicketDateInfo, String mSeatSelectedInfo, String mDId, String mMovieId, String mCinemaId, String mDate, boolean show_not_vip, boolean show_new_gif_dlg, int requestCode) {
        LoginActivity.launch(context, refer, mTotalPrice, mIntroduction, movieName, cinemaName, mSeatId, selectedSeatCount, serviceFee, subOrderID, mTicketDateInfo, mSeatSelectedInfo, mDId, mMovieId, mCinemaId, mDate, show_not_vip, show_new_gif_dlg, requestCode);
    }

    /**
     * ???????????????
     * ??????????????????????????????????????????????????????
     */
    public static void startBindPhoneWithLoginActivity(Context context, String refer, int bindFrom, String account, String password) {
        BindPhoneWithLoginActivity.launch(context, refer, bindFrom, account, password);
    }

    /**
     * ???????????????
     * ????????????????????????????????????
     */
    public static void startBindPhoneWithLoginActivity(Context context, String refer, int bindFrom, String accessToken, String thirdAccessToken,
                                                       String wechatCode, String platformId, String expires, String targetActivityId,
                                                       boolean hasPassword, boolean registerStatus, int requestCode) {
        BindPhoneWithLoginActivity.launch(context, refer, bindFrom, accessToken, thirdAccessToken,
                wechatCode, platformId, expires, targetActivityId, hasPassword,
                registerStatus, requestCode);
    }

    /**
     * ???????????????
     * ?????????????????????????????????????????????????????????????????????
     */
    public static void startBindPhoneActivity(Context context, String refer, boolean hasPassword) {
        BindPhoneActivity.launch(context, refer, hasPassword);
    }

    /**
     * ???????????????
     * ????????????_????????????_??????????????????_????????????????????? ??????
     */
    public static void startSecuritycodeActivity(Context context, String refer, String bindMobie, int bindType) {
        SecuritycodeActivity.launch(context, refer, bindMobie, bindType);
    }

    /**
     * ???????????????
     */
    public static void startRetrievePasswordActivity(Context context, String refer) {
        RetrievePasswordActivity.launch(context, refer);
    }

    /**
     * ???????????????
     */
    public static void startSetPasswordActivity(Context context, String refer, String token) {
        SetPasswordActivity.launch(context, refer, token);
    }

    /**
     * ???????????????
     */
    public static void startSetPasswordActivity(Context context, String refer, String mobileToken, String mobileNumber, boolean bindMobileWithLogin, String token) {
        SetPasswordActivity.launch(context, refer, mobileToken, mobileNumber, bindMobileWithLogin, token);
    }

    /**
     * ???????????????
     */
    public static void startSetPasswordActivity(Context context, String refer, String mobileToken, String mobileNumber,
                                                boolean isLoginBind, String token, String thirdOauthToken, String accessToken) {
        SetPasswordActivity.launch(context, refer, mobileToken, mobileNumber, isLoginBind, token, thirdOauthToken, accessToken);
    }

    /**
     * ????????????_????????????????????????
     */
    public static void startChangePasswordActivity(Context context, String refer) {
        ChangePasswordActivity.launch(context, refer);
    }

    /**
     * ???????????????????????????????????????
     */
    public static void startChangePasswordLoginActivity(Context context, String refer) {
        ChangePasswordLoginActivity.launch(context, refer);
    }

    /**
     * ????????????????????????
     * @param context
     * @param refer
     * @param type
     * @param content
     * @param requestCode
     */
    public static void startEditInfoActivity(Context context, String refer, int type, String content, int requestCode) {
        EditInfoActivity.launch(context, refer, type, content, requestCode);
    }

    /**
     * ???????????????
     *
     * @param context
     */
    public static void startActorDetail(Context context, String refer, String id) {
        ActorViewActivity.launch(context, refer, id);
    }

    public static void startActorDetail(Context context, String refer, String id, String name) {
        ActorViewActivity.launch(context, refer, id, name);
    }

    /**
     * ????????????_??????????????? ?????????????????????????????????Activity??????????????????
     *
     * @param context
     */
    public static void startSettingNoticeManageActivity(Context context, String refer) {
        if (!UserManager.Companion.getInstance().isLogin()) {
            startLoginActivity(context, refer);
            return;
        }
        SettingNoticeManageActivity.launch(context, refer);
    }

    /**
     * ?????????????????????
     */
    public static void startCinemaShowtimeActivity(Context context, String refer, String cinemaId, String movieId, String newDate, int requestCode) {
        NewCinemaShowtimeActivity.launch(context, refer, cinemaId, movieId, newDate, requestCode);
    }

    /**
     * ?????????????????????
     *
     * @param context
     */
    public static void startCinemaViewActivity(Context context, String refer, String cinemaId) {
        CinemaViewActivity.launch(context, refer, cinemaId);
    }

    // ?????????????????????
    public static void startMovieShowtimeActivity(Context context, String refer, String movieId, String movieEName, boolean movieTicket, String movieDate, int requestCode) {
        MovieShowtimeActivity.launch(context, refer, movieId, movieEName, movieTicket, movieDate, requestCode);
    }

    /**
     * ??????-??????-??????????????????????????????
     *
     * @param context
     */
    public static void startNewsPhotoDetailActivity(Context context, String refer, ArrayList<String> urlList, String newsId, int itemClicked, int from, boolean isFromReview) {
        NewsPhotoDetailActivity.launch(context, refer, urlList, newsId, itemClicked, from, isFromReview);
    }

    /**
     * ??????????????????
     */
    public static void startPostPhotoDetailActivity(Context context, ArrayList<String> urlList, int itemClicked, String refer) {
        NewsPhotoDetailActivity.launch(context, refer, urlList, "0", itemClicked, 1, false);
    }

    // ????????????
    public static void startCityChangeActivityForResult(AppCompatActivity context, String refer, int requestcode) {
        CityChangeActivity.launch(context, refer, requestcode);
    }

    /**
     * ???????????????
     *
     * @param context
     */
    public static void startActorFilmographyActivity(Context context, String refer, String personId, String personName, int workCount) {
        ActorFilmographyActivity.launch(context, refer, personId, personName, workCount);
    }

    /**
     * ?????????????????????
     *
     * @param context
     */
    public static void startActorHonorsActivity(Context context, String refer, String personId) {
        ActorHonorsActivity.launch(context, refer, personId);
    }

    // ???????????????
    public static void startPhotoListActivity(Context context, String refer, int type, String id, String title, String typeString) {
        MovieStillsActivity.launch(context, type, id, title, refer);
    }

    // ?????????????????????
    public static void startActorExperienceActivity(Context context, String refer, String personId) {
        ActorExperienceActivity.launch(context, refer, personId);
    }

    //???????????????
    public static void startTwitterActivity(Context context, String refer, int tweetId, int type, String title, int requestcode) {
        TwitterActivity.launch(context, refer, tweetId, type, title, requestcode);
    }

    //????????????????????????
    public static void startCommentImageDetailActivity(Context context, String imagePath) {
        CommentImageDetailActivity.launch(context, imagePath);
    }

    //??????????????????
    public static void startCompanyDetailActivity(Context context, String companyId, String companyName) {
        CompanyDetailActivity.launch(context, companyId, companyName);
    }

    //????????????????????????????????????
    public static void startGuideViewActivity(Context context, int tabIndex, String newRegisterGitUrl) {
        GuideViewActivity.launch(context, tabIndex, newRegisterGitUrl);
    }

    //????????????????????????
    public static void startMapViewActivity(Context context, double longitude, double latitude, String cinemaId, String cinemaName, String cinemaAddress, String title) {
        MapViewActivity.launch(context, longitude, latitude, cinemaId, cinemaName, cinemaAddress, title);
    }

    public static void startMemberCardListActivity(Context context) {
        MemberCardListActivity.launch(context);
    }

    //???????????????
    public static void startMovieShortCommentActivity(Context context, String refer, String movieId, String movieNameCN, String movieNameEN, String movieCommentTitle, int commentCount) {
        MovieShortCommentsActivity.launch(context, refer, movieId, movieNameCN, movieNameEN, movieCommentTitle, commentCount);
    }

    //?????????????????????
    public static void startPhotoDetailActivity(Context context, int targetType, ArrayList<Photo> photos, int clickPosition) {
        MovieStillsDetailActivity.launch(context, targetType, photos, clickPosition);
    }

    public static void startPictureActivityForResult(Activity context, String refer, int currentId, List<ImageBean> images, int requestCode) {
        PictureActivity.launch(context, refer, currentId, images, requestCode);
    }

    //?????????????????????
    public static void startPictureSelectActivityForResult(Activity context, String refer, List<ImageBean> imageBeans, int requestCode) {
        PictureSelectActivity.launch(context, refer, imageBeans, requestCode);
    }

    public static void startPictureSelectActivityForResult(Activity context, String refer, List<ImageBean> imageBeans,
                                                           int requestCode, int max) {
        PictureSelectActivity.launch(context, refer, imageBeans, requestCode, max, false);
    }

    public static void startPictureSelectActivityForResult(Activity context, String refer, List<ImageBean> imageBeans,
                                                           int requestCode, int max, boolean includeGif) {
        PictureSelectActivity.launch(context, refer, imageBeans, requestCode, max, includeGif);
    }

    //?????? ??????-->???????????????
    public static void startNewsCommentListActivity(Context context, String refer, String newsId, String reviewId, int commentSize) {
        NewsCommentListActivity.launch(context, refer, newsId, reviewId, commentSize);
    }

    //????????????
    public static void startTwitterCinemaActivity(Context context, int topicId, String title, int requestCode) {
        TwitterCinemaActivity.launch(context, topicId, title, requestCode);
    }

    //???????????????
    public static void startVideoListActivity(Context context, String refer, String movieId) {
        CategoryVideoListActivity.launch(context, refer, movieId);
    }

//    //?????????
//    public static void startSeatSelectActivity(Context context, String refer, String seatId, String mCinemaId, String movieId, String passDateString, String excel) {
//        SeatSelectActivity.launch(context, refer, seatId, mCinemaId, movieId, passDateString, excel);
//        JavaOpenSeatActivity.INSTANCE.openSeatActivity(seatId, null, movieId, mCinemaId, passDateString);
//    }

    //???????????????
    public static void startActorListActivity(Context activity, String refer, String movieId) {
        MovieActorListActivity.launch(activity, refer, movieId);
    }

    //????????????
    public static void startMovieSecretActivity(Context context, String refer, String movieId) {
        MovieEventActivity.launch(context, refer, movieId);
    }

    //???????????????
    public static void startHotLongCommentListActivity(Context context, String refer, String movieId, String movieNameTxt) {
        MovieLongCommentListActivity.launch(context, refer, movieId, movieNameTxt);
    }

    //???????????????
    public static void startFindFilmReviewDetailActivity(Context context, String refer, int reviewIndex, String reviewId, String activityFrom) {
//        FindFilmReviewDetailActivity.launch(context, refer, reviewIndex, reviewId, activityFrom);
        IUgcProvider instance = ProviderExtKt.getProvider(IUgcProvider.class);
        if (instance != null) {
            if (!TextUtils.isEmpty(reviewId) && TextUtils.isDigitsOnly(reviewId)) {
                instance.launchDetail(Long.parseLong(reviewId), CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT,0L,false);
            }
        }
    }

    //?????????????????????
    public static void startRelatedMovieActivity(Context context, String refer, String movieId) {
        MovieRelatedMovieActivity.launch(context, refer, movieId);
    }

    //??????????????????
    public static void startFindTopGlobalActivity(Context context, String refer, int type) {
//        FindTopGlobalActivity.launch(context, refer, type);
        MovieGlobalBoxOfficeActivity.launch(context, refer, type);
    }

    //???????????????
    public static void startMovieHonorsActivity(MovieDetailsBasic.Award award, Context context, String refer, String movieId) {
        MovieHonorListActivity.sAward = award;
        MovieHonorListActivity.launch(context, refer, movieId);
    }

    //????????????
    public static void startProducerListActivity(Context context, String refer, String movieId) {
        ProducerListActivity.launch(context, refer, movieId);
    }

    //????????????
    public static void startMediaReviewActivity(Context context, String refer, String movieId) {
        MovieMediaReviewActivity.launch(context, refer, movieId);
    }

    //????????????
    public static void startMovieMoreInfoActivity(Context context, String refer, String movieId) {
        MovieMoreInfoActivity.launch(context, refer, movieId);
    }

    //???????????????
    public static void startMediaVideoDetailActivity(Context context, String articleId, int vid, int videoSourceType, String recommendId, String recommendType, String refer) {
//        MediaVideoDetailActivity.launch(context, articleId, vid, videoSourceType, recommendId, recommendType, refer);
        IUgcProvider instance = ProviderExtKt.getProvider(IUgcProvider.class);
        if (instance != null) {
            if (!TextUtils.isEmpty(articleId) && TextUtils.isDigitsOnly(articleId)) {
                instance.launchDetail(Long.parseLong(articleId), CommConstant.PRAISE_OBJ_TYPE_ARTICLE,0L,false);
            }
        }
    }

    //????????????????????????
    public static void startPrevueVideoPlayerActivity(Context context, String videoId, int videoType, boolean continuePlay, String refer) {
        IVideoProvider instance = ProviderExtKt.getProvider(IVideoProvider.class);

        if (!TextUtils.isEmpty(videoId) &&TextUtils.isDigitsOnly(videoId)) {
            instance.startPreVideoActivity(Long.parseLong(videoId));
        }
//        PrevueVideoPlayerActivity.launch(context, videoId, videoType, continuePlay, refer);
    }

    /**
     * ???????????????
     */
    public static void startActivityInstructionActivity(Context context, String detail) {
        ActivitiesInstructionsActivity.launch(context, detail);
    }

    /**
     * ?????????H5??????
     *
     * @param context            ?????????
     * @param url                ??????
     * @param pageLabel          ????????????????????????????????????
     * @param adTag              ??????tag
     * @param isShowTitleBar     ????????????title
     * @param isShowBack         ????????????????????????
     * @param isShowClose        ????????????????????????
     * @param isHorizontalScreen ?????????????????????
     * @param refer              ??????????????????????????????????????????
     */
    public static void startCommonWebActivity(Context context, String url, String pageLabel, String adTag,
                                              boolean isShowTitleBar, boolean isShowBack, boolean isShowClose,
                                              boolean isHorizontalScreen, String refer) {
        startCommonWebActivity(context, url, pageLabel, adTag, isShowTitleBar, isShowBack, isShowClose, isHorizontalScreen, true, refer);
    }

    /**
     * ?????????H5??????
     *
     * @param context            ?????????
     * @param url                ??????
     * @param pageLabel          ????????????????????????????????????
     * @param adTag              ??????tag
     * @param isShowTitleBar     ????????????title
     * @param isShowBack         ????????????????????????
     * @param isShowClose        ????????????????????????
     * @param isHorizontalScreen ?????????????????????
     * @param isInterceptUrl     ????????????????????????????????????????????????
     * @param refer              ??????????????????????????????????????????
     */
    public static void startCommonWebActivity(Context context, String url, String pageLabel, String adTag,
                                              boolean isShowTitleBar, boolean isShowBack, boolean isShowClose,
                                              boolean isHorizontalScreen, boolean isInterceptUrl, String refer) {
        startCommonWebActivity(context, url, pageLabel, adTag, isShowTitleBar, isShowBack, isShowClose, isHorizontalScreen, isInterceptUrl, false, refer);
    }

    /**
     * ?????????H5??????
     *
     * @param context               ?????????
     * @param url                   ??????
     * @param pageLabel             ????????????????????????????????????
     * @param adTag                 ??????tag
     * @param isShowTitleBar        ????????????title
     * @param isShowBack            ????????????????????????
     * @param isShowClose           ????????????????????????
     * @param isHorizontalScreen    ?????????????????????
     * @param isInterceptUrl        ????????????????????????????????????????????????
     * @param isInterceptPrivateUrl ???????????????????????????????????????youku://
     * @param refer                 ??????????????????????????????????????????
     */
    public static void startCommonWebActivity(Context context, String url, String pageLabel, String adTag,
                                              boolean isShowTitleBar, boolean isShowBack, boolean isShowClose,
                                              boolean isHorizontalScreen, boolean isInterceptUrl, boolean isInterceptPrivateUrl, String refer) {
        CommonWebActivity.launch(context, url, pageLabel, adTag, isShowTitleBar, isShowBack, isShowClose, isHorizontalScreen, isInterceptUrl, isInterceptPrivateUrl, refer);
    }

    /**
     * ?????????????????????
     * @param activity ?????????null
     */
    public static void startMemberCenterActivity(Activity activity){
        IMineProvider instance = (IMineProvider) ProviderExtKt.getProvider(IMineProvider.class);
        if (instance != null) {
            if (activity != null) {
                instance.startMemberCenterActivity(activity);
            }else {
                instance.startMemberCenterActivity(null);
            }
        }
    }
}
