package com.kotlin.android.mine.ui.authentication.home

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mine.BR
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.AuthenticatonCardViewBean
import com.kotlin.android.mine.databinding.ActivityAuthenticationBinding
import com.kotlin.android.mine.ui.authentication.movier.MovierAuthActivity
import com.kotlin.android.mine.ui.authentication.organization.OrganizationAuthActivity
import com.kotlin.android.mine.ui.authentication.reviewer.ReviewerAuthenticationActivity
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.widget.titlebar.CommonTitleBar
import kotlinx.android.synthetic.main.activity_authentication.*
import org.jetbrains.anko.find

/**
 * 身份认证首页
 */
@Route(path = RouterActivityPath.Mine.PAGE_AUTHEN_ACTIVITY)
class AuthenticationActivity : BaseVMActivity<AuthenticationViewModel, ActivityAuthenticationBinding>() {

    override fun initTheme() {
        super.initTheme()
        immersive().statusBarDarkFont(true)
    }

    override fun initVM(): AuthenticationViewModel = viewModels<AuthenticationViewModel>().value

    override fun initView() {


        reviewLL?.find<View>(R.id.authBtn)?.apply {//电影人认证-去认证
            ShapeExt.setShapeCorner2Color(this, R.color.color_ffffff, 25)
            onClick {
                handleAuthBtn(AuthenticatonCardViewBean.TYPE_MOVIE_PERSON)
            }
        }
        movierLL?.find<View>(R.id.authBtn)?.apply {//影评人认证-去认证
            ShapeExt.setShapeCorner2Color(this, R.color.color_ffffff, 25)
            onClick {
//                需要判断是否符合认证影评人条件
                mViewModel?.checkPermission()
//                handleAuthBtn(AuthenticatonCardViewBean.TYPE_REVIEW_PERSON)
            }

        }
        orgnizationLL?.find<View>(R.id.authBtn)?.apply {//机构认证-去认证
            ShapeExt.setShapeCorner2Color(this, R.color.color_ffffff, 25)
            onClick {
                handleAuthBtn(AuthenticatonCardViewBean.TYPE_ORGANIZATION)
            }
        }

    }


    override fun initCommonTitleView() {
        super.initCommonTitleView()
        CommonTitleBar().init(this, false).setTitle(R.string.mine_authen_title).create()
    }

    override fun initData() {
        mBinding?.setVariable(BR.viewModel, mViewModel)
        mViewModel?.initData()

    }

    override fun startObserve() {
// 影评人认证条件申请
        mViewModel?.authPermissionState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (success && permission) {
//                        请求成功且有有权限，就跳转到影评人认证页面
                        handleAuthBtn(AuthenticatonCardViewBean.TYPE_REVIEW_PERSON)
                    } else {
                        error?.showToast()
                    }
                }
                netError?.showToast()
                error?.showToast()
            }
        })
    }

    /**
     * 处理去认证按钮
     */
    private fun handleAuthBtn(type: Long) {
        showToast(R.string.mine_goto_authen_info_tips)
        when (type) {
            AuthenticatonCardViewBean.TYPE_REVIEW_PERSON -> {//影评人  需要校验该用户是否符合申请条件
                startActivity(Intent(this, ReviewerAuthenticationActivity::class.java))
            }
            AuthenticatonCardViewBean.TYPE_MOVIE_PERSON -> {//电影人认证
                startActivity(Intent(this, MovierAuthActivity::class.java))
            }
            AuthenticatonCardViewBean.TYPE_ORGANIZATION -> {//机构认证
                startActivity(Intent(this, OrganizationAuthActivity::class.java))
            }
        }
    }


}