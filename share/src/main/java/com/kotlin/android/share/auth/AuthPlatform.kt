package com.kotlin.android.share.auth

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.kotlin.android.share.R

/**
 * 授权平台：微信，微博，QQ，
 *
 * Created on 2020/6/22.
 *
 * @author o.s
 */
enum class AuthPlatform(val id: Int, @StringRes val title: Int, @DrawableRes val icon: Int) {
    WEI_BO(1, R.string.wei_bo, R.mipmap.ic_sina),
    QQ(2, R.string.qq, R.mipmap.ic_qq),
    WE_CHAT(4, R.string.we_chat, R.mipmap.ic_wechat),
}