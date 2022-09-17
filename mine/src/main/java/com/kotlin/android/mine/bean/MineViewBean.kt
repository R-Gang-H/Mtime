package com.kotlin.android.mine.bean

import android.text.TextUtils
import com.kotlin.android.app.data.entity.mine.AccountStatisticsInfo
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.mine.R
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.user.isLogin

/**
 * create by lushan on 2020/8/27
 * description: 我的页面使用viewBean
 */

//个人中心，用户相关
data class MineUserViewBean(
        var userName: String = "",//昵称
        var userHeadPic: String = "",//头像
        var sex: Long = 0L,//昵称 男、女、保密
        var fansCount: Long = 0L,//粉丝数量
        var attentionCount: Long = 0L,//关注人数
        var signIn: String = "",//个性签名
        var level: Long = 0L,//会员等级
        var authenType: Long = 0L////用户认证 申请认证、审核中 用户认证类型：null代表没有认证， 1"个人", 2"影评人", 3"电影人", 4"机构", -1“审核中”;
) : ProguardRule {


    /**
     * 此时需要显示等级图标
     */
    fun isShowLevelIcon(): Boolean {
        return level in (0..4) && isLogin()
    }

    //是不是机构认证用户
    fun isInstitutionAuthUser(): Boolean {
        return authenType == 4L
    }

    //是不是认证用户
    fun isAuthUser(): Boolean {
        return authenType > 1 && isLogin()
    }

    fun getFansFormat(isLogin: Boolean): String {
        return getString(R.string.mine_fans_format).format(if (isLogin) formatCount(fansCount) else 0.toString())
    }

    fun getAttentionFromat(isLogin: Boolean): String {
        return getString(R.string.mine_attention_format).format(if (isLogin) formatCount(attentionCount) else 0.toString())
    }

    fun isShowIndentificationBtn(): Boolean {
        return authenType !in arrayOf(2L, 3L, 4L)
    }

    /**
     * 获取认证审核文案
     */
    fun getIdentificationText(isLogin: Boolean): String {
        return if (isLogin) {
            if (authenType == -1L) {
                getString(R.string.mine_request_indentifity_ing)
            } else {
                getString(R.string.mine_request_indentifity)
            }
        } else {
            getString(R.string.mine_request_indentifity)
        }

    }
}


//个人中心，想看电影
data class MineWannaViewBean(
        var firstMoviePic: String = "",//第一部影片图片url
        var secondMoviePic: String = "",//第二部影片url
        var movieId: Long = 0L,//第一部影片id
        var releaseDate: String = "",//上映日期
        var sellType: Long = 0L,//是否显示购票 1预售 2购票
        var wannaMovieNum: Long = 0L//想看影片数量
) : ProguardRule {

    companion object {
        fun covert(wantSeeCount: Long, wantSeeMovies: MutableList<AccountStatisticsInfo.WantSeeMovie>?): MineWannaViewBean {
            val mineWannaViewBean = MineWannaViewBean()
            if (wantSeeMovies?.isNotEmpty() == true) {//有想看数据
                mineWannaViewBean.movieId = wantSeeMovies[0].movieId
                mineWannaViewBean.firstMoviePic = wantSeeMovies[0].img.orEmpty()
                mineWannaViewBean.releaseDate = wantSeeMovies[0].releaseDate.orEmpty()
                mineWannaViewBean.sellType = wantSeeMovies[0].btnShow ?: 0L

                if (wantSeeMovies.size > 1) {
                    mineWannaViewBean.secondMoviePic = wantSeeMovies[1].img.orEmpty()
                }

            }
            mineWannaViewBean.wannaMovieNum = wantSeeCount

            return mineWannaViewBean

        }
    }

    fun hasWannaMovie(): Boolean {
        return if (isLogin()) {
            wannaMovieNum != 0L
        } else {
            false
        }
    }


    /**
     * 获取以想看多少部影片文案
     */
    fun getHasWannaCount(): String {
        return if (isLogin()) {
            getString(R.string.mine_movie_num_format).format(wannaMovieNum)
        } else {
            getString(R.string.mine_scan_after_login)
        }
    }

    /**
     * 是否显示购票按钮
     */
    fun hasShowBuyBtn(): Boolean {
        return sellType == 1L || sellType == 2L
    }


}

//个人中心，看过电影
data class MineHasSeenViewBean(
        var firstMoviePic: String = "",//第一部影片图片url
        var secondMoviePic: String = "",//第二部影片url
        var hasSeeMovieNum: Long = 0L,//想看影片数量
        var evaluateMovieNum: Long = 0L//需要评论的影片数量
) : ProguardRule {
    companion object {
        fun covert(hasSeeCount: Long, ratingWaitCount: Long, watchedMovies: MutableList<AccountStatisticsInfo.WantSeeMovie>?): MineHasSeenViewBean {
            val mineHasSeenViewBean = MineHasSeenViewBean()
            if (watchedMovies?.isNotEmpty() == true) {
                mineHasSeenViewBean.firstMoviePic = watchedMovies[0].img.orEmpty()
                if (watchedMovies.size > 1) {
                    mineHasSeenViewBean.secondMoviePic = watchedMovies[1].img.orEmpty()
                }
            }

            mineHasSeenViewBean.hasSeeMovieNum = hasSeeCount
            mineHasSeenViewBean.evaluateMovieNum = ratingWaitCount
            return mineHasSeenViewBean
        }
    }

    //    是否有影片
    fun hasSeenMovie(): Boolean {
        return if (isLogin()) {
            hasSeeMovieNum != 0L
        } else {
            false
        }
    }

    fun hasFirstMovie(): Boolean {
        return TextUtils.isEmpty(firstMoviePic).not()
    }

    fun hasSecondMovie(): Boolean {
        return TextUtils.isEmpty(secondMoviePic).not()
    }

    /**
     * 获取已看过文案
     */
    fun getHasSeenCount(): String {
        return if (isLogin()) {
            getString(R.string.mine_movie_num_format).format(hasSeeMovieNum)
        } else {
            getString(R.string.mine_scan_after_login)
        }
    }
}

//个人中心，订单
data class MineOrderViewBean(
        var hasOrder: Boolean = false,//是否有订单
        var moviePic: String = "",//要显示的订单封面
        var dispTitle: String = "",//要显示的标题
        var dispTips: String = "",//要显示的内容
        var isNotPay: Boolean = false,//是否是未支付订单
        var openFlag: Boolean = false//是否是即将开映
) : ProguardRule {

    companion object {
        fun covert(orders: MutableList<AccountStatisticsInfo.Order>?): MineOrderViewBean {
            val mineOrderViewBean = MineOrderViewBean()
            mineOrderViewBean.hasOrder = orders?.isNotEmpty() == true

            if (orders?.isNotEmpty() == true) {//有订单
                mineOrderViewBean.openFlag = orders[0].openFlag
                mineOrderViewBean.moviePic = orders[0].movieImg.orEmpty()
                mineOrderViewBean.isNotPay = orders[0].payStatus == 1L//有未支付订单
                if (mineOrderViewBean.isNotPay) {//未支付订单
                    mineOrderViewBean.dispTitle = orders[0].movieName.orEmpty()
                    mineOrderViewBean.dispTips = getString(R.string.ticket_order_no_pay)
                } else {//已支付订单
                    if (orders[0].openFlag) {//即将开映
                        mineOrderViewBean.dispTitle = orders[0].openTime.orEmpty()
                        mineOrderViewBean.dispTips = getString(R.string.mine_order_open)
                    } else {
                        mineOrderViewBean.dispTitle = orders[0].movieName.orEmpty()
                        mineOrderViewBean.dispTips = orders[0].cinemaName.orEmpty()
                    }
                }
            } else {//没有订单
                mineOrderViewBean.dispTitle = getString(R.string.mine_order_empty)
                mineOrderViewBean.dispTips = getString(R.string.mine_order_buy_ticket_to_cinema)
            }
            return mineOrderViewBean
        }
    }

    fun getTitle(): String {
        return if (isLogin()) {
            if (TextUtils.isEmpty(dispTitle)) {
                getString(R.string.mine_order_empty)
            } else {
                dispTitle
            }
        } else {
            getString(R.string.mine_scan_after_login)
        }
    }

    fun getTips(): String {
        return if (isLogin()) {
            if (TextUtils.isEmpty(dispTips)) {
                getString(R.string.mine_order_buy_ticket_to_cinema)
            } else {
                dispTips
            }
        } else {
            ""
        }
    }

}

//个人中心，钱包
data class MineWalletViewBean(
        var titleContent:String = "",
        var tipsContent:String = ""

) : ProguardRule {
    fun getTitle(): String {
        return if (isLogin()) {
            if (TextUtils.isEmpty(titleContent).not()) {
                titleContent
            } else {
                getString(R.string.mine_wallet_community)
            }
        } else {
            getString(R.string.mine_scan_after_login)
        }
    }

    fun getTips():String{
        return if (isLogin()){
            if (TextUtils.isEmpty(tipsContent).not()){
                tipsContent
            }else{
                getString(R.string.mine_wallet_gift)
            }
        }else{
            ""
        }
    }
}

//影迷俱乐部
data class MineClubViewBean(var mAddNum: Long = 0L,//m豆增加数量
                            var url: String = ""//抽奖url
) : ProguardRule