package com.mtime.bussiness.main.maindialog;

import androidx.appcompat.app.AppCompatActivity;

import com.mtime.bussiness.main.maindialog.bean.DialogDataBean;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.List;

/**
 * 需求 http://wiki.inc-mtime.com/pages/viewpage.action?pageId=145653840
 * */
public class MainDialogManager {

    private SoftReference<AppCompatActivity> mActivity;
    private List<MainDialogApi.Api> mQueue;
    private boolean mIsShowNewUserGift; //是否显示过了新手礼包

    public void init(AppCompatActivity activity) {
        mActivity = new SoftReference<>(activity);
        MainDialogApi mMainDialogApi = new MainDialogApi();
        mMainDialogApi.requestDatas(new MainDialogApi.MainDialogApiListener() {
            @Override
            public void onRequestApiFinish(List<MainDialogApi.Api> queue) {
                mQueue = queue;
                showNext();
            }
        });
    }

    private void showNext() {
        if(null != mQueue && !mQueue.isEmpty() && null != mActivity && null != mActivity.get()) {
            MainDialogApi.Api api = mQueue.get(0);
            mQueue.remove(api);
            DialogDataBean bean = api.getData();
            // 新用户保护机制
            if(null == bean || (mIsShowNewUserGift && bean.isNewUserSave)) {
                return;
            }
            boolean shown = api.onShow(mActivity.get(), new MainDialogApi.Api.ApiShowListener() {
                @Override
                public void onDismiss(DialogDataBean item) {
                    if (null != item) {
                        mIsShowNewUserGift = item.type == DialogDataBean.TYPE_OF_NEW_USER_GIFT;
                        if (item.isNextShow) {
                            showNext();
                        }
                    }
                }
            });
            if (!shown && bean.isNextShow) {
                showNext();
            }
        }
    }

    /**
     * app运行中弹出框休
     * 所为的运行中，实现就是在主页面底部tab切换到"首页"tab时调用此方法
     */
    public void appRuningShow() {
        if(null != mQueue && !mQueue.isEmpty() && null != mActivity && null != mActivity.get()) {
            Iterator<MainDialogApi.Api> iterator= mQueue.iterator();
            while (iterator.hasNext()) {
                MainDialogApi.Api api = iterator.next();
                iterator.remove();
                DialogDataBean bean = api.getData();
                if(null != bean && bean.isAppRuningShow) {
                    // 新用户保护机制
                    if(mIsShowNewUserGift && bean.isNewUserSave) {
                        continue;
                    }
                    if (api.onShow(mActivity.get(), null)) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * 资源释放
     */
    public void destroy() {
        if(null != mQueue && !mQueue.isEmpty()) {
            for(MainDialogApi.Api api : mQueue) {
                api.onDestroy();
            }
            mQueue.clear();
        }
        if(null != mActivity) {
            mActivity.clear();
            mActivity = null;
        }
    }
}
