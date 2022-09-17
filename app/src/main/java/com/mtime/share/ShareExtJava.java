package com.mtime.share;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.kotlin.android.app.data.entity.common.CommonShare;
import com.kotlin.android.ktx.utils.LogUtils;
import com.kotlin.android.share.SharePlatform;
import com.kotlin.android.share.entity.ShareEntity;
import com.kotlin.android.share.ext.ShareExtKt;
import com.kotlin.android.share.ui.ShareFragment;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.share.api.ShareApi;
import com.mtime.util.UIUtil;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/10/16
 */
public class ShareExtJava {
    public static void dismissShareDialog(FragmentActivity activity) {
        ShareExtKt.dismissShareDialog(activity);
    }

    public static void dismissShareDialog(Fragment fragment) {
        ShareExtKt.dismissShareDialog(fragment);
    }

    /**
     * 加载分享数据并弹出分享弹框
     *
     * @param fragment
     * @param type
     * @param shareId
     * @param secondRelateId
     */
    public static void showShareDialog(
            Fragment fragment,
            String type,
            String shareId,
            String secondRelateId) {
        UIUtil.showLoadingDialog(fragment.getActivity());
        new ShareApi().getShareInfo(type, shareId, secondRelateId,
                new NetworkManager.NetworkListener<CommonShare>() {
                    @Override
                    public void onSuccess(CommonShare result, String showMsg) {
                        UIUtil.dismissLoadingDialog();
                        ShareExtKt.showShareDialog(
                                fragment,
                                ShareEntity.Companion.build(result),
                                ShareFragment.LaunchMode.STANDARD,
                                true,
                                (SharePlatform sp) -> {
                                    LogUtils.e("showShareDialog callback..." + sp.name());
                                    return null;
                                });
                    }

                    @Override
                    public void onFailure(NetworkException<CommonShare> exception, String showMsg) {
                        UIUtil.dismissLoadingDialog();
                        MToastUtils.showShortToast("获取分享数据失败");
                    }
                });
    }

    /**
     * 加载分享数据并弹出分享弹框
     *
     * @param activity
     * @param type
     * @param shareId
     * @param secondRelateId
     */
    public static void showShareDialog(
            FragmentActivity activity,
            String type,
            String shareId,
            String secondRelateId) {
        UIUtil.showLoadingDialog(activity);
        new ShareApi().getShareInfo(type, shareId, secondRelateId,
                new NetworkManager.NetworkListener<CommonShare>() {
                    @Override
                    public void onSuccess(CommonShare result, String showMsg) {
                        UIUtil.dismissLoadingDialog();
                        ShareExtKt.showShareDialog(
                                activity,
                                ShareEntity.Companion.build(result),
                                ShareFragment.LaunchMode.STANDARD,
                                true,
                                (SharePlatform sp) -> {
                                    LogUtils.e("showShareDialog callback..." + sp.name());
                                    return null;
                                });
                    }

                    @Override
                    public void onFailure(NetworkException<CommonShare> exception, String showMsg) {
                        UIUtil.dismissLoadingDialog();
                        MToastUtils.showShortToast("获取分享数据失败");
                    }
                });
    }
}
