package com.kotlin.chat_component.inner.model.styles

import android.graphics.drawable.Drawable
import com.kotlin.chat_component.inner.model.styles.EaseMessageListItemStyle

/**
 * Created by wei on 2016/11/29.
 */
class EaseMessageListItemStyle(builder: Builder) {
    var isShowUserNick: Boolean
    var isShowAvatar: Boolean
    var myBubbleBg: Drawable?
    var otherBubbleBg: Drawable?

    class Builder {
        var showUserNick = false
        var showAvatar = false
        var myBubbleBg: Drawable? = null
        var otherBubbleBg: Drawable? = null
        fun showUserNick(showUserNick: Boolean): Builder {
            this.showUserNick = showUserNick
            return this
        }

        fun showAvatar(showAvatar: Boolean): Builder {
            this.showAvatar = showAvatar
            return this
        }

        fun myBubbleBg(myBubbleBg: Drawable?): Builder {
            this.myBubbleBg = myBubbleBg
            return this
        }

        fun otherBuddleBg(otherBuddleBg: Drawable?): Builder {
            otherBubbleBg = otherBuddleBg
            return this
        }

        fun build(): EaseMessageListItemStyle {
            return EaseMessageListItemStyle(this)
        }
    }

    init {
        isShowUserNick = builder.showUserNick
        isShowAvatar = builder.showAvatar
        myBubbleBg = builder.myBubbleBg
        otherBubbleBg = builder.otherBubbleBg
    }
}