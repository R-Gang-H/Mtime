package com.kotlin.android.mine.ui.wallet

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.mine.AddCoupoBean
import com.kotlin.android.app.data.entity.mine.AddGiftCardBean
import com.kotlin.android.mine.bean.CouponViewBean
import com.kotlin.android.mine.repoistory.WalletRepository
import kotlinx.coroutines.launch

/**
 * create by WangWei on 2020/10/24
 * description:钱包viewmodel
 */
class WalletViewModel : BaseViewModel() {
    private val repo by lazy {
        WalletRepository()
    }
    private val couponUIModel: BaseUIModel<CouponViewBean> = BaseUIModel()
    val couponState = couponUIModel.uiState

    private val giftCardUIModel: BaseUIModel<CouponViewBean> = BaseUIModel()
    val giftCardState = giftCardUIModel.uiState

    private val addCouponUIModel: BaseUIModel<AddCoupoBean> = BaseUIModel()
    val addCouponState = addCouponUIModel.uiState

    private val addCardUIModel: BaseUIModel<AddGiftCardBean> = BaseUIModel()
    val addCardState = addCardUIModel.uiState

    fun loadCoupons(isMore: Boolean, type: Long) {
        viewModelScope.launch(main) {
            couponUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                repo.getCoupons(isMore, type)
            }

            couponUIModel.emitUIState(showLoading = false)
            checkResult(result, success = { couponUIModel.emitUIState(success = it, loadMore = isMore, noMoreData = it.hasNext.not()) },
                    error = { couponUIModel.emitUIState(error = it) }, netError = { couponUIModel.emitUIState(netError = it) })
        }
    }

    fun loadGiftCards(isMore: Boolean) {
        viewModelScope.launch(main) {
            giftCardUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                repo.getGiftCards(isMore)
            }
            giftCardUIModel.emitUIState(showLoading = false)
            checkResult(result, success = { giftCardUIModel.emitUIState(success = it, loadMore = isMore, noMoreData = it.hasNext.not()) },
                    error = { giftCardUIModel.emitUIState(error = it) }, netError = { giftCardUIModel.emitUIState(netError = it) })
        }

    }

    /**
     * 添加券
     */
    fun addCoupon(voucherCode: String) {
        viewModelScope.launch(main) {
            addCouponUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                repo.addCoupon(voucherCode)
            }
            addCouponUIModel.emitUIState(showLoading = false)
            checkResult(result,
                    success = { addCouponUIModel.emitUIState(success = it) },
                    error = { addCouponUIModel.emitUIState(error = it) },
                    netError = { addCouponUIModel.emitUIState(netError = it) })

        }
    }

    /**
     * 添加卡
     */
    fun addGiftCard(cardNum:String,password:String){
        viewModelScope.launch(main) {
            addCardUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                repo.addGiftCard(cardNum, password)
            }
            addCardUIModel.emitUIState(showLoading = false)
            checkResult(result,
                    success = { addCardUIModel.emitUIState(success = it) },
                    error = { addCardUIModel.emitUIState(error = it) },
                    netError = { addCardUIModel.emitUIState(netError = it) })
        }
    }

}

