package com.mtime.bussiness.splash;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.kotlin.android.app.api.download.DownloadManager;
import com.kotlin.android.app.api.download.listener.DownloadListener;
import com.kotlin.android.app.data.ext.VariateExt;
import com.kotlin.android.mtime.ktx.FileEnv;
import com.mtime.bussiness.splash.bean.PullRefreshFilmWord;
import com.mtime.bussiness.splash.bean.SplashLoadingIconBean;
import com.mtime.bussiness.splash.bean.SplashSeatsIconBean;
import com.mtime.bussiness.splash.bean.SplashSeatsIconList;
import com.mtime.bussiness.splash.bean.SplashStartLoad;
import com.mtime.common.utils.LogWriter;
import com.mtime.frame.App;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by CSY on 2018/5/8.
 * 闪屏页预加载信息
 */
public class LoadManager {
    public static String SEAT_ICON_SP_PREFIX = "Seat_Icon_";

    //全局是否强制绑定手机的开关
    private static final boolean isOnlyMemberBuyTicket = true; // false //是否只有会员能购票，false表示非会员也能购票，true表示只有会员能购票。
    //强制性别
    private static final boolean isEditGender = true;
    // 增加一个标记，标记允许运行的url host地址。
    private static final String allowUsedHost = null; // ;.mtime.com;.weixin.com;.qq.com;weibo.com
    private static final boolean allowUsingHost = false;
    //商城域名
    private static final String mallDomain = "mall.wv.mtime.cn/#!";
    //图片代理地址
    private static final String imageProxy = VariateExt.INSTANCE.getImgProxyUrl();

    private static final String Key_LOAD_INFO = "key_load_info";
    private static final String Key_SERVICE_URL = "key_user_agreement_url";
    //loading加载图
    private static final String LOAD_ICON_URL = "load_icon_url";
    //loading加载失败图
    private static final String LOAD_FAIL_ICON_URL = "load_fail_icon_url";
    //location加载失败图
    private static final String LOAD_LOCATION_FAIL_ICON_URL = "load_location_fail_icon_url";

    private static SplashStartLoad splashStartLoad = null;

    private static final String AD_STATISTIC_URL = "ad_statistic_url";

    /**
     * 是否强制绑定手机的开关
     */
    public static boolean isOnlyMemberBuyTicket() {
        return isOnlyMemberBuyTicket;
    }

    /**
     * 获取登录文案
     */
    public static String getLoginText() {
        return splashStartLoad != null ? splashStartLoad.getLoginText() : "";
    }

    /**
     * 找回密码提示文案
     */
    public static String getFindPasswordText() {
        return splashStartLoad != null ? splashStartLoad.getFindPasswordText() : "";
    }

    /**
     * host白名单地址。
     */
    public static String getAllowHost() {
        return splashStartLoad != null ? splashStartLoad.getAllowHost() : "";
    }


    /**
     * 是否检测host白名单地址
     */
    public static boolean isCheckHost() {
        return splashStartLoad != null ? splashStartLoad.isCheckHost() : allowUsingHost;
    }

    /**
     * 商城域名
     */
    public static String getMallDomain() {
        return splashStartLoad != null ? splashStartLoad.getMallDomain() : mallDomain;
    }

    /**
     * 强制性别开关
     */
    public static boolean getIsEditGender() {
        return splashStartLoad != null ? splashStartLoad.getIsEditGender() : isEditGender;
    }

    /**
     * 影片广告词
     */
    public static List<PullRefreshFilmWord> getMovieAdvlist() {
        return splashStartLoad != null ? splashStartLoad.getMovieAdvlist() : null;
    }

    /**
     * 图片代理地址
     */
    public static String getImageProxy() {
        return splashStartLoad != null ? splashStartLoad.getImageProxy() : imageProxy;
    }

    /**
     * 服务条款地址
     */
    public static String getRegisterServiceUrl() {
        return splashStartLoad != null ? splashStartLoad.getRegisterServiceUrl() : "https://general.mtime.cn/help/policy/index.html";
    }

    /**
     * 隐私政策地址
     */
    public static String getRegisterPrivacyUrl() {
        return "https://general.mtime.cn/help/privacyPolicy/index.html";
    }

    /**
     * 新人礼包
     */
    public static String getNewPeopleGiftImage() {
        return splashStartLoad != null ? splashStartLoad.getNewPeopleGiftImage() : "";
    }

    /**
     * tab默认选择
     */
    public static int getAndroidTab() {
        return 0;
    }

    /**
     * 加载相关图片
     */
    public static SplashLoadingIconBean getLoadingIcon() {
        return splashStartLoad != null ? splashStartLoad.getLoadingIcon() : null;
    }

    /**
     * 加载失败图片
     */
    public static String getLoadFailIcon() {
        return splashStartLoad != null && splashStartLoad.getLoadingIcon() != null ? splashStartLoad.getLoadingIcon().getLoadFailImg() : "";
    }

    /**
     * 获取加载图片
     */
    public static String getLoadIcon() {
        return App.getInstance().getPrefsManager().getString(LOAD_ICON_URL) != null ? App.getInstance().getPrefsManager().getString(LOAD_ICON_URL) : "";
    }

    /**
     * 更新加载图片
     */
    public static void saveLoadIcon(String url) {
        App.getInstance().getPrefsManager().putString(LOAD_ICON_URL, url);
    }

    /**
     * 加载相关图片
     */
    public static String getLocationIcon() {
        return App.getInstance().getPrefsManager().getString(LOAD_LOCATION_FAIL_ICON_URL) != null ? App.getInstance().getPrefsManager().getString(LOAD_LOCATION_FAIL_ICON_URL) : "";
    }

    /**
     * 更新加载图片
     */
    public static void setLocationIcon(String url) {
        App.getInstance().getPrefsManager().putString(LOAD_LOCATION_FAIL_ICON_URL, url);
    }


    /**
     * 初始化预加载信息
     */
    public static void initLoadInfo() {
        parserEntryData(getLoadInfo());
    }


    /**
     * 更新预加载信息
     */
    public static void saveLoadInfo(SplashStartLoad bean) {
        if (null != bean) {
            splashStartLoad = bean;
            String loadInfo = new Gson().toJson(bean);
            App.getInstance().getPrefsManager().putString(Key_LOAD_INFO, loadInfo);

            // 单独保存 时光网用户协议 地址，方便user模块一键登录调用
            String url = bean.getRegisterServiceUrl();
            App.getInstance().getPrefsManager().putString(Key_SERVICE_URL, url);
        }
    }

    /**
     * 获得启动信息实体
     */
    public static SplashStartLoad getLoadInfo() {
        if (null == splashStartLoad) {
            String loadInfo = App.getInstance().getPrefsManager().getString(Key_LOAD_INFO);
            if (!TextUtils.isEmpty(loadInfo)) {
                splashStartLoad = new Gson().fromJson(loadInfo, SplashStartLoad.class);
            }
        }
        return splashStartLoad;
    }

    /**
     * 获取广告adURL(取第一条不分尺寸)
     */

    public static String getAdUrl() {
        return App.getInstance().getPrefsManager().getString(AD_STATISTIC_URL);
    }

    /**
     * 缓存广告adURL(取第一条不分尺寸)
     */
    public static void setAdUrl(String adUrl) {
        App.getInstance().getPrefsManager().putString(AD_STATISTIC_URL, adUrl);
    }

    /**
     * 解析返回数据并保存本地
     */
    public static void parserEntryData(final SplashStartLoad obj) {
        if (null == obj) {
            return;
        }
        LogWriter.e("checkTime", "start to parse response");
        // tab的图标, 优先进行下载(9.1.5版本去掉下载，与IOS保持一致)
//        downloadTabIcons(obj.getBottomIconList());

        // 图片加载、上传服务器地址
        if (!TextUtils.isEmpty(obj.getImageProxy())) {
            VariateExt.INSTANCE.setImgProxyUrl(obj.getImageProxy());
        }
        if (!TextUtils.isEmpty(obj.getImageUploadUrl())) {
            VariateExt.INSTANCE.setImgUploadUrl(obj.getImageUploadUrl());
        }
        //【意见反馈】模块用的群组帖子ID
        VariateExt.INSTANCE.setFeedbackPostId(obj.getFeedbackPostIdAndroid());
        if (!TextUtils.isEmpty(obj.getAuthTemplateImg())) {
            VariateExt.INSTANCE.setAuthTemplateImg(obj.getAuthTemplateImg());
        }
        // 主页底部TAB的索引
        VariateExt.INSTANCE.setInitMainTabIndex(getAndroidTab());

//        LoadManager.saveLoadInfo(obj);
        // 加载动画的图，废弃，9.0.0版本开始，使用本地gif来loading
        //downloadLoadingIcon(obj.getLoadingIcon());
        // 座位图
        downloadSeatIcons(obj.getSeatIcon());
        LogWriter.e("checkTime", "end parse response");
    }

    private static void downloadSeatIcons(final SplashSeatsIconList seats) {
        if (null != seats && !TextUtils.isEmpty(seats.getTag()) &&
                null != seats.getList() && seats.getList().size() > 0) {
            App.getInstance().getPrefsManager().putString(App.getInstance().SEATS_ICON_TAG, seats.getTag());
            if (!TextUtils.equals(seats.getTag(), App.getInstance().getPrefsManager().getString(App.getInstance().SEATS_ICON_TAG))) {
                for (SplashSeatsIconBean seatsIconBean : seats.getList()) {
//                ImageHelper.with(ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
//                        .load(seatsIconBean.getImageUrl()).callback(new ImageDownloadCallback() {
//                    @Override
//                    public void onDownloadCompleted(String s, File file) {
//                        try {
//                            //这种KEY保存有些问题，没有时机来清楚这些KEY，长期下来sp中会有很多无用的缓存，所以后期抽时间需要把这块优化
//                            App.getInstance().getPrefsManager().putString(SEAT_ICON_SP_PREFIX + seatsIconBean.getImgId(), file.getCanonicalPath());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onDownLoadFailed(String s) {
//                    }
//                }).download();

                    final String url = seatsIconBean.getImageUrl();
                    final int lastIndexOf = url.lastIndexOf("/") + 1;
                    if (lastIndexOf > 0) {
                        final String fileName = "seat_" + seatsIconBean.getImgId() + "_" + url.substring(lastIndexOf);
                        DownloadManager.INSTANCE.download(
                                url,
                                fileName,
                                FileEnv.INSTANCE.getDownDir(),
                                new DownloadListener() {
                                    @Override
                                    public void onProgress(int progress) {
                                    }

                                    @Override
                                    public void onFailed(@Nullable String msg) {
                                    }

                                    @Override
                                    public void onComplete(@NotNull String filePath) {
                                        try {
                                            //这种KEY保存有些问题，没有时机来清楚这些KEY，长期下来sp中会有很多无用的缓存，所以后期抽时间需要把这块优化
                                            App.getInstance().getPrefsManager().putString(SEAT_ICON_SP_PREFIX + seatsIconBean.getImgId(), filePath);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                        );
                    }
                }
            }
        } else {
            App.getInstance().getPrefsManager().putString(App.getInstance().SEATS_ICON_TAG, "");
        }
    }

    //(9.1.5版本去掉下载，与IOS保持一致)
//    private static void downloadTabIcons(List<SplashBottomIcons> icons) {
//        PrefsManager mPM = App.getInstance().getPrefsManager();
//        //本地保存的文件名称
//        if (null == icons || icons.size() < 1) {
//            // 清除掉就有的
//            delSaveBottomIcon(mPM, App.getInstance().HOME_ICON_SELECTED, App.getInstance().HOME_ICON_UNSELECTED);
//            delSaveBottomLabel(mPM, App.getInstance().HOME_NAME);
//            // 购票
//            delSaveBottomIcon(mPM, App.getInstance().TICKET_ICON_SELECTED, App.getInstance().TICKET_ICON_UNSELECTED);
//            delSaveBottomLabel(mPM, App.getInstance().TICKET_NAME);
//            // 游戏
//            delSaveBottomIcon(mPM, App.getInstance().GAME_ICON_SELECTED, App.getInstance().GAME_ICON_UNSELECTED);
//            delSaveBottomLabel(mPM, App.getInstance().GAME_NAME);
//            // 发现
//            delSaveBottomIcon(mPM, App.getInstance().VIDEO_ICON_SELECTED, App.getInstance().VIDEO_ICON_UNSELECTED);
//            delSaveBottomLabel(mPM, App.getInstance().VIDEO_NAME);
//            // 我的
//            delSaveBottomIcon(mPM, App.getInstance().MINE_ICON_SELECTED, App.getInstance().MINE_ICON_UNSELECTED);
//            delSaveBottomLabel(mPM, App.getInstance().MINE_NAME);
//            // 社区
//            delSaveBottomIcon(mPM, App.getInstance().COMMUNITY_ICON_SELECTED, App.getInstance().COMMUNITY_ICON_UNSELECTED);
//            delSaveBottomLabel(mPM, App.getInstance().COMMUNITY_NAME);
//            return;
//        }
//
//        int index = 0;
//        /**
//         * 底部4个tab
//         */
//        boolean[] isDelIcon = {true, true, true, true};
//        boolean[] isDelLabel = {true, true, true, true};
//        for (SplashBottomIcons otherIcons : icons) {
//            String iconSelectedFileName = "";
//            String iconUnSelectedFileName = "";
//            String iconName = "";
//
//            switch (Integer.parseInt(otherIcons.getType())) {
//                case BottomNavigationHelper.TYPE_HOME://首页
//                    iconSelectedFileName = App.getInstance().HOME_ICON_SELECTED;
//                    iconUnSelectedFileName = App.getInstance().HOME_ICON_UNSELECTED;
//                    iconName = App.getInstance().HOME_NAME;
//                    index = 1;
//                    break;
//                case BottomNavigationHelper.TYPE_TICKET://购票
//                    iconSelectedFileName = App.getInstance().TICKET_ICON_SELECTED;
//                    iconUnSelectedFileName = App.getInstance().TICKET_ICON_UNSELECTED;
//                    iconName = App.getInstance().TICKET_NAME;
//                    index = 2;
//                    break;
////                case BottomNavigationHelper.TYPE_GAME: //时光猜电影
////                    iconSelectedFileName = App.getInstance().GAME_ICON_SELECTED;
////                    iconUnSelectedFileName = App.getInstance().GAME_ICON_UNSELECTED;
////                    iconName = App.getInstance().GAME_NAME;
////                    index = 3;
////                    break;
////                case BottomNavigationHelper.TYPE_MALL: //商城
////                    iconSelectedFileName = App.getInstance().MALL_ICON_SELECTED;
////                    iconUnSelectedFileName = App.getInstance().MALL_ICON_UNSELECTED;
////                    iconName = App.getInstance().MALL_NAME;
////                    index = 3;
////                    break;
//                case BottomNavigationHelper.TYPE_COMMUNITY: //社区
//                    iconSelectedFileName = App.getInstance().COMMUNITY_ICON_SELECTED;
//                    iconUnSelectedFileName = App.getInstance().COMMUNITY_ICON_UNSELECTED;
//                    iconName = App.getInstance().COMMUNITY_NAME;
//                    index = 3;
//                    break;
////                case BottomNavigationHelper.TYPE_VIDEO://视频
////                    iconSelectedFileName = App.getInstance().VIDEO_ICON_SELECTED;
////                    iconUnSelectedFileName = App.getInstance().VIDEO_ICON_UNSELECTED;
////                    iconName = App.getInstance().VIDEO_NAME;
////                    index = 4;
////                    break;
//                case BottomNavigationHelper.TYPE_MINE://我的
//                    iconSelectedFileName = App.getInstance().MINE_ICON_SELECTED;
//                    iconUnSelectedFileName = App.getInstance().MINE_ICON_UNSELECTED;
//                    iconName = App.getInstance().MINE_NAME;
//                    index = 4;
//                    break;
//            }
//            if (0 == index) {
//                continue;
//            }
//
//            if (!MtimeUtils.isNull(otherIcons.getImg()) && !MtimeUtils.isNull(otherIcons.getSelectedImg())) {
//                isDelIcon[index - 1] = false;
//                //如果本地已下载的图片被删除，则重新下载。比较上次地址和本次是否相同，不相同则下载，
//                //本地保存的上次图片url地址
//                String lastIconSelected = mPM.getString(iconSelectedFileName);
//                String lastIconUnSelected = mPM.getString(iconUnSelectedFileName);
//                if (!UIUtil.fileExist(iconSelectedFileName) ||
//                        !UIUtil.fileExist(iconUnSelectedFileName) || !lastIconSelected.equals(otherIcons.getSelectedImg())
//                        || !lastIconUnSelected.equals(otherIcons.getImg())
//                ) {
//                    //缓存选中跟未选中图片
//                    downLoadAndSaveBottomIcon(otherIcons.getSelectedImg(), iconSelectedFileName, index);
//                    downLoadAndSaveBottomIcon(otherIcons.getImg(), iconUnSelectedFileName, index);
//                }
//            }
//
//            if (!MtimeUtils.isNull(otherIcons.getText())) {
//                mPM.putString(iconName, otherIcons.getText());
//                isDelLabel[index - 1] = false;
//            }
//        }
//
//        // 这里清除旧数据
//        // 清除掉就有的
//        if (isDelIcon[0]) {
//            delSaveBottomIcon(mPM, App.getInstance().HOME_ICON_SELECTED, App.getInstance().HOME_ICON_UNSELECTED);
//        }
//        if (isDelLabel[0]) {
//            delSaveBottomLabel(mPM, App.getInstance().HOME_NAME);
//        }
//
//        // 购票
//        if (isDelIcon[1]) {
//            delSaveBottomIcon(mPM, App.getInstance().TICKET_ICON_SELECTED, App.getInstance().TICKET_ICON_UNSELECTED);
//        }
//        if (isDelLabel[1]) {
//            delSaveBottomLabel(mPM, App.getInstance().TICKET_NAME);
//        }
//        // 游戏
////        if (isDelIcon[2]) {
////            delSaveBottomIcon(mPM, App.getInstance().GAME_ICON_SELECTED, App.getInstance().GAME_ICON_UNSELECTED);
////        }
////        if (isDelLabel[2]) {
////            delSaveBottomLabel(mPM, App.getInstance().GAME_NAME);
////        }
//        // 商城
////        if (isDelIcon[2]) {
////            delSaveBottomIcon(mPM, App.getInstance().MALL_ICON_SELECTED, App.getInstance().MALL_ICON_UNSELECTED);
////        }
////        if (isDelLabel[2]) {
////            delSaveBottomLabel(mPM, App.getInstance().MALL_NAME);
////        }
//        // 社区
//        if (isDelIcon[2]) {
//            delSaveBottomIcon(mPM, App.getInstance().COMMUNITY_ICON_SELECTED, App.getInstance().COMMUNITY_ICON_UNSELECTED);
//        }
//        if (isDelLabel[2]) {
//            delSaveBottomLabel(mPM, App.getInstance().COMMUNITY_NAME);
//        }
//        // 视频
////        if (isDelIcon[3]) {
////            delSaveBottomIcon(mPM, App.getInstance().VIDEO_ICON_SELECTED, App.getInstance().VIDEO_ICON_UNSELECTED);
////        }
////        if (isDelLabel[3]) {
////            delSaveBottomLabel(mPM, App.getInstance().VIDEO_NAME);
////        }
//        // 我的
//        if (isDelIcon[3]) {
//            delSaveBottomIcon(mPM, App.getInstance().MINE_ICON_SELECTED, App.getInstance().MINE_ICON_UNSELECTED);
//        }
//        if (isDelLabel[3]) {
//            delSaveBottomLabel(mPM, App.getInstance().MINE_NAME);
//        }
//    }
//
//    private static void downLoadAndSaveBottomIcon(final String iconUrl, final String iconName, final int index) {
//        ImageHelper.with(ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
//                .load(iconUrl)
//                .callback(new ImageDownloadCallback() {
//                    @Override
//                    public void onDownloadCompleted(String s, File file) {
//                        App.getInstance().getPrefsManager().putString(iconName, iconUrl);
//                    }
//
//                    @Override
//                    public void onDownLoadFailed(String s) {
//                    }
//                })
//                .download();
//    }

//    private static void downloadLoadingIcon(final SplashLoadingIconBean loadingIcon) {
//        if (null == loadingIcon) {
//            return;
//        }
//        // 加载动画和失败的图片处理
//        String loadIconUrl = loadingIcon.getLoadIcon();
//        String loadFailIconUrl = loadingIcon.getLoadFailImg();
//        if (loadIconUrl != null && "".equals(loadIconUrl)) {
//            //删除保存的文件
//            LoadManager.saveLoadIcon("");
//        } else if (!TextUtils.isEmpty(loadIconUrl) && !loadIconUrl.equals(LoadManager.getLoadIcon())) {
//            //判断url和以前是否相同,相同的话不再下载 loadingIcon.getLoadIcon();
//            ImageHelper.with()
//                    .load(loadIconUrl)
//                    .callback(new ImageDownloadCallback() {
//                        @Override
//                        public void onDownloadCompleted(String s, File file) {
//                            LoadManager.saveLoadIcon(loadIconUrl);
//                        }
//
//                        @Override
//                        public void onDownLoadFailed(String s) {
//                            LoadManager.saveLoadIcon("");
//                        }
//                    })
//                    .download();
//            //加载失败图
//            ImageHelper.with(ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
//                    .load(loadFailIconUrl)
//                    .download();
//        }
//
//        // 定位失败的图片单独处理
//        if (TextUtils.isEmpty(loadingIcon.getLocationFailImg()) || "".equals(loadingIcon.getLocationFailImg())) {
////            MtimeUtils.removeLocationImage();
//            LoadManager.setLocationIcon("");
//            return;
//        }
//        String locationFailUrl = LoadManager.getLocationIcon();
//        if (TextUtils.isEmpty(locationFailUrl) ||
//                (!TextUtils.isEmpty(loadingIcon.getLocationFailImg()) && !locationFailUrl.equals(loadingIcon.getLocationFailImg()))) {
//            ImageHelper.with(ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
//                    .load(locationFailUrl)
//                    .callback(new ImageDownloadCallback() {
//                        @Override
//                        public void onDownloadCompleted(String s, File file) {
//                            LoadManager.setLocationIcon(locationFailUrl);
//                        }
//
//                        @Override
//                        public void onDownLoadFailed(String s) {
//
//                        }
//                    })
//                    .download();
//        } else {
//            LoadManager.setLocationIcon("");
//        }
//    }

//    private static void delSaveBottomIcon(final PrefsManager ps, final String selectKey, final String unselectKey) {
//        ps.putString(selectKey, "");
//        ps.putString(unselectKey, "");
//    }
//
//    private static void delSaveBottomLabel(final PrefsManager ps, final String labelKey) {
//        ps.putString(labelKey, "");
//    }
}
