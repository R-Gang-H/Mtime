package com.kotlin.android.community.ui.home

import android.app.Activity
import android.view.Gravity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.observe
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_POST
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.message_center.IMessageCenterProvider
import com.kotlin.android.app.router.provider.message_center.UnReadMessageObserver
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.FragCommunityBinding
import com.kotlin.android.community.family.component.ui.home.FamilyFragment
import com.kotlin.android.community.ui.follow.FollowFragment
import com.kotlin.android.community.ui.selection.SelectionFragment
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mtime.ktx.ext.topStatusMargin
import com.kotlin.android.mtime.ktx.formatMsgCount
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.user.UserManager
import com.kotlin.android.user.afterLogin
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim
import com.kotlin.android.widget.titlebar.message
import kotlinx.android.synthetic.main.frag_community.*
import kotlinx.android.synthetic.main.view_community_header.*

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/17
 */
class CommunityFragment : BaseVMFragment<CommunityViewModel, FragCommunityBinding>(),
    LifecycleObserver {
    init {
        lifecycle.addObserver(this)
    }

    companion object {
        fun newInstance() = CommunityFragment()
    }

    private val mProvider = getProvider(ICardMonopolyProvider::class.java)

    override fun initVM(): CommunityViewModel {
        return viewModels<CommunityViewModel>().value
    }

    private var unReadMessageObserver: UnReadMessageObserver? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun unRegisterUnreadMessageCount() {
        unReadMessageObserver?.let {
            getProvider(IMessageCenterProvider::class.java)?.removeUnreadMessageCountObserver(it)
        }
    }

    /**
     * 添加未读消息数的监听
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun registerUnreadMessageCount() {
        if (unReadMessageObserver == null) {
            unReadMessageObserver = object : UnReadMessageObserver {
                override fun onNotifyMessageCount(totalCount: Long) {
                    // 更新消息中心红点
                    titleBar.apply {
                        updateRedPoint(
                            isReversed = true,
                            isShow = totalCount != 0L,
                            title = formatMsgCount(totalCount)
                        )
                    }
                }
            }
        }
        unReadMessageObserver?.let {
            getProvider(IMessageCenterProvider::class.java)?.addUnreadMessageCountObserver(it)
        }
    }

    override fun initView() {
        mBinding?.titleBar?.apply {
            setTitle(
                title = getString(R.string.community_title),
                isBold = true,
                gravity = Gravity.CENTER,
                drawablePadding = 5.dp,
            )
                .message {
                    getProvider(IMessageCenterProvider::class.java)
                        ?.startMessageCenterActivity(context as Activity)
                }
        }
        communityRootView?.topStatusMargin()
        val adapter = FragPagerItemAdapter(
            fm = childFragmentManager,
            pages = FragPagerItems(mContext)
                .add(titleRes = R.string.community_tab_follow, clazz = FollowFragment::class.java)
                .add(
                    titleRes = R.string.community_tab_selection,
                    clazz = SelectionFragment::class.java
                )
                .add(titleRes = R.string.community_tab_family, clazz = FamilyFragment::class.java)
        )
        mCommunityViewPager.adapter = adapter
        mCommunityTabLayout.setViewPager(mCommunityViewPager)
        mCommunityTabLayout.setSelectedAnim()
        mCommunityViewPager.setCurrentItem(1, false)

        //卡片大富翁
        mCommunityHeaderGameNameTv?.onClick {
            startCardMainPage()
        }
        mCommunityHeaderGameImgIv?.onClick {
            startCardMainPage()
        }
        //我的家族全部
        mCommunityHeaderFamilyMoreTv?.onClick {
            afterLogin {
                getProvider(ICommunityPersonProvider::class.java) {
                    startPerson(userId = UserManager.instance.userId, type = 4)
                }
            }
        }
        //发布
        mCommunityPublishBtnIv.onClick {
            afterLogin {
                getProvider(IPublishProvider::class.java) {
                    startEditorActivity(type = CONTENT_TYPE_POST)
                }
            }
        }
    }

    private fun startCardMainPage(){
        mProvider?.startCardMainActivity(context as Activity)
    }

    var count = 0
    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        mViewModel?.apply {
            loadMyFamilyData()
        }
    }

    override fun startObserve() {
        mViewModel?.run {
            uiState.observe(this@CommunityFragment) {
                it.apply {
                    success?.apply {
                        createMultiTypeAdapter(mCommunityHeaderFamilyRv)
                            .notifyAdapterAdded(this)
                    }
                }
            }
        }
    }
}