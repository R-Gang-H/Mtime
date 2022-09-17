package com.kotlin.chat_component.inner.domain

class EaseEmojicon {
    constructor() {}

    /**
     * constructor
     * @param icon- resource id of the icon
     * @param emojiText- text of emoji icon
     */
    constructor(icon: Int, emojiText: String?) {
        this.icon = icon
        this.emojiText = emojiText
        type = Type.NORMAL
    }

    /**
     * constructor
     * @param icon - resource id of the icon
     * @param emojiText - text of emoji icon
     * @param type - normal or big
     */
    constructor(icon: Int, emojiText: String?, type: Type?) {
        this.icon = icon
        this.emojiText = emojiText
        this.type = type
    }
    /**
     * get identity code
     * @return
     */
    /**
     * set identity code
     * @param identityCode
     */
    /**
     * identity code
     */
    var identityCode: String? = null
    /**
     * get the resource id of the icon
     * @return
     */
    /**
     * set the resource id of the icon
     * @param icon
     */
    /**
     * static icon resource id
     */
    var icon = 0
    /**
     * get the resource id of the big icon
     * @return
     */
    /**
     * set the resource id of the big icon
     * @return
     */
    /**
     * dynamic icon resource id
     */
    var bigIcon = 0
    /**
     * get text of emoji icon
     * @return
     */
    /**
     * set text of emoji icon
     * @param emojiText
     */
    /**
     * text of emoji, could be null for big icon
     */
    var emojiText: String? = null
    /**
     * get name of emoji icon
     * @return
     */
    /**
     * set name of emoji icon
     * @param name
     */
    /**
     * name of emoji icon
     */
    var name: String? = null
    /**
     * get type
     * @return
     */
    /**
     * set type
     * @param type
     */
    /**
     * normal or big
     */
    var type: Type? = null
    /**
     * get icon path
     * @return
     */
    /**
     * set icon path
     * @param iconPath
     */
    /**
     * path of icon
     */
    var iconPath: String? = null
    /**
     * get path of big icon
     * @return
     */
    /**
     * set path of big icon
     * @param bigIconPath
     */
    /**
     * path of big icon
     */
    var bigIconPath: String? = null

    enum class Type {
        /**
         * normal icon, can be input one or more in edit view
         */
        NORMAL,

        /**
         * big icon, send out directly when your press it
         */
        BIG_EXPRESSION
    }

    companion object {
        fun newEmojiText(codePoint: Int): String {
            return if (Character.charCount(codePoint) == 1) {
                codePoint.toString()
            } else {
                String(Character.toChars(codePoint))
            }
        }
    }
}