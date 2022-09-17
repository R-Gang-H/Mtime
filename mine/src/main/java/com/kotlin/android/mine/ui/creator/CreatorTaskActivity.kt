package com.kotlin.android.mine.ui.creator

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_ARTICLE
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_FILM_COMMENT
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_POST
import com.kotlin.android.app.data.entity.mine.NoviceTaskInfo
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mine.R
import com.kotlin.android.mine.databinding.ActivityCreatorTaskBinding
import com.kotlin.android.mine.binder.CreatorTaskItemBinder
import com.kotlin.android.mine.ui.creatcenter.CreatCenterViewModel
import com.kotlin.android.publish.component.ui.selectedvideo.showVideoListDialog
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.titlebar.TitleBarManager
import java.util.regex.Pattern

/**
 * 任务中心
 */
@Route(path = RouterActivityPath.Mine.PAGE_CREATOR_ACTIVITY)
class CreatorTaskActivity : BaseVMActivity<CreatCenterViewModel, ActivityCreatorTaskBinding>() {

    private var iPublishProvider = getProvider(IPublishProvider::class.java)
    private var movieId: Long? = null//影片id
    private var familyId: Long? = null//家族id
    private lateinit var mRecyclerViewAdapter: MultiTypeAdapter//活动任务
    private lateinit var mDailyRecyclerViewAdapter: MultiTypeAdapter//日常任务
    private lateinit var mNoviceRecyclerViewAdapter: MultiTypeAdapter//新手任务
    override fun initView() {
        mBinding?.apply {
            mRecyclerViewAdapter = createMultiTypeAdapter(mRecyclerView)
            mDailyRecyclerViewAdapter = createMultiTypeAdapter(mDailyRecyclerView)
            mNoviceRecyclerViewAdapter = createMultiTypeAdapter(mNoviceRecyclerView)
        }
        initListener()
    }

    private fun initListener() {
        mRecyclerViewAdapter.setOnClickListener { view, binder ->
            when (binder) {
                is CreatorTaskItemBinder -> {
                    when (view.id) {
                        R.id.tv_status -> {
                            onClick(binder)
                        }
                    }
                }
            }
        }
        mDailyRecyclerViewAdapter.setOnClickListener { view, binder ->
            when (binder) {
                is CreatorTaskItemBinder -> {
                    when (view.id) {
                        R.id.tv_status -> {
                            onClick(binder)
                        }
                    }
                }
            }
        }
        mNoviceRecyclerViewAdapter.setOnClickListener { view, binder ->
            when (binder) {
                is CreatorTaskItemBinder -> {
                    when (view.id) {
                        R.id.tv_status -> {
                            onClick(binder)
                        }
                    }
                }
            }
        }
    }

    /**
     * 点击事件
     */
    private fun onClick(binder: CreatorTaskItemBinder) {
        if (binder.taskInfos.finishFactor != "") {
            binder.taskInfos.finishFactor?.apply {
                if (!checkCountName(this)) {//不包含汉字
                    if (contains(",")) {
                        split(",").apply {
                            movieId = this[0].toLong()
                            familyId = this[0].toLong()
                        }
                    } else {
                        movieId = this.toLong()
                        familyId = this.toLong()
                    }
                }
            }
        }
        //根据类型跳转对应的界面
        when (binder.taskInfos.theme) {
            10L -> {//写文章指定字数
                iPublishProvider?.startEditorActivity(CONTENT_TYPE_ARTICLE)
            }
            20L -> {//写帖子指定家族
                if (familyId != null) {
                    mViewModel?.checkUserInGroup(familyId!!)
                }
            }
            30L -> {//写长影评
                if (binder.taskInfos.finishFactor != "") {
                    iPublishProvider?.startEditorActivity(
                        CONTENT_TYPE_FILM_COMMENT,
                        isLongComment = true,
                        movieId = movieId
                    )
                }
            }
            40L -> {//发布视频
                showVideoListDialog()
            }
        }
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initData() {
        mViewModel?.loadData()//传入userid
    }

    override fun startObserve() {
        mViewModel?.taskState?.observe(this) {
            it?.apply {
                success?.apply {
                    mBinding?.bean = this
                    mRecyclerViewAdapter.notifyAdapterAdded(getBinder(this.activityTaskInfos))//活动任务
                    mDailyRecyclerViewAdapter.notifyAdapterAdded(getBinder(this.dailyTaskInfos))
                    mNoviceRecyclerViewAdapter.notifyAdapterAdded(getBinder(this.noviceTaskInfos))
                }
            }
        }
        mViewModel?.checkUserUiState?.observe(this) {
            it.apply {
                success?.apply {
                    if (status == 2L) {//表示不在当前家族
                        getProvider(ICommunityFamilyProvider::class.java)
                            ?.startFamilyDetail(familyId!!)
                    } else {
                        iPublishProvider?.startEditorActivity(
                            CONTENT_TYPE_POST,
                            familyId = familyId
                        )
                    }
                }
            }
        }
    }


    private fun getBinder(
        noviceTaskInfo: List<NoviceTaskInfo>?,
    ): MutableList<MultiTypeBinder<*>> {
        val listbinder = mutableListOf<MultiTypeBinder<*>>()
        noviceTaskInfo?.forEach {
            listbinder.add(CreatorTaskItemBinder(it, true))
        }
        return listbinder
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this)
            .setTitle(titleRes = R.string.mine_write_center_task, isBold = true, textSize = 16f)
            .addItem(
                isReversed = false,
                drawableRes = R.drawable.ic_title_bar_36_back,
                reverseDrawableRes = R.drawable.ic_title_bar_36_back_reversed,
                click = {
                    this.finish()
                }
            )
    }

    private fun checkCountName(str: String?): Boolean {
        val compile = Pattern.compile("[\u4ee0-\u9fa5]")
        val matcher = compile.matcher(str.toString())
        if (matcher.find()) {
            return true
        }
        return false
    }
}