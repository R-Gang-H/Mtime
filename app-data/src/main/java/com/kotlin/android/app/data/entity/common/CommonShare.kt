package com.kotlin.android.app.data.entity.common

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by zhousuqiang on 2020-09-21
 */
data class CommonShare(var qq: Qq?,
                       var weixin: Weixin?,
                       var weibo: Weibo?,
                       var smsDesc: String?,
                       var mtime: Mtime?,
                       var url: String?,
                       var email: Email?,
                       var success: Boolean = false
) : ProguardRule {
    data class Qq(var img: String?,
                  var title: String?,
                  var url: String?,
                  var desc: String?) : ProguardRule

    data class Weixin(var img: String?,
                      var title: String?,
                      var url: String?,
                      var desc: String?) : ProguardRule

    data class Weibo(var img: String?,
                     var desc: String?) : ProguardRule

    data class Mtime(var img: String?,
                     var desc: String?) : ProguardRule

    data class Email(var img: String?,
                     var title: String?,
                     var desc: String?) : ProguardRule
}