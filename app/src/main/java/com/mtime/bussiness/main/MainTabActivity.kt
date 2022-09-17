package com.mtime.bussiness.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.app.router.liveevent.event.LoginState
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.audio.floatview.component.aduiofloat.FloatingView
import com.kotlin.android.bonus.scene.component.bean.BonusSceneDialogDismissBean
import com.kotlin.android.bonus.scene.component.bean.PopupBonusSceneBean
import com.kotlin.android.bonus.scene.component.showBonusSceneDialog
import com.kotlin.android.community.ui.home.CommunityFragment
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.core.CoreApp
import com.kotlin.android.core.entity.PAGE_FLAG
import com.kotlin.android.core.entity.PageFlag
import com.kotlin.android.core.ext.observeLiveData
import com.kotlin.android.home.ui.home.HomeFragment
import com.kotlin.android.ktx.ext.core.getCompatDrawable
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.mine.ui.home.MineVMFragment
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.isLogin
import com.kotlin.android.router.liveevent.CLOSE_BONUS_SCENE
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.router.liveevent.POPUP_BONUS_SCENE
import com.kotlin.chat_component.HuanxinConnector
import com.mtime.R
import com.mtime.applink.ApplinkManager
import com.mtime.bussiness.main.dialogs.MainPublishDialog
import com.mtime.bussiness.main.maindialog.MainDialogManager
import com.mtime.bussiness.main.widget.tab.TabBarItem
import com.mtime.bussiness.ticket.TicketFragment
import com.mtime.databinding.ActMainTabBinding
import com.mtime.frame.App

/**
 * Created by suq on 2022/4/6
 * des:
 */
@Route(path = RouterActivityPath.Main.PAGER_MAIN)
class MainTabActivity : BaseVMActivity<MainViewModel, ActMainTabBinding>() {

    companion object {
        const val KEY_SPLASH_JUMP_URL = "key_splash_jump_url"

        fun start(context: Context, flag: PageFlag?, applink: String?) {
            val intent = Intent(context, MainTabActivity::class.java)
            intent.putExtra(PAGE_FLAG, flag)
            intent.putExtra(KEY_SPLASH_JUMP_URL, applink)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private val mPublishDialog by lazy { MainPublishDialog(this) }
    private val mMainDialogManager by lazy { MainDialogManager() }
    private var checkPopupBonusAction = 0L

    private var pageFlag: PageFlag? = null
    private var isNewIntent: Boolean = false

    //记录用户首次点击返回键的时间
    private var firstTime: Long = 0

    private var homeTag = "home_frag"
    private var filmTag = "film_frag"
    private var communityTag = "community_frag"
    private var mineTag = "mine_frag"
    private var homeFragment: Fragment? = null
    private var filmFragment: Fragment? = null
    private var communityFragment: Fragment? = null
    private var mineFragment: Fragment? = null

    override fun initRecover(savedInstanceState: Bundle) {
        super.initRecover(savedInstanceState)
        homeFragment = supportFragmentManager.findFragmentByTag(homeTag)
        filmFragment = supportFragmentManager.findFragmentByTag(filmTag)
        communityFragment = supportFragmentManager.findFragmentByTag(communityTag)
        mineFragment = supportFragmentManager.findFragmentByTag(mineTag)
    }

    override fun getIntentData(intent: Intent?) {
        super.getIntentData(intent)
        intent?.apply {
            pageFlag = getParcelableExtra(PAGE_FLAG)

            // 外部h5调用起来
            if (TextUtils.equals("mtime", scheme)) {
                val deeplink = dataString
                if (!TextUtils.isEmpty(deeplink)) {
                    data?.apply {
                        val applinkData = getQueryParameter("applinkData");
                        if (!TextUtils.isEmpty(applinkData)) {
                            ApplinkManager.jump4Scheme(this@MainTabActivity, applinkData);
                        }
                    }
                }
            } else {
                val url = getStringExtra(KEY_SPLASH_JUMP_URL);
                if (!TextUtils.isEmpty(url)) {
                    ApplinkManager.jump(this@MainTabActivity, url, null);
                }
            }
        }
    }

    override fun initNewData() {
        super.initNewData()
        isNewIntent = true
    }

    override fun initVB(): ActMainTabBinding? {
        return try {
            super.initVB()
        } catch (e: Exception) {
            e.printStackTrace()
            "${javaClass.simpleName} initVB: (ActMainTabBinding) 异常".e()
            ActMainTabBinding.inflate(layoutInflater)
        }
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .transparentStatusBar(isFitsSystemWindows = false)
            .statusBarDarkFont(true)
    }

    override fun initView() {
        mMainDialogManager.init(this)
        initTabBar()
    }

    private fun initTabBar() {
        mBinding?.tabBar?.apply {
            init(container = R.id.container, fragmentManager = supportFragmentManager)
            addItem(
                TabBarItem(
                    context = this@MainTabActivity,
                    tag = homeTag,
                    fragment = homeFragment ?: HomeFragment(),
                    titleRes = R.string.str_tabname_home,
                    iconDrawable = getCompatDrawable(R.drawable.ic_home_tab_normal),
                    iconDrawableHighlight = getCompatDrawable(R.drawable.ic_home_tab_selected),
                )
            )
            addItem(
                TabBarItem(
                    context = this@MainTabActivity,
                    tag = filmTag,
                    fragment = filmFragment ?: TicketFragment(),
                    titleRes = R.string.str_tabname_payticket,
                    iconDrawable = getCompatDrawable(R.drawable.ic_ticket_tab_normal),
                    iconDrawableHighlight = getCompatDrawable(R.drawable.ic_ticket_tab_selected),
                )
            )
            addItem(
                TabBarItem(
                    context = this@MainTabActivity,
                    tag = "",
                    fragment = null,
                    titleRes = R.string.str_tabname_publish,
                    iconDrawable = getCompatDrawable(R.drawable.ic_publish_tab),
                    itemHeight = 69.dp,
                    iconWidth = 45.dp,
                    iconHeight = 45.dp
                )
            )
            addItem(
                TabBarItem(
                    context = this@MainTabActivity,
                    tag = communityTag,
                    fragment = communityFragment ?: CommunityFragment(),
                    titleRes = R.string.str_tabname_community,
                    iconDrawable = getCompatDrawable(R.drawable.ic_forum_tab_normal),
                    iconDrawableHighlight = getCompatDrawable(R.drawable.ic_forum_tab_selected),
                )
            )
            addItem(
                TabBarItem(
                    context = this@MainTabActivity,
                    tag = mineTag,
                    fragment = mineFragment ?: MineVMFragment(),
                    titleRes = R.string.str_tabname_user,
                    iconDrawable = getCompatDrawable(R.drawable.ic_profile_tab_normal),
                    iconDrawableHighlight = getCompatDrawable(R.drawable.ic_profile_tab_selected),
                )
            )

            action = {
                when (it) {
                    0 -> {
                        //主页面,app运行中显示业务弹框
                        mMainDialogManager.appRuningShow()
                    }
                    2 -> {
                        //发布
                        mPublishDialog.show()
                    }
                }
            }

            applyPageFlag(pageFlag)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isNewIntent) {
            isNewIntent = false
            mBinding?.tabBar?.applyPageFlag(pageFlag)
        }
    }

    override fun initData() {

    }

    override fun startObserve() {
        //彩蛋事件
        popupEvent()
        //环信 消息相关的连接和监听
        huanxinEvent()

        observeLiveData(mViewModel?.checkPopupBonusUIState) {
            it.apply {
                success?.apply {
                    if (code == 0L && success) {
                        val app = App.getInstance() as CoreApp
                        val topActivity = app.getTopActivity()
                        topActivity?.let { top ->
                            if (top is FragmentActivity && !top.isFinishing() && !top.isDestroyed()) {
                                top.showBonusSceneDialog(checkPopupBonusAction);
                            }
                        }
                        return@observeLiveData
                    }
                }
                postCloseBonusSceneDialog()
            }
        }
    }

    override fun onBackPressed() {
        val secondTime = System.currentTimeMillis()
        if (secondTime - firstTime > 2000) {//间隔大于2s
            showToast(R.string.exit_app_hint)
            firstTime = secondTime
        } else {
            FloatingView.get()?.remove()
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        mMainDialogManager.destroy()
        super.onDestroy()
    }

    private fun popupEvent() {
        LiveEventBus.get(POPUP_BONUS_SCENE).observe(this) {
            if (isLogin()) {
                mBinding?.tabBar?.postDelayed({
                    if (it != null && it is PopupBonusSceneBean) {
                        checkPopupBonusAction = it.action
                        mViewModel?.checkPopupBonus(it.action)
                    }
                }, 1000L)
            }
        }
    }

    private fun postCloseBonusSceneDialog() {
        LiveEventBus.get(CLOSE_BONUS_SCENE).post(BonusSceneDialogDismissBean(true));
    }

    private fun huanxinEvent() {
        HuanxinConnector.instance.connect()
        LiveEventBus.get(LOGIN_STATE, LoginState::class.java)
            .observe(this) {
                if (it.isLogin) {
                    HuanxinConnector.instance.connect();
                } else {
                    HuanxinConnector.instance.disConnect();
                }
            }
    }
}