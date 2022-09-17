package com.kotlin.android.card.monopoly

import com.kotlin.android.app.data.entity.monopoly.Card


/**
 * 卡片大富翁模块：
 *
 * Created on 2020/7/2.
 *
 * @author o.s
 */

const val AVATAR_WIDTH: Float = 48F
const val AVATAR_HEIGHT: Float = 48F

const val CARD_WIDTH: Float = 67F
const val CARD_HEIGHT: Float = 98F

const val SUIT_WIDTH: Float = 70F
const val SUIT_HEIGHT: Float = 108.5F

const val BOX_WIDTH: Float = 70F
const val BOX_HEIGHT: Float = 61F

const val FRIEND_RESULT_CODE: Int = 99
const val FRIEND_RESULT_CODE_100: Int = 100
const val FRIEND_RESULT_CODE_101: Int = 101
const val FRIEND_RESULT_CODE_102: Int = 102
const val TAB_MY_WISH: Int = 0
const val TAB_WISH_WALL: Int = 1
const val TAB_MESSAGE_BOARD: Int = 2
const val TAB_AUCTION_BUY: Int = 0
const val TAB_AUCTION_AUCTION: Int = 1
const val TAB_AUCTION_BID: Int = 2

const val KEY_CARD_MONOPOLY_STYLE = "key_card_monopoly_style"
const val KEY_CARD_MONOPOLY_MAIN_TAB = "key_card_monopoly_main_tab"
const val KEY_CARD_MONOPOLY_USER_ID = "key_card_monopoly_user_id"
const val KEY_CARD_MONOPOLY_ROBOT = "key_card_monopoly_robot"
const val KEY_CARD_MONOPOLY_USER_INFO = "key_card_monopoly_user_info"
const val KEY_CARD_MONOPOLY_CARD_ID = "key_card_monopoly_card_id"
const val KEY_CARD_MONOPOLY_SUIT_C = "key_card_monopoly_suit_c"
const val KEY_CARD_MONOPOLY_SUIT_FROM = "key_card_monopoly_suit_from"
const val KEY_CARD_MONOPOLY_AUCTION_TAB = "key_card_monopoly_auction_tab"
const val KEY_CARD_MONOPOLY_WISH_SUIT = "key_card_monopoly_wish_suit"
const val KEY_CARD_MONOPOLY_WISH_TAB = "key_card_monopoly_wish_tab"
const val KEY_CARD_MONOPOLY_SUIT_TYPE = "key_card_monopoly_suit_type"
const val KEY_CARD_MONOPOLY_AUCTION_TYPE = "key_card_monopoly_auction_type"
const val KEY_FRIENDS = "key_friends"
const val KEY_CARD_FRIEND = "card_friend"  //跳转好友详情
const val KEY_SUIT_ID = "suitID"  //套装id
const val KEY_SUIT_TYPE = "suitType"  //套装id

const val CARD_MONOPOLY_FAMILY_ID = 9824L
const val CARD_MONOPOLY_POST_ID = 227107320L

const val LIMIT_BOX_ID = 6L

fun List<Card>.userCardIds(): String {
    val sb = StringBuilder()
    forEachIndexed { index, card ->
        if (index > 0) {
            sb.append(",")
        }
        sb.append(card.userCardId)
    }
    return sb.toString()
}

fun <T> List<T>.ids(with: T.() -> Any): String {
    val sb = StringBuilder()
    forEachIndexed { index, entity ->
        if (index > 0) {
            sb.append(",")
        }
        sb.append(with(entity))
    }
    return sb.toString()
}