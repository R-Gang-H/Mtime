package com.kotlin.android.app.data.ext

/**
 * 创建者: zl
 * 创建时间: 2020/9/3 10:14 AM
 * 描述:存放内存变量
 */
object VariateExt {
    /**
     * 图片代理地址
     * 此数据目前是从旧框架里获取的，独立出来，方便新框架使用
     * 本地备份兜底
     */
    var imgProxyUrl: String = "https://imgproxy.mtime.cn/get.ashx?uri="

    /**
     * 图片上传地址
     * 此数据目前是从旧框架里获取的，独立出来，方便新框架使用
     * 本地备份兜底
     */
    var imgUploadUrl: String = "https://front-gateway.mtime.com/image/upload"

    /**
     * 【意见反馈】模块用的群组帖子ID
     */
    var feedbackPostId: Long = 0

    /**
     * 身份认证公函模板的图片地址
     */
    var authTemplateImg: String = ""

    var initMainTabIndex: Long = 0

}