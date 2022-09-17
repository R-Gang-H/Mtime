package com.kotlin.android.ktx.ext.permission

/**
 * 权限授权回调
 *
 * Created on 2020/6/8.
 *
 * @author o.s
 */
interface PermissionsCallback {

    /**
     * 授权成功
     */
    fun onGranted(permissions: List<String>)

    /**
     * 授权被拒绝
     * [permissions] 拒绝的权限列表
     */
    fun onDenied(permissions: List<String>)

    /**
     * 显示具有请求权限的UI界面
     * [request] 权限请求相关封装对象
     */
    fun onShowRationale(request: PermissionRequest) {
        request.run {
            permissionFragment.requestPermissions(premissions.toTypedArray(), requestCode)
        }
    }

    /**
     * 不再询问授权
     * [permissions] 不再询问权限列表
     */
    fun onNeverAskAgain(permissions: List<String>)

}