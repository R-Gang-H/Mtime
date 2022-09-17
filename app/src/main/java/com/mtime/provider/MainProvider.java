package com.mtime.provider;

import static com.kotlin.android.core.entity.PageFlagKt.PAGE_FLAG;
import static com.kotlin.android.ktx.ext.KeyExtKt.KEY_JPUSH_REGID;
import static com.kotlin.android.router.liveevent.EventKeyExtKt.LOGIN_STATE;
import static com.mtime.network.ConstantUrl.APP_DOWNLOAD_H5_URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.kotlin.android.app.router.ext.AppLinkExtKt;
import com.kotlin.android.app.router.liveevent.event.LoginState;
import com.kotlin.android.core.entity.PageFlag;
import com.kotlin.android.mtime.ktx.GlobalDimensionExt;
import com.kotlin.android.retrofit.cookie.CookieManager;
import com.kotlin.android.router.RouterManager;
import com.kotlin.android.app.router.path.RouterActivityPath;
import com.kotlin.android.app.router.path.RouterProviderPath;
import com.kotlin.android.app.router.provider.main.IMainProvider;
import com.kotlin.android.user.UserManager;
import com.kotlin.android.user.login.UserLoginKt;
import com.kotlin.android.user.login.jguang.JLoginManager;
import com.kotlin.android.widget.dialog.BaseDialogExtKt;
import com.mtime.R;
import com.mtime.applink.ApplinkManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.common.utils.MSharePreferenceHelper;
import com.mtime.bussiness.information.NewsPhotoDetailActivity;
import com.mtime.bussiness.main.MainTabActivity;
import com.mtime.bussiness.mine.bean.MessageConfigsSetBean;
import com.mtime.bussiness.mine.bean.StatusBean;
import com.mtime.bussiness.mine.profile.activity.ChangeMobileBindingActivity;
import com.mtime.bussiness.mine.profile.activity.SecuritycodeActivity;
import com.mtime.bussiness.mine.profile.widget.BirthDlg;
import com.mtime.bussiness.splash.LoadManager;
import com.mtime.bussiness.ticket.movie.activity.ActorViewActivity;
import com.mtime.common.cache.CacheManager;
import com.mtime.constant.SpManager;
import com.mtime.event.EventManager;
import com.mtime.frame.App;
import com.mtime.network.ConstantUrl;
import com.mtime.network.CookiesHelper;
import com.mtime.network.RequestCallback;
import com.mtime.util.CustomAlertDlg;
import com.mtime.util.HttpUtil;
import com.mtime.util.JumpUtil;
import com.mtime.util.ToolsUtils;
import com.mtime.util.UIUtil;
import com.mtime.widgets.PromotionPromptView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/4
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_MAIN)
public class MainProvider implements IMainProvider {

    private Context mContext;

    @Override
    public void init(Context context) {
        mContext = context;
    }

    @Override
    public void startCityChangeActivity() {
        RouterManager.Companion.getInstance().navigation(
                RouterActivityPath.Main.PAGER_CITY_CHANGE,
                null,
                null,
                -1,
                0,
                false,
                null
        );
    }

    @Override
    public void startCityChangeActivity(String address) {
        Bundle bundle = new Bundle();
        bundle.putString(App.getInstance().KEY_LOCATION_LEVEL_RELATION, address);
        RouterManager.Companion.getInstance().navigation(
                RouterActivityPath.Main.PAGER_LOCAL_SELECT,
                bundle,
                null,
                -1,
                0,
                false,
                null
        );
    }

    @Override
    public void startLoginActivity(@Nullable Bundle bundle) {
        UserLoginKt.gotoLoginPage(null, bundle, null);
    }

    @Override
    public void startLoginActivityForResult(@NotNull Activity activity, @Nullable Bundle bundle, int requestCode) {
        UserLoginKt.gotoLoginPage(activity, bundle, requestCode);
    }

    @Override
    public void startNormalLoginActivity(@Nullable Bundle bundle) {
        RouterManager.Companion.getInstance().navigation(
                RouterActivityPath.AppUser.PAGER_LOGIN,
                bundle,
                null,
                -1,
                0,
                false,
                null
        );
    }

    @Override
    public void startNormalLoginActivityForResult(@NotNull Activity activity, @Nullable Bundle bundle, int requestCode) {
        RouterManager.Companion.getInstance().navigation(
                RouterActivityPath.AppUser.PAGER_LOGIN,
                bundle,
                activity,
                requestCode,
                0,
                false,
                null
        );
    }

    @Override
    public void startForApplink(@NotNull String applink) {
        ApplinkManager.jump(mContext, applink, null);
    }

    @Override
    public void startPhotoDetailActivity(@NotNull ArrayList<String> urlList, int itemClicked) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(NewsPhotoDetailActivity.INTENT_PHOTO_LIST_DATA, urlList);
        bundle.putInt(NewsPhotoDetailActivity.INTENT_PHOTO_LIST_POSITION_CLICKED, itemClicked);
        bundle.putInt(NewsPhotoDetailActivity.INTENT_FROM, 1);
        RouterManager.Companion.getInstance().navigation(
                RouterActivityPath.Main.PAGE_PHOTO_DETAIL,
                bundle,
                null,
                -1,
                0,
                false,
                null
        );
    }

    @Override
    public void startActorViewActivity(long id, @NotNull String name) {
        Bundle bundle = new Bundle();
        bundle.putString(ActorViewActivity.MOVIE_PERSOM_ID, String.valueOf(id));
        bundle.putString(ActorViewActivity.KEY_MOVIE_PERSOM_NAME, name);
        RouterManager.Companion.getInstance().navigation(
                RouterActivityPath.Main.PAGE_ACTORVIEW,
                bundle,
                null,
                -1,
                0,
                false,
                null
        );
    }

    @Override
    public void startCinemaViewActivity(long cinemaId) {
        Bundle bundle = new Bundle();
        bundle.putString(App.getInstance().KEY_CINEMA_ID, String.valueOf(cinemaId));
        RouterManager.Companion.getInstance().navigation(
                RouterActivityPath.Main.PAGE_CINEMAVIEW,
                bundle,
                null,
                -1,
                0,
                false,
                null
        );
    }

    @Override
    public void startOrderDetailActivity(long orderId, boolean isETicket, boolean withoutPay, boolean isFromAccount) {
        Bundle bundle = new Bundle();
        bundle.putString(App.getInstance().KEY_SEATING_ORDER_ID, String.valueOf(orderId));
        bundle.putBoolean(App.getInstance().PAY_ETICKET, isETicket);
        bundle.putBoolean(App.getInstance().KEY_IS_DO_WITH_OUT_PAY_ORDER, withoutPay);
        bundle.putBoolean(App.getInstance().KEY_ISFROM_ACCOUNT, isFromAccount);
        RouterManager.Companion.getInstance().navigation(
                RouterActivityPath.Main.PAGE_ORDER_DETAIL,
                bundle,
                null,
                -1,
                0,
                false,
                null
        );
    }

    @Override
    public void startPhotoDetailActivityFromAlbum(@NotNull ArrayList<String> urlList, int itemClicked, long albumId, long albumUserId, @NotNull long[] imageIdList) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(NewsPhotoDetailActivity.INTENT_PHOTO_LIST_DATA, urlList);
        bundle.putInt(NewsPhotoDetailActivity.INTENT_PHOTO_LIST_POSITION_CLICKED, itemClicked);
        bundle.putInt(NewsPhotoDetailActivity.INTENT_FROM, 1);

        bundle.putLongArray(NewsPhotoDetailActivity.INTENT_IMAGEID_LIST, imageIdList);
        bundle.putLong(NewsPhotoDetailActivity.INTENT_ALBUM_ID, albumId);
        bundle.putLong(NewsPhotoDetailActivity.INTENT_ALBUM_USER_ID, albumUserId);
        RouterManager.Companion.getInstance().navigation(
                RouterActivityPath.Main.PAGE_PHOTO_DETAIL,
                bundle,
                null,
                -1,
                0,
                false,
                null
        );
    }

    @Override
    public void startMainActivity(
            @androidx.annotation.Nullable PageFlag flag,
            @androidx.annotation.Nullable String applink
    ) {
        MainTabActivity.Companion.start(App.getInstance(), flag, applink);
    }

    @Override
    public void startForApplinkH5(@NotNull String applink) {
        ApplinkManager.jump4H5(mContext, applink, null);
    }

    @Override
    public void startGuideViewActivity(@NotNull Context context) {
        JumpUtil.startGuideViewActivity(context, LoadManager.getAndroidTab(), LoadManager.getNewPeopleGiftImage());
    }

    @Override
    public void startCinemaShowtimeActivity(@NotNull Context context, @NotNull String cinemaId, @NotNull String movieId, @NotNull String newDate) {
        JumpUtil.startCinemaShowtimeActivity(context, "", cinemaId, movieId, newDate, 0);
    }

    @Override
    public void startVideoListActivity(@NotNull Context context, @NotNull String movieId) {
        JumpUtil.startVideoListActivity(context, "", movieId);
    }

    @Override
    public void pushReceiverOnRegister(Context context, @androidx.annotation.Nullable String registrationID) {
        // 保存并上传registration id
        MSharePreferenceHelper.get().putString(KEY_JPUSH_REGID, registrationID);

        Map<String, String> parameterList = new ArrayMap<String, String>(4);
        parameterList.put("deviceToken", ToolsUtils.getToken(context.getApplicationContext()));
        parameterList.put("setMessageConfigType", String.valueOf(1));
        String location = GlobalDimensionExt.INSTANCE.getCurrentCityId();
        parameterList.put("locationId", location);
        parameterList.put("jPushRegId", registrationID);
        // 发送push的id的接口
        HttpUtil.post(ConstantUrl.SET_MESSAGECONFIGS, parameterList, MessageConfigsSetBean.class, null);
    }

    @Override
    public void onNotifyMessageOpened(@Nullable Context context, @Nullable String applinkData) {
        AppLinkExtKt.parseAppLink(context, applinkData);
    }

    @Override
    public void onNotifyMessageArrived(@Nullable Context context, @Nullable String applinkData) {

        //通知到达回调
        //例如  前台时展示自定义弹框、在前台时清除通知栏的操作可以写在这里

        //预留方法  需求待定
    }

    @Override
    public void showCustomAlertDlg(@Nullable Context context, @NonNull String message) {
        CustomAlertDlg changeErrorDlg = new CustomAlertDlg(context, CustomAlertDlg.TYPE_OK);
        changeErrorDlg.setBtnOKListener(view -> changeErrorDlg.dismiss());
        changeErrorDlg.show();
        changeErrorDlg.setText(message);
    }

    @Override
    public void showDatePicker(@Nullable Context context, int initYear, int initMonthOfYear, int initDayOfMonth, @androidx.annotation.Nullable Function3<? super Integer, ? super Integer, ? super Integer, Unit> okListener) {
        new BirthDlg(context, initYear, initMonthOfYear, initDayOfMonth, (year, monthOfYear, dayOfMonth) -> okListener.invoke(year, monthOfYear, dayOfMonth)).show();
    }

    @Override
    public void showToast(@NonNull String message) {
        MToastUtils.showShortToast(message);
    }

    @Override
    public void callNumber(@Nullable Context context, @NonNull String number) {
        JumpUtil.actionCall(context, number);
    }

    @Override
    public void startChangePhoneNum(@NonNull Context context) {
        context.startActivity(new Intent(context, SecuritycodeActivity.class).putExtra(App.getInstance().KEY_BINDPHONE, UserManager.Companion.getInstance().getBindMobile()).putExtra(App.getInstance().KEY_BIND_TYPE, 1));
    }

    @Override
    public void modifyPwd(@NonNull Context context) {
        JumpUtil.startChangePasswordActivity(context, "");
    }

    @Override
    public void upgradeAppDialog(@NonNull Context context) {
        if (context instanceof Activity) {
            BaseDialogExtKt.showDialog(
                    context,
                    R.string.upgrade_content,
                    R.string.away_upgrade,
                    R.string.widget_sure,
                    R.string.widget_cancel,
                    null,
                    () -> {
                        AppLinkExtKt.callBrowser(context, APP_DOWNLOAD_H5_URL);
                        return null;
                    }
            );
        }
    }

    @Override
    public void showCommonAlertDlg(@Nullable Context context, @NonNull String message, @Nullable Function0<Unit> finishListener) {
        CustomAlertDlg changeErrorDlg = new CustomAlertDlg(context, CustomAlertDlg.TYPE_OK);
        changeErrorDlg.setBtnOKListener(view -> {
            changeErrorDlg.dismiss();
            UIUtil.showLoadingDialog(context);
            HttpUtil.post(ConstantUrl.POST_LOGOUT, StatusBean.class, new RequestCallback() {
                public void onSuccess(final Object o) {
                    UIUtil.dismissLoadingDialog();
                    StatusBean successBean = (StatusBean) o;
                    if (successBean.isSuccess()) {
                        // 清空未读消息
                        App.getInstance().UNREADMESSAGEBEAN = null;
                        // 清除任何与用户有关的信息
//                            NetworkManager.getInstance().mCookieJarManager.clear();
                        CookieManager.Companion.getInstance().clear();
//                            AccountManager.removeAccountInfo();
                        UserManager.Companion.getInstance().clear();
                        SpManager.clearUsageRecord();
                        PromotionPromptView.removeNextRequestTime(context);

                        CacheManager.getInstance().cleanAllFileCache();
                        CookiesHelper.clearWebViewCache();
                        CookiesHelper.clearCookies();
                        CookiesHelper.initialize = true;
                        File cacheDir = new File(context.getCacheDir(), "volley");
                        if (cacheDir != null && cacheDir.isDirectory()) {
                            File[] fileList = cacheDir.listFiles();
                            for (int i = 0; i < fileList.length; i++) {
                                fileList[i].delete();
                            }
                        }

                        String pushtoken = ToolsUtils.getToken(context.getApplicationContext());
                        String jpushid = ToolsUtils.getJPushId(context.getApplicationContext());

                        if (!TextUtils.isEmpty(pushtoken) || !TextUtils.isEmpty(jpushid)) {
                            Map<String, String> parameterList = new ArrayMap<String, String>(3);
                            parameterList.put("deviceToken", pushtoken);
                            parameterList.put("setMessageConfigType", String.valueOf(2));
                            parameterList.put("jPushRegId", jpushid);
                            HttpUtil.post(ConstantUrl.SET_MESSAGECONFIGS, parameterList, MessageConfigsSetBean.class, null);
                        }
                        MToastUtils.showShortToast(R.string.str_logout_success);
                        JLoginManager.Companion.getInstance().prepare();
                        LiveEventBus.get(LOGIN_STATE).post(new LoginState(false));
                        EventManager.getInstance().sendLogoutEvent();
                        finishListener.invoke();
                    } else {
                        MToastUtils.showShortToast("登出失败");
                    }
                }

                @Override
                public void onFail(final Exception e) {
                    UIUtil.dismissLoadingDialog();
                    String tip = "退出登录失败" + (TextUtils.isEmpty(e.getLocalizedMessage()) ? "" : ":" + e.getLocalizedMessage());
                    MToastUtils.showShortToast(tip);
                }
            });
        });
        changeErrorDlg.setBtnCancelListener(view -> changeErrorDlg.dismiss());
        changeErrorDlg.show();
        changeErrorDlg.setText(message);
    }

    @Override
    public void startOrderConfirmActivity(
            @NonNull String seatIds,
            int seatCount,
            double totalPrice,
            double serviceFee,
            @Nullable String buyPhone,
            @Nullable String movieName,
            @Nullable String cinemaName,
            @Nullable String ticketDateInfo,
            @Nullable String seatSelectedInfo,
            @Nullable String orderId,
            @Nullable String subOrderId,
            boolean isFromSeatActivity,
            @Nullable String dId,
            @Nullable String movieId,
            @Nullable String cinemaId,
            @Nullable String showtimeDate,
            @Nullable String ticketTime,
            @Nullable String ticketHallName,
            @Nullable String ticketVersion,
            @Nullable String ticketLanguage
    ) {
        Bundle bundle = new Bundle();
        bundle.putString(App.getInstance().KEY_SEATING_SEAT_ID, seatIds);
        bundle.putInt(App.getInstance().KEY_SEATING_SELECTED_SEAT_COUNT, seatCount);
        bundle.putDouble(App.getInstance().KEY_SEATING_TOTAL_PRICE, totalPrice);
        bundle.putDouble(App.getInstance().KEY_SEATING_SERVICE_FEE, serviceFee);
        bundle.putString(App.getInstance().KEY_USER_BUY_TICKET_PHONE, buyPhone);
        bundle.putString(App.getInstance().KEY_MOVIE_NAME, movieName);
        bundle.putString(App.getInstance().KEY_CINEMA_NAME, cinemaName);
        bundle.putString(App.getInstance().KEY_TICKET_DATE_INFO, ticketDateInfo);
        bundle.putString(App.getInstance().KEY_SEAT_SELECTED_INFO, seatSelectedInfo);
        bundle.putString(App.getInstance().KEY_SEATING_SUBORDER_ID, subOrderId);
        bundle.putBoolean(App.getInstance().IS_FROM_SEATSELECT_ACTIVITY, isFromSeatActivity);
        bundle.putString(App.getInstance().KEY_SEATING_DID, dId);
        bundle.putString(App.getInstance().KEY_MOVIE_ID, movieId);
        bundle.putString(App.getInstance().KEY_CINEMA_ID, cinemaId);
        bundle.putString(App.getInstance().KEY_SHOWTIME_DATE, showtimeDate);
        bundle.putString(App.getInstance().KEY_TICKET_TIME_INFO, ticketTime);
        bundle.putString(App.getInstance().KEY_TICKET_HALLNAME_INFO, ticketHallName);
        bundle.putString(App.getInstance().KEY_TICKET_VERSIONDESC_INFO, ticketVersion);
        bundle.putString(App.getInstance().KEY_TICKET_LANGUAGE_INFO, ticketLanguage);

        RouterManager.Companion.getInstance().navigation(
                RouterActivityPath.Main.PAGER_ORDER_CONFIRM_ACTIVITY,
                bundle,
                null,
                -1,
                0,
                false,
                null
        );
    }

    @Override
    public void startOrderPayActivity(
            @NonNull String dId,
            @Nullable String orderId,
            @Nullable String subOrderId,
            boolean isETicket,
            boolean isFromSeatActivity,
            double totalPrice,
            double serviceFee,
            long payEndTime,
            @Nullable String buyPhone,
            @NonNull String seatIds,
            int seatCount,
            @Nullable String ticketDateInfo,
            @Nullable String seatSelectedInfo
    ) {
        Bundle bundle = new Bundle();
        bundle.putString(App.getInstance().KEY_SEATING_DID, dId);
        bundle.putString(App.getInstance().KEY_SEATING_ORDER_ID, orderId);
        bundle.putString(App.getInstance().KEY_SEATING_SUBORDER_ID, subOrderId);
        bundle.putBoolean(App.getInstance().PAY_ETICKET, isETicket);
        bundle.putBoolean(App.getInstance().IS_FROM_SEATSELECT_ACTIVITY, isFromSeatActivity);
        bundle.putDouble(App.getInstance().KEY_SEATING_TOTAL_PRICE, totalPrice);
        bundle.putDouble(App.getInstance().KEY_SEATING_SERVICE_FEE, serviceFee);
        bundle.putLong(App.getInstance().KEY_SEATING_PAY_ENDTIME, payEndTime);
        bundle.putString(App.getInstance().KEY_USER_BUY_TICKET_PHONE, buyPhone);
        bundle.putString(App.getInstance().KEY_SEATING_SEAT_ID, seatIds);
        bundle.putInt(App.getInstance().KEY_SEATING_SELECTED_SEAT_COUNT, seatCount);
        bundle.putString(App.getInstance().KEY_TICKET_DATE_INFO, ticketDateInfo);
        bundle.putString(App.getInstance().KEY_SEAT_SELECTED_INFO, seatSelectedInfo);

        RouterManager.Companion.getInstance().navigation(
                RouterActivityPath.Main.PAGER_ORDER_PAY_ACTIVITY,
                bundle,
                null,
                -1,
                0,
                false,
                null
        );
    }
}
