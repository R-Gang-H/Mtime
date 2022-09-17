package com.kotlin.android.ugc.detail.component.listener

import android.app.Activity
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.statusBarHeight
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ugc.detail.component.ui.widget.UgcTitleView

/**
 * create by lushan on 2020/9/11
 * description:文章、日志、帖子相册滑动监听
 */
class IAlbumScrollListener(var isAlbum: Boolean, private var albumTitleView: UgcTitleView?) : RecyclerView.OnScrollListener() {
    private var totalScrollY = 0
    private var bannerHeight = 531.dp - statusBarHeight//轮播图的高度
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (isAlbum.not()) return
        totalScrollY += dy

        val isDark = totalScrollY <= bannerHeight

        val alpha = if (!isDark) {
            255
        } else {
            (255 * totalScrollY.toFloat() / bannerHeight).toInt()
        }
        val colorInt = Color.argb(alpha, 255, 255, 255)

        (albumTitleView?.context as? Activity)?.run {
            immersive().updateStatusBarColor(colorInt)
                .statusBarDarkFont(!isDark)
        }
        albumTitleView?.setTitleBackgroundColor(colorInt)
        albumTitleView?.setTitleColor(isDark = isDark)

    }
}