package com.kotlin.android.community.family.component.ui.manage

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ActFamilyEditInfoBinding
import com.kotlin.android.community.family.component.ui.manage.constant.FamilyConstant
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.ktx.ext.keyboard.hideSoftInput
import com.kotlin.android.ktx.ext.keyboard.isShowSoftInput
import com.kotlin.android.ktx.ext.keyboard.showSoftInput
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.widget.titlebar.CommonTitleBar
import kotlinx.android.synthetic.main.act_family_edit_info.*

/**
 * @author vivian.wei
 * @date 2020/8/6
 * @desc 编辑家族名称/简介页
 */
@Route(path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_EDIT_INFO)
class FamilyEditInfoActivity: BaseVMActivity<BaseViewModel, ActFamilyEditInfoBinding>() {

    companion object {
        const val NAME_MAX_SIZE = 12
        const val DES_MAX_SIZE = 500
    }

    private lateinit var titleView: CommonTitleBar
    private var mEditType = FamilyConstant.FAMILY_EDIT_INFO_TYPE_DES
    private var mContent: String ?= ""
    private var mValid = true

    override fun initVM(): BaseViewModel {
        intent?.let {
            mEditType = it.getIntExtra(FamilyConstant.KEY_FAMILY_EDIT_INFO_TYPE, 0)
            mContent = it.getStringExtra(FamilyConstant.KEY_FAMILY_EDIT_INFO_CONTENT)
        }

        return viewModels<BaseViewModel>().value
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        mValid = mEditType != FamilyConstant.FAMILY_EDIT_INFO_TYPE_NAME || !TextUtils.isEmpty(mContent)
        titleView = CommonTitleBar()
        titleView.init(this)
                .setTitle(if(mEditType == FamilyConstant.FAMILY_EDIT_INFO_TYPE_NAME) R.string.family_name else R.string.family_des)
                .setRightTextAndClick(R.string.community_save_btn, View.OnClickListener {
                    save()
                })
                .setRightTextColor(if(mValid) R.color.color_20a0da else R.color.color_aab7c7_20_alpha)
                .create()
    }

    override fun initView() {
        when(mEditType) {
            FamilyConstant.FAMILY_EDIT_INFO_TYPE_NAME -> {
                mActFamilyEditInfoEt.hint = getString(R.string.family_edit_name_hint, NAME_MAX_SIZE)
            }
            FamilyConstant.FAMILY_EDIT_INFO_TYPE_DES -> {
                mActFamilyEditInfoEt.hint = getString(R.string.family_edit_des_hint, DES_MAX_SIZE)
            }
            else -> {
            }
        }
        mActFamilyEditInfoEt.isFocusable = true
        mActFamilyEditInfoEt.isFocusableInTouchMode = true
        mActFamilyEditInfoEt.setText(mContent)
        mActFamilyEditInfoEt.setSelection(mActFamilyEditInfoEt.text.length) // 将光标移至最后
        // 软键盘
        mActFamilyEditInfoEt.showSoftInput()
        // 事件
        initEvent()
    }

    override fun initData() {

    }

    override fun startObserve() {

    }

    // 初始化事件
    private fun initEvent() {
        mActFamilyEditInfoEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                val content = s.toString().trim { it <= ' ' }
                mValid = true
                if(mEditType == FamilyConstant.FAMILY_EDIT_INFO_TYPE_NAME) {
                    // 名称不能为空，最多12个字
                    if(content.length == 0) {
                        mValid = false
                    } else if(content.length > NAME_MAX_SIZE) {
                        mValid = false
                    }
                } else if(mEditType == FamilyConstant.FAMILY_EDIT_INFO_TYPE_DES) {
                    // 简介可以为空，最多500字
                    if (content.length > DES_MAX_SIZE) {
                        mValid = false
                    }
                }
                titleView.setRightTextColor(if(mValid) R.color.color_20a0da else R.color.color_aab7c7_20_alpha)

            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        // 隐藏软键盘
        if (isShowSoftInput()) {
            hideSoftInput()
        }

    }

    /**
     * 保存
     */
    private fun save() {
        if(mValid) {
            var content = mActFamilyEditInfoEt.text.toString().trim({ it <= ' ' })
            val intent = Intent()
             intent.run {
                putExtra(FamilyConstant.KEY_FAMILY_EDIT_INFO_TYPE, mEditType)
                putExtra(FamilyConstant.KEY_FAMILY_EDIT_INFO_CONTENT, content)
                setResult(Activity.RESULT_OK, this)
            }
            finish()
        }
    }
}