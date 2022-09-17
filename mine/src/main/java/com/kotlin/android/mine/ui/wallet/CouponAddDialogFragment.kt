package com.kotlin.android.mine.ui.wallet

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.kotlin.android.ktx.ext.encode
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.mine.R
import com.kotlin.android.mine.WALLET_CARD_ADD
import com.kotlin.android.mine.WALLET_COUPON_ADD
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.getColor
import kotlinx.android.synthetic.main.frag_coupon_add.*


/**
 * @author: WangWei
 * @date: 2020/10/27
 * 添加券、礼品卡dialog
 */
class CouponAddDialogFragment : DialogFragment() {

    var type = WALLET_COUPON_ADD//1coupon 2card

    var walletViewModel: WalletViewModel? = null

    companion object {
        fun newInstance() = CouponAddDialogFragment()
    }


    /**
     * 添加成功回调
     */
    interface AddOkOnClickListener {
        fun onAddOK()
    }

    private var addOkOnClickListener: AddOkOnClickListener? = null

    fun setAddOkOnClickListener(listener: AddOkOnClickListener) {
        this.addOkOnClickListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NORMAL, R.style.ViewsBottomDialog)

    }

    override fun onStart() {
        super.onStart()
        val win: Window = dialog!!.window ?: return
        win.setBackgroundDrawable(ColorDrawable(getColor(R.color.color_9e000000)))
        ShapeExt.setShapeCorner2Color(dialogView, R.color.color_ffffff, 4)
        ShapeExt.setShapeCorner2Color2Stroke(tv_cancel, strokeColor = R.color.color_20a0da, corner = 20)
        ShapeExt.setShapeCorner2Color(tv_ok, R.color.color_20a0da, 20)

        val params: WindowManager.LayoutParams = win.attributes
        params.gravity = Gravity.CENTER
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        win.attributes = params
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_coupon_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (type == WALLET_COUPON_ADD) {//隐藏卡密码
            tv_pwd_tip?.gone()
            et_pwd?.gone()
            ShapeExt.setShapeCorner2Color2Stroke(et_code, strokeColor = R.color.color_979797, corner = 4)
            tv_code_tip.text = getString(R.string.mine_add_coupon)
        } else {
            ShapeExt.setShapeCorner2Color2Stroke(et_code, strokeColor = R.color.color_979797, corner = 4)
            ShapeExt.setShapeCorner2Color2Stroke(et_pwd, strokeColor = R.color.color_979797, corner = 4)
        }

//        root?.setOnClickListener { dismiss() }
        tv_cancel?.setOnClickListener { dismiss() }

        tv_ok?.setOnClickListener {//发送更新信息
            var codeContent = et_code?.text?.trim()
            var pwdContent = et_pwd?.text?.trim()

            if (codeContent?.isEmpty() == true) {
                showToast("号码不可为空哦")
                return@setOnClickListener
            }
            if (type == WALLET_CARD_ADD && pwdContent?.isEmpty() == true) {
                showToast("密码不可为空哦")
                return@setOnClickListener
            }

            if (type == WALLET_COUPON_ADD) walletViewModel?.addCoupon(codeContent.toString())
            else if (type == WALLET_CARD_ADD) walletViewModel?.addGiftCard(codeContent.toString(), encode(pwdContent.toString()))
        }
    }

}