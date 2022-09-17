package com.kotlin.android.mine.ui.setting

import android.content.Intent
import android.text.TextUtils
import android.view.WindowManager
import androidx.core.widget.addTextChangedListener
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mine.R
import com.kotlin.android.mine.databinding.ViewEditInputBinding
import com.kotlin.android.mine.ui.setting.viewmodel.UpdateMemberInfoViewModel
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.user.UserManager
import com.kotlin.android.widget.titlebar.TitleBarManager

@Route(path = RouterActivityPath.Mine.PAGE_EDIT_SIGN_ACTIVITY)
class EditSignInputActivity : BaseVMActivity<UpdateMemberInfoViewModel, ViewEditInputBinding>() {

    companion object {
        const val KEY_MINE_EDIT_SIGN = "mine_edit_sign"
        const val EDIT_SIGN_RESULT_CODE = 10004
    }

    private var inputStr: String? = ""

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this)
            .setTitle(titleRes = R.string.mine_edit_sign)
            .addItem(drawableRes = R.drawable.ic_title_bar_36_back) {
                onBackPressed()
            }
            .addItem(
                isReversed = true,
                titleRes = R.string.mine_edit_sava,
                colorRes = R.color.color_ffffff,
                titleHeight = 25.dp,
                titlePaddingStart = 10.dp,
                titlePaddingEnd = 10.dp,
                titleMarginEnd = 18.dp,
                bgDrawable = getShapeDrawable(
                    colorRes = R.color.color_1da7dd,
                    cornerRadius = 13.dpF,
                )
            ) {
                if (!TextUtils.isEmpty(inputStr)) {
                    if (inputStr == UserManager.instance.sign) {
                        getProvider(IMainProvider::class.java)?.showToast(getString(R.string.mine_please_modify_sign_before_saving))
                    }
                    mViewModel?.updateMemberInfo(userSign = inputStr, type = "3")
                } else {
                    getProvider(IMainProvider::class.java)?.showToast(getString(R.string.mine_sign_is_not_null))
                }
            }
    }

    override fun initView() {
        mBinding?.apply {
            inputEt.requestFocus()
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            inputRl.setBackground(colorRes = R.color.color_ffffff, cornerRadius = 12.dpF)
            inputEt.apply {
                addTextChangedListener {
                    it?.apply {
                        inputStr = this.toString()
                        countTv.text = "${this.toString().length}/150"
                    }
                }
                setHint(R.string.mine_input_sign)
                setText(UserManager.instance.sign)
                setSelection(UserManager.instance.sign.length)
            }
        }
    }

    override fun initData() {

    }

    override fun startObserve() {
        mViewModel?.signDataState?.observe(this) {
            it?.apply {
                success?.run {
                    if (bizCode == 0) {
                        // 更新成功（后台审核通过后才能显示，不用设置到UserManager中）
                        getProvider(IMainProvider::class.java)?.showToast(bizMsg)
                        Intent().apply {
                            putExtra(KEY_MINE_EDIT_SIGN, inputStr)
                        }.apply {
                            setResult(EDIT_SIGN_RESULT_CODE, this)
                        }
                        finish()
                    }
                }
                netError?.run {
                    getProvider(IMainProvider::class.java)?.showToast("${getString(R.string.mine_change_sign_fail)}:$this")
                }
                error?.run {
                    getProvider(IMainProvider::class.java)?.showToast("${getString(R.string.mine_change_sign_fail)}:$this")
                }
            }
        }
    }
}