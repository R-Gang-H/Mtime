package com.kotlin.android.mine.ui.wallet

import androidx.fragment.app.viewModels
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.mine.databinding.FragBalanceBinding
import com.kotlin.android.user.UserStore
import kotlinx.android.synthetic.main.frag_balance.*
import java.math.BigDecimal

/**
 * create by WangWei on 2020/10/25
 * description:余额
 */
class BalanceFragment : BaseVMFragment<WalletViewModel, FragBalanceBinding>() {
    companion object {
        fun newInstance(): BalanceFragment = BalanceFragment()
    }

    override fun initVM(): WalletViewModel = viewModels<WalletViewModel>().value

    override fun initView() {
        val balance = UserStore.instance.getUser()?.balance?.div(100f)
        tv_count.text = BigDecimal(balance.toString()).toPlainString()
    }

    override fun initData() {
    }

    override fun startObserve() {
    }

    override fun destroyView() {
    }

}