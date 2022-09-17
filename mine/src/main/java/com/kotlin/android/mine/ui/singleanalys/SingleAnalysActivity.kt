package com.kotlin.android.mine.ui.singleanalys

import android.graphics.Color
import android.graphics.Outline
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.entity.mine.DataCenterBean
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.core.ext.observeLiveData
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.DataCenterViewBean
import com.kotlin.android.mine.bean.TodayPerformViewBean
import com.kotlin.android.mine.binder.AnalysisBinder
import com.kotlin.android.mine.binder.TodayPerformsBinder
import com.kotlin.android.mine.databinding.MineActivitySingleAnalysBinding
import com.kotlin.android.mine.databinding.MinePopupExpressDescBinding
import com.kotlin.android.mine.databinding.MinePopupTimeDownBinding
import com.kotlin.android.mine.ui.datacenter.adapter.TabsListAdapter
import com.kotlin.android.mine.ui.datacenter.fragment.formatNumber
import com.kotlin.android.popup.*
import com.kotlin.android.user.UserStore.Companion.context
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.binder.LabelBinder
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.android.widget.titlebar.back

/**
 *
 * @Package:        com.kotlin.android.mine.ui.singleanalys
 * @ClassName:      SingleAnalysActivity
 * @Description:    单篇分析详情
 * @Author:         haoruigang
 * @CreateDate:     2022/3/21 10:10
 */
@Route(path = RouterActivityPath.Mine.PAGE_ANALYS_DETAIL)
class SingleAnalysActivity :
    BaseVMActivity<SingleAnalysViewModel, MineActivitySingleAnalysBinding>() {

    private lateinit var mTitleBar: TitleBarManager

    private lateinit var todayBinder: TodayPerformsBinder

    override fun initVM(): SingleAnalysViewModel = viewModels<SingleAnalysViewModel>().value

    override fun initTheme() {
        super.initTheme()
        immersive().statusBarColor(getColor(R.color.color_ffffff)).statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        mTitleBar = TitleBarManager.instance
        TitleBarManager.with(this)
            .setTitle(titleRes = R.string.mine_single_analys_detail)
            .back {
                finish()
            }

    }

    override fun initView() {

        // 公共初始化视图
        commonPreformInitView()
    }

    /*===============表现公共部分 start================*/
    private var mIsInitData: Boolean = false

    private var mAnalysisBinder: ArrayList<MultiTypeBinder<*>>? = ArrayList()

    private var mLabelAdapter: MultiTypeAdapter? = null
    private var mLabelListData: ArrayList<MultiTypeBinder<*>>? = ArrayList()

    private var mTodayAdapter: MultiTypeAdapter? = null
    private var mTodayListData: ArrayList<MultiTypeBinder<*>>? = ArrayList()

    private lateinit var labelBinder: LabelBinder

    var descContent = ""

    private var expressDescBinding: MinePopupExpressDescBinding? = null
    private var timeDownBinding: MinePopupTimeDownBinding? = null
    private var checkType = 0L

    private fun commonPreformInitView() {
        mBinding?.apply {

            //内容类型  VIDEO(5, "视频"), AUDIO(6, "音频")
            checkType = intent.getLongExtra("type", 0L)
            // 单篇详情
            val analysisBinder = AnalysisBinder(
                intent.getSerializableExtra("bean") as DataCenterBean.SingleAnalysisBean.Item,
                checkType,
                amountShow = false
            )
            createMultiTypeAdapter(singleAnalys, LinearLayoutManager(context)).apply {
                analysisBinder.apply {
                    mAnalysisBinder?.add(this)
                    mAnalysisBinder?.apply {
                        notifyAdapterDataSetChanged(this)
                    }
                }
            }

        }?.commonPer?.apply {

            labelBinder = LabelBinder(tag = rvLabeled,
                title = getString(R.string.mine_data_center_performance),
                subTitle = getString(R.string.mine_data_center_update_data),
                actionTitle = mViewModel?.expressTimeFilter?.value,
                actionDrawable = AppCompatResources.getDrawable(this@SingleAnalysActivity,
                    R.drawable.ic_label_15_triangle_down),
                displayAction = true,
                rootMargin = Rect(15.dp, 20.dp, 15.dp, 0))

            mLabelAdapter = createMultiTypeAdapter(rvLabeled, LinearLayoutManager(context)).apply {
                labelBinder.apply {
                    mLabelListData?.add(this)
                    mLabelListData?.apply {
                        notifyAdapterDataSetChanged(this)
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
                layoutManager = GridLayoutManager(context, 4)
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

        expressDescBinding = MinePopupExpressDescBinding.inflate(layoutInflater)
        timeDownBinding = MinePopupTimeDownBinding.inflate(layoutInflater)
    }

    private fun initChart() {
        mBinding?.commonPer?.apply {
            lineChart.apply {
                // todo 测试数据
                mViewModel?.dataCenterDetailBean?.apply {
                    val amountList = arrayListOf<Long>()
                    val dateList = arrayListOf<String>()
                    statisticsDetailsInfos.forEach {
                        dateList.add(it.time.toString())
                        when (mViewModel?.position) {
                            0 -> {
                                mViewModel?.posDesc =
                                    if (checkType == 5L || checkType == 6L) "播放量" else "阅读量"
                                amountList.add(it.viewsCount)
                            }
                            /*1 -> {
                                mViewModel?.posDesc = "内容量"
                                amountList.add(it.contentCount)
                            }*/
                            1 -> {
                                mViewModel?.posDesc = "点赞量"
                                amountList.add(it.upCount)
                            }
                            2 -> {
                                mViewModel?.posDesc = "评论量"
                                amountList.add(it.commentCount)
                            }
                            3 -> {
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
                        com.kotlin.android.mtime.ktx.getDrawable(com.kotlin.android.widget.R.drawable.ic_label_15_triangle_up)
                    endDrawable?.apply {
                        setBounds(0, 0, minimumWidth, minimumHeight)
                    }
                    (mLabelListData?.get(0) as LabelBinder).binding?.actionView?.setCompoundDrawables(
                        null,
                        null,
                        endDrawable,
                        null)

                    mDropDownPop?.showAtAnchorView(view,
                        YGravity.BELOW,
                        XGravity.ALIGN_RIGHT)
                }
            }
        }
    }

    // 公共初始化数据
    private fun todayTitle() {
        todayBinder = TodayPerformsBinder(TodayPerformViewBean(
            date = "日期",
            playback = if (checkType == 5L || checkType == 6L) "播放量" else "阅读量",
            praise = "点赞量",
            comment = "评论量",
            collect = "收藏量"))
    }

    private fun commonPerformInitData() {
        mViewModel?.apply {
            var tabNameUi = arrayListOf<DataCenterViewBean.Tabs>()
            //if (tabNames.value == null) {
            mViewModel?.dataCenterDetailBean?.apply {
                tabNameUi.add(DataCenterViewBean.Tabs(0,
                    true,
                    if (checkType == 5L || checkType == 6L) "播放量" else "阅读量",
                    tabNum = formatNumber(viewsCount)))
                /*tabNameUi.add(DataCenterViewBean.Tabs(1, false, "内容量",
                        tabNum = formatNumber(contentCount)))*/
                tabNameUi.add(DataCenterViewBean.Tabs(2, false, "点赞量",
                    tabNum = formatNumber(upCount)))
                tabNameUi.add(DataCenterViewBean.Tabs(3, false, "评论量",
                    tabNum = formatNumber(commentCount)))
                tabNameUi.add(DataCenterViewBean.Tabs(4, false, "收藏量",
                    tabNum = formatNumber(collectCount)))
                setTabNames(tabNameUi)
            }
            /*} else {
                tabNameUi = tabNames.value as ArrayList<DataCenterViewBean.Tabs>
            }*/
            mTabAdapter.setData(tabNameUi)

            descContent = expressContent.value.toString()

            initChart()

            expressDescBinding?.earthModel = this
            timeDownBinding?.earthModel = this

            timeSelect()

            //todo 测试数据
            mViewModel?.dataCenterDetailBean?.apply {
                //todo 测试数据
                mTodayListData?.clear()
                todayTitle()
                mTodayListData?.add(todayBinder)
                statisticsDetailsInfos.forEach {
                    TodayPerformsBinder(TodayPerformViewBean(
                        date = it.time.toString(),
                        playback = formatNumber(it.viewsCount),
                        //reading = it.contentCount.toString(),
                        praise = formatNumber(it.upCount),
                        comment = formatNumber(it.commentCount),
                        collect = formatNumber(it.collectCount)
                    )).apply {
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
                        val drawable = TriangleDrawable(ARROWDIRECTION.BOTTOM,
                            Color.parseColor("#FFFFFF"))
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
            ?.setOnDismissListener(object : PopupWindow.OnDismissListener {
                override fun onDismiss() {
                    val endDrawable =
                        com.kotlin.android.mtime.ktx.getDrawable(com.kotlin.android.widget.R.drawable.ic_label_15_triangle_down)
                    endDrawable?.apply {
                        setBounds(0, 0, minimumWidth, minimumHeight)
                    }
                    (mLabelListData?.get(0) as LabelBinder).binding?.actionView?.setCompoundDrawables(
                        null,
                        null,
                        endDrawable,
                        null)
                }
            })
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

    }

    fun loadDataRefresh() {
        mViewModel?.apply {
            expressTimeSelect.value?.apply {
                getStatisticDetail(
                    intent.getLongExtra("type", 0L),
                    intent.getLongExtra("contentId", 0L),
                    this)
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
        observeLiveData(mViewModel?.statisticDetailStatic) {
            this.mIsInitData = true
            it?.apply {
                if (isEmpty) {
                    setContentState(MultiStateView.VIEW_STATE_EMPTY)
                }
                success?.apply {
                    setContentState(MultiStateView.VIEW_STATE_CONTENT)
                    mViewModel?.dataCenterDetailBean = this
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

}