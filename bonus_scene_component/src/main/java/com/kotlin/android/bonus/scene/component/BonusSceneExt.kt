package com.kotlin.android.bonus.scene.component

import androidx.fragment.app.FragmentActivity
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.bonus.scene.component.bean.PopupBonusSceneBean
import com.kotlin.android.app.data.entity.bonus.*
import com.kotlin.android.router.liveevent.POPUP_BONUS_SCENE

/**
 * create by lushan on 2020/12/29
 * description:
 */

fun FragmentActivity.showBonusSceneDialog(action: Long): BonusSceneDialog {
    return getOrGenerateBonusSceneDialog().apply {
        data = action
    }
}

fun FragmentActivity.getOrGenerateBonusSceneDialog(): BonusSceneDialog {
    var fragment = getBonusSceneDialog()
    if (fragment == null) {
        fragment = BonusSceneDialog()
        fragment?.showNow(supportFragmentManager, TAG_FRAGMENT_BonusScene_DIALOG)
    }
    return fragment
}

fun FragmentActivity.getBonusSceneDialog(): BonusSceneDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_BonusScene_DIALOG) as? BonusSceneDialog
}

const val TAG_FRAGMENT_BonusScene_DIALOG = "tag_fragment_bonus_scene_dialog"

fun FragmentActivity.dismissBonusSceneDialog() {
    getBonusSceneDialog()?.dismiss()
}


private fun postEvent(action: Long){
    LiveEventBus.get(POPUP_BONUS_SCENE).post(PopupBonusSceneBean(action))
}

/**
 * 每天第一次分享
 */
fun postShareEvent(){
    postEvent(ACTION_SHARE_SUCCESS)
}

/**
 * 发布长短影评
 */
fun postPublishReview(){
    postEvent(ACTION_PUBLISH_REVIEW)
}

/**
 * 发布帖子
 */
fun postPublishPost(){
    postEvent(ACTION_PUBLISH_POST)
}

/**
 * 加入家族
 */
fun postJoinFamily(){
    postEvent(ACTION_JOIN_FAMILY)
}

/**
 * 第一次创建家族
 */
fun postCreateFamily(){
    postEvent(ACTION_CREATE_FAMILY)
}


/**
 * 第一次合成套装
 */
fun postComposedArmor(){
    postEvent(ACTION_COMPOSED_ARMOR)
}

/**
 * 第一次支付成功
 */
fun postPaySuccess(){
    postEvent(ACTION_PAY_SUCCESS)
}



const val PRIZE_TYPE_GOODS_COUPONS  =1L//购物券
const val PRIZE_TYPE_TICKET_COUPONS = 2L//购票券
const val PRIZE_TYPE_M_BEAN = 3L//m豆
const val PRIZE_TYPE_EXPERIENCE = 4L//经验值
const val PRIZE_TYPE_UN_LOTTERY = 5L//未中奖
const val PRIZE_TYPE_COINS = 6L//大富翁金币
const val PRIZE_TYPE_ENTITY = 7L//实物奖励