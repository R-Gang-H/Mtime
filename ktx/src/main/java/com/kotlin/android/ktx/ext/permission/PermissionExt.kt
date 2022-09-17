package com.kotlin.android.ktx.ext.permission

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 *
 * Created on 2020/6/8.
 *
 * @author o.s
 */

private const val TAG = "ktx-permission"

/**
 * 判断权限是否已经授权成功
 */
fun Activity.isGranted(permission: String): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.M
            || ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

/**
 * 申请需要的权限时调用 [FragmentActivity.permissions] 即可：
 * 例如：SD权限申请如下
 */
fun FragmentActivity.sdPermission(callbacks: PermissionsCallbackDSL.() -> Unit) {
    permissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            callbacks = callbacks
    )
}

/**
 * 权限授权请求
 * [permissions] 权限列表
 * [callbacks] DSL风格调用，现实需要的部分
 */
fun FragmentActivity.permissions(vararg permissions: String, callbacks: PermissionsCallbackDSL.() -> Unit) {
    val callback = PermissionsCallbackDSL().apply {
        callbacks()
    }
    val requestCode = PermissionsCallbackCache.put(callback)

    // 已经授权的权限列表
    val granted = permissions.filter { isGranted(it) }
    // 过滤出需要授权的权限列表
    val need = permissions.filter { !isGranted(it) }
    val shouldShow = mutableListOf<String>()
    val notShow = mutableListOf<String>()

    if (need.isEmpty()) {
        // 权限都已授予回调
        callback.onGranted(granted)
    } else {
        need.forEach {
            // 分解是否应显示具有请求权限的UI界面的权限列表
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, it)) {
                shouldShow.add(it)
            } else {
                notShow.add(it)
            }
        }

        handlePermissionFragment(granted, shouldShow, notShow, requestCode, callback)
        /**
        val permissionFragment = getPermissionFragment()
        // 设置已经授权的权限列表，准备后续回调，保持权限信息一致
        permissionFragment.grantedPermissions = granted

        // 需要显示请求权限的UI界面
        if (shouldShow.isNotEmpty()) {
            val request = PermissionRequest(permissionFragment, shouldShow, requestCode)
            request.run {
                permissionFragment.requestPermissions(premissions.toTypedArray(), requestCode)
            }
            callback.onShowRationale(request)
        }

        // 不需要显示UI界面的授权
        if (notShow.isNotEmpty()) {
            permissionFragment.requestPermissions(notShow.toTypedArray(), requestCode)
        }*/
    }
}

/**
 * 获取授权UI界面所需的Fragment
 */
private fun FragmentActivity.getPermissionFragment(): PermissionFragment {
    var fragment = supportFragmentManager.findFragmentByTag(TAG)
    if (fragment == null) {
        fragment = PermissionFragment()
        supportFragmentManager.beginTransaction().add(fragment, TAG).commitNow()
    }
    return fragment as PermissionFragment
}

/**
 * 处理授权Fragment及后续授权行为
 */
private fun FragmentActivity.handlePermissionFragment(
        granted: List<String>,
        shouldShow: MutableList<String>,
        notShow: MutableList<String>,
        requestCode: Int,
        callback: PermissionsCallbackDSL
) {
    var fragment = supportFragmentManager.findFragmentByTag(TAG) as? PermissionFragment
    if (fragment == null) {
        fragment = PermissionFragment()
        try {
            supportFragmentManager.beginTransaction().add(fragment, TAG).commitNow()
            fragment.doRequestPermissions(granted, shouldShow, notShow, requestCode, callback)
        } catch (e: IllegalStateException) {
            // FragmentManager is already executing transactions
            e.printStackTrace()
            try {
                Looper.myLooper()?.let {
                    Handler(it).postDelayed({
                        supportFragmentManager.beginTransaction().add(fragment, TAG).commitNow()
                        fragment.doRequestPermissions(granted, shouldShow, notShow, requestCode, callback)
                    }, 100)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    } else {
        fragment.doRequestPermissions(granted, shouldShow, notShow, requestCode, callback)
    }
}

/**
 * 执行授权
 */
private fun PermissionFragment.doRequestPermissions(
        granted: List<String>,
        shouldShow: MutableList<String>,
        notShow: MutableList<String>,
        requestCode: Int,
        callback: PermissionsCallbackDSL
) {
    // 设置已经授权的权限列表，准备后续回调，保持权限信息一致
    grantedPermissions = granted

    // 需要显示请求权限的UI界面
    if (shouldShow.isNotEmpty()) {
        val request = PermissionRequest(this, shouldShow, requestCode)
        request.run {
            permissionFragment.requestPermissions(premissions.toTypedArray(), requestCode)
        }
        callback.onShowRationale(request)
    }

    // 不需要显示UI界面的授权
    if (notShow.isNotEmpty()) {
        requestPermissions(notShow.toTypedArray(), requestCode)
    }
}