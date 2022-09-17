package com.mtime.applink;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;

import com.kotlin.android.app.router.ext.AppLinkExtKt;
import com.kotlin.android.ktx.ext.uri.UrlExtKt;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.MLogWriter;
import com.mtime.base.utils.MToastUtils;
import com.mtime.constant.AppConstants;
import com.mtime.network.ConstantUrl;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.push.StatisticPush;
import com.mtime.util.MtimeUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Applink跳转协议
 * http://wiki.inc-mtime.com/pages/viewpage.action?pageId=44204142
 */
public class ApplinkManager {

    public static void jump(Context context, String applinkData, String refer) {
        jump(context, applinkData, refer, null);
    }

    public static void jump4H5(Context context, String applinkData, String refer) {
        jump4H5(context, applinkData, refer, null);
    }

    public static void jump4Push(Context context, String applinkData) {
        jump4Push(context, applinkData, null);
    }

    public static void jump4Scheme(Context context, String applinkData) {
        jump4Scheme(context, applinkData, null);
    }

    public static void jump(Context context, String applinkData, String refer, ApplinkListener listener) {
        parseApplinkData(context, applinkData, refer, ApplinkFrom.Internal, listener);
    }

    public static void jump4H5(Context context, String applinkData, String refer, ApplinkListener listener) {
        parseApplinkData(context, applinkData, refer, ApplinkFrom.Internal_H5, listener);
    }

    public static void jump4Push(Context context, String applinkData, ApplinkListener listener) {
        parseApplinkData(context, applinkData, ApplinkFrom.getValue(ApplinkFrom.Push), ApplinkFrom.Push, listener);
    }

    public static void jump4Scheme(Context context, String applinkData, ApplinkListener listener) {
        parseApplinkData(context, applinkData, ApplinkFrom.getValue(ApplinkFrom.Scheme_H5), ApplinkFrom.Scheme_H5, listener);
    }

    private static void parseApplinkData(Context context, String applinkData, String refer, ApplinkFrom from, ApplinkListener listener) {
        JSONObject jsonObject = null;
        try {
            String baseData = UrlExtKt.urlDecode(applinkData, "UTF-8");
            if (TextUtils.isEmpty(baseData)) {
                MToastUtils.showLongToast("applink Data error");
                return;
            }
            jsonObject = new JSONObject(baseData);
        } catch (Exception e) {
            MLogWriter.e("ApplinkManager", e.getMessage());
        }

        if (jsonObject == null) {
            MToastUtils.showLongToast("applink Data error");
            return;
        }

        switch (jsonObject.optString("handleType")) {
            case ApplinkConstants.HANDLE_TYPE_GET_DATA_BY_MSG_ID:
                getPushMsgByMsgId(context,
                        jsonObject.optString("source"),
                        jsonObject.optString("msgID"),
                        refer, from, listener);
                break;

            case ApplinkConstants.HANDLE_TYPE_JUMP_PAGE:
                //上报push----保证reacheID
                if (from == ApplinkFrom.Push) {
                    submitPush();
                }
                AppLinkExtKt.parseAppLink(context, applinkData);
                //jumpPage(context, jsonObject, refer, listener);
                break;
            default:
                showUpdateAppDialog(context);
                break;
        }
    }

    /**
     * push 上报
     */
    private static void submitPush() {
        StatisticPageBean bean = new StatisticPageBean();
        bean.pageName = StatisticPush.PAGE_PUSH;
        bean.path = new HashMap<>();
        bean.path.put(StatisticConstant.AREA1, StatisticPush.PUSH_ONE);
        Map<String, String> businessParam = new HashMap<>();
        businessParam.put(StatisticPush.PUSH_REACH_ID, AppConstants.getInstance().getPushReachId());
        bean.businessParam = businessParam;
        StatisticManager.getInstance().submit(bean);
    }

    /**
     * 预处理------app退出热启动情况，如果不预处理，跳转到相关页面，返回时直接退出app了
     *
     * @param context
     */
//    private static void preJump(Context context) {
//        // 如果app没有启动，则先启动
//        if (!Constants.sAppStartup) {
////            JumpUtil.jumpHomeRecommend(context, null);
//            JumpUtil.startMainActivity(context, 0, null);
//        }
//    }

    // http://wiki.inc-mtime.com/pages/viewpage.action?pageId=44204144
    private static void getPushMsgByMsgId(Context context, String source, String messageId, String refer, ApplinkFrom from, ApplinkListener listener) {
        new ApplinkApi().getPushMsgByMsgId(source, messageId, new NetworkManager.NetworkListener<ApplinkPushInfoBean>() {
            @Override
            public void onSuccess(ApplinkPushInfoBean bean, String s) {
                if (null != bean && !TextUtils.isEmpty(bean.applinkData)) {
                    AppConstants.getInstance().setPushReachId(bean.reach_id);
                    parseApplinkData(context, bean.applinkData, refer, from, listener);
                }
            }

            @Override
            public void onFailure(NetworkException<ApplinkPushInfoBean> networkException, String s) {
            }
        });
    }

    private static void showUpdateAppDialog(Context context) {
        if (context instanceof Activity) {
            new AlertDialog.Builder(context)
                    .setMessage("您当前版本过低，不支持此功能，需要升级")
                    .setNeutralButton("取消", (dialog, which) -> dialog.cancel())
                    .setNegativeButton("立即升级", (dialog, which) -> {
                        dialog.cancel();
                        MtimeUtils.callBrowser(context, ConstantUrl.APP_DOWNLOAD_H5_URL);
                    }).show();
        }
    }

    /*private static void jumpPage(Context context, JSONObject jsonObject, String refer, ApplinkListener listener) {
        preJump(context);

        final String pageType = jsonObject.optString("pageType");
        if (null != listener && listener.onJumpPage(pageType, jsonObject, refer)) {
            return;
        }
        switch (pageType) {
            case ApplinkConstants.PAGE_TYPE_h5: {
                //1、H5界面（社区活动H5界面专用，指定URL，只是换ID而已）
                final String url = jsonObject.optString("url");
                final boolean isOpenByBrowser = TextUtils.equals("YES", jsonObject.optString("isOpenByBrowser", "NO"));
                final boolean isHorizontalScreen = TextUtils.equals("YES", jsonObject.optString("isHorizontalScreen", "NO"));
                if (isOpenByBrowser) {
                    MtimeUtils.callBrowser(context, url);
                } else {
                    JumpUtil.startCommonWebActivity(context, url, StatisticH5.PN_H5, null,
                            true, true, true, isHorizontalScreen, refer);
                }
                break;
            }
            case ApplinkConstants.PAGE_TYPE_home: {
                //2、首页
                JumpUtil.startMainActivity(context, 0, refer);
                break;
            }

            case ApplinkConstants.PAGE_TYPE_onShowList: {
                //3、底部Tab购票-正在热映
                JumpUtil.startMainMovieHot(context, refer);
                break;
            }

            case ApplinkConstants.PAGE_TYPE_futureSchedule: {
                //4、底部Tab购票-即将上映
                JumpUtil.startMainMovieComing(context, refer);
                break;
            }

            case ApplinkConstants.PAGE_TYPE_movieDetail: {
                //5、影片详情页
                final String movieId = jsonObject.optString("movieId");
                if (!TextUtils.isEmpty(movieId)) {
                    JumpUtil.startMovieInfoActivity(context, refer, movieId, 0);
                }
                break;
            }

            case ApplinkConstants.PAGE_TYPE_generalDetails: {
                //6、通用详情页 （文章详情页 、长、短影评详情页、帖子详情页、日志详情页 为一个页面 统称为 通用详情页、UGC详情页）
                String contentId = jsonObject.optString("contentId");
                String type = jsonObject.optString("type");
                IUgcProvider ugcProvider = ProviderExtKt.getProvider(IUgcProvider.class);
                if (ugcProvider != null) {
                    ugcProvider.launchDetail(
                            Long.parseLong(contentId),
                            Long.parseLong(type),
                            false,
                            true,
                            false
                    );
                }
                break;
            }

            case ApplinkConstants.PAGE_TYPE_newMovieScore: {
                //7、新的电影评分页
                String movieId = jsonObject.optString("movieId");
                String movieName = jsonObject.optString("movieName");
                IPublishProvider provider = ProviderExtKt.getProvider(IPublishProvider.class);
                if (null != provider) {
                    provider.startPublishActivity(PUBLISH_FILM_COMMENT, Long.parseLong(movieId), movieName, 0L, "");
                }
                break;
            }

            case ApplinkConstants.PAGE_TYPE_memberCenter: {
                //8、会员中心-影迷俱乐部
                IMineProvider provider = ProviderExtKt.getProvider(IMineProvider.class);
                if (provider != null) {
                    provider.startMemberCenterActivity(null, null);
                }
                break;
            }

            case ApplinkConstants.PAGE_TYPE_ticketOrderList: {
                //9、电影票订单列表页
                ITicketOrderProvider ticketOrderProvider = ProviderExtKt.getProvider(ITicketOrderProvider.class);
                if (ticketOrderProvider != null) {
                    ticketOrderProvider.startTicketOrderListActivity(null, null);
                }
                break;
            }

            case ApplinkConstants.PAGE_TYPE_wallet: {
                //10、个人钱包
                IMineProvider provider = ProviderExtKt.getProvider(IMineProvider.class);
                if (provider != null) {
                    provider.startMyWalletActivity(null, null);
                }
                break;
            }

            case ApplinkConstants.PAGE_TYPE_login: {
                //11、登录
                JumpUtil.startLoginActivity(context, refer);
                break;
            }

            case ApplinkConstants.PAGE_TYPE_starDetail: {
                //12、影人详情页
                final String starId = jsonObject.optString("starId");
                if (!TextUtils.isEmpty(starId)) {
                    JumpUtil.startActorDetail(context, refer, starId);
                }
                break;
            }

            case ApplinkConstants.PAGE_TYPE_cinemaTime: {
                //13、影院排片页
                final String cinemaId = jsonObject.optString("cinemaId");
                final String movieId = jsonObject.optString("movieId"); //（可选）
                final String date = jsonObject.optString("date"); //（可选）日期，格式为yyyymmdd，例如20170218
                JumpUtil.startCinemaShowtimeActivity(context, refer, cinemaId, movieId, date, 0);
                break;
            }

            case ApplinkConstants.PAGE_TYPE_movieTime: {
                //14、影片排片页
                final String movieId = jsonObject.optString("movieId");
                if (!TextUtils.isEmpty(movieId)) {
                    JumpUtil.startMovieShowtimeActivity(context, refer, movieId, null, false, null, 0);
                }
                break;
            }

            case ApplinkConstants.PAGE_TYPE_videoList: {
                //15、影片预告片列表页
                final String movieId = jsonObject.optString("movieId");
                if (!TextUtils.isEmpty(movieId)) {
                    JumpUtil.startVideoListActivity(context, refer, movieId);
                }
                break;
            }

            case ApplinkConstants.PAGE_TYPE_cinemaList: {
                //16、购票-影院列表
                JumpUtil.startMainCinemaList(context, refer);
                break;
            }

            case ApplinkConstants.PAGE_TYPE_cinemaDetail: {
                //17、影院详情页
                final String cinemaId = jsonObject.optString("cinemaId");
                if (!TextUtils.isEmpty(cinemaId)) {
                    JumpUtil.startCinemaViewActivity(context, refer, cinemaId);
                }
                break;
            }

            case ApplinkConstants.PAGE_TYPE_onlineTicket: {
                //18、选座页
                final String showTimeId = jsonObject.optString("showTimeId");
                final String date = jsonObject.optString("date");
                final String movieId = jsonObject.optString("movieId");
                final String cinemaId = jsonObject.optString("cinemaId");
                if (!TextUtils.isEmpty(showTimeId)) {
                    JumpUtil.startSeatSelectActivity(context, refer, showTimeId, cinemaId, movieId, date, null);
                }
                break;
            }

            case ApplinkConstants.PAGE_TYPE_dailyRecommend: {
                //19、每日推荐
                JumpUtil.jumpDailyRecommend(context, refer);
                break;
            }

            case ApplinkConstants.PAGE_TYPE_community: {
                //20、社区首页
                JumpUtil.startMainActivity(context, 2, refer);
                break;
            }

            case ApplinkConstants.PAGE_TYPE_groupDetails: {
                //21、家族详情页
                String id = jsonObject.optString("groupID");
                ICommunityFamilyProvider provider = ProviderExtKt.getProvider(ICommunityFamilyProvider.class);
                if (provider != null) {
                    provider.startFamilyDetail(Long.parseLong(id));
                }
                break;
            }

            case ApplinkConstants.PAGE_TYPE_groupSquare: {
                //22、家族广场
                ICommunityFamilyProvider provider = ProviderExtKt.getProvider(ICommunityFamilyProvider.class);
                if (provider != null) {
                    provider.startFamilyClassList();
                }
                break;
            }

            case ApplinkConstants.PAGE_TYPE_albumDetails: {
                //23、相册详情页
                IUgcProvider provider = ProviderExtKt.getProvider(IUgcProvider.class);
                String albumId = jsonObject.optString("albumId");
                if (null != provider && !TextUtils.isEmpty(albumId)) {
                    provider.startAlbumDetail(Long.parseLong(albumId));
                }
                break;
            }

            case ApplinkConstants.PAGE_TYPE_userPocket: {
                //24、卡片大富翁- 用户口袋
                String userId = jsonObject.optString("userId");
                ICardMonopolyProvider provider = ProviderExtKt.getProvider(ICardMonopolyProvider.class);
                if (null != provider && !TextUtils.isEmpty(userId)) {
                    provider.startCardMainActivity(null, Long.parseLong(userId), AnnotationExtKt.CARD_MONOPOLY_UNKNOWN, null);
                }
                break;
            }

            case ApplinkConstants.PAGE_TYPE_videoPlayDetail: {
                //25、视频详情页  ( 同一页面不同叫法: 预告片播放详情页)
                String videoId = jsonObject.optString("videoId");
                String videoSource = jsonObject.optString("videoSource");
                IVideoProvider provider = ProviderExtKt.getProvider(IVideoProvider.class);
                if (provider != null && !TextUtils.isEmpty(videoId)) {
                    provider.startPreVideoActivity(Long.parseLong(videoId));
                }
                break;
            }

            case ApplinkConstants.PAGE_TYPE_commonListDetail: {
                //26、榜单详情页 ( 备注：指的是 影人、影片、电视剧榜单详情页， iOS 是三个独立的页面、android 是一个通用页面)
                String listID = jsonObject.optString("listID");
                IHomeProvider provider = ProviderExtKt.getProvider(IHomeProvider.class);
                if (provider != null && !TextUtils.isEmpty(listID)) {
                    provider.startToplistDetailActivity(Long.parseLong(listID));
                }
                break;
            }

            case ApplinkConstants.PAGE_TYPE_userProfile: {
                //27、用户个人主页
                String userId = jsonObject.optString("userId");
                ICommunityPersonProvider provider = ProviderExtKt.getProvider(ICommunityPersonProvider.class);
                if (provider != null && !TextUtils.isEmpty(userId)) {
                    provider.startPerson(Long.parseLong(userId), 0L, null, null);
                }
                break;
            }

            default:
                showUpdateAppDialog(context);
                break;
        }
    }*/
}
