package com.kotlin.android.ktx.ext.permission

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * 授权请求Fragment，授权结果分发
 *
 * Created on 2020/6/8.
 *
 * @author o.s
 */
class PermissionFragment : Fragment() {

    /**
     * 已经授予的权限列表
     */
    var grantedPermissions: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 在重新创建 Activity 时保留（在需要时可设置该值）
        retainInstance = true
    }

    /**
     * 权限请求回调结果处理
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 权限处理结果分类
        val granted = mutableListOf<String>()
        val denied = mutableListOf<String>()
        val neverAskAgain = mutableListOf<String>()
        permissions.forEachIndexed { index, permission ->
            if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                if (shouldShowRequestPermissionRationale(permission)) {
                    denied.add(permission)
                } else {
                    neverAskAgain.add(permission)
                }
            } else {
                granted.add(permission)
            }
        }
        // 根据处理结果进行回调
        PermissionsCallbackCache.get(requestCode)?.apply {
            if (denied.isNotEmpty()) {
                onDenied(denied)
            }
            if (neverAskAgain.isNotEmpty()) {
                onNeverAskAgain(neverAskAgain)
            }
            if (granted.isNotEmpty() || grantedPermissions?.isNotEmpty() == true) {
                grantedPermissions?.run {
                    granted.addAll(this)
                }
                onGranted(granted)
            }
        }
    }
}