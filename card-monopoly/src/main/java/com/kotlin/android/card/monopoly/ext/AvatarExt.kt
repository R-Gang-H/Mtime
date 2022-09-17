package com.kotlin.android.card.monopoly.ext

import com.kotlin.android.card.monopoly.AVATAR_HEIGHT
import com.kotlin.android.card.monopoly.AVATAR_WIDTH
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.widget.CircleImageView
import com.kotlin.android.app.data.entity.monopoly.UserInfo
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.dimension.dp

/**
 *
 * Created on 2020/11/20.
 *
 * @author o.s
 */

inline fun CircleImageView?.setUserInfo(userInfo: UserInfo?) {
    this?.loadImage(
        data = userInfo?.avatarUrl,
        width = AVATAR_WIDTH.dp,
        height = AVATAR_HEIGHT.dp,
        defaultImgRes = R.drawable.default_user_head
    )
}

inline fun CircleImageView?.setUserInfo(url: String?) {
    this?.loadImage(
        data = url,
        width = AVATAR_WIDTH.dp,
        height = AVATAR_HEIGHT.dp,
        defaultImgRes = R.drawable.default_user_head
    )
}