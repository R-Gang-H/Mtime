package com.kotlin.android.card.monopoly.ui.menu

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.kotlin.android.card.monopoly.CARD_MONOPOLY_FAMILY_ID
import com.kotlin.android.card.monopoly.CARD_MONOPOLY_POST_ID
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.TAB_MESSAGE_BOARD
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.menu.FunctionMenuView
import com.kotlin.android.card.monopoly.widget.menu.FunctionMenuView.Item
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.app.router.provider.home.IHomeProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.share.entity.ShareEntity
import com.kotlin.android.share.ext.showShareDialog

/**
 *
 * Created on 2020/9/8.
 *
 * @author o.s
 */
class FunctionMenuFragment : BaseVMDialogFragment<CardMonopolyApiViewModel, ViewBinding>() {
    private val mProvider by lazy {
        getProvider(ICardMonopolyProvider::class.java)
    }
    private val mCommunityProvider by lazy {
        getProvider(ICommunityFamilyProvider::class.java)
    }
    private val mHomeProvider by lazy {
        getProvider(IHomeProvider::class.java)
    }

    private val mUgcProvider by lazy {
        getProvider(IUgcProvider::class.java)
    }

    var event: ((item: Item) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return context?.let { context ->
            FunctionMenuView(context).apply {
                action = {
                    event?.invoke(it)
                    when (it) {
                        Item.OFFICIAL_FAMILY -> {
                            mCommunityProvider?.startFamilyDetail(id = CARD_MONOPOLY_FAMILY_ID)
                            dismissAllowingStateLoss()
                        }
                        Item.GAME_INFO -> {
                            mProvider?.startDealRecordsActivity()
                            dismissAllowingStateLoss()
                        }
                        Item.GAME_RANKING_LIST -> {
                            mHomeProvider?.startToplistGameActivity()
                            dismissAllowingStateLoss()
                        }
                        Item.GAME_GUIDE -> {
                            mUgcProvider?.launchDetail(CARD_MONOPOLY_POST_ID, CommConstant.PRAISE_OBJ_TYPE_POST)
//                            mProvider?.startGameGuideActivity()
                            dismissAllowingStateLoss()
                        }
                        Item.MSG_BOARD -> {
                            mProvider?.startWishingActivity(tab = TAB_MESSAGE_BOARD)
                            dismissAllowingStateLoss()
                        }
                        Item.GAME_SHARE -> {
                            mViewModel?.getShareInfo()
                        }
                    }
                }
            }
        }
    }

    override fun initEnv() {
        theme = {
            setStyle(STYLE_NORMAL, R.style.ImmersiveDialog)
        }
        window = {
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.TOP)
            setWindowAnimations(R.style.TopDialogAnimation)
        }
        immersive = {
            immersive()
                    .transparentStatusBar()
                    .statusBarDarkFont(false)
        }
    }

    override fun initVM(): CardMonopolyApiViewModel = viewModels<CardMonopolyApiViewModel>().value

    override fun initView() {
    }

    override fun initData() {
    }

    override fun startObserve() {
        mViewModel?.shareUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    val entity = ShareEntity.build(this)
                    showShareDialog(entity)
                    dismissAllowingStateLoss()
                }
            }
        }
    }
}