package com.kotlin.android.app.router.provider.simple

import android.app.Activity
import android.os.Bundle
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * 创建者: zl
 * 创建时间: 2020/6/5 10:11 AM
 * 描述:根据不同业务，在不同的包下创建接口
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_SIMPLE)
interface ISimpleProvider : IBaseProvider {

    fun startCallActivity()
    fun startRepoActivity()
    fun startBatchActivity()
    fun startPermissionActivity()
    fun startProgressDialogActivity()
    fun startDataBindingActivity()
    fun startMyActivity()
    fun startSetActivity()
    fun startUserDetailActivity(bundle: Bundle?, activity: Activity,
                                callback: NavigationCallback)

    fun startLoginActivity(
            bundle: Bundle? = null
    )

    fun startMTimeApiActivity()
    fun startUserListActivity()
    fun startShareActivity()
//    fun startShareSimpleActivity()
    fun startMultiTypeActivityActivity()
    fun startShapeActivity()
    fun startMultiStateViewActivity()
    fun startStatusBarActivity()
    fun startPublishActivity()
    fun startPhotoAlbumActivity()
    fun startCommentActivity()
    fun startTipsActivity()
    fun startBrowserActivity()
}