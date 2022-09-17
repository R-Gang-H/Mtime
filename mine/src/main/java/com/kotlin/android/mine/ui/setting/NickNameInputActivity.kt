package com.kotlin.android.mine.ui.setting

import android.content.Intent
import android.text.TextUtils
import android.view.WindowManager
import androidx.activity.viewModels
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
import com.kotlin.android.mine.ui.setting.viewmodel.NickNameViewModel
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.user.UserManager.Companion.instance
import com.kotlin.android.widget.titlebar.TitleBarManager

@Route(path = RouterActivityPath.Mine.PAGE_MODIFY_NICKNAME_ACTIVITY)
class NickNameInputActivity : BaseVMActivity<NickNameViewModel, ViewEditInputBinding>() {

    companion object {
        const val KEY_MINE_NICKNAME = "mine_nickname"
        const val NICKNAME_RESULT_CODE = 10003
        const val MIN_LENGTH = 4
        const val MAX_LENGTH = 20
    }

    private var inputStr: String = ""

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this)
            .setTitle(titleRes = R.string.mine_nickname)
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
                    val rs: Boolean =
                        inputStr.matches("^[\\u4e00-\\u9fa5[A-Za-z0-9]\\w\\--_]*$".toRegex())
                    // 4-20个字符，1个英文或1个汉字都算1个字符
                    val length: Int = inputStr.length
                    if (length < MIN_LENGTH || length > MAX_LENGTH || inputStr.matches("[0-9]*".toRegex()) || !rs) {
                        getProvider(IMainProvider::class.java)?.showToast(getString(R.string.mine_edit_nick_name_tip))
                        return@addItem
                    }
                    mViewModel?.saveNickName(inputStr)
                }
            }
    }

    override fun initVM(): NickNameViewModel = viewModels<NickNameViewModel>().value

    override fun initView() {
        mBinding?.apply {
            inputEt.requestFocus()
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            inputRl.setBackground(colorRes = R.color.color_ffffff, cornerRadius = 12.dpF)
            inputEt.apply {
                addTextChangedListener {
                    it?.apply {
                        if (!TextUtils.isEmpty(it.toString())) {
                            inputStr = it.toString().trim()
                            // 4-20个字符，1个英文或1个汉字都算1个字符
                            val length = inputStr.length
                            val clickable = length in MIN_LENGTH..MAX_LENGTH && !inputStr.equals(
                                instance.nickname,
                                ignoreCase = true
                            )
                            countTv.text = "${inputStr.length}/20"
                        }
                    }
                }
                setHint(R.string.mine_input_nickname)
                maxLines = 20
                setText(instance.nickname)
                setSelection(instance.nickname.length)
            }
        }
    }

    override fun initData() {

    }

    override fun startObserve() {
        mViewModel?.nickNameState?.observe(this) {
            it?.apply {
                success?.run {
                    if (this.success) {
                        instance.nickname = inputStr
                        Intent().apply {
                            putExtra(KEY_MINE_NICKNAME, inputStr)
                        }.apply {
                            setResult(NICKNAME_RESULT_CODE, this)
                        }
                        getProvider(IMainProvider::class.java)?.showToast(getString(R.string.mine_change_nickname_success))
                        finish()
                    } else {
                        getProvider(IMainProvider::class.java)?.showCustomAlertDlg(
                            this@NickNameInputActivity,
                            this.error
                        )
                    }
                }
                netError?.run {
                    getProvider(IMainProvider::class.java)?.showToast("${getString(R.string.mine_change_nickname_fail)}$this")
                }
                error?.run {
                    getProvider(IMainProvider::class.java)?.showToast("${getString(R.string.mine_change_nickname_fail)}$this")
                }
            }
        }
    }
}