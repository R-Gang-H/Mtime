package com.kotlin.android.mine.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.message.UnReadMessage
import com.kotlin.android.app.data.entity.mine.AccountStatisticsInfo
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.*
import com.kotlin.android.mine.repoistory.MineRepository
import com.kotlin.android.mtime.ktx.getString

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.user.UserManager
import com.kotlin.android.app.data.entity.user.User
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.user.isLogin
import kotlinx.coroutines.launch

class MineViewModel : BaseViewModel() {
    private val repo by lazy {
        MineRepository()
    }

    //    是否是登录
    private val _loginState = MutableLiveData<Boolean>()//是否是登录了
    val loginState: LiveData<Boolean>
        get() = _loginState

    //    用户信息
    private val _mineUserState = MutableLiveData<MineUserViewBean>()
    val mineUserState: LiveData<MineUserViewBean>
        get() = _mineUserState

    //    想看电影
    private val _mineWannaState = MutableLiveData<MineWannaViewBean>()
    val mineWannaState: LiveData<MineWannaViewBean>
        get() = _mineWannaState

    //    看过的电影
    private val _mineHasSeenState = MutableLiveData<MineHasSeenViewBean>()
    val mineHasSeenState: LiveData<MineHasSeenViewBean>
        get() = _mineHasSeenState

    //    个人中心-订单
    private val _mineOrderState = MutableLiveData<MineOrderViewBean>()
    val mineOrderState: LiveData<MineOrderViewBean>
        get() = _mineOrderState

    //    个人中心-钱包
    private val _mineWalletState = MutableLiveData<MineWalletViewBean>()
    val mineWalletState: LiveData<MineWalletViewBean>
        get() = _mineWalletState

    //    个人中心-影人俱乐部
    private val _mineClubState = MutableLiveData<MineClubViewBean>()
    val mineClubState: LiveData<MineClubViewBean>
        get() = _mineClubState

    //    用户统计信息
    private val statisticUIModel = BaseUIModel<AccountStatisticsInfo>()
    val statisticState = statisticUIModel.uiState

    //    用户详情信息
    private val accountDetailUIModel = BaseUIModel<User>()
    val accountDetailState = accountDetailUIModel.uiState

//    未读消息数量
    private val unReadMessageUIModel = BaseUIModel<UnReadMessage>()
    val unReadMessageState = unReadMessageUIModel.uiState

    /**
     * 更新登录状态
     */
    fun setLoginState(isLogin: Boolean) {
        if (loginState.value != isLogin) {
            _loginState.value = isLogin
            updateUserData()
        }

    }

    fun initData() {
        _loginState.value = isLogin()
        if (isLogin()) {
            updateUserData()
        } else {
            clearUserData()
        }
        clearUserStatisticData()
    }

    private fun updateUserData() {
        _mineUserState.value = MineUserViewBean(UserManager.instance.user?.nickname.orEmpty(), UserManager.instance.userAvatar.orEmpty(), UserManager.instance.userSex.toLong(), 0, 0, UserManager.instance.sign, UserManager.instance.userLevel.toLong(), UserManager.instance.userAuthType)
    }

    /**
     * 清空用户信息
     */
    private fun clearUserData() {
        _mineUserState.value = MineUserViewBean()
    }

    /**
     * 清空用户统计信息
     */
    private fun clearUserStatisticData() {
        _mineWannaState.value = MineWannaViewBean()
        _mineHasSeenState.value = MineHasSeenViewBean()
        _mineOrderState.value = MineOrderViewBean()
        _mineWalletState.value = MineWalletViewBean()
        _mineClubState.value = MineClubViewBean()
        unReadMessageUIModel.emitUIState(success = UnReadMessage())
    }

    /**
     * 获取用户统计信息
     */
    fun getMineStatisticInfo() {
        if (isLogin().not()) {//未登录情况，需要清除用户信息
            setLoginState(isLogin())
            clearUserData()
            clearUserStatisticData()
            return
        }
        viewModelScope.launch(main) {
            val result = withOnIO {
                repo.getMineStatisticInfo()
            }
            statisticUIModel.checkResultAndEmitUIState(result)
        }

    }

    /**
     * 更新账号信息
     */
    fun updateUserAccountDetail() {
        val value = _mineUserState.value
        value?.apply {
            userName = UserManager.instance.nickname
            userHeadPic = UserManager.instance.userAvatar.orEmpty()
            sex = UserManager.instance.userSex.toLong()
            signIn = UserManager.instance.sign
            level = UserManager.instance.userLevel.toLong()
            authenType = UserManager.instance.userAuthType
        }?.also {
            _mineUserState.value = it
        }
    }

    /**
     * 更新我的页面信息
     */
    fun updateMineData(accountStatisticsInfo: AccountStatisticsInfo) {
//请求失败了
        if (accountStatisticsInfo.success.not()) {
            return
        }

//        影迷会员俱乐部
        if (accountStatisticsInfo.myClub == null) {
            _mineClubState.value = MineClubViewBean(0L, "")
        } else {
            accountStatisticsInfo.myClub?.apply {
                _mineClubState.value = MineClubViewBean(this.todayMBean, this.checkInUrl.orEmpty())
            }
        }


//        社区数据，粉丝和关注数
        if (accountStatisticsInfo.myCommunity == null) {
            val value = _mineUserState.value
            value?.let {
                it.attentionCount = 0L
                it.fansCount = 0L
                _mineUserState.value = it
            }
        } else {
            accountStatisticsInfo.myCommunity?.apply {
                val value = _mineUserState.value
                value?.let {
                    it.attentionCount = this.followCount
                    it.fansCount = this.fansCount
                    _mineUserState.value = it
                }
            }
        }

//        我的记录 想看电影和看过电影
        if (accountStatisticsInfo.myRecords == null) {
            _mineWannaState.value = MineWannaViewBean()
            _mineHasSeenState.value = MineHasSeenViewBean()
        } else {
            accountStatisticsInfo.myRecords?.apply {
                _mineWannaState.value = MineWannaViewBean.covert(this.wantSeeCount, this.wantSeeMovies)
                _mineHasSeenState.value = MineHasSeenViewBean.covert(this.watchedCount, ratingWaitCount, watchedMovies)
            }
        }


//        订单
        if (accountStatisticsInfo.myOrders ==null){
            _mineOrderState.value = MineOrderViewBean()
        }else{
            accountStatisticsInfo.myOrders?.apply {
                _mineOrderState.value = MineOrderViewBean.covert(items)
            }
        }


//        钱包
        if (accountStatisticsInfo.myWallet == null){
            _mineWalletState.value = MineWalletViewBean(getString(R.string.mine_wallet_community), getString(R.string.mine_wallet_gift))
        }else{
            accountStatisticsInfo.myWallet?.apply {
//                _mineWalletState.value = MineWalletViewBean(if (TextUtils.isEmpty(this.couponWillExpire).not()) this.couponWillExpire.orEmpty() else if (TextUtils.isEmpty(this.cardWillExpire).not()) this.cardWillExpire.orEmpty() else "玩转社区得礼包")
                _mineWalletState.value = MineWalletViewBean(this.msgUp.orEmpty(),this.msgDown.orEmpty())
            }
        }

    }


    /**
     * 加载用户详情信息
     */
    fun getAccountDetail() {
        if (isLogin().not()) {
            return
        }
        viewModelScope.launch(main) {
            val result = withOnIO {
                repo.getMineUserDetail()
            }
            accountDetailUIModel.checkResultAndEmitUIState(result)
        }
    }

    fun gotoMovieShowTime(movieId:Long):Unit{
        val ticketProvider: ITicketProvider? = getProvider(ITicketProvider::class.java)
        ticketProvider?.startMovieShowtimeActivity(movieId)
    }

}