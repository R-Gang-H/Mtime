package com.kotlin.android.card.monopoly.provider

import android.app.Activity
import android.view.View
import com.kotlin.android.app.data.annotation.CARD_MONOPOLY_UNKNOWN
import com.kotlin.android.app.data.entity.monopoly.UserInfo
import com.kotlin.android.ktx.ext.core.getActivity

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.router.ext.getProvider

/**
 *
 * Created on 2020/11/18.
 *
 * @author o.s
 */

fun View?.startCardMainActivity(userInfo: UserInfo?, tab: Int = CARD_MONOPOLY_UNKNOWN) {
    this?.apply {
        getProvider(ICardMonopolyProvider::class.java)?.apply {
            getActivity()?.apply {
                startCardMainActivity(
                        context = this,
                        userId = userInfo?.userId,
                        tab = tab
                )
            }
        }
    }
}

fun Activity?.startCardMainActivity(userInfo: UserInfo?, tab: Int = CARD_MONOPOLY_UNKNOWN) {
    this?.apply {
        getProvider(ICardMonopolyProvider::class.java)?.apply {
            startCardMainActivity(
                    context = this@startCardMainActivity,
                    userId = userInfo?.userId,
                    tab = tab
            )
        }
    }
}