package com.mtime.bussiness.daily.provider;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kotlin.android.router.RouterManager;
import com.kotlin.android.app.router.path.RouterActivityPath;
import com.kotlin.android.app.router.path.RouterProviderPath;
import com.kotlin.android.app.router.provider.daily.IDailyProvider;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/4
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_DAILY_RCMD)
public class DailyRcmdProvider implements IDailyProvider {
    @Override
    public void startDailyRecommendActivity() {
        RouterManager.Companion.getInstance()
                .navigation(
                        RouterActivityPath.Daily.PAGER_RCMD,
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
