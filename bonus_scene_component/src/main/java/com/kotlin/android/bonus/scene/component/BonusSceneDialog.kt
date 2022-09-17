package com.kotlin.android.bonus.scene.component

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.bonus.scene.component.bean.BonusSceneDialogDismissBean
import com.kotlin.android.bonus.scene.component.viewmodel.BonusSceneViewModel
import com.kotlin.android.app.data.entity.bonus.BonusScene
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.liveevent.CLOSE_BONUS_SCENE
import kotlinx.android.synthetic.main.dialog_bonus_scene.*

/**
 * create by lushan on 2020/12/28
 * description:彩蛋弹框Dialog
 */
class BonusSceneDialog : DialogFragment {
    constructor() : super()

    private var viewModel: BonusSceneViewModel? = null
    var data: Long = 0L
        set(value) {
            field = value
            initView()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ImmersiveDialog)
        isCancelable = true
        viewModel = viewModels<BonusSceneViewModel>().value
        viewModel?.bonusState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (this.success) {//中奖
                        lottery(this)
                    } else {//未中奖
                        losingLottery()
                    }
                }
                netError?.showToast()
                error?.showToast()
            }
        })
    }

    private fun lottery(bonusScene: BonusScene) {
        ShapeExt.setShapeColorAndCorner(bigCL, R.color.color_ffffff, 15)
        var prizeType = bonusScene.prizeType
        boxIv?.gone()
        lotteryIv?.visible()
        lotteryTitleTv?.apply {
            visible()
            text = getString(R.string.bonus_scene_lottery_title)
        }
        bigCL?.visible()
        tipsTv?.visible()
        closeBtnFl?.gone()
        bagContentTv?.apply {
            visible()
            text = "${bonusScene.prizeName.orEmpty()}: ${bonusScene.quantity}${if (prizeType == PRIZE_TYPE_GOODS_COUPONS || prizeType == PRIZE_TYPE_TICKET_COUPONS) "张" else ""}"
            setTextColor(getColor(R.color.color_1d2736))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        }
    }


    /**
     * 未中奖
     */
    private fun losingLottery() {
        boxIv?.gone()
        lotteryIv?.visible()
        lotteryTitleTv?.apply {
            visible()
            text = getString(R.string.bonus_scene_losing_lottery_title)
        }
        bigCL?.visible()
        ShapeExt.setShapeColorAndCorner(bigCL, R.color.color_ffffff, 15)
        tipsTv?.gone()
        closeBtnFl?.visible()
        bagContentTv?.apply {
            visible()
            text = getString(R.string.bonus_scene_losing_lottery)
            setTextColor(getColor(R.color.color_4e5e73))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        }
    }

    private fun initView() {
        closeBtn?.onClick { //右上角关闭按钮
            dismiss()
        }
        boxIv?.onClick {//打开彩蛋
//            请求开彩蛋接口
            viewModel?.getBonusScene(data)
        }

        closeBtnFl?.onClick {
            dismiss()
        }


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.apply {
            window?.apply {
                decorView?.setBackgroundColor(Color.TRANSPARENT)
                setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
                setWindowAnimations(R.style.BottomDialogAnimation)
            }
            isCancelable = true
            setCanceledOnTouchOutside(true)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_bonus_scene, container, false)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
//            弹框消失，需要发送消息
        LiveEventBus.get(CLOSE_BONUS_SCENE).post(BonusSceneDialogDismissBean(true))
    }

}

