package com.kotlin.android.js.sdk.entity

import com.kotlin.android.app.data.ProguardRule
import java.util.ArrayList

/**
 * 创建者: zl
 * 创建时间: 2020/12/2 2:32 下午
 * 描述:
 */
interface JsEntity {
    class AppLinkEntity : ProguardRule {
        var data: DataBean? = null

        class DataBean : ProguardRule {
            var applinkData: String? = null
            var originUrl: String? = null //弃用
        }
    }

    class HandleGoBackEntity : ProguardRule {
        var data: DataBean? = null

        class DataBean : ProguardRule {
            var isCloseWindow: Boolean = false // 含义：true表示回app上一页面，false表示回h5上一页面，如果没有则回app上一页面
            var refreshWindow: Boolean = false
        }
    }

    class ImageBrowserEntity : ProguardRule {
        var data: DataBean? = null

        class DataBean : ProguardRule {
            var currentImageIndex // 点击图片位置
                    = 0
            var photoImageUrls // 图片数组
                    : ArrayList<String>? = null
            var isGif // false or true ，图片类型是否是gif图，埋点使用，在iOS10.5.0和Android6.5.0版本添加
                    = false
            var originUrl // 埋点用的字段，目前传原始URL，iOS10.5.0 & 6.5.0 开始支持
                    : String? = null
        }
    }

    data class LoginStateEntity(var userId: String,
                                var loginToken: String,
                                var action: String//"login" 或者 "logout" // 是登录还是登出
    ) : ProguardRule

    data class NetStatusEntity(val nativeNetStatus: String = "0") : ProguardRule

    data class PlatformEntity(val platform: String = "Android") : ProguardRule

    class ShareEntity : ProguardRule {
        var data: DataBean? = null

        class DataBean : ProguardRule {
            var summary // 内容总纲
                    : String? = null
            var title // 标题
                    : String? = null
            var url // 链接地址
                    : String? = null
            var pic // 图像地址
                    : String? = null
        }
    }

    class VideoPlayerEntity : ProguardRule {
        var data: DataBean? = null

        class DataBean : ProguardRule {
            var videoID //视频ID
                    : String? = null
            var videoType //视频类型
                    : String? = null
            var videoTop //视频顶部位置
                    = 0f
            var videoLeft //视频左侧位置;
                    = 0f
            var videoWidth //视频宽度;
                    = 0f
            var videoHeight //视频高度;
                    = 0f
        }
    }
}