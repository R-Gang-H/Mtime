package com.kotlin.android.community.card.component.item

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.kotlin.android.app.api.viewmodel.CommViewModel
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.community.card.component.R
import com.kotlin.android.community.card.component.item.adapter.CommunityCardBaseBinder
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.core.ext.observeLiveData
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.afterLogin
import com.kotlin.android.router.ext.isLogin
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

abstract class BaseCardFragment<VM : CommViewModel<MultiTypeBinder<*>>, VB : ViewBinding>
    : BaseVMFragment<VM, VB>() {
    
    private var mLoginState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLoginState = isLogin()
    }

    override fun onResume() {
        super.onResume()
        if (mLoginState != isLogin()) {
            mLoginState = isLogin()
            onLoginStateChanged()
        }
    }
    
    abstract fun onLoginStateChanged()

    override fun startObserve() {
        registerPraiseUpObserve()
    }

    //点赞、取消点赞
    private fun registerPraiseUpObserve() {
        observeLiveData(mViewModel?.mCommPraiseUpUIState) {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                showToast(error)
                showToast(netError)

                success?.apply {
                    if (result.isSuccess()) {
                        when (extend) {
                            is CommunityCardBaseBinder -> {
                                (extend as CommunityCardBaseBinder).praiseUpChanged()
                            }
                        }
                    } else {
                        showToast(result.bizMsg.orEmpty())
                    }
                }
            }
        }
    }
    
    /**
     * 从MultiTypeBinder回调的一些点击事件
     * 此事件已通过Adapter注册
     */
    open fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        when (binder) {
            is CommunityCardBaseBinder -> {
                //社区帖子相关点击事件
                onCommunityCardBinderClickListener(view, binder)
            }
        }
    }

    //社区帖子相关点击事件
    private fun onCommunityCardBinderClickListener(
        view: View,
        binder: CommunityCardBaseBinder<*>
    ) {
        when (view.id) {
            R.id.mCommunityCardLikeTv -> {
                //帖子内容的点赞、取消点赞
                onPraiseUpBtnClick(
                    isLike = binder.item.isLike,
                    objType = binder.item.praiseObjType,
                    objId = binder.item.id,
                    binder = binder
                )
            }
        }
    }

    /**
     * 点赞、取消点赞
     */
    private fun onPraiseUpBtnClick(
        isLike: Boolean,
        objType: Long,
        objId: Long,
        binder: MultiTypeBinder<*>
    ) {
        afterLogin {
            mViewModel?.praiseUp(
                action = if (isLike) CommConstant.PRAISE_ACTION_CANCEL else CommConstant.PRAISE_ACTION_SUPPORT,
                objType = objType,
                objId = objId,
                extend = binder
            )
        }
    }
}