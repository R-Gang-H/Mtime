package com.kotlin.android.mine.ui.creatcenter

import android.graphics.Color
import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.annotation.*
import com.kotlin.android.app.data.entity.js.sdk.BrowserEntity
import com.kotlin.android.app.data.entity.mine.NoviceTaskInfo
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.app.router.provider.mine.IMineProvider
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.app.router.provider.sdk.IJsSDKProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.core.ext.getSpValue
import com.kotlin.android.core.ext.observeLiveData
import com.kotlin.android.core.ext.putSpValue
import com.kotlin.android.dialog.RankProblemDialog
import com.kotlin.android.dialog.showRankProblemDialog
import com.kotlin.android.image.bindadapter.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.marginStart
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.date.nowMillis
import com.kotlin.android.ktx.ext.date.weeOfTomorrow
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.utils.LogUtils
import com.kotlin.android.mine.R
import com.kotlin.android.mine.RANK_PROBLEM
import com.kotlin.android.mine.VIP_HELP_HOME
import com.kotlin.android.mine.bean.CreatorViewBean
import com.kotlin.android.mine.bean.DataCenterViewBean
import com.kotlin.android.mine.binder.CreatorTaskItemBinder
import com.kotlin.android.mine.binder.DataLableBinder
import com.kotlin.android.mine.binder.MyContentBinder
import com.kotlin.android.mine.databinding.MineActivityCreatCenterBinding
import com.kotlin.android.mine.databinding.MinePopupReleaseDownBinding
import com.kotlin.android.mine.ui.datacenter.fragment.formatNumber
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.popup.*
import com.kotlin.android.publish.component.ui.selectedvideo.showVideoListDialog
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.user.afterLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.binder.LabelBinder
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.android.widget.titlebar.add
import com.kotlin.android.widget.titlebar.back
import com.kotlin.android.widget.titlebar.help
import org.jetbrains.anko.dip
import java.util.regex.Pattern


/**
 *
 * @Package:        com.kotlin.android.mine.ui.creatcenter
 * @ClassName:      haoruigang
 * @Description:    ????????????
 * @Author:         haoruigang
 * @CreateDate:     2022/3/28 10:58
 */
@Route(path = RouterActivityPath.Mine.PAGE_CREAT_CENTER)
class CreatCenterActivity : BaseVMActivity<CreatCenterViewModel, MineActivityCreatCenterBinding>() {

    private lateinit var mTitleBar: TitleBarManager

    private var rankProblem: RankProblemDialog? = null

    private var releaseBinding: MinePopupReleaseDownBinding? = null

    private var mTaskLabelAdapter: MultiTypeAdapter? = null
    private var mTaskLabelListData: ArrayList<MultiTypeBinder<*>>? = ArrayList()
    private var mTaskContentAdapter: MultiTypeAdapter? = null//???????????????

    private var mDataLabelAdapter: MultiTypeAdapter? = null
    private var mDataLabelListData: ArrayList<MultiTypeBinder<*>>? = ArrayList()
    private var mDataAmountAdapter: MultiTypeAdapter? = null//???????????????

    private var mContentLabelAdapter: MultiTypeAdapter? = null
    private var mContentLabelListData: ArrayList<MultiTypeBinder<*>>? = ArrayList()
    private var mContentAdapter: MultiTypeAdapter? = null// ????????????

    private var iMineProvider = getProvider(IMineProvider::class.java)

    override fun initVM(): CreatCenterViewModel = viewModels<CreatCenterViewModel>().value
    private var iPublishProvider = getProvider(IPublishProvider::class.java)
    private var movieId: Long? = null//??????id
    private var familyId: Long? = null//??????id

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        mTitleBar = TitleBarManager.instance
        TitleBarManager.with(this)
            .setTitle(titleRes = R.string.mine_creat_center)
            .back {
                finish()
            }.add {
                mBinding?.dimView?.layoutParams?.height = mBinding?.rlNested?.measuredHeight
                mBinding?.dimView?.visible()
                mReleasePop?.showAtAnchorView(it, YGravity.BELOW, XGravity.ALIGN_RIGHT)
            }.help {
                // ??????????????????
                rankProblem = showRankProblemDialog(
                    getString(R.string.rank_problem),
                    isCancelable = true,
                    content = mViewModel?.helpLeveList
                )
            }

    }

    /**
     * ????????????
     */
    private val mReleasePop by lazy {
        EasyPopup.create().setContentView(releaseBinding?.root)
            ?.setOnViewListener(object : EasyPopup.OnViewListener {
                override fun initViews(view: View?, popup: EasyPopup?) {
                    releaseBinding?.apply {
                        vArrow.background =
                            TriangleDrawable(
                                ARROWDIRECTION.TOP,
                                Color.parseColor("#FFFFFF")
                            )
                        rlArticle.setOnClickListener { onClick(it) }
                        rlVideo.setOnClickListener { onClick(it) }
                    }
                }
            })
            ?.setOnDismissListener { mBinding?.dimView?.gone() }
            ?.setFocusAndOutsideEnable(true)
            ?.apply()
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.rlArticle -> {
                // todo ??????????????????
                afterLogin {
                    getProvider(IPublishProvider::class.java)?.startEditorActivity(
                        type = CONTENT_TYPE_ARTICLE
                    )
                    mReleasePop?.dismiss()
                }
            }
            R.id.rlVideo -> {
                // todo ??????????????????
                afterLogin {
                    showVideoListDialog()
                    mReleasePop?.dismiss()
                }
            }
        }
    }

    override fun initView() {
        mBinding?.apply {
            releaseBinding = MinePopupReleaseDownBinding.inflate(layoutInflater)
        }
    }

    /**
     * ????????????
     * ????????????
     */
    private fun levelRefresh() {
        mViewModel?.creatorCenterBean?.let {
            it.levelInfo.apply {
                mViewModel?.levelNum = levelName.toString()
                mBinding?.levelCard?.apply {
                    var levelBg = AppCompatResources.getDrawable(
                        this@CreatCenterActivity,
                        R.drawable.ic_creatcenter_level1_bg
                    )
                    when (mViewModel?.levelNum) {
                        "L2" -> {
                            levelBg = AppCompatResources.getDrawable(
                                this@CreatCenterActivity,
                                R.drawable.ic_creatcenter_level2_bg
                            )
                        }
                        "L3" -> {
                            levelBg = AppCompatResources.getDrawable(
                                this@CreatCenterActivity,
                                R.drawable.ic_creatcenter_level3_bg
                            )
                        }
                        "L4" -> {
                            levelBg = AppCompatResources.getDrawable(
                                this@CreatCenterActivity,
                                R.drawable.ic_creatcenter_level4_bg
                            )
                        }
                        "L5" -> {
                            levelBg = AppCompatResources.getDrawable(
                                this@CreatCenterActivity,
                                R.drawable.ic_creatcenter_level5_bg
                            )
                        }
                    }
                    rlLevelBg.background = levelBg
                    /*tvLvNum.text = resources.getString(R.string.mine_creat_level_name)
                        .plus(mViewModel?.levelNum?.substring(1))*/
                    tvCreatNum.text = "${it.points}"
                    tvLvCreatNum.text = getString(R.string.mine_lv_creat_num)
                        .plus("$endPoints")
                    tvRightsDesc.setBackground(
                        colorRes = R.color.color_25ffffff,
                        cornerRadius = dip(11f).toFloat()
                    ).onClick {
                        // todo ??????????????????
                        iMineProvider?.startActivityCreatorRewardActivity()
                    }
                    lvProgressbar.apply {
                        // todo (endPoints.toFloat() - startPoints.toFloat())
                        progress = if (endPoints != 0L)
                            (it.points.toFloat() / endPoints.toFloat() * 100).toInt() else 0
                    }
                }
            }
        }
    }

    /**
     * ????????????
     */
    private fun taskCard() {
        mBinding?.taskCard?.apply {
            val labelBinder = LabelBinder(
                tag = rvTitleLabeled,
                titleRes = R.string.mine_task_center,
                actionTitleRes = R.string.mine_content_all,
                displayAction = true,
                rootMargin = Rect(15.dp, 24.dp, 15.dp, 0)
            )

            mTaskLabelAdapter =
                createMultiTypeAdapter(
                    rvTitleLabeled,
                    LinearLayoutManager(this@CreatCenterActivity)
                ).apply {
                    labelBinder.apply {
                        mTaskLabelListData?.clear()
                        mTaskLabelListData?.add(this)
                        mTaskLabelListData?.apply {
                            notifyAdapterAdded(this)
                        }
                    }
                    setOnClickListener { view, binder ->
                        (binder is LabelBinder).apply {
                            when (view.id) {
                                R.id.actionView -> {
                                    // todo ??????????????????
                                    iMineProvider?.startActivityCreatorTaskActivity()
                                }
                            }
                        }
                    }
                }

            mTaskContentAdapter =
                createMultiTypeAdapter(
                    rvCreatCon,
                    LinearLayoutManager(this@CreatCenterActivity)
                )
            mTaskContentAdapter?.setOnClickListener { view, binder ->
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
    }

    /**
     * ????????????
     */
    private fun dataCard() {
        mBinding?.dataCard?.apply {
            val labelBinder = LabelBinder(
                tag = rvTitleLabeled,
                titleRes = R.string.mine_data_center,
                subTitleRes = R.string.mine_data_center_update_data,
                displayAction = true,
                rootMargin = Rect(15.dp, 24.dp, 15.dp, 11.dp)
            )

            mDataLabelAdapter =
                createMultiTypeAdapter(
                    rvTitleLabeled,
                    LinearLayoutManager(this@CreatCenterActivity)
                ).apply {
                    labelBinder.apply {
                        mDataLabelListData?.clear()
                        mDataLabelListData?.add(this)
                        mDataLabelListData?.apply {
                            notifyAdapterAdded(this)
                        }
                    }
                    setOnClickListener { view, binder ->
                        (binder is LabelBinder).apply {
                            when (view.id) {
                                R.id.actionView -> {
                                    // todo ??????????????????
                                    iMineProvider?.startActivityDataCenterActivity()
                                }
                            }
                        }
                    }
                }

            mDataAmountAdapter =
                createMultiTypeAdapter(
                    rvCreatCon,
                    GridLayoutManager(this@CreatCenterActivity, 4)
                ).apply {
                    // todo ????????????
                    mViewModel?.creatorCenterBean?.apply {
                        val dataBinder = mutableListOf<MultiTypeBinder<*>>()
                        dataBinder.add(
                            DataLableBinder(
                                DataCenterViewBean.DataAmount(
                                    bgColor = R.color.color_1afeb12a,
                                    frameColor = R.color.color_66feb12a,
                                    fontColor = getColor(R.color.color_feb12a),
                                    // todo ?????????????????? ??????????????????
                                    amount = formatNumber(viewsCount),
                                    desText = "?????????/?????????"
                                ), Rect(15.dp, 0.dp, 4.dp, 0.dp)
                            )
                        )
                        dataBinder.add(
                            DataLableBinder(
                                DataCenterViewBean.DataAmount(
                                    bgColor = R.color.color_1a91d959,
                                    frameColor = R.color.color_8091d959,
                                    fontColor = getColor(R.color.color_91d959),
                                    // todo ?????????????????? ??????????????????
                                    amount = formatNumber(upCount),
                                    desText = "????????????"
                                )
                            )
                        )
                        dataBinder.add(
                            DataLableBinder(
                                DataCenterViewBean.DataAmount(
                                    bgColor = R.color.color_0f36c096,
                                    frameColor = R.color.color_6636c096,
                                    fontColor = getColor(R.color.color_36c096),
                                    // todo ?????????????????? ??????????????????
                                    amount = formatNumber(commentCount),
                                    desText = "????????????"
                                )
                            )
                        )
                        dataBinder.add(
                            DataLableBinder(
                                DataCenterViewBean.DataAmount(
                                    bgColor = R.color.color_2ab5e1_alpha_6,
                                    frameColor = R.color.color_802ab5e1,
                                    fontColor = getColor(R.color.color_20a0da),
                                    // todo ?????????????????? ??????????????????
                                    amount = formatNumber(collectCount),
                                    desText = "????????????"
                                ), Rect(4.dp, 0.dp, 15.dp, 0.dp)
                            )
                        )
                        notifyAdapterAdded(dataBinder)
                    }
                }
        }
    }

    /**
     * ????????????
     */
    private fun contentCard() {
        mBinding?.myConCard?.apply {
            val labelBinder = LabelBinder(
                tag = rvTitleLabeled,
                titleRes = R.string.mine_content_title,
                actionTitleRes = R.string.mine_content_all,
                displayAction = true,
                rootMargin = Rect(15.dp, 22.dp, 15.dp, 0)
            )

            mContentLabelAdapter =
                createMultiTypeAdapter(
                    rvTitleLabeled,
                    LinearLayoutManager(this@CreatCenterActivity)
                ).apply {
                    labelBinder.apply {
                        mContentLabelListData?.clear()
                        mContentLabelListData?.add(this)
                        mContentLabelListData?.apply {
                            notifyAdapterAdded(this)
                        }
                    }
                    setOnClickListener { view, binder ->
                        (binder is LabelBinder).apply {
                            when (view.id) {
                                R.id.actionView -> {
                                    // todo ?????????????????? ??????
                                    iMineProvider?.startMyContent(this@CreatCenterActivity)
                                }
                            }
                        }
                    }
                }

            mContentAdapter =
                createMultiTypeAdapter(
                    rvCreatCon,
                    GridLayoutManager(this@CreatCenterActivity, 5)
                ).apply {
                    mViewModel?.apply {
                        val contentBinder = mutableListOf<MultiTypeBinder<*>>()
                        contentBinder.add(MyContentBinder(
                            CreatorViewBean.MyContentBean(
                                conIcon = AppCompatResources.getDrawable(
                                    this@CreatCenterActivity,
                                    R.drawable.ic_article
                                ),
                                conName = "??????"
                            )
                        ) {   // todo ??????????????????
                            iMineProvider?.startMyContent(
                                this@CreatCenterActivity,
                                CONTENT_TYPE_ARTICLE
                            )
                        })
                        contentBinder.add(MyContentBinder(
                            CreatorViewBean.MyContentBean(
                                conIcon = AppCompatResources.getDrawable(
                                    this@CreatCenterActivity,
                                    R.drawable.ic_long_film_review
                                ),
                                conName = "?????????"
                            )
                        ) {   // todo ?????????????????????
                            iMineProvider?.startMyContent(
                                this@CreatCenterActivity,
                                CONTENT_TYPE_FILM_COMMENT
                            )
                        })
                        contentBinder.add(MyContentBinder(
                            CreatorViewBean.MyContentBean(
                                conIcon = AppCompatResources.getDrawable(
                                    this@CreatCenterActivity,
                                    R.drawable.ic_posts
                                ),
                                conName = "??????"
                            )
                        ) {   // todo ??????????????????
                            iMineProvider?.startMyContent(
                                this@CreatCenterActivity,
                                CONTENT_TYPE_POST
                            )
                        })
                        contentBinder.add(MyContentBinder(
                            CreatorViewBean.MyContentBean(
                                conIcon = AppCompatResources.getDrawable(
                                    this@CreatCenterActivity,
                                    R.drawable.ic_video
                                ),
                                conName = "??????"
                            )
                        ) {   // todo ??????????????????
                            iMineProvider?.startMyContent(
                                this@CreatCenterActivity,
                                CONTENT_TYPE_VIDEO
                            )
                        })
                        contentBinder.add(MyContentBinder(
                            CreatorViewBean.MyContentBean(
                                conIcon = AppCompatResources.getDrawable(
                                    this@CreatCenterActivity,
                                    R.drawable.ic_audio
                                ),
                                conName = "??????"
                            )
                        ) {   // todo ??????????????????
                            if (mCannotPublishAudio) {
                                iMineProvider?.startMyContent(
                                    this@CreatCenterActivity,
                                    CONTENT_TYPE_AUDIO
                                )
                            }
                        })
                        notifyAdapterAdded(contentBinder)
                    }
                }
        }
    }

    /**
     * ????????????
     */
    private fun myMedal() {
        mBinding?.apply {
            rlMyMedal.onClick {
                // todo ??????????????????
                iMineProvider?.startMyMedal(this@CreatCenterActivity)
            }
            // tode ????????????
            mViewModel?.creatorCenterBean?.apply {
                if (medalInfos?.isNotEmpty() == true)
                    llMedalIcon.removeAllViews()
                medalInfos?.forEachIndexed { index, medalInfo ->
                    if (index > 2) { // ????????????????????????
                        return
                    }
                    val imageIcon = AppCompatImageView(this@CreatCenterActivity)
                    val parame = LinearLayout.LayoutParams(54.dp, 54.dp)
                    parame.gravity = Gravity.CENTER_HORIZONTAL
                    imageIcon.layoutParams = parame
                    imageIcon.scaleType = ImageView.ScaleType.CENTER_CROP
                    imageIcon.marginStart = 10.dp
                    loadImage(
                        imageView = imageIcon,
                        url = medalInfo.appLogoUrl,
                        width = 56.dp,
                        height = 56.dp,
                        defaultImg = AppCompatResources.getDrawable(
                            this@CreatCenterActivity,
                            R.mipmap.ic_medal_awarded_default
                        )
                    )
                    llMedalIcon.addView(imageIcon)
                }
            }

            rlHelp.onClick {
                // todo ??????????????????
                getProvider(IJsSDKProvider::class.java)?.startH5Activity(
                    BrowserEntity(
                        title = getString(R.string.mine_help_center),
                        url = VIP_HELP_HOME,
                        true
                    )
                )
            }
        }
    }

    override fun initData() {
        mViewModel?.getQueryArticleUser()
        mViewModel?.getHelpInfos()
    }

    override fun onResume() {
        super.onResume()
        mViewModel?.getCreator()
    }

    override fun startObserve() {
        getArticleUserObserve()
        // ??????????????????
        observeLiveData(mViewModel?.helpLeveState) {
            it?.apply {
                success?.apply {
                    mViewModel?.helpLeveList?.helpInfos = this.helpInfos
                    val rankIsOpen = getSpValue(RANK_PROBLEM, false)
                    if (!rankIsOpen) {
                        putSpValue(RANK_PROBLEM, true)
                        // ??????????????????
                        rankProblem = showRankProblemDialog(
                            getString(R.string.rank_problem),
                            isCancelable = true,
                            content = this
                        )
                    }
                }
                netError?.apply {

                }
            }
        }

        // ???????????????
        observeLiveData(mViewModel?.creatorState) {
            it?.apply {
                success?.apply {
                    mViewModel?.creatorCenterBean = this

                    mBinding?.creatorModel = mViewModel // ui??????

                    // ????????????
                    levelRefresh()
                    // ????????????
                    taskCard()
                    // ????????????
                    dataCard()

                    // ????????????
                    myMedal()

                    // ????????????
                    mTaskContentAdapter?.notifyAdapterAdded(getTaskBinder(this.taskInfos))

                }
                netError?.apply {

                }
            }
        }

        observeLiveData(mViewModel?.checkUserUiState) {
            it.apply {
                success?.apply {
                    if (status == 2L) {//????????????????????????
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

    /**
     * ????????????Binder
     * ???????????????
     */
    private fun getTaskBinder(noviceTaskInfo: List<NoviceTaskInfo>): MutableList<MultiTypeBinder<*>> {
        val listbinder = mutableListOf<MultiTypeBinder<*>>()
        noviceTaskInfo.forEachIndexed { index, it ->
            var startStamp = 0L // ??????0 ???????????????
            var endStamp = weeOfTomorrow // ???????????????????????? ??????
            it.startTime?.apply {
                startStamp = stamp
            }
            it.endTime?.apply {
                endStamp = stamp
            }
            // ????????????????????????????????????????????????????????????????????????
            if (nowMillis in startStamp until endStamp) {
                if (listbinder.size >= 2) {
                    return listbinder
                }
                listbinder.add(CreatorTaskItemBinder(it))
            }
        }
        return listbinder
    }

    /**
     * ????????????????????????Observe
     */
    private fun getArticleUserObserve() {
        mViewModel?.queryArticleUserState?.observe(this) {
            it.apply {
                if (isEmpty) {
                    // ????????????
                    contentCard()
                }
                success.apply {
                    // ????????????????????????????????????????????????????????????
                    mViewModel?.mCannotPublishAudio = this != null && this.type != null
                    LogUtils.d("isAudio===" + mViewModel?.mCannotPublishAudio)
                    // ????????????
                    contentCard()
                }
                netError?.run {
                    showToast(this)
                    // ????????????
                    contentCard()
                }
                error?.run {
                    showToast(this)
                    // ????????????
                    contentCard()
                }
            }
        }
    }

    /**
     * ????????????
     */
    private fun onClick(binder: CreatorTaskItemBinder) {
        if (binder.taskInfos.finishFactor != "") {
            binder.taskInfos.finishFactor?.apply {
                if (!checkCountName(this)) {//???????????????
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
        //?????????????????????????????????
        when (binder.taskInfos.theme) {
            10L -> {//?????????????????????
                iPublishProvider?.startEditorActivity(CONTENT_TYPE_ARTICLE)
            }
            20L -> {//?????????????????????
                if (familyId != null) {
                    mViewModel?.checkUserInGroup(familyId!!)
                }
            }
            30L -> {//????????????
                if (binder.taskInfos.finishFactor != "") {
                    iPublishProvider?.startEditorActivity(
                        CONTENT_TYPE_FILM_COMMENT,
                        isLongComment = true,
                        movieId = movieId
                    )
                }
            }
            40L -> {//????????????
                showVideoListDialog()
            }
        }
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
