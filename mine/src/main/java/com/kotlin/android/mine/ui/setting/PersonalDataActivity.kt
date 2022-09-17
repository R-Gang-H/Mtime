package com.kotlin.android.mine.ui.setting

import android.annotation.SuppressLint
import android.content.Intent
import android.text.TextUtils
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.app.router.provider.mine.IMineProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.image.component.CROP_TYPE_1_1
import com.kotlin.android.image.component.getChooseAvatarFragment
import com.kotlin.android.image.component.showChooseAvatarDialog
import com.kotlin.android.image.component.showPhotoCropDialog
import com.kotlin.android.ktx.ext.date.toDate
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mine.R
import com.kotlin.android.mine.databinding.ActivityPersonalDataBinding
import com.kotlin.android.mine.ui.setting.viewmodel.UpdateMemberInfoViewModel
import com.kotlin.android.mine.ui.widgets.dialog.SexSelectDialog
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.user.UserManager.Companion.instance
import com.kotlin.android.widget.titlebar.TitleBarManager
import java.util.*

@Route(path = RouterActivityPath.Mine.PAGE_PERSONAL_DATA_ACTIVITY)
class PersonalDataActivity :
    BaseVMActivity<UpdateMemberInfoViewModel, ActivityPersonalDataBinding>() {

    companion object {
        const val NICKNAME_REQUEST_CODE = 10001
        const val EDIT_SIGN_REQUEST_CODE = 10002
        const val TYPE_BIRTHDAY = "1"   // 1、生日,2、居住地,3、用户签名
    }

    private val mineProvider =
        getProvider(IMineProvider::class.java)
    private var birthYear = 1990
    private var birthMonthOfYear = 0
    private var birthDayOfMonth = 1
    private var sexType: Int = 3 // 1、男,2、女,3、保密
    private var sexSelectDialog: SexSelectDialog? = null

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this)
            .setTitle(titleRes = R.string.mine_personal_data)
            .addItem(drawableRes = R.drawable.ic_title_bar_36_back) {
                onBackPressed()
            }
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initView() {
        sexSelectDialog = SexSelectDialog(
            maleCallBack = {
                sexType = 1
                mViewModel?.updateSexInfo("$sexType")
                sexSelectDialog?.dismiss()
            },
            femaleCallBack = {
                sexType = 2
                mViewModel?.updateSexInfo("$sexType")
                sexSelectDialog?.dismiss()
            },
            secretCallBack = {
                sexType = 3
                mViewModel?.updateSexInfo("$sexType")
                sexSelectDialog?.dismiss()
            }
        )
        mBinding?.apply {
            mineHeadPortraitRl.setOnClickListener {
                showChooseAvatarDialog { photo: PhotoInfo ->
                    showPhotoCropDialog(
                        photo,
                        CommConstant.IMAGE_UPLOAD_USER_UPLOAD, CROP_TYPE_1_1,
                        { avatarUrl: String ->
                            showAvatarView(avatarUrl)
                        }
                    )
                }
            }
            nameRl.setOnClickListener {
                mineProvider?.startNickname(this@PersonalDataActivity, NICKNAME_REQUEST_CODE)
            }
            signRl.setOnClickListener {
                mineProvider?.startEditSign(this@PersonalDataActivity, EDIT_SIGN_REQUEST_CODE)
            }
            sexRl.setOnClickListener {
                sexSelectDialog?.show(
                    supportFragmentManager,
                    SexSelectDialog.TAGx_SEX_SELECT_FRAGMENT
                )
            }
            birthDateRl.setOnClickListener {
                getProvider(IMainProvider::class.java)?.showDatePicker(
                    this@PersonalDataActivity,
                    birthYear,
                    birthMonthOfYear,
                    birthDayOfMonth,
                    okListener = { year, monthOfYear, dayOfMonth ->
                        updateBirthday(year, monthOfYear, dayOfMonth)
                    }
                )
            }
            liveAddressRl.setOnClickListener {
                getProvider(IMainProvider::class.java)?.startCityChangeActivity(
                    if (null == instance.location || TextUtils.isEmpty(instance.location?.levelRelation)) "" else instance.location?.levelRelation
                )
            }
        }
    }

    override fun initData() {

    }

    override fun startObserve() {
        mViewModel?.birthState?.observe(this) {
            it?.apply {
                success?.run {
                    if (bizCode == 0) {
                        mBinding?.dateTv?.text = birthday
                        instance.birthday = birthday
                        val d: Date? = birthday.toDate("yyyy-MM-dd")
                        val c = Calendar.getInstance()
                        c.time = d
                        birthYear = c[Calendar.YEAR]
                        birthMonthOfYear = c[Calendar.MONTH]
                        birthDayOfMonth = c[Calendar.DAY_OF_MONTH]
                        getProvider(IMainProvider::class.java)?.showToast(getString(R.string.mine_change_birthday_success))
                    } else {
                        getProvider(IMainProvider::class.java)?.showToast(bizMsg)
                    }
                }
                netError?.run {
                    getProvider(IMainProvider::class.java)?.showToast("${getString(R.string.mine_change_birthday_fail)}:$this")
                }
                error?.run {
                    getProvider(IMainProvider::class.java)?.showToast("${getString(R.string.mine_change_birthday_fail)}:$this")
                }
            }
        }
        startSexObserve()
    }

    private fun startSexObserve() {
        mViewModel?.sexDataState?.observe(this) {
            it?.apply {
                success?.run {
                    if (success) {
                        instance.userSex = sexType
                        sexUpdate()
                        getProvider(IMainProvider::class.java)?.showToast(getString(R.string.mine_change_sex_success))
                    } else {
                        getProvider(IMainProvider::class.java)?.showToast(error)
                    }
                }
                netError?.run {
                    getProvider(IMainProvider::class.java)?.showToast("${getString(R.string.mine_change_sex_fail)}:$this")
                }
                error?.run {
                    getProvider(IMainProvider::class.java)?.showToast("${getString(R.string.mine_change_sex_fail)}:$this")
                }
            }
        }
    }

    private fun updateBirthday(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        if (!TextUtils.isEmpty(instance.birthday) && year == birthYear && monthOfYear == birthMonthOfYear && dayOfMonth == birthDayOfMonth) return
        val builder = StringBuilder()
        builder.append(year).append("-")
        if (monthOfYear + 1 < 10) builder.append("0")
        builder.append(monthOfYear + 1).append("-")
        if (dayOfMonth < 10) builder.append("0")
        builder.append(dayOfMonth)
        var newBirth = builder.toString()
        mViewModel?.updateMemberInfo(birth = newBirth, type = TYPE_BIRTHDAY)
    }

    @SuppressLint("CheckResult")
    private fun showAvatarView(avatarUrl: String?) {
        if (TextUtils.isEmpty(avatarUrl) || null == instance.user) {
            return
        }
        instance.userAvatar = avatarUrl
        mBinding?.let { Glide.with(this).load(avatarUrl).circleCrop().into(it.mineHeadPortraitIma) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == NickNameInputActivity.NICKNAME_RESULT_CODE) {
            mBinding?.nickNameTv?.text =
                data?.getStringExtra(NickNameInputActivity.KEY_MINE_NICKNAME)
        }
        if (resultCode == EditSignInputActivity.EDIT_SIGN_RESULT_CODE) {
            mBinding?.signStrTv?.text =
                data?.getStringExtra(EditSignInputActivity.KEY_MINE_EDIT_SIGN)
        }
        var dialog = this.getChooseAvatarFragment()
        dialog?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        var location = instance.location
        var birthday = instance.birthday
        mBinding?.apply {
            nickNameTv.text = instance.nickname
            signStrTv.text = instance.sign
            sexUpdate()
            if (TextUtils.isEmpty(birthday)) {
                dateTv.text = getString(R.string.mine_please_setting)
            } else {
                dateTv.text = birthday
                val d: Date? = birthday?.toDate("yyyy-MM-dd")
                val c = Calendar.getInstance()
                c.time = d
                birthYear = c[Calendar.YEAR]
                birthMonthOfYear = c[Calendar.MONTH]
                birthDayOfMonth = c[Calendar.DAY_OF_MONTH]
            }
            addressTv.text =
                if (location != null && location.locationId > 0 && !TextUtils.isEmpty(location.locationName)) location.locationName else getString(
                    R.string.mine_please_setting
                )
        }
        showAvatarView(instance.userAvatar)
    }

    private fun sexUpdate() {
        mBinding?.apply {
            sexTv.text =
                when (instance.userSex) {
                    1 -> getString(R.string.mine_sex_male)
                    2 -> getString(R.string.mine_sex_female)
                    else -> getString(R.string.mine_sex_secrecy)
                }
        }
    }
}