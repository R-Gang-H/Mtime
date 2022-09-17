package com.kotlin.android.ktx.ext.permission

/**
 * 权限授权回调，DSL风格实现
 *
 * Created on 2020/6/8.
 *
 * @author o.s
 */
class PermissionsCallbackDSL : PermissionsCallback {
    private var granted: ((permissions: List<String>) -> Unit)? = null
    private var denied: ((permissions: List<String>) -> Unit)? = null
    private var showRationale: ((request: PermissionRequest) -> Unit)? = null
    private var neverAskAgain: ((permissions: List<String>) -> Unit)? = null
    
    fun onGranted(block: (permissions: List<String>) -> Unit) {
        granted = block
    }
    
    fun onDenied(block: (permissions: List<String>) -> Unit) {
        denied = block
    }
    
    fun onShowRationale(block: (request: PermissionRequest) -> Unit) {
        showRationale = block
    }
    
    fun onNeverAskAgain(block: (permissions: List<String>) -> Unit) {
        neverAskAgain = block
    }

    /**
     * 发起权限授权请求UI
     */
    fun request(request: PermissionRequest) {
        request.run {
            permissionFragment.requestPermissions(premissions.toTypedArray(), requestCode)
        }
    }

    override fun onGranted(permissions: List<String>) {
        granted?.invoke(permissions)
    }

    override fun onDenied(permissions: List<String>) {
        denied?.invoke(permissions)
    }

    override fun onShowRationale(request: PermissionRequest) {
        showRationale?.invoke(request)
    }

    override fun onNeverAskAgain(permissions: List<String>) {
        neverAskAgain?.invoke(permissions)
    }

}