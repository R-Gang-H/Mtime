package com.kotlin.android.ugc.detail.component.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/8/7
 * description: ugc图集详情bannerView
 */

data class UgcImageViewBean(var imageId: String = "",
                            var ugcContent: String = "",//每一张图片配图
                            var ugcPic: String = "",//图片地址
                            var title:String = ""//图片标题
): ProguardRule