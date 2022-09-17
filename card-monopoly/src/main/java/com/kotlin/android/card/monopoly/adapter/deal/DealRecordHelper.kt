package com.kotlin.android.card.monopoly.adapter.deal

import com.kotlin.android.card.monopoly.constants.Constants.TYPE_CARD
import com.kotlin.android.card.monopoly.constants.Constants.TYPE_SUIT
import com.kotlin.android.card.monopoly.constants.Constants.TYPE_TOOLS
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.PropCard
import com.kotlin.android.app.data.entity.monopoly.Record
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.ktx.ext.time.TimeExt
import com.kotlin.android.mtime.ktx.formatPublishTime

/**
 * @desc 游戏信息中统一的方法
 * @author zhangjian
 * @date 2020/11/12 13:47
 */
class DealRecordHelper {
    companion object {
        /**
         * 获取拍卖的卡片封面
         */
        fun getImageCover(data: Record): String {
            return when(getImageCardType(data)){
                TYPE_TOOLS->{
                    data.toolCards?.firstOrNull()?.toolCover ?: ""
                }
                TYPE_SUIT->{
                    data.suits?.firstOrNull()?.cardCover ?: ""
                }
                else->{
                    data.cards?.firstOrNull()?.cardCover ?: ""
                }
            }
        }

        /**
         * 获取数据类型(1L--普卡,2L--道具卡,3L--套装)
         */
        fun getImageCardType(data: Record): Long {
            val cardListEmpty = data.cards?.firstOrNull()?.cardName.isNullOrEmpty()
            val toolsListEmpty = data.toolCards?.firstOrNull()?.toolName.isNullOrEmpty()
            val suitListEmpty = data.suits?.firstOrNull()?.suitName.isNullOrEmpty()
            return if (!cardListEmpty) {
                TYPE_CARD
            } else if (!toolsListEmpty) {
                TYPE_TOOLS
            } else if (!suitListEmpty) {
                TYPE_SUIT
            } else {
                TYPE_CARD
            }
        }

        /**
         * 获取第二张卡片封面
         */
        fun getSecondImageCover(data: Record): String {
            return if (data.cards?.size ?: 0 > 1) {
                data.cards?.get(1)?.cardCover ?: ""
            } else {
                ""
            }
        }

        /**
         * 转换时间
         */
        fun setTime(mill: Long): String {
            return TimeExt.millis2String(mill * 1000, "yyyy-MM-dd  HH:mm:ss")
//            return formatPublishTime(mill)
        }

        /**
         * 获取卡片的名称
         */
        fun getCardName(
            cardList: List<Card>?,
            toolsList: List<PropCard>?,
            suitList: List<Suit>?
        ): String {
            val cardListEmpty = cardList?.firstOrNull()?.cardName.isNullOrEmpty()
            val toolsListEmpty = toolsList?.firstOrNull()?.toolName.isNullOrEmpty()
            val suitListEmpty = suitList?.firstOrNull()?.suitName.isNullOrEmpty()
            return if (!cardListEmpty) {
                cardList?.firstOrNull()?.cardName ?: "普通卡"
            } else if (!toolsListEmpty) {
                toolsList?.firstOrNull()?.toolName ?: "道具卡"
            } else if (!suitListEmpty) {
                suitList?.firstOrNull()?.suitName ?: "套装"
            } else {
                ""
            }
        }
    }
}