package com.kotlin.chat_component.inner.domain

import com.kotlin.chat_component.inner.domain.EaseEmojicon

/**
 * 一组表情所对应的实体类
 *
 */
class EaseEmojiconGroupEntity {
    /**
     * 表情数据
     */
    var emojiconList: List<EaseEmojicon>? = null

    /**
     * 图片
     */
    var icon = 0

    /**
     * 组名
     */
    var name: String? = null

    /**
     * 表情类型
     */
    var type: EaseEmojicon.Type? = null

    constructor() {}
    constructor(icon: Int, emojiconList: List<EaseEmojicon>?) {
        this.icon = icon
        this.emojiconList = emojiconList
        type = EaseEmojicon.Type.NORMAL
    }

    constructor(icon: Int, emojiconList: List<EaseEmojicon>?, type: EaseEmojicon.Type?) {
        this.icon = icon
        this.emojiconList = emojiconList
        this.type = type
    }
}