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
 * description: ????????????fragment
 */
class LiveDetailFragment: BaseVMFragment<LiveDetailFragmentViewModel, FragmentLiveDetailBinding>(),
    LiveDetailVideoItemBinder.ILiveDetailClickCallBack {

    companion object {
        // ????????????????????????
        const val START_TIME_PATTERN_MD = "MM???dd??? HH:mm"
        const val START_TIME_PATTERN_YMD = "yyyy???MM???dd??? HH:mm"  // ?????????????????????
        // ????????????
        const val MOVIE_IS_SHOW = 1L
        const val MOVIE_IMG_WIDTH = 50
        const val MOVIE_IMG_HEIGHT = 75
        const val MOVIE_TYPE_COUNT = 3
    }

    private val mTicketProvider: ITicketProvider? = getProvider(ITicketProvider::class.java)

    private var mMovieId: Long = 0L
    // ??????
    private var mVideoAdapter: MultiTypeAdapter? = null
    private var mVideos: MutableList<LiveDetail.Video>? = null
    private var mVideoSelectPosition: Int = -1 // ????????????position
    private var mVideoBinderList: MutableList<MultiTypeBinder<ItemLiveDetailVideoBinding>> = mutableListOf()
    private var mLiveStatus: Long = -1L //????????????
    // ??????
    private var mNewsAdapter: MultiTypeAdapter? = null

    override fun initVM(): LiveDetailFragmentViewModel  = viewModels<LiveDetailFragmentViewModel>().value

    override fun initView() {
        // ????????????
        setVideoVisible(false)
        // ????????????
        mFragLiveDetailIntroTitleTv.isVisible = false
        mFragLiveDetailStartTimeTv.isVisible = false
        // ????????????
        mFragLiveDetailMovieLayoutTitleTv.isVisible = false
        mFragLiveDetailMovieLayoutCv.isVisible = false
        mFragLiveDetailMovieRatingWantSeeLayout.isVisible = false
        mFragLiveDetailMovieLengthTypeTv.isVisible = false
        mFragLiveDetailMovieReleaseTv.isVisible = false
        // ??????
        mFragLiveDetailMovieTicketTv.isVisible = false
        // ????????????
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
     * ???????????????
     */
    private fun initEvent() {
        // ??????????????????
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
                // ??????????????????????????????????????????
                mVideos = it.video
                showVideoList()
            }
            // ????????????
            showLiveIntro(it)
            // ??????????????????
            showMovie(it)
            // ????????????????????????
            mViewModel?.getLiveNews(it.liveId)
        }
    }

    override fun startObserve() {
        // observe????????????
        observeNewsList()
        // observe???????????????????????????
        observePlayNextVideo()
        // ??????????????????
        observeLiveStatusChange()
    }

    /**
     * ??????????????????
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
                        // ????????????
                        if (mLiveStatus == LIVE_STATUS_APPOINT || mLiveStatus == LIVE_STATUS_REVIEW) {
                            // ????????????????????????
                            mVideos = it.video
                            if(mVideos.isNullOrEmpty()) {
                                setVideoVisible(false)
                            } else {
                                showVideoList()
                            }
                        } else {
                            // ?????????????????????????????????
                            setVideoVisible(false)
                        }
                    }
                })
    }

    /**
     * observe????????????
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
     * observe???????????????????????????
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
                                // ??????????????????
                                setVideoStatus(postion)
                            }
                        }
                    })
        }
    }

    override fun destroyView() {
    }

    /**
     * ??????????????????????????????
     */
    private fun setTitleBg(view: View) {
        ShapeExt.setGradientColorWithColor(view, GradientDrawable.Orientation.TOP_BOTTOM,
                getColor(R.color.color_f9f9fb), getColor(R.color.color_ffffff))
    }

    /**
     * ??????????????????
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
     * ??????????????????????????????
     */
    private fun setVideoVisible(visible: Boolean) {
        mFragLiveDetailVideoTitleTv.isVisible = visible
        mFragLiveDetailVideoRv.isVisible = visible
    }

    /**
     * ????????????item
     */
    override fun clickCallBack(bean: LiveDetail.Video, position: Int) {
        // ??????????????????
        setVideoStatus(position)
        // ????????????????????????
        LiveEventBus.get(LIVE_DETAIL_CLICK_VIDEO).post(
            com.kotlin.android.app.router.liveevent.event.LiveDetailVideoPlayState(
                bean.videoId
            )
        )
    }

    /**
     * ??????????????????
     */
    private fun setVideoStatus(position: Int) {
        // ?????????????????????
        if(mVideoSelectPosition > -1) {
            setVideo(mVideoSelectPosition, false)
        }
        // ??????????????????
        setVideo(position, true)
        mVideoSelectPosition = position
        // ????????????
        mFragLiveDetailVideoRv.smoothScrollToPosition(position)
    }

    /**
     * ??????/????????????????????????
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
     * ??????????????????
     */
    private fun showLiveIntro(bean: LiveDetail) {
        // ????????????
        mFragLiveDetailIntroTitleTv.text = bean.title ?: ""
        mFragLiveDetailIntroTitleTv.isVisible = mFragLiveDetailIntroTitleTv.text.isNotEmpty()
        // ????????????
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
     * ??????????????????
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
                // ?????????
                it.name?.let { name ->
                    mFragLiveDetailMovieNameTv.text = if (name.isEmpty()) {
                        it.nameEn ?: ""
                    } else {
                        it.name
                    }
                }
                // ?????????
                it.img?.let { url ->
                    loadImage(mFragLiveDetailMovieImgIv, url, MOVIE_IMG_WIDTH, MOVIE_IMG_HEIGHT)
                }
                // ??????
                showRating = it.overallRating > 0
                if (showRating) {
                    mFragLiveDetailMovieRatingTv.text = it.overallRating.toString()
                }
                // ?????????
                showWantSeeCount = it.wantCount > 0
                if (showWantSeeCount) {
                    mFragLiveDetailMovieWantSeeCountTv.text = formatCount(it.wantCount, false)
                }
                // ????????????
                var length = it.mins ?: ""
                var types = if (length.isNotEmpty()) "???" else ""
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
                // ??????
                var release = it.releaseDate ?: ""
                release += it.releaseArea ?: ""
                showRelease = release.isNotEmpty()
                if (showRelease) {
                    mFragLiveDetailMovieReleaseTv.text =
                            getString(R.string.live_component_live_detail_movie_release, release)
                }
                // ??????
                if (ticketText.isNotEmpty()) {
                        mFragLiveDetailMovieTicketTv.apply {
                            text = ticketText
                            // ?????????
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
     * ??????????????????
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