package com.mtime.bussiness.video.dialog;

/**
 * Created by lys on 10/17/17.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kotlin.android.share.ShareManager;
import com.kotlin.android.share.SharePlatform;
import com.kotlin.android.share.entity.ShareEntity;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.imageload.ImageShowLoadCallback;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.StatisticEnum;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.common.cache.FileCache;
import com.mtime.common.utils.Utils;
import com.mtime.constant.FrameConstant;
import com.mtime.network.ConstantUrl;
import com.mtime.statistic.large.h5.StatisticH5;
import com.mtime.util.JumpUtil;
import com.mtime.util.MtimeUtils;
import com.mtime.util.UIUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;


/**
 * 使用新接口的分享界面
 * 仅视频专题页使用
 */
@Deprecated
public class PlayerShareDialog implements View.OnClickListener {

    private static final String ID_DEFAULT_CITY = "0";
    private static final String ID_DEFAULT_PID = "0";
    private static final String ID_DEFAULT_WBLACK = "";

    private int mShareChannel;

    public static final int SHARE_CHANNEL_WECHAT = 1;
    public static final int SHARE_CHANNEL_WECHAT_CIRCLE = 2;
    public static final int SHARE_CHANNEL_QQ = 3;
    public static final int SHARE_CHANNEL_SINA = 4;

    private static final float FULL_ROOT_HEIGHT = 109;  // dp 横屏分享高

    // share type
    public static final String SHARE_TYPE_MOVIE_RATE = "1009"; // 影片评分
    private Bitmap screenshotBitmap = null;
    private final int THUMB_SIZE = 150;

    // WeiXin APP ID
    public static final String APP_ID = "wx839739a08ff78016";

    private Activity context = null;
//    private ShareBean share;
    private View shareView;
    private final Dialog shareViewDlg;

    // private BitmapCallback emailBitmapCallback;
    private final int mExtarFlag = 0x00;

    // query share info
//    private NetworkManager.NetworkListener<ShareBean> weChatCallback;
//    private NetworkManager.NetworkListener<ShareBean> wechatfriendCallback;
//    private NetworkManager.NetworkListener<ShareBean> qqCallback;
//    private NetworkManager.NetworkListener<ShareBean> sinaCallback;

    //
    private String shareType;
    private String shareId;
    private String cityId;
    private String pid;
    private String urlImage;

    private String h5Title;
    private String h5Content;
    private String h5Link;

    private String h5pic;
    private String logxParamType;
    private String logxParam;

    //4.0上报
    private String mFirstRegion;
    private String mShareToSecRegion;
    private String mCloseSecRegion;
    private String mBusinessParamKey;
    private String mBusinessParamValue;
    private String mCloseWay = StatisticEnum.EnumCloseWay.OTHER_AREA.getValue();

    //分享model--第三方
//    private ShareMessage mShareMessage = null;
    //分享结果--第三方
//    private ShareListener mShareListener;

    private OnShareListener mOnShareListener;

    public void setLogxParam(final String paramType, final String param) {
        this.logxParamType = paramType;
        this.logxParam = param;
    }

    // 须要对图片进行处理,不然微信会在log中输出thumbData搜检错误
    private static byte[] getBitmapBytes(final Bitmap bitmap, final boolean paramBoolean) {
        final Bitmap localBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565);
        final Canvas localCanvas = new Canvas(localBitmap);
        int i;
        int j;
        if (bitmap.getHeight() > bitmap.getWidth()) {
            i = bitmap.getWidth();
            j = bitmap.getWidth();
        } else {
            i = bitmap.getHeight();
            j = bitmap.getHeight();
        }

        while (true) {
            localCanvas.drawBitmap(bitmap, new Rect(0, 0, i, j), new Rect(0, 0, 80, 80), null);
            if (paramBoolean) {
                bitmap.recycle();
            }
            final ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100, localByteArrayOutputStream);
            localBitmap.recycle();
            final byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (final Exception e) {
            }
            i = bitmap.getHeight();
            j = bitmap.getHeight();
        }
    }

    public void dismiss(){
        if(shareViewDlg!=null && shareViewDlg.isShowing()){
            shareViewDlg.dismiss();
        }
    }

    /**
     * 外部按钮指向，直接调用某一项分享功能
     *
     * @param c
     * @param isFull
     */
    public PlayerShareDialog(final Activity c, boolean isFull) {
        clear();
        this.context = c;
        int style = R.style.transparentFrameWindowStyle;
        setLogxParam(null, null);
        initCallbacks();

        shareView = View.inflate(c, R.layout.act_video_horizontal_share, null);
        if (isFull) {
            shareView = View.inflate(c, R.layout.player_share_layout, null);
        }

        shareViewDlg = new Dialog(c, style) {

            @Override
            public void dismiss() {
                super.dismiss();
            }

        };

        if (isFull) {
            final View shareList = shareView.findViewById(R.id.ll_share_list);
            shareList.setVisibility(View.GONE);
            LinearLayout shareWexin = shareView.findViewById(R.id.player_sdk_share_weixin);
            shareWexin.setOnClickListener(this);
            LinearLayout shareFriend = shareView.findViewById(R.id.player_sdk_share_friend);
            shareFriend.setOnClickListener(this);
            LinearLayout shareWeiBoSina = shareView.findViewById(R.id.player_sdk_share_sina);
            shareWeiBoSina.setOnClickListener(this);
            LinearLayout shareQQ = shareView.findViewById(R.id.player_sdk_share_qq);
            shareQQ.setOnClickListener(this);

            // 设置弹窗宽高
            final DisplayMetrics dm = new DisplayMetrics();
            c.getWindowManager().getDefaultDisplay().getMetrics(dm);
            final RelativeLayout rel = shareView.findViewById(R.id.rel_share_dialog_root_layout);
            rel.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    rel.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rel.getLayoutParams();
                            params.width = -1;
                            params.height = Utils.dip2px(context, FULL_ROOT_HEIGHT);
                            rel.setLayoutParams(params);
                            shareList.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });

        } else {
            TextView shareWeiChat = shareView.findViewById(R.id.share_weichat_world);
            shareWeiChat.setOnClickListener(this);

            TextView shareWeiBoSina = shareView.findViewById(R.id.share_weibo_sina);
            shareWeiBoSina.setOnClickListener(this);

            TextView shareQQFriend = shareView.findViewById(R.id.share_qq_friend);
            shareQQFriend.setOnClickListener(this);

            TextView shareWeiXinFriend = shareView.findViewById(R.id.share_weixin_friend);
            shareWeiXinFriend.setOnClickListener(this);

            ImageButton close = shareView.findViewById(R.id.close);
            close.setOnClickListener(this);
        }
    }

    public void setValues(final String id, final String type, final String cityId, final String pid,
                          final String urlImage) {
        this.shareId = id;
        this.shareType = type;
        this.cityId = TextUtils.isEmpty(cityId) ? ID_DEFAULT_CITY : cityId;
        this.pid = TextUtils.isEmpty(pid) ? ID_DEFAULT_PID : pid;
        this.urlImage = TextUtils.isEmpty(urlImage) ? ID_DEFAULT_WBLACK : urlImage;
    }

    public void setValues(final String id, final String type) {
        setValues(id, type, null, null, null);
    }

    public void setH5Values(final String title, final String content, final String link, final String pic) {
        this.h5Title = TextUtils.isEmpty(title) ? ID_DEFAULT_WBLACK : title;
        this.h5Content = TextUtils.isEmpty(content) ? ID_DEFAULT_WBLACK : content;
        this.h5Link = TextUtils.isEmpty(link) ? ID_DEFAULT_WBLACK : link;
        this.h5pic = TextUtils.isEmpty(pic) ? ID_DEFAULT_WBLACK : pic;
        try {
            this.h5pic = URLDecoder.decode(this.h5pic, "UTF-8");
            this.h5Title = URLDecoder.decode(this.h5Title, "UTF-8");
            this.h5Content = URLDecoder.decode(this.h5Content, "UTF-8");
            this.h5Link = URLDecoder.decode(this.h5Link, "UTF-8");
        } catch (Exception e) {

        }
    }

    // 设置4.0埋点参数
    public void setStatistic(String firstRegion,
                             String shareToSecRegion, String closeSecRegion,
                             String businessParamKey, String businessParamValue) {
        mFirstRegion = firstRegion;
        mShareToSecRegion = shareToSecRegion;
        mCloseSecRegion = closeSecRegion;
        mBusinessParamKey = businessParamKey;
        mBusinessParamValue = businessParamValue;
    }

    private void clear() {
        this.shareId = null;
        this.shareType = null;
        this.cityId = ID_DEFAULT_WBLACK;
        this.pid = ID_DEFAULT_WBLACK;
        this.urlImage = ID_DEFAULT_WBLACK;

        this.h5Content = ID_DEFAULT_WBLACK;
        this.h5Link = ID_DEFAULT_WBLACK;
        this.h5Title = ID_DEFAULT_WBLACK;
        this.h5pic = null;
    }

    public void showActionSheet(OnShareListener onShareListener) {
        mShareChannel = 0;
        mOnShareListener = onShareListener;
        if (null != context && !context.isFinishing() && shareViewDlg != null && !shareViewDlg.isShowing()) {
            shareViewDlg.setContentView(this.shareView,
                    new ViewGroup.LayoutParams(FrameConstant.SCREEN_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT));
            Window window = shareViewDlg.getWindow();
            // 设置显示动画

            window.setWindowAnimations(R.style.main_menu_animstyle);
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.x = 0;
            wl.width = WindowManager.LayoutParams.MATCH_PARENT;
            wl.y = FrameConstant.SCREEN_HEIGHT;

            // 设置显示位置
            shareViewDlg.onWindowAttributesChanged(wl);
            // 设置点击外围解散
            shareViewDlg.setCanceledOnTouchOutside(true);
            shareViewDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (mOnShareListener != null) {
                        mOnShareListener.onShareDismiss(mShareChannel);
                    }
                }
            });
            shareViewDlg.show();
        }
    }

    // 监听物理返回键
    private void setKeyBackListener() {
        shareViewDlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    mCloseWay = StatisticEnum.EnumCloseWay.KEYCODE_BACK.getValue();
                }
                return false;
            }
        });
    }

    private void initCallbacks() {

        /*qqCallback = new NetworkManager.NetworkListener<ShareBean>() {
            @Override
            public void onSuccess(ShareBean share, String s) {
                UIUtil.dismissLoadingDialog();

                String titleString = share.getWeixinFriendTitle();
                String qqUrl = share.getWeixinFriendUrl();
                if (share.getWeixinFriendTitle() != null && !"".equals(share.getWeixinFriendTitle())) {
                    titleString = share.getWeixinFriendTitle();
                    if (titleString.length() > 70) {
                        titleString = titleString.substring(0, 66) + "...";
                    }
                    titleString = ".  ";
                }

                if (TextUtils.isEmpty(share.getWeixinFriendUrl())) {
                    qqUrl = ConstantUrl.APP_DOWNLOAD_PC_URL;
                }

                ShareEntity shareBeanNew = new ShareEntity();
                shareBeanNew.setImageUrl(share.getWeixinFriendImage());
                shareBeanNew.setTargetUrl(qqUrl);
                shareBeanNew.setTitle(titleString);
                shareBeanNew.setSummary(share.getWeixinFriendContent());
                ShareManager.INSTANCE.share(SharePlatform.QQ, shareBeanNew);
            }

            @Override
            public void onFailure(NetworkException<ShareBean> e, String s) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast("获取分享失败，请稍候再试:" + e.getMessage());
                if (mOnShareListener != null) {
                    mOnShareListener.onShareFailure(mShareChannel, "获取分享失败");
                }
            }
        };

        weChatCallback = new NetworkManager.NetworkListener<ShareBean>() {
            @Override
            public void onSuccess(ShareBean share, String s) {
                if (null == share.getWeixinContent()) {
                    MToastUtils.showShortToast("获取分享内容为空!");
                    UIUtil.dismissLoadingDialog();

                    if (mOnShareListener != null) {
                        mOnShareListener.onShareFailure(mShareChannel, "获取分享内容为空");
                    }

                    return;
                }

                if (null != screenshotBitmap && SHARE_TYPE_MOVIE_RATE.equalsIgnoreCase(shareType)) {//如果有截图直接分享截图且是分享影片评分
                    String fileName = "screenshotBitmap.png";
                    saveBitmap(screenshotBitmap, fileName);
                    File file = new File(FileCache.CACHE_PATH + fileName);

                    UIUtil.dismissLoadingDialog();
                    ShareEntity shareBeanNew = new ShareEntity();
                    shareBeanNew.setImageLocalUrl(FileCache.CACHE_PATH + fileName);
                    ShareManager.INSTANCE.share(SharePlatform.WE_CHAT_TIMELINE, shareBeanNew);
                    return;
                }
                if (TextUtils.isEmpty(share.getWeixinFriendImage())) {
                    Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_image);
                    String fileName = "default.png";
                    saveBitmap(bmp, fileName);
                    File file = new File(FileCache.CACHE_PATH + fileName);
                    // TODO: 2018/4/11 本地图片
                    ShareEntity shareBeanNew = new ShareEntity();
                    shareBeanNew.setImageLocalUrl(FileCache.CACHE_PATH + fileName);
                    shareBeanNew.setTargetUrl(share.getWeixinFriendUrl());
                    shareBeanNew.setTitle(share.getWeixinFriendTitle());
                    shareBeanNew.setSummary(share.getWeixinFriendContent());
                    ShareManager.INSTANCE.share(SharePlatform.WE_CHAT_TIMELINE, shareBeanNew);

                    UIUtil.dismissLoadingDialog();
                    return;
                }

                ImageShowLoadCallback callback = new ImageShowLoadCallback() {
                    @Override
                    public void onLoadCompleted(Bitmap resource) {
                        if (resource != null) {
                            UIUtil.dismissLoadingDialog();
                            ShareEntity shareBeanNew = new ShareEntity();
                            shareBeanNew.setImageUrl(share.getWeixinFriendImage());
                            shareBeanNew.setTargetUrl(share.getWeixinUrl());
                            shareBeanNew.setTitle(share.getWeixinTitle());
                            ShareManager.INSTANCE.share(SharePlatform.WE_CHAT_TIMELINE, shareBeanNew);
                        }
                    }

                    @Override
                    public void onLoadFailed() {
                        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_image);
                        String fileName = "default.png";
                        saveBitmap(bmp, fileName);
                        File file = new File(FileCache.CACHE_PATH + fileName);
                        UIUtil.dismissLoadingDialog();
                        ShareEntity shareBeanNew = new ShareEntity();
                        shareBeanNew.setImageLocalUrl(FileCache.CACHE_PATH + fileName);
                        shareBeanNew.setTargetUrl(share.getWeixinFriendUrl());
                        shareBeanNew.setTitle(share.getWeixinFriendTitle());
                        shareBeanNew.setSummary(share.getWeixinFriendContent());
                        ShareManager.INSTANCE.share(SharePlatform.WE_CHAT_TIMELINE, shareBeanNew);
                    }
                };

                if (SHARE_TYPE_MOVIE_RATE.equalsIgnoreCase(shareType)) {
                    ImageHelper.with(context, ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                            .load(share.getWeixinFriendImage())
                            .callback(callback)
                            .showload();
                } else {
                    ImageHelper.with(context, ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.SCALE_TO_FIT)
                            .load(share.getWeixinFriendImage())
                            .override(MScreenUtils.dp2px(90), MScreenUtils.dp2px(90))
                            .callback(callback)
                            .showload();
                }
            }

            @Override
            public void onFailure(NetworkException<ShareBean> e, String s) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast("获取分享失败!");
                if (mOnShareListener != null) {
                    mOnShareListener.onShareFailure(mShareChannel, "获取分享失败");
                }
            }
        };

        wechatfriendCallback = new NetworkManager.NetworkListener<ShareBean>() {
            @Override
            public void onSuccess(ShareBean share, String s) {
                if (null == share.getWeixinContent()) {
                    MToastUtils.showShortToast("获取分享内容为空!");
                    UIUtil.dismissLoadingDialog();

                    if (mOnShareListener != null) {
                        mOnShareListener.onShareFailure(mShareChannel, "获取分享内容为空");
                    }
                    return;
                }

                if (null != screenshotBitmap && SHARE_TYPE_MOVIE_RATE.equalsIgnoreCase(shareType)) {//如果有截图直接分享截图
                    String fileName = "screenshotBitmap.png";
                    saveBitmap(screenshotBitmap, fileName);
                    File file = new File(FileCache.CACHE_PATH + fileName);

                    UIUtil.dismissLoadingDialog();
                    ShareEntity shareEntity = new ShareEntity();
                    shareEntity.setImageLocalUrl(FileCache.CACHE_PATH + fileName);
                    ShareManager.INSTANCE.share(SharePlatform.WE_CHAT, shareEntity);
                    return;
                }
                // share the big bitmap for actor honors, experience, and
                if (TextUtils.isEmpty(share.getImageUrl())) {
                    Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_image);
                    String fileName = "default.png";
                    saveBitmap(bmp, fileName);
                    File file = new File(FileCache.CACHE_PATH + fileName);
                    UIUtil.dismissLoadingDialog();
                    ShareEntity shareBeanNew = new ShareEntity();
                    shareBeanNew.setImageLocalUrl(FileCache.CACHE_PATH + fileName);
                    shareBeanNew.setTargetUrl(share.getWeixinUrl());
                    shareBeanNew.setTitle(share.getWeixinTitle());
                    shareBeanNew.setSummary(share.getWeixinContent());
                    ShareManager.INSTANCE.share(SharePlatform.WE_CHAT, shareBeanNew);
                    return;
                }

                // rate
                ShareEntity shareBeanNew = new ShareEntity();
                shareBeanNew.setImageUrl(share.getImageUrl());
                shareBeanNew.setTargetUrl(share.getWeixinUrl());
                shareBeanNew.setTitle(share.getWeixinTitle());
                shareBeanNew.setSummary(share.getWeixinContent());
                ShareManager.INSTANCE.share(SharePlatform.WE_CHAT, shareBeanNew);
                UIUtil.dismissLoadingDialog();
            }

            @Override
            public void onFailure(NetworkException<ShareBean> e, String s) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast("获取分享失败：" + e.getMessage());
                if (mOnShareListener != null) {
                    mOnShareListener.onShareFailure(mShareChannel, "获取分享失败");
                }
            }
        };

        sinaCallback = new NetworkManager.NetworkListener<ShareBean>() {
            @Override
            public void onSuccess(ShareBean share, String s) {
                UIUtil.dismissLoadingDialog();

                if (null == share) {
                    MToastUtils.showShortToast("暂不支持当前分享类型");
                    if (mOnShareListener != null) {
                        mOnShareListener.onShareFailure(mShareChannel, "暂不支持当前分享类型");
                    }
                    return;
                }

                if (null != screenshotBitmap) {
                    String text = TextUtils.isEmpty(share.getAppWeiboContent()) ? "" : share.getAppWeiboContent();
                    String fileName = "screenshotBitmap.png";
                    saveBitmap(setBitmapMaxSize((Bitmap.createScaledBitmap(screenshotBitmap, THUMB_SIZE, THUMB_SIZE, true)), 30), fileName);
                    File file = new File(FileCache.CACHE_PATH + fileName);

                    ShareEntity shareEntity = new ShareEntity();
                    shareEntity.setImageLocalUrl(FileCache.CACHE_PATH + fileName);
                    shareEntity.setSummary(text);
                    ShareManager.INSTANCE.share(SharePlatform.WEI_BO, shareEntity);
                    return;
                }
                if (TextUtils.isEmpty(share.getAppWeiboImageUrl()) && TextUtils.isEmpty(share.getAppWeiboContent())
                        && TextUtils.isEmpty(share.getAppWeiboVideoUrl())) {
                    MToastUtils.showShortToast("暂不支持当前分享类型");
                    return;
                }
                // start the WeiBo share activity now.
                final String url = TextUtils.isEmpty(share.getAppWeiboImageUrl()) ? h5pic : share.getAppWeiboImageUrl();

                // if has video url, check the image url
                if (TextUtils.isEmpty(share.getAppWeiboVideoUrl())) {
                    // share with image or text
                    if (TextUtils.isEmpty(url)) {
                        if (!MtimeUtils.isAppInstalled(context, "com.sina.weibo") && !TextUtils.isEmpty(share.getWeiboContent())) {
//                            JumpUtil.startShareDetailActivity(context, "", share.getWeiboContent(), false);
                            JumpUtil.startCommonWebActivity(context, share.getWeiboContent(), StatisticH5.PN_H5, null,
                                    true, true, true, false, null);
                        } else {
                            ShareEntity shareBeanNew = new ShareEntity();
                            shareBeanNew.setImageUrl(url);
                            shareBeanNew.setTargetUrl(TextUtils.isEmpty(share.getAppWeiboVideoUrl()) ? "" : share.getAppWeiboVideoUrl());
                            shareBeanNew.setTitle("");
                            shareBeanNew.setSummary(share.getAppWeiboContent());
                            ShareManager.INSTANCE.share(SharePlatform.WEI_BO, shareBeanNew);
                        }

                    } else {
                        ImageHelper.with(context, ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                                .load(url)
                                .callback(new ImageShowLoadCallback() {
                                    @Override
                                    public void onLoadCompleted(Bitmap resource) {
                                        if (resource != null) {
                                            Bitmap weiboBitmap = UIUtil.compressImage(resource);
                                            if (!MtimeUtils.isAppInstalled(context, "com.sina.weibo") && !TextUtils.isEmpty(share.getWeiboContent())) {
                                                JumpUtil.startCommonWebActivity(context, share.getWeiboContent(), StatisticH5.PN_H5, null,
                                                        true, true, true, false, null);
                                            } else {
                                                ShareEntity shareBeanNew = new ShareEntity();
                                                shareBeanNew.setImageUrl(url);
                                                shareBeanNew.setTargetUrl(TextUtils.isEmpty(share.getAppWeiboVideoUrl()) ? "" : share.getAppWeiboVideoUrl());
                                                shareBeanNew.setTitle("");
                                                shareBeanNew.setSummary(share.getAppWeiboContent());
                                                ShareManager.INSTANCE.share(SharePlatform.WEI_BO, shareBeanNew);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onLoadFailed() {
                                        MToastUtils.showShortToast("下载图片失败");
                                    }
                                })
                                .showload();
                    }
                } else {
                    if (!MtimeUtils.isAppInstalled(context, "com.sina.weibo") && !TextUtils.isEmpty(share.getWeiboContent())) {
//                        JumpUtil.startShareDetailActivity(context, "", share.getWeiboContent(), false);
                        JumpUtil.startCommonWebActivity(context, share.getWeiboContent(), StatisticH5.PN_H5, null,
                                true, true, true, false, null);
                    } else {
                        ShareEntity shareBeanNew = new ShareEntity();
                        shareBeanNew.setImageUrl(url);
                        shareBeanNew.setTargetUrl(TextUtils.isEmpty(share.getAppWeiboVideoUrl()) ? "" : share.getAppWeiboVideoUrl());
                        shareBeanNew.setTitle("");
                        shareBeanNew.setSummary(share.getAppWeiboContent());
                        ShareManager.INSTANCE.share(SharePlatform.WEI_BO, shareBeanNew);
                    }
                }
            }

            @Override
            public void onFailure(NetworkException<ShareBean> e, String s) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast("获取分享失败：" + e.getMessage());
                if (mOnShareListener != null) {
                    mOnShareListener.onShareFailure(mShareChannel, "获取分享失败");
                }
            }
        };*/
    }


    // private functions.
    @Override
    public void onClick(View arg0) {
        // 点击关闭按钮
        if (R.id.close == arg0.getId()) {
            if (shareViewDlg != null) {
                mCloseWay = StatisticEnum.EnumCloseWay.CLOSE_BTN.getValue();
                shareViewDlg.dismiss();
                if (mOnShareListener != null) {
                    mOnShareListener.onShareCancel(true);
                }
            }
            return;
        }

        UIUtil.showLoadingDialog(context);

        replaceImgUrl();

        // 埋点业务字段
        boolean needStatistic = !TextUtils.isEmpty(mFirstRegion); // 是否需要上报分享埋点
        Map<String, String> shareToBusinessParam = null;
        if (needStatistic) {
            shareToBusinessParam = new HashMap<>();
            shareToBusinessParam.put(mBusinessParamKey, mBusinessParamValue);
        }
        String logxShareTo = "";

        String label = null;
        switch (arg0.getId()) {
            case R.id.share_weixin_friend:
            case R.id.player_sdk_share_weixin:
                mShareChannel = SHARE_CHANNEL_WECHAT;
                logxShareTo = StatisticEnum.EnumShareTo.WE_CHAT.getValue();
                label = "微信好友";
                shareToWXFriend();
                break;
            case R.id.share_weichat_world:
            case R.id.player_sdk_share_friend:
                mShareChannel = SHARE_CHANNEL_WECHAT_CIRCLE;
                logxShareTo = StatisticEnum.EnumShareTo.MOMENTS.getValue();
                label = "微信朋友圈";
                shareToWXCircle();
                break;
            case R.id.share_weibo_sina:
            case R.id.player_sdk_share_sina:
                mShareChannel = SHARE_CHANNEL_SINA;
                logxShareTo = StatisticEnum.EnumShareTo.WEIBO.getValue();
                label = "微博";
                shareToXLWeibo();
                break;
            case R.id.share_qq_friend:
            case R.id.player_sdk_share_qq:
                mShareChannel = SHARE_CHANNEL_QQ;
                logxShareTo = StatisticEnum.EnumShareTo.QQ.getValue();
                label = "QQ";
                shareToQQFriend();
                break;
            default:
                break;
        }

        if (mOnShareListener != null) {
            mOnShareListener.onShareChannelClick(logxShareTo);
        }

//        // 埋点上报
//        if(needStatistic && !TextUtils.isEmpty(logxShareTo)) {
//            shareToBusinessParam.put(StatisticConstant.SHARE_TO, logxShareTo);
//            submitStatisticBean(shareToBusinessParam);
//        }
//
    }

    // 上报关闭埋点
//    private void submitCloseStatistic() {
//        if(TextUtils.isEmpty(mFirstRegion)) {
//            return;
//        }
//
//        Map<String, String> closeBusinessParam = new HashMap<>();
//        closeBusinessParam.put(mBusinessParamKey, mBusinessParamValue);
//        closeBusinessParam.put(StatisticConstant.CLOSE_WAY, mCloseWay);
//        StatisticPageBean closeStatisticBean = context.assemble(mFirstRegion, "", mCloseSecRegion, "", "", "", closeBusinessParam);
//        StatisticManager.getInstance().submit(closeStatisticBean);
//    }
//
//    // 上报埋点数据
//    private String submitStatisticBean(Map<String, String> businessParam) {
//        StatisticPageBean statisticBean = context.assemble(mFirstRegion, "", mShareToSecRegion, "", "", "", businessParam);
//        StatisticManager.getInstance().submit(statisticBean);
//        return statisticBean.toString();
//    }
//
//    private boolean submitEvent(int viewId) {
//        if (null == screenshotBitmap) {
//            return false;
//        }
//        switch (viewId) {
//            case R.id.share_weichat_world:
//                return true;
//            case R.id.share_weixin_friend:
//                return true;
//            case R.id.share_qq_friend:
//                return true;
//            case R.id.share_weibo_sina:
//                return true;
//            default:
//                return false;
//        }
//    }

    /**
     * 分享到给微信好友
     */
    private void shareToWXFriend() {
        if (!checkAppInstalled(this.context, "com.tencent.mm")) {
            MToastUtils.showShortToast("请先安装微信客户端");
            UIUtil.dismissLoadingDialog();
            return;
        }
        // WeiXin friend
//        getShareText(this.wechatfriendCallback, shareId, shareType,
//                cityId, pid, urlImage, h5Title, h5Content, h5Link);
    }

    /**
     * 分享到给微信朋友圈
     */
    private void shareToWXCircle() {
        if (!checkAppInstalled(this.context, "com.tencent.mm")) {
            MToastUtils.showLongToast("请先安装微信客户端");
            UIUtil.dismissLoadingDialog();
            return;
        }

        // WeiXin friends world
//        getShareText(this.weChatCallback, shareId, shareType, cityId, pid,
//                urlImage, h5Title, h5Content, h5Link);
    }

    /**
     * 分享到给新浪微博
     */
    private void shareToXLWeibo() {
        // WeiBo of Sina
//        getShareText(this.sinaCallback, shareId, shareType, cityId, pid,
//                urlImage, h5Title, h5Content, h5Link);
    }

    /**
     * 分享到给QQ好友
     */
    private void shareToQQFriend() {
        // QQ friends
        if (null != screenshotBitmap) {
            UIUtil.dismissLoadingDialog();
            String fileName = "screenshotBitmap.png";
            saveBitmap(screenshotBitmap, fileName);
            File file = new File(FileCache.CACHE_PATH + fileName);
            if (file.exists()) {
                //如果图片存在，就分享图片
                ShareEntity shareEntity = new ShareEntity();
                shareEntity.setImageLocalUrl(FileCache.CACHE_PATH + fileName);
                ShareManager.INSTANCE.share(SharePlatform.QQ, shareEntity);
            }

        } else {
//            getShareText(this.qqCallback, shareId, shareType, cityId, pid,
//                    urlImage, h5Title, h5Content, h5Link);
        }

    }

    private boolean saveBitmap(Bitmap loadedImage, final String postfix) {
        final File fileDir = new File(FileCache.CACHE_PATH);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        final File file = new File(FileCache.CACHE_PATH + postfix);
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(FileCache.CACHE_PATH + postfix);
            loadedImage.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
        } catch (final Exception e) {
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException e1) {
                }
            }

            return false;
        }

        return true;
    }


    private boolean checkAppInstalled(final Context context, final String pkgName) {
        if (null == context || TextUtils.isEmpty(pkgName)) {
            return false;
        }

        try {
            context.getPackageManager().getApplicationInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

//    public void getShareText(final NetworkManager.NetworkListener<ShareBean> callback, final String id,
//                             final String type, final String cityID, final String fId, final String imageUrl, final String h5Title,
//                             final String h5Content, final String h5Link) {
//        // GetShareContent.api?id={0}&type={1}&cityId={2}&fId={3}&imageUrl={4}&h5Title={5}&h5Content={6}&h5Link={7}
//        ShareApi shareApi = new ShareApi();
//        shareApi.getShareContent(id, type, cityID, fId, imageUrl, h5Title, h5Content, h5Link, callback);
//    }

    private void replaceImgUrl() {
        if (TextUtils.isEmpty(urlImage) || urlImage.length() < 10 && !TextUtils.isEmpty(h5pic) && h5pic.length() > 10) {
            urlImage = h5pic;
        }
    }

    public void setScreenshotBitmap(Bitmap bitmap) {
        this.screenshotBitmap = bitmap;
    }


    //设置bitmap最大上限KB。
    private Bitmap setBitmapMaxSize(Bitmap image, double maxSize) {
        int bitmapSize = getBitmapSize(image) >> 10;
        if (bitmapSize > maxSize) {
            double i = bitmapSize / maxSize;
            image = scaleImage(image, image.getWidth() / Math.sqrt(i),
                    image.getHeight() / Math.sqrt(i));
        }
        return image;
    }

    private Bitmap scaleImage(Bitmap image, double newWidth,
                              double newHeight) {
        float width = image.getWidth();
        float height = image.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(image, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    /**
     * 获取bitmap所占的大小
     *
     * @param image
     * @return
     */
    private int getBitmapSize(Bitmap image) {
        int size = 0;
        if (null == image) {
            return size;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            size = image.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            size = image.getByteCount();
        } else {
            size = image.getRowBytes() * image.getHeight();
        }
        return size;
    }

    public interface OnShareListener {
        void onShareSuccess(int shareChannel);

        void onShareChannelClick(String shareChannel);

        void onShareFailure(int shareChannel, String message);

        void onShareCancel(boolean userClose);

        void onShareDismiss(int shareChannel);
    }
}














