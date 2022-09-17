package com.kotlin.android.mine.repoistory

import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.mine.AddCoupoBean
import com.kotlin.android.app.data.entity.mine.AddGiftCardBean
import com.kotlin.android.app.data.entity.mine.CouponList
import com.kotlin.android.app.data.entity.mine.GiftCardList
import com.kotlin.android.mine.bean.CardItemViewBean
import com.kotlin.android.mine.bean.CouponItemViewBean
import com.kotlin.android.mine.bean.CouponViewBean
import com.kotlin.android.mine.binder.CouponBinder
import com.kotlin.android.mine.binder.GiftCardBinder
import com.kotlin.android.retrofit.getRequestBody
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import java.text.SimpleDateFormat
import java.util.*

/**
 * create by WangeWei on 2020/10/24
 * description:我的钱包repository  券、卡
 */
class WalletRepository : BaseRepository() {
    private val START_PAGEINDEX = 1L
    private var pageIndex: Long = START_PAGEINDEX// 我的收藏中每次只会有一种类型请求，故此处只用了一个pageIndex
    private val PAGESIZE = 20L

    /**
     * 分页查询购票优惠券
     */
    suspend fun getCoupons(isMore: Boolean, type: Long): ApiResult<CouponViewBean> {
        resetPageIndex(isMore)
        return request(
            api = { apiMTime.getCoupons(pageIndex, PAGESIZE, type) },
            converter = { data ->
                pageIndex++
                CouponViewBean(
                    false, 0L, data.voucherList?.map { covertCouponBinder(it) }?.toMutableList()
                        ?: mutableListOf<MultiTypeBinder<*>>()
                )
            })
    }

    /**
     * 分页查询礼品卡
     */
    suspend fun getGiftCards(isMore: Boolean): ApiResult<CouponViewBean> {
        resetPageIndex(isMore)
        return request(
            api = { apiMTime.getGiftCards(pageIndex, PAGESIZE) },
            converter = { data ->
                pageIndex++
                CouponViewBean(
                    false, 0L, data.memberList?.map { covertGiftCardBinder(it) }?.toMutableList()
                        ?: mutableListOf<MultiTypeBinder<*>>()
                )
            })
    }


    /**
     * 添加购票优惠券
     */
    suspend fun addCoupon(voucherCode: String): ApiResult<AddCoupoBean> {
        val params = arrayMapOf<String, Any>()
        params["voucherCode"] = voucherCode
        params["vcode"] = "vcode"
        params["vcodeId"] = ""
        return request { apiMTime.addCoupon(getRequestBody(params)) }
    }

    /**
     * 添加礼品卡
     */
    suspend fun addGiftCard(cardNum: String, password: String): ApiResult<AddGiftCardBean> {
        val params = arrayMapOf<String, Any>()
        params["cardNum"] = cardNum
        params["password"] = password
        params["vcode"] = ""
        params["vcodeId"] = ""
        params["orderId"] = ""
        return request { apiMTime.addGiftCard(getRequestBody(params)) }
    }


    /**
     * 刷新重置pageIndex
     */
    private fun resetPageIndex(isMore: Boolean) {
        if (isMore.not()) pageIndex = START_PAGEINDEX
    }

    /**
     * 获取收藏帖子binder
     */
    private fun covertCouponBinder(bean: CouponList.Voucher): MultiTypeBinder<*> {
        return CouponBinder(
            CouponItemViewBean(
                status = bean.status,
                timeDes = "有效期至：" + getSimpleDate(bean.endTime),
                name = bean.name,
                description = bean.description
            )
        )
    }


    /**
     * 获取收藏帖子binder
     */
    private fun covertGiftCardBinder(bean: GiftCardList.Member): MultiTypeBinder<*> {
        val unitDes = when (bean.cType) {
            0L, 2L -> "次"
            1L, 3L -> "点"
            else -> "点"
        }

        val count = when (bean.cType) {
            0L, 2L -> bean.balance.toString()
            1L, 3L -> bean.balancePoint ?: ""
            else -> bean.balancePoint ?: ""
        }

        return GiftCardBinder(
            CardItemViewBean(
                timeDes = "有效期至：" + getSimpleDate(bean.endTime),
                membershipCardId = bean.cId,
                balance = bean.balance,
                balancePoint = bean.balancePoint ?: "",
                type = bean.cType,
                name = bean.cName ?: "",
                unitDes = unitDes,
                count = count,
                cNum = bean.cNum ?: ""

            )
        )
    }

    private fun getSimpleDate(d: Long): String? {
        return SimpleDateFormat("yyyy-MM-dd").format(Date(d))
    }

}