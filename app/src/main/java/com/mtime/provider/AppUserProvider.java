package com.mtime.provider;

import android.app.Activity;
import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kotlin.android.app.router.path.RouterActivityPath;
import com.kotlin.android.app.router.path.RouterProviderPath;
import com.kotlin.android.app.router.provider.user.IAppUserProvider;
import com.kotlin.android.router.RouterManager;

import org.jetbrains.annotations.NotNull;

/**
 * create by lushan on 2020/8/28
 * description:
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_APP_USER)
public class AppUserProvider implements IAppUserProvider {

    //    个人资料页
    @Override
    public void startProfileActivity(@NotNull Activity activity) {
        RouterManager.Companion.getInstance().navigation(
                RouterActivityPath.AppUser.PAGE_PROFILE,
                null,
                activity,
                -1,
                0,
                false,
                null
        );
    }

    //关于我们
    @Override
    public void startAboutActivity() {
        RouterManager.Companion.getInstance().navigation(
                RouterActivityPath.AppUser.PAGE_ABOUT,
                null,
                null,
                -1,
                0,
                false,
                null
        );
    }

    //设置页面
    @Override
    public void startSettingActivity() {
        RouterManager.Companion.getInstance().navigation(
                RouterActivityPath.AppUser.PAGE_SETTING,
                null,
                null,
                -1,
                0,
                false,
                null
        );

    }

    //任务中心
    @Override
    public void startTaskActivity() {
        RouterManager.Companion.getInstance().navigation(RouterActivityPath.Mine.PAGE_CREATOR_ACTIVITY,
                null,
                null,
                -1,
                0,
                false,
                null
        );
    }

    @Override
    public void init(Context context) {

    }
}
