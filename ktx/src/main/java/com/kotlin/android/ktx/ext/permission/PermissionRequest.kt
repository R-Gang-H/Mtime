package com.kotlin.android.ktx.ext.permission

/**
 * 权限授权请求UI界面相关封装
 *
 * Created on 2020/6/8.
 *
 * @author o.s
 */
data class PermissionRequest(
    var permissionFragment: PermissionFragment,
    var premissions: List<String>,
    var requestCode: Int
)