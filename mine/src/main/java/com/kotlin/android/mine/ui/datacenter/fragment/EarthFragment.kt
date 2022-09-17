package com.kotlin.android.mine.ui.datacenter.fragment

import android.graphics.Color
import android.graphics.Outline
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.core.ext.observeLiveData
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.DataCenterViewBean
import com.kotlin.android.mine.bean.TodayPerformViewBean
import com.kotlin.android.mine.binder.TodayPerformsBinder
import com.kotlin.android.mine.databinding.MineFragEarthBinding
import com.kotlin.android.mine.databinding.MinePopupExpressDescBinding
import com.kotlin.android.mine.databinding.MinePopupTimeDownBinding
import com.kotlin.android.mine.ui.datacenter.adapter.TabsListAdapter
import com.kotlin.android.mine.ui.datacenter.adapter.TagsListAdapter
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.getDrawable
import com.kotlin.android.popup.*
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.binder.LabelBinder
import com.kotlin.android.widget.multistate.MultiStateView


/**
 *
 * @Package:        com.kotlin.android.mine.ui.datacenter.fragment
 * @ClassName:      EarthFragment
 * @Description:    整体概览
 * @Author:         haoruigang
 * @CreateDate:     2022/3/11 13:46
 */
class EarthFragment : BaseVMFragment<EarthViewModel, MineFragEarthBinding>() {

    companion object {
        fun newInstance() = EarthFragment()
    }

    private lateinit var labelBinder: LabelBinder
    private lateinit var todayBinder: TodayPerformsBinder

    override fun initVM(): EarthViewModel = viewModels<EarthViewModel>().value

    override fun initView() {
        mBinding?.apply {
            tagRv.apply {
                layoutManager = LinearLayoutManager(context).apply {
                    orientation = RecyclerView.HORIZONTAL
                }
                adapter = mTagAdapter
            }
            // 公共初始化视图
            commonPreformInitView()
        }
    }

    private var checkTag = ""

    // 内容分类切换 adapter
    private val mTagAdapter by lazy {
        TagsListAdapter(fun(data: DataCenterViewBean.Tags, pos: Int, adapter: TagsListAdapter) {
            data.isSelect = true
            mViewModel?.apply {
                statisticsType = data.index
                checkTag = data.tagName
                tagNames.value?.forEachIndexed { index, tags ->
                    if (pos != index) {
                        tags.isSelect = false
                    }
                }
                adapter.notifyDataSetChanged()

                loadDataRefresh()

                tagSelectDesc(pos)
            }
        })
    }

    /*===============表现公共部分 start================*/
    private var mIsInitData: Boolean = false

    private var mLabelAdapter: MultiTypeAdapter? = null
    private var mLabelListData: ArrayList<MultiTypeBinder<*>>? = ArrayList()

    private var mTodayAdapter: MultiTypeAdapter? = null
    private var mTodayListData: ArrayList<MultiTypeBinder<*>>? = ArrayList()

    private var expressDescBinding: MinePopupExpressDescBinding? = null
    private var timeDownBinding: MinePopupTimeDownBinding? = null

    private fun commonPreformInitView() {
        mBinding?.commonPer?.apply {
            labelBinder = LabelBinder(
                tag = rvLabeled,
                title = getString(R.string.mine_data_center_performance),
                subTitle = getString(R.string.mine_data_center_update_data),
                actionTitle = mViewModel?.expressTimeFilter?.value,
                actionDrawable = getDrawable(com.kotlin.android.widget.R.drawable.ic_label_15_triangle_down),
                displayAction = true, displayTitleInfo = true,
                rootMargin = Rect(15.dp, 20.dp, 15.dp, 0)
            )

            mLabelAdapter = createMultiTypeAdapter(rvLabeled, LinearLayoutManager(context)).apply {
                labelBinder.apply {
                    mLabelListData?.add(this)
                    mLabelListData?.apply {
                        notifyAdapterAdded(this)
                    }
                }
                setOnClickListener(::onBinderClick)
            }

            var timeData = arrayListOf<String>()
            mViewModel?.apply {
                timeSelec.apply {
                    if (value == null) {
                        timeData.addAll(arrayListOf("7天", "近半年", "历史累计"))
                        setTimeNames(timeData)
                    } else {
                        timeData = value as ArrayList<String>
                    }
                }
            }

            tabRv.apply {
                layoutManager = GridLayoutManager(context, 5)
                adapter = mTabAdapter
            }

            todayTitle()
            mTodayAdapter = createMultiTypeAdapter(rvToday, LinearLayoutManager(context)).apply {
                todayBinder.apply {
                    mTodayListData?.add(this)
                }
            }

            stateView.setMultiStateListener(object : MultiStateView.MultiStateListener {
                override fun onMultiStateChanged(viewState: Int) {
                    if (viewState == MultiStateView.VIEW_STATE_NO_NET || viewState == MultiStateView.VIEW_STATE_ERROR) {
                        loadDataRefresh()
                    }
                }

            })

        }

        activity?.apply {
            expressDescBinding = MinePopupExpressDescBinding.inflate(layoutInflater)
            timeDownBinding = MinePopupTimeDownBinding.inflate(layoutInflater)
        }
    }

    private fun initChart() {
        mBinding?.commonPer?.apply {
            lineChart.apply {
                // todo 测试数据
                mViewModel?.earthBean?.apply {
                    val amountList = arrayListOf<Long>()
                    val dateList = arrayListOf<String>()
                    statisticsInfos.forEach {
                        dateList.add(it.time.toString())
                        when (mViewModel?.position) {
                            0 -> {
                                mViewModel?.posDesc =
                                    if (checkTag == "视频" || checkTag == "音频") "播放量" else "阅读量"
                                amountList.add(it.viewsCount)
                            }
                            1 -> {
                                mViewModel?.posDesc = "内容量"
                                amountList.add(it.contentCount)
                            }
                            2 -> {
                                mViewModel?.posDesc = "点赞量"
                                amountList.add(it.upCount)
                            }
                            3 -> {
                                mViewModel?.posDesc = "评论量"
                                amountList.add(it.commentCount)
                            }
                            4 -> {
                                mViewModel?.posDesc = "收藏量"
                                amountList.add(it.collectCount)
                            }
                        }
                    }
                    updateTime(amountList, dateList)
                    setDescName(mViewModel?.posDesc)
                }

            }
        }
    }

    private fun tagSelectDesc(pos: Int) {
        mViewModel?.apply {
            setExpressContent(
                when (pos) {
                    1 -> {
                        "包括了日志、长影评、文章、帖子四类内容的数据"
                    }
                    2 -> {
                        "视频内容的数据"
                    }
                    3 -> {
                        "音频内容的数据"
                    }
                    else -> {
                        "包括了日志、长影评、文章、帖子、视频、音频六类内容的数据"
                    }
                }
            )
            expressDescBinding?.tvDescContent?.text = expressContent.value
        }
    }

    // 内容分类说明 adapter
    private val mTabAdapter by lazy {
        TabsListAdapter(fun(
            data: ArrayList<DataCenterViewBean.Tabs>,
            pos: Int,
            adapter: TabsListAdapter,
        ) {
            mViewModel?.position = pos
            data[pos].isSelect = true
            mViewModel?.tabNames?.value?.forEachIndexed { index, tags ->
                if (pos != index) {
                    tags.isSelect = false
                }
            }
            adapter.notifyDataSetChanged()

            initChart() // 刷新图表数据
        })
    }

    private fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        (binder is LabelBinder).apply {
            when (view.id) {
                R.id.titleInfoView -> {
                    mAbovePop?.showAtAnchorView(view, YGravity.ABOVE, XGravity.LEFT)
                }
                R.id.actionView -> {
                    val endDrawable =
                        getDrawable(com.kotlin.android.widget.R.drawable.ic_label_15_triangle_up)
                    endDrawable?.apply {
                        setBounds(0, 0, minimumWidth, minimumHeight)
                    }
                    (mLabelListData?.get(0) as LabelBinder).binding?.actionView?.setCompoundDrawables(
                        null,
                        null,
                        endDrawable,
                        null)

                    mDropDownPop?.showAtAnchorView(
                        view,
                        YGravity.BELOW,
                        XGravity.ALIGN_RIGHT
                    )
                }
            }
        }
    }

    // 公共初始化数据
    private fun todayTitle() {
        todayBinder = TodayPerformsBinder(
            TodayPerformViewBean(
                "日期", if (checkTag == "视频" || checkTag == "音频") "播放量" else "阅读量",
                "内容量", "点赞量", "评论量", "收藏量"
            )
        )
    }

    private fun commonPerformInitData() {
        mViewModel?.apply {
            var tabNameUi = arrayListOf<DataCenterViewBean.Tabs>()
            //if (tabNames.value == null) {
            mViewModel?.earthBean?.apply {
                tabNameUi.add(
                    DataCenterViewBean.Tabs(
                        0,
                        true,
                        tabName = if (checkTag == "视频" || checkTag == "音频") "播放量" else "阅读量",
                        tabNum = formatNumber(viewsCount)
                    )
                )
                tabNameUi.add(
                    DataCenterViewBean.Tabs(
                        1, false, "内容量",
                        tabNum = formatNumber(contentCount)
                    )
                )
                tabNameUi.add(
                    DataCenterViewBean.Tabs(
                        2, false, "点赞量",
                        tabNum = formatNumber(upCount)
                    )
                )
                tabNameUi.add(
                    DataCenterViewBean.Tabs(
                        3, false, "评论量",
                        tabNum = formatNumber(commentCount)
                    )
                )
                tabNameUi.add(
                    DataCenterViewBean.Tabs(
                        4, false, "收藏量",
                        tabNum = formatNumber(collectCount)
                    )
                )
                setTabNames(tabNameUi)
            }
            /*} else {
                tabNameUi = tabNames.value as ArrayList<DataCenterViewBean.Tabs>
            }*/
            mTabAdapter.setData(tabNameUi)

            initChart()

            expressDescBinding?.earthModel = this
            timeDownBinding?.earthModel = this

            timeSelect()

            mViewModel?.earthBean?.apply {
                //todo 测试数据
                mTodayListData?.clear()
                todayTitle()
                mTodayListData?.add(todayBinder)
                statisticsInfos.forEach {
                    TodayPerformsBinder(TodayPerformViewBean(
                        date = it.time.toString(),
                        playback = formatNumber(it.viewsCount),
                        reading = formatNumber(it.contentCount),
                        praise = formatNumber(it.upCount),
                        comment = formatNumber(it.commentCount),
                        collect = formatNumber(it.collectCount))
                    ).apply {
                        mTodayListData?.add(this)
                    }
                }
            }
            mTodayListData?.apply {
                mTodayAdapter?.notifyAdapterDataSetChanged(this)
            }
        }
    }

    private val mAbovePop by lazy {
        EasyPopup.create().setContentView(expressDescBinding?.root)
            ?.setOnViewListener(object : EasyPopup.OnViewListener {
                override fun initViews(view: View?, popup: EasyPopup?) {
                    expressDescBinding?.apply {
                        val drawable = TriangleDrawable(
                            ARROWDIRECTION.BOTTOM,
                            Color.parseColor("#FFFFFF")
                        )
                        vArrow.background = drawable
                        vArrow.outlineProvider = object : ViewOutlineProvider() {
                            override fun getOutline(view: View, outline: Outline) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    outline.setPath(drawable.createPath())
                                } else {
                                    outline.setConvexPath(drawable.createPath())
                                }
                            }
                        }
                    }
                }
            })
            ?.setFocusAndOutsideEnable(true)
            ?.apply()
    }

    private val mDropDownPop by lazy {
        EasyPopup.create().setContentView(timeDownBinding?.root)
            ?.setOnViewListener(object : EasyPopup.OnViewListener {
                override fun initViews(view: View?, popup: EasyPopup?) {
                    timeDownBinding?.apply {
                        tvJqt.setOnClickListener { onClick(it) }
                        tvJbn.setOnClickListener { onClick(it) }
                        tvHistory.setOnClickListener { onClick(it) }
                    }
                }
            })
            ?.setOnDismissListener {
                val endDrawable =
                    getDrawable(com.kotlin.android.widget.R.drawable.ic_label_15_triangle_down)
                endDrawable?.apply {
                    setBounds(0, 0, minimumWidth, minimumHeight)
                }
                (mLabelListData?.get(0) as LabelBinder).binding?.actionView?.setCompoundDrawables(
                    null,
                    null,
                    endDrawable,
                    null)
            }
            ?.setAnimationStyle(R.style.RightTop2PopAnim)
            ?.setFocusAndOutsideEnable(true)
            ?.apply()
    }

    fun onClick(v: View) {
        var timeName = ""
        var checkNum = 1L
        var todayPerform = ""
        when (v.id) {
            // 近7天
            R.id.tvJqt -> {
                timeName = "近7天"
                checkNum = 1L
                todayPerform = "每日详情"
            }
            // 近半年
            R.id.tvJbn -> {
                timeName = "近半年"
                checkNum = 2L
                todayPerform = "每月详情"
            }
            // 历史累计
            R.id.tvHistory -> {
                timeName = "历史累计"
                checkNum = 3L
                todayPerform = "每年详情"
            }
        }
        mDropDownPop?.dismiss()
        mViewModel?.setExpressTime(timeName, checkNum)
        (mLabelListData?.get(0) as LabelBinder).binding?.actionView?.text =
            mViewModel?.expressTimeFilter?.value
        mBinding?.commonPer?.tvToday?.text = todayPerform

        loadDataRefresh()

        timeSelect()
    }

    private fun timeSelect() {
        mViewModel?.apply {
            timeDownBinding?.tvJqt?.isSelected = getTimeSelect(1)
            timeDownBinding?.tvJbn?.isSelected = getTimeSelect(2)
            timeDownBinding?.tvHistory?.isSelected = getTimeSelect(3)
        }
    }

    /*===============表现公共部分 end ================*/

    override fun initData() {
        mViewModel?.getQueryArticleUser()
    }

    fun loadDataRefresh() {
        mViewModel?.apply {
            expressTimeSelect.value?.apply {
                getStatistics(statisticsType, this)
            }
        }
    }

    private fun setContentState(@MultiStateView.ViewState state: Int) {
        mBinding?.commonPer?.stateView?.setViewState(state)
    }

    override fun onResume() {
        super.onResume()
        if (mIsInitData.not()) {
            loadDataRefresh()
        }
    }

    override fun startObserve() {
        getArticleUserObserve()
        observeLiveData(mViewModel?.dataCenterState) {
            this.mIsInitData = true
            it?.apply {
                if (isEmpty) {
                    setContentState(MultiStateView.VIEW_STATE_EMPTY)
                }
                success?.apply {
                    setContentState(MultiStateView.VIEW_STATE_CONTENT)
                    mViewModel?.earthBean = this
                    // 公共初始化数据
                    commonPerformInitData()
                }
                netError?.apply {
                    setContentState(MultiStateView.VIEW_STATE_NO_NET)
                }
                error?.apply {
                    setContentState(MultiStateView.VIEW_STATE_ERROR)
                }
            }
        }
    }

    /**
     * 当前文章用户信息Observe
     */
    private fun getArticleUserObserve() {
        mViewModel?.queryArticleUserState?.observe(this) {
            it.apply {
                if (isEmpty) {
                    // 整体概览Tab
                    switchTab()
                }
                success.apply {
                    // 音频：如该账号不能发布音频该模块不可点击
                    mViewModel?.mCannotPublishAudio = this != null && this.type != null

                    // 整体概览Tab
                    switchTab()

                }
                netError?.run {
                    showToast(this)
                    // 整体概览Tab
                    switchTab()
                }
                error?.run {
                    showToast(this)
                    // 整体概览Tab
                    switchTab()
                }
            }
        }
    }

    private fun switchTab() {
        mViewModel?.apply {
            var tagNameUi = arrayListOf<DataCenterViewBean.Tags>()
            //if (tagNames.value == null) {
            tagNameUi.add(DataCenterViewBean.Tags(0, true, "全部"))
            tagNameUi.add(DataCenterViewBean.Tags(1, false, "内容"))
            tagNameUi.add(DataCenterViewBean.Tags(2, false, "视频"))
            if (mCannotPublishAudio) { // 能发布音频展示该模块
                tagNameUi.add(DataCenterViewBean.Tags(3, false, "音频"))
            }
            setTagNames(tagNameUi)
            /*} else {
                tagNameUi = tagNames.value as ArrayList<DataCenterViewBean.Tags>
            }*/
            mTagAdapter.setData(tagNameUi)
        }
    }

}