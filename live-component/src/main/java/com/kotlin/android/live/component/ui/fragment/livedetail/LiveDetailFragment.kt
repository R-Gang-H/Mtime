package com.kotlin.android.live.component.ui.fragment.livedetail

import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.app.data.entity.live.LiveDetail
import com.kotlin.android.app.data.entity.live.LiveNewsList
import com.kotlin.android.image.bindadapter.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.time.TimeExt.millis2String
import com.kotlin.android.live.component.R
import com.kotlin.android.live.component.constant.LIVE_MOVIE_TICKET_STATUS_PRESELL
import com.kotlin.android.live.component.constant.LIVE_MOVIE_TICKET_STATUS_TICKET
import com.kotlin.android.live.component.constant.LIVE_STATUS_APPOINT
import com.kotlin.android.live.component.constant.LIVE_STATUS_REVIEW
import com.kotlin.android.live.component.databinding.FragmentLiveDetailBinding
import com.kotlin.android.live.component.databinding.ItemLiveDetailVideoBinding
import com.kotlin.android.live.component.ui.adapter.LiveDetailNewsItemBinder
import com.kotlin.android.live.component.ui.adapter.LiveDetailVideoItemBinder
import com.kotlin.android.live.component.ui.detail.LiveDetailActivity
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.router.liveevent.LIVE_DETAIL_CHANGE_STATUS
import com.kotlin.android.router.liveevent.LIVE_DETAIL_CLICK_VIDEO
import com.kotlin.android.router.liveevent.LIVE_DETAIL_PLAY_NEXT_VIDEO
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import kotlinx.android.synthetic.main.fragment_live_detail.*

/**
 * create by vivian.wei on 2021/3/2
 * description: 直播详情fragment
 */
class LiveDetailFragment: BaseVMFragment<LiveDetailFragmentViewModel, FragmentLiveDetailBinding>(),
    LiveDetailVideoItemBinder.ILiveDetailClickCallBack {

    companion object {
        // 直播开始时间格式
        const val START_TIME_PATTERN_MD = "MM月dd日 HH:mm"
        const val START_TIME_PATTERN_YMD = "yyyy年MM月dd日 HH:mm"  // 回放状态显示年
        // 关联电影
        const val MOVIE_IS_SHOW = 1L
        const val MOVIE_IMG_WIDTH = 50
        const val MOVIE_IMG_HEIGHT = 75
        const val MOVIE_TYPE_COUNT = 3
    }

    private val mTicketProvider: ITicketProvider? = getProvider(ITicketProvider::class.java)

    private var mMovieId: Long = 0L
    // 视频
    private var mVideoAdapter: MultiTypeAdapter? = null
    private var mVideos: MutableList<LiveDetail.Video>? = null
    private var mVideoSelectPosition: Int = -1 // 选中视频position
    private var mVideoBinderList: MutableList<MultiTypeBinder<ItemLiveDetailVideoBinding>> = mutableListOf()
    private var mLiveStatus: Long = -1L //直播状态
    // 资讯
    private var mNewsAdapter: MultiTypeAdapter? = null

    override fun initVM(): LiveDetailFragmentViewModel  = viewModels<LiveDetailFragmentViewModel>().value

    override fun initView() {
        // 关联视频
        setVideoVisible(false)
        // 直播简介
        mFragLiveDetailIntroTitleTv.isVisible = false
        mFragLiveDetailStartTimeTv.isVisible = false
        // 关联电影
        mFragLiveDetailMovieLayoutTitleTv.isVisible = false
        mFragLiveDetailMovieLayoutCv.isVisible = false
        mFragLiveDetailMovieRatingWantSeeLayout.isVisible = false
        mFragLiveDetailMovieLengthTypeTv.isVisible = false
        mFragLiveDetailMovieReleaseTv.isVisible = false
        // 购票
        mFragLiveDetailMovieTicketTv.isVisible = false
        // 相关资讯
        mFragLiveDetailNewsTitleTv.isVisible = false
        mFragLiveDetailNewsRv.isVisible = false

        setTitleBg(mFragLiveDetailShadowView)
        setTitleBg(mFragLiveDetailNewsTitleTv)

        mVideoAdapter = createMultiTypeAdapter(mFragLiveDetailVideoRv,
                LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false))
        mNewsAdapter = createMultiTypeAdapter(mFragLiveDetailNewsRv, LinearLayoutManager(mContext))

        initEvent()
    }

    /**
     * 初始化事件
     */
    private fun initEvent() {
        // 点击关联电影
        mFragLiveDetailMovieLayoutCv.onClick {
            mTicketProvider?.startMovieDetailsActivity(mMovieId)
        }
    }

    override fun initData() {
        val mDetailBean = (activity as? LiveDetailActivity)?.getDetailDataBean()
        mDetailBean?.let {
            it.movie?.let { movie ->
                mMovieId = movie.movieId
            }
            mLiveStatus = it.liveStatus
            if (mLiveStatus == LIVE_STATUS_APPOINT || mLiveStatus == LIVE_STATUS_REVIEW) {
                // 视频列表（直播前、回放显示）
                mVideos = it.video
                showVideoList()
            }
            // 直播信息
            showLiveIntro(it)
            // 显示关联电影
            showMovie(it)
            // 获取直播资讯列表
            mViewModel?.getLiveNews(it.liveId)
        }
    }

    override fun startObserve() {
        // observe资讯列表
        observeNewsList()
        // observe自动播放下一个视频
        observePlayNextVideo()
        // 切换直播状态
        observeLiveStatusChange()
    }

    /**
     * 切换直播状态
     */
    private fun observeLiveStatusChange() {
        LiveEventBus.get(LIVE_DETAIL_CHANGE_STATUS,Any::class.java)
                .observe(this,{
                    var mDetailBean = (activity as? LiveDetailActivity)?.getDetailDataBean()
                    mDetailBean?.let {
                        mVideos = null
                        mVideoSelectPosition = -1
                        mVideoBinderList.clear()
                        mLiveStatus = it.liveStatus
                        // 视频列表
                        if (mLiveStatus == LIVE_STATUS_APPOINT || mLiveStatus == LIVE_STATUS_REVIEW) {
                            // 预约、回放：显示
                            mVideos = it.video
                            if(mVideos.isNullOrEmpty()) {
                                setVideoVisible(false)
                            } else {
                                showVideoList()
                            }
                        } else {
                            // 直播中、直播结束：隐藏
                            setVideoVisible(false)
                        }
                    }
                })
    }

    /**
     * observe资讯列表
     */
    private fun observeNewsList() {
        mViewModel?.mNewsListUiState?.observe(this) {
            it.apply {

                success?.apply {
                    showNewsList(this)
                }

            }
        }
    }

    /**
     * observe自动播放下一个视频
     */
    private fun observePlayNextVideo() {
        if (mLiveStatus == LIVE_STATUS_APPOINT || mLiveStatus == LIVE_STATUS_REVIEW) {
            LiveEventBus.get(LIVE_DETAIL_PLAY_NEXT_VIDEO, com.kotlin.android.app.router.liveevent.event.LiveDetailVideoPlayState::class.java)
                    .observe(this, {
                        mVideos?.let { videos ->
                            var postion = -1
                            run flag@{
                                videos.mapIndexed { index, video ->
                                    if (video.videoId == it.videoId) {
                                        postion = index
                                        return@flag
                                    }
                                }
                            }
                            if (postion > -1) {
                                // 设置视频状态
                                setVideoStatus(postion)
                            }
                        }
                    })
        }
    }

    override fun destroyView() {
    }

    /**
     * 设置模块标题渐变底色
     */
    private fun setTitleBg(view: View) {
        ShapeExt.setGradientColorWithColor(view, GradientDrawable.Orientation.TOP_BOTTOM,
                getColor(R.color.color_f9f9fb), getColor(R.color.color_ffffff))
    }

    /**
     * 显示视频列表
     */
    private fun showVideoList() {
        mVideos?.let {
            if(it.isNotEmpty()) {
                setVideoVisible(true)
                mFragLiveDetailVideoTitleTv.text = getString(R.string.live_component_live_detail_video, it.size)
                it.map { video ->
                    var binder = LiveDetailVideoItemBinder(video)
                    binder.mClickCallBack = this
                    mVideoBinderList.add(binder)
                }
                mVideoAdapter?.notifyAdapterDataSetChanged(mVideoBinderList)
            }
        }
    }

    /**
     * 设置视频列表是否可见
     */
    private fun setVideoVisible(visible: Boolean) {
        mFragLiveDetailVideoTitleTv.isVisible = visible
        mFragLiveDetailVideoRv.isVisible = visible
    }

    /**
     * 点击视频item
     */
    override fun clickCallBack(bean: LiveDetail.Video, position: Int) {
        // 设置视频状态
        setVideoStatus(position)
        // 发送事件给播放器
        LiveEventBus.get(LIVE_DETAIL_CLICK_VIDEO).post(
            com.kotlin.android.app.router.liveevent.event.LiveDetailVideoPlayState(
                bean.videoId
            )
        )
    }

    /**
     * 设置视频状态
     */
    private fun setVideoStatus(position: Int) {
        // 取消之前选中的
        if(mVideoSelectPosition > -1) {
            setVideo(mVideoSelectPosition, false)
        }
        // 设置新选中的
        setVideo(position, true)
        mVideoSelectPosition = position
        // 显示完整
        mFragLiveDetailVideoRv.smoothScrollToPosition(position)
    }

    /**
     * 设置/取消视频选中状态
     */
    private fun setVideo(position: Int, isSelect: Boolean) {
        mVideos?.let {
            if(position > -1 && position < it.size) {
                var video: LiveDetail.Video? = it[position]
                video?.let { v ->
                    v.isSelect = isSelect
                    var binder = LiveDetailVideoItemBinder(v)
                    binder.mClickCallBack = this
                    mVideoBinderList[position] = binder
                    mVideoAdapter?.notifyItemChanged(position)
                }
            }
        }
    }

    /**
     * 显示直播信息
     */
    private fun showLiveIntro(bean: LiveDetail) {
        // 直播标题
        mFragLiveDetailIntroTitleTv.text = bean.title ?: ""
        mFragLiveDetailIntroTitleTv.isVisible = mFragLiveDetailIntroTitleTv.text.isNotEmpty()
        // 开始时间
        if(bean.startTime > 0) {
            mFragLiveDetailStartTimeTv.text =
                    getString(R.string.live_component_live_detail_start_time,
                            millis2String(bean.startTime * 1000,
                                    if(mLiveStatus == LIVE_STATUS_REVIEW)
                                        START_TIME_PATTERN_YMD else START_TIME_PATTERN_MD))
        }
        mFragLiveDetailStartTimeTv.isVisible = bean.startTime > 0
    }

    /**
     * 显示关联电影
     */
    private fun showMovie(bean: LiveDetail) {
        var showMovie = bean.movieIsShow == MOVIE_IS_SHOW
        mFragLiveDetailMovieLayoutTitleTv.isVisible = showMovie
        mFragLiveDetailMovieLayoutCv.isVisible = showMovie
        if(showMovie) {
            var showRating = false
            var showWantSeeCount = false
            var showLengthType = false
            var showRelease = false
            var ticketText =
                    when (bean.ticketStatus) {
                        LIVE_MOVIE_TICKET_STATUS_TICKET ->
                            getString(R.string.live_component_live_detail_movie_ticket)
                        LIVE_MOVIE_TICKET_STATUS_PRESELL ->
                            getString(R.string.live_component_live_detail_movie_presell)
                        else -> ""
                    }
            bean.movie?.let {
                // 电影名
                it.name?.let { name ->
                    mFragLiveDetailMovieNameTv.text = if (name.isEmpty()) {
                        it.nameEn ?: ""
                    } else {
                        it.name
                    }
                }
                // 封面图
                it.img?.let { url ->
                    loadImage(mFragLiveDetailMovieImgIv, url, MOVIE_IMG_WIDTH, MOVIE_IMG_HEIGHT)
                }
                // 评分
                showRating = it.overallRating > 0
                if (showRating) {
                    mFragLiveDetailMovieRatingTv.text = it.overallRating.toString()
                }
                // 想看数
                showWantSeeCount = it.wantCount > 0
                if (showWantSeeCount) {
                    mFragLiveDetailMovieWantSeeCountTv.text = formatCount(it.wantCount, false)
                }
                // 时长类型
                var length = it.mins ?: ""
                var types = if (length.isNotEmpty()) "—" else ""
                it.type?.let { typeArray ->
                    typeArray.take(MOVIE_TYPE_COUNT).map { type ->
                        types += type
                        types += "/"
                    }
                }
                if (types.endsWith("/")) {
                    types = types.dropLast(1)
                }
                showLengthType = length.isNotEmpty() || types.isNotEmpty()
                if (showLengthType) {
                    mFragLiveDetailMovieLengthTypeTv.text = length + types
                }
                // 上映
                var release = it.releaseDate ?: ""
                release += it.releaseArea ?: ""
                showRelease = release.isNotEmpty()
                if (showRelease) {
                    mFragLiveDetailMovieReleaseTv.text =
                            getString(R.string.live_component_live_detail_movie_release, release)
                }
                // 购票
                if (ticketText.isNotEmpty()) {
                        mFragLiveDetailMovieTicketTv.apply {
                            text = ticketText
                            // 背景色
                            if(bean.ticketStatus == LIVE_MOVIE_TICKET_STATUS_PRESELL) {
                                ShapeExt.setGradientColorWithColor(this,
                                        GradientDrawable.Orientation.TOP_BOTTOM,
                                        getColor(R.color.color_afd956),
                                        getColor(R.color.color_c0dc4d),
                                        27)
                            } else {
                                ShapeExt.setGradientColorWithColor(this,
                                        GradientDrawable.Orientation.BL_TR,
                                        getColor(R.color.color_20a0da),
                                        getColor(R.color.color_1bafe0),
                                        27)
                            }
                        }
                    }
            }

            mFragLiveDetailMovieRatingWantSeeLayout.isVisible = showRating || showWantSeeCount
            mFragLiveDetailMovieWantSeeSplitView.isVisible = showRating && showWantSeeCount
            mFragLiveDetailMovieRatingTipTv.isVisible = showRating
            mFragLiveDetailMovieRatingTv.isVisible = showRating
            mFragLiveDetailMovieWantSeeCountTv.isVisible = showWantSeeCount
            mFragLiveDetailMovieWantSeeCountTipTv.isVisible = showWantSeeCount
            mFragLiveDetailMovieLengthTypeTv.isVisible = showLengthType
            mFragLiveDetailMovieReleaseTv.isVisible = showRelease
            mFragLiveDetailMovieTicketTv.isVisible = ticketText.isNotEmpty()
        }
    }

    /**
     * 显示相关资讯
     */
    private fun showNewsList(list: LiveNewsList) {
        list?.news?.let {
            if(it.isNotEmpty()) {
                mFragLiveDetailNewsTitleTv.isVisible = true
                mFragLiveDetailNewsRv.isVisible = true
                var binderList: MutableList<MultiTypeBinder<*>> = mutableListOf()
                it.map { news ->
                    var binder = LiveDetailNewsItemBinder(news)
                    binderList.add(binder)
                }
                mNewsAdapter?.notifyAdapterAdded(binderList)
            }
        }
    }
}