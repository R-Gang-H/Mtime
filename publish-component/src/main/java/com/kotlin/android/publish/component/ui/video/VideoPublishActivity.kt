package com.kotlin.android.publish.component.ui.video

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.util.Range
import android.view.View
import androidx.core.view.get
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.flexbox.FlexboxLayoutManager
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener
import com.kotlin.android.app.api.upload.TencentUploadManager
import com.kotlin.android.app.data.annotation.*
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.entity.community.content.ContentInit
import com.kotlin.android.app.data.entity.community.record.Image
import com.kotlin.android.app.data.entity.community.record.PostContent
import com.kotlin.android.app.data.entity.community.record.ReObjs
import com.kotlin.android.app.data.entity.community.record.Videos
import com.kotlin.android.app.data.entity.search.Movie
import com.kotlin.android.app.data.entity.search.Person
import com.kotlin.android.app.data.entity.video.VideoPlayList
import com.kotlin.android.app.data.entity.video.VideoPlayUrl
import com.kotlin.android.app.data.ext.VariateExt
import com.kotlin.android.app.router.liveevent.event.CloseState
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.mine.IMineProvider
import com.kotlin.android.app.router.provider.search.ISearchProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.core.ext.showDialogFragment
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.image.component.*
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.keyboard.hideSoftInput
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.orFalse
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.ktx.ext.span.toTextSizeSpan
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.OrientationHelper
import com.kotlin.android.player.PlayerHelper
import com.kotlin.android.player.bean.MTimeVideoData
import com.kotlin.android.player.dataprovider.MTimeDataProvider
import com.kotlin.android.player.splayer.ISPayer
import com.kotlin.android.player.splayer.PreviewVideoPlayer
import com.kotlin.android.publish.component.Publish
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.bean.RelateMovieViewBean
import com.kotlin.android.publish.component.databinding.ActVideoPublishBinding
import com.kotlin.android.publish.component.ui.binder.RelateMovieBinder
import com.kotlin.android.publish.component.ui.binder.RelateMovieClassBinder
import com.kotlin.android.player.widgets.videodialog.VideoDialogFragment
import com.kotlin.android.player.widgets.videodialog.dismissVideoDialog
import com.kotlin.android.player.widgets.videodialog.showVideoDialog
import com.kotlin.android.publish.component.ui.selectedvideo.onSelectVideoResult
import com.kotlin.android.publish.component.widget.VideoUploadStateView
import com.kotlin.android.publish.component.widget.dialog.showPublishSuccessDialog
import com.kotlin.android.router.bus.ext.observe
import com.kotlin.android.router.bus.ext.post
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.search.newcomponent.Search
import com.kotlin.android.user.UserManager
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.dialog.BaseDialog
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.TitleBar

/**
 * create by lushan on 2022/3/29
 * des:视频发布
 **/
@Route(path = RouterActivityPath.Publish.PAGER_VIDEO_PUBLISH_ACTIVITY)
class VideoPublishActivity : BaseVMActivity<VideoPublishViewModel, ActVideoPublishBinding>(),
    OnReceiverEventListener , DialogInterface.OnDismissListener{
    private var contentId: Long = 0L//记录id
    private var recId: Long = 0L
    private var videoPath: String = ""//选中的视频id
    private var relateMovieAdapter: MultiTypeAdapter? = null
    private var movieClassAdapter: MultiTypeAdapter? = null
    private var titleBar: TitleBar? = null
    private val RELATE_MOVIE_MAX_COUNT = 10//最大关联电影数
    private val mSearchProvider by lazy {
        getProvider(ISearchProvider::class.java)
    }

    //视频播放器在activity中初始化，
    private var orientationHelper: OrientationHelper? = null
    private var videoFragment: VideoDialogFragment? = null
    private var mVideoId: Long = 0L//请求接口后获取的videoId
    private var uploadUrl: String = ""//上传到视频腾讯云后的视频地址
    private var isPublishing = false//是否是正在

    private val TITLE_MAX_LENGTH = 50//标题最大字数
    private val DES_MAX_LENGTH = 200//描述最大字数
    private var editBtn: CommunityContent.BtnShow? = null//编辑按钮

    /**
     * 视频播放数据提供类
     */
    private val videoDataProvider: MTimeDataProvider? = MTimeDataProvider {
        setVideoData()
    }

    private var videoBean: VideoPlayList? = null//视频播放实体类

    //视频封面
    private var videoImageBean: Image? = null//视频封面，重新上传后、加载内容详情的时候赋值

    override fun initVariable() {
        super.initVariable()
        contentId = intent?.getLongExtra(Publish.KEY_CONTENT_ID, 0).orZero()
        recId = intent?.getLongExtra(Publish.KEY_RECORD_ID, 0).orZero()
        videoPath = intent?.getStringExtra(Publish.KEY_VIDEO_PATH).orEmpty()
    }

    override fun initView() {
        initRecyclerView()
        initOptionTitleView()
        initListener()

    }

    override fun initTheme() {
        super.initTheme()
        setStatusBar()
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        mBinding?.titleBar?.addItem(
            drawableRes = R.drawable.ic_title_bar_back_light,
            reverseDrawableRes = R.drawable.ic_title_bar_back_dark
        ) {
            finish()
        }
            ?.setTitle(titleRes = if (isNew()) R.string.publish_component_video_publish else R.string.publish_component_video_edit)
            ?.addItem(//发布
                titleRes = R.string.publish,
                colorRes = R.color.white,
                bgDrawable = getShapeDrawable(
                    colorRes = R.color.color_20a0da,
                    cornerRadius = 13.dpF,
                ),
                textSize = 14F,
                titleWidth = 48.dp,
                titleHeight = 25.dp,
                isBold = true,
                isReversed = true,
                titlePaddingStart = 10.dp,
                titlePaddingEnd = 10.dp
            ) {//发布
                publishVideo(true)
            }
        if (isNew()) {
            addSaveDraftButton()
        }

    }

    private fun addSaveDraftButton() {
        if (editBtn == null || editBtn?.draftAble == null || editBtn?.draftAble == true) {
            mBinding?.titleBar?.addItem(//存草稿
                titleRes = R.string.publish_component_video_publish_save_draft,
                colorRes = R.color.color_8798af,
                textSize = 14f,
                isReversed = true,
                titleMarginEnd = 14.dp
            ) {//存草稿
                publishVideo(false)
            }
        }
    }

    private fun publishVideo(isPublish: Boolean, finish: Boolean = false) {
        //上传失败，或未上传视频，提示先上传视频
        if (mVideoId == 0L || TextUtils.isEmpty(uploadUrl)) {
            showToast(R.string.publish_component_please_upload_video_first)
            return
        }
        if (TextUtils.isEmpty(mBinding?.titleEt?.text?.trim())) {//为空进行提示
            showToast(R.string.publish_component_please_input_title)
            return
        }
        //需要判断一些必填字段是否填写
        if (isPublish) {
            //视频、封面、
            if (videoImageBean == null) {
                showToast(R.string.publish_component_please_upload_video_cover)
                return
            }

            if (movieClassAdapter?.getList()
                    ?.none { (it as? RelateMovieClassBinder)?.bean?.isSelected.orFalse() }.orFalse()
            ) {//视频分类
                showToast(R.string.publish_component_please_select_movie_class)
                return
            }
        }

        if (isPublish){
            if (isPublishing) return
            isPublishing = true
        }
        mViewModel?.postContent(buildPostContent(isPublish), isPublish, finish)
    }

    private fun initOptionTitleView() {
        val relateMovie =
            "${getString(R.string.publish_component_related_movie)}${getString(R.string.publish_component_optional)}"
        mBinding?.movieRelateTv?.text = relateMovie.toSpan().toTextSizeSpan(
            10,
            Range(getString(R.string.publish_component_related_movie).length, relateMovie.length)
        )

        val movieClass =
            "${getString(R.string.publish_component_class_select)}${getString(R.string.publish_component_required_optional)}"
        mBinding?.classSelectOptionTitleTv?.text = movieClass.toSpan().toTextSizeSpan(
            10,
            Range(getString(R.string.publish_component_class_select).length, movieClass.length)
        )

    }

    private fun loadVideoCover(path: String, isVideo: Boolean = false) {
        if (isVideo) {
            mBinding?.videoCoverIv?.loadImage(path, isLoadVideo = true, useProxy = false)
        } else {
            mBinding?.videoCoverIv?.loadImage(
                path,
                width = screenWidth - 30.dp,
                height = 186.dp
            )
        }

    }

    private fun initListener() {
        //选择相关电影
        mBinding?.selectMovieIv?.onClick {
            if ((relateMovieAdapter?.getList()?.size.orZero()) < RELATE_MOVIE_MAX_COUNT) {
                // 查询电影信息，生成电影卡片数据
                mSearchProvider?.startPublishSearchActivity(
                    activity = this,
                    searchType = SEARCH_MOVIE,
                    from = Search.PUBLISH_SEARCH_FROM_PUBLISH
                )

                // TODO: 搜索影人请用下面代码 by vivian.wei
//                            mSearchProvider?.startPublishSearchActivity(
//                                    activity = activity,
//                                    searchType = SEARCH_PERSON
//                            )

            } else {
                showToast(
                    getString(
                        R.string.publish_only_add_movie_at_most,
                        RELATE_MOVIE_MAX_COUNT
                    )
                )
            }
        }
        //编辑封面
        mBinding?.editCoverTv?.onClick {
            showDialogFragment(clazz = PhotoAlbumDialogFragment::class.java)?.apply {
                needUpload = false
                limitPhotoSelectCount = 1
                actionSuccessPhotos = { photos ->
                    photos.e()
                    if (photos.isNotEmpty()) {
                        //上传图片完成，进行裁剪
                        showPhotoCropDialog(
                            photos[0],
                            CommConstant.IMAGE_UPLOAD_COMMON,
                            CROP_TYPE_16_9,
                            callback = {
                                "图片上传完成：${it}".e()
                                videoImageBean = Image(
                                    imageId = it.fileID,
                                    imageUrl = it.url.orEmpty(),
                                    imageFormat = it.imageFormat
                                )
//                        加载裁剪后照片
                                loadVideoCover(it.url.orEmpty())
                            })

                    }

                }
            }

        }
        mBinding?.videoCoverIv?.onClick {
            //视频预览弹框，加载网络视频或加载本地视频
            initVideoView()
            videoBean?.apply {
                playVideo(this)
            }
        }

        //编辑title
        mBinding?.titleEt?.doOnTextChanged { text, start, before, count ->
            mBinding?.titleLengthTv?.text = "${mBinding?.titleEt?.text?.length.orZero()}/50"
        }
        //编辑描述
        mBinding?.videoDesEt?.doOnTextChanged { text, start, before, count ->
            mBinding?.desLengthTv?.text = "${mBinding?.videoDesEt?.text?.length.orZero()}/200"
        }

        mBinding?.stateView?.setMultiStateListener(object : MultiStateView.MultiStateListener {
            override fun onMultiStateChanged(viewState: Int) {
                if (viewState == MultiStateView.VIEW_STATE_NO_NET || viewState == MultiStateView.VIEW_STATE_ERROR) {
                    initData()
                }
            }
        })
        mBinding?.videoStateView?.onClick {
            if (mBinding?.videoStateView?.getState() == VideoUploadStateView.VIDEO_UPLOAD_STATE_FAILED) {
                mBinding?.videoStateView?.setState(VideoUploadStateView.VIDEO_UPLOAD_STATE_UPLOADING)
                mViewModel?.applyUpload(videoPath)
            }
        }

    }

    private fun initVideoView() {
        orientationHelper = OrientationHelper(this)
        orientationHelper?.keepOnScreen(this)
        orientationHelper?.sensorEnable(false)
        PreviewVideoPlayer.get()?.apply {
//            attachContainer(mBinding?.videoFl)
            addOnReceiverEventListener(this@VideoPublishActivity)
            updateAutoPlayFlag(false)
            setDataProvider(videoDataProvider)
            setReceiverGroupConfigState(
                this@VideoPublishActivity,
                ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_STATE
            )
            setShareVisibility(false)
            setOnBackRequestListener {
                onBackPressed()
            }
            videoFragment = showVideoDialog().apply {
            }
//            videoFragment?.dialog?.setOnDismissListener {
//                if (PreviewVideoPlayer.get()?.isLandScape(this@VideoPublishActivity) == true){
//                    orientationHelper?.toggleScreen()
//                }
//                PreviewVideoPlayer.get()?.logicPause()
//                setStatusBar()
//            }
        }

    }

    private var mDataSource = MTimeVideoData("", 0L, 0L, 0L)
    fun playVideo(bean: VideoPlayList) {
        mDataSource = MTimeVideoData("", 0, 0, 0L)
        videoDataProvider?.updateSourceData(0, 0)
        val isEqualData: Boolean = PreviewVideoPlayer.get()?.isEqualData(0) ?: false
        val isInPlaybackState: Boolean = PreviewVideoPlayer.get()?.isInPlaybackState() ?: false
        if (!isEqualData || !isInPlaybackState) {
            PreviewVideoPlayer.get()?.play(mDataSource, true)
        }
        videoDataProvider?.setVideoPlayUrlList(bean)
    }

    private fun setVideoData() {
        //本地视频和网络视频
        videoBean?.apply {
            videoDataProvider?.setVideoPlayUrlList(this)
        }
    }

    private fun initRecyclerView() {
        mBinding?.movieRelateRv?.apply {
            relateMovieAdapter =
                createMultiTypeAdapter(this, FlexboxLayoutManager(this@VideoPublishActivity))
            relateMovieAdapter?.setOnClickListener { view, binder ->
                updateSelectMovieIvVisible(getMovieListCount() - 1)
            }
        }
        mBinding?.movieClassRv?.apply {
            movieClassAdapter =
                createMultiTypeAdapter(this, FlexboxLayoutManager(this@VideoPublishActivity))
            movieClassAdapter?.setOnClickListener(::handleClassAction)
        }
    }

    private fun handleClassAction(view: View, multiTypeBinder: MultiTypeBinder<*>) {
        when (view.id) {
            R.id.classTv -> {
                movieClassAdapter?.getList()?.forEach {
                    if (it is RelateMovieClassBinder) {
                        if (multiTypeBinder is RelateMovieClassBinder && multiTypeBinder.bean.id == it.bean.id) {
                            it.updateSelected(true)
                        } else {
                            it.updateSelected(false)
                        }
                    }
                }
            }
        }
    }

    private fun isNew() = contentId == 0L

    override fun initData() {
        if (isNew().not()) {//如果不是新发布，需要请求记录接口
            mViewModel?.loadRecordData(contentId, recId)
        } else {
            videoBean = VideoPlayList(arrayListOf(VideoPlayUrl(url = videoPath)))
            mViewModel?.loadClassifiesData()
            //加载视频第一帧图片
            loadVideoCover(videoPath, true)
            mBinding?.videoStateView?.setState(VideoUploadStateView.VIDEO_UPLOAD_STATE_UPLOADING)
            //上传缩略图
            mViewModel?.uploadPhoto(videoPath)
        }
    }

    override fun onStart() {
        super.onStart()
        orientationHelper?.enable()
    }

    override fun onStop() {
        super.onStop()
        orientationHelper?.disable()
    }

    override fun onPause() {
        super.onPause()
        PreviewVideoPlayer.get()?.logicPause()
    }

    override fun onDestroy() {
        PreviewVideoPlayer.get()?.removeReceiverEventListener(this)
        orientationHelper?.destroy()
//        PreviewVideoPlayer.get()?.destroy()
        super.onDestroy()
    }

    override fun startObserve() {
        movieClassObserve()
        detailObserve()
        uploadObserve()
        publishObserve()
        photoObserve()
        closeObserve()
    }

    private fun closeObserve() {
        observe(CloseState::class.java, Observer {
            finish()
        })
    }

    private fun photoObserve() {
        mViewModel?.uploadPhotoState?.observe(this, Observer {
            it?.apply {
                success?.apply {
                    videoImageBean = Image(
                        imageId = this.fileID,
                        imageUrl = this.url.orEmpty(),
                        imageFormat = this.imageFormat
                    )
//                        加载裁剪后照片
                    loadVideoCover(this.url.orEmpty())
                }
            }
        })
    }

    private fun publishObserve() {
        mViewModel?.saveContentState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                isPublishing = showLoading
                success?.apply {
                    if (extend.first) {//发布
                        if (result.isSuccess()) {//发布成功
                            //显示发布成功弹框
                            showPublishSuccessDialog(getString(R.string.publish_component_video_publish_success)) {
                                //跳转到我的内容页
                                getProvider(IMineProvider::class.java) {
                                    startMyContent(this@VideoPublishActivity, CONTENT_TYPE_VIDEO)
                                }

                                CloseState().post()
                            }
                        } else {//发布失败
                            if (TextUtils.isEmpty(result.bizMsg).not()) {
                                showToast(result.bizMsg.orEmpty())
                            } else {
                                showToast(R.string.publish_fail)
                            }
                        }
                    } else {//保存草稿箱
                        if (result.isSuccess()) {//保存草稿成功
                            contentId = result.contentId
                            if (extend.second) {//是否退出当前页面
                                CloseState().post()
                            } else {
                                showToast(R.string.publish_component_drawft_had_saved)
                            }
                        } else {//保存草稿失败
                            if (TextUtils.isEmpty(result.bizMsg).not()) {
                                showToast(result.bizMsg.orEmpty())
                            } else {
                                showToast(R.string.publish_component_drawft_save_failed)
                            }
                        }
                    }
                }
                error?.showToast()
                netError?.showToast()
            }
        })
    }


    private fun uploadToTencent(videoId: Long, token: String) {
        mVideoId = videoId
        TencentUploadManager.upload(
            videoPath,
            token.orEmpty()
        ) { complete, isSuccess, progress, tVid, videoUrl, _ ->
            if (complete) {//上传完成
                if (isSuccess) {
                    mBinding?.videoStateView?.setProgress(100)
                    mViewModel?.completeUpload(videoId, tVid, videoUrl)
                    uploadUrl = videoUrl
                } else {
                    uploadFailed()
                }

            } else {
                mBinding?.videoStateView?.setProgress((progress * 100).toInt())
            }

        }
    }

    private fun uploadObserve() {
        //发起上传
        mViewModel?.applyUploadState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (this.bizCode == 0L) {
                        uploadToTencent(videoId, token.orEmpty())
                    } else {
                        uploadFailed()
                    }
                }
                error?.apply {
                    uploadFailed()
                }
                netError?.apply {
                    uploadFailed()
                }
            }
        })

        //视频上传完成
        mViewModel?.completeState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    mBinding?.editCoverTv?.visible(isSuccess())
                    if (isSuccess()) {
                        //上传成功
                        mBinding?.videoStateView?.setState(VideoUploadStateView.VIDEO_UPLOAD_STATE_INIT)
                    } else {
                        uploadFailed()
                    }
                }
                error?.apply {
                    uploadFailed()
                }
                netError?.apply {
                    uploadFailed()
                }
            }
        })
    }

    private fun uploadFailed() {
        mVideoId = 0L
        uploadUrl = ""
        mBinding?.videoStateView?.setState(VideoUploadStateView.VIDEO_UPLOAD_STATE_FAILED)
    }

    private fun setStatusBar() {
        immersive().statusBarColor(getColor(R.color.color_ffffff)).statusBarDarkFont(true)
    }

    override fun onBackPressed() {
        if (videoFragment?.isVisible == true) {
            if (PreviewVideoPlayer.get()?.isLandScape(this) == true){
                orientationHelper?.toggleScreen()
                return
            }
            dismissVideoDialog()
            setStatusBar()
            PreviewVideoPlayer.get()?.logicPause()
        } else {
//            if (mVideoId != 0L && TextUtils.isEmpty(uploadUrl).not()) {
//                //显示是否保存草稿箱弹框
//                showSaveDrawftDialog()
//            } else {
//            }
            super.onBackPressed()
        }
    }

    private fun showSaveDrawftDialog() {
        BaseDialog.Builder(this).setContent(R.string.publish_save_draft)
            .setPositiveButton(R.string.ok) { dialog, id ->
                publishVideo(false, true)
            }.setNegativeButton(R.string.cancel) { dialog, id ->
                dialog?.dismiss()
                finish()
            }.create().show()
    }

    private fun detailObserve() {
        mViewModel?.videoRecordState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    (this[1] as? CommunityContent)?.let { content ->
                        //如果有封面图显示封面图，如果没有封面图显示视频url
                        if (content.covers?.isNotEmpty() == true) {
                            val cover = content.covers?.get(0)
                            loadVideoCover(cover?.imageUrl.orEmpty())
                            videoImageBean = Image(
                                imageId = cover?.imageId.orEmpty(),
                                imageUrl = cover?.imageUrl.orEmpty(),
                                imageFormat = cover?.imageFormat.orEmpty(),
                                imageDesc = cover?.imageDesc.orEmpty()
                            )
                        } else {
                            loadVideoCover(content.video?.posterUrl.orEmpty(), true)
                        }
                        mBinding?.editCoverTv?.visible(true)
                        mVideoId = content.video?.videoId.orZero()
                        if (content.video?.videoResolutions?.isNotEmpty().orFalse()) {
                            uploadUrl = content.video?.videoResolutions?.get(0)?.url.orEmpty()
                        }
                        editBtn = content.creatorAuthority?.btnEdit

                        videoBean = VideoPlayList(content.video?.videoResolutions)
                        //标题
                        mBinding?.titleEt?.setText(content.title.orEmpty())
                        //描述
                        mBinding?.videoDesEt?.setText(content.body.orEmpty())
                        //相关电影
                        relateMovieAdapter?.notifyAdapterDataSetChanged(
                            content.reObjs?.filter { reObj -> reObj.roMovie != null }
                                ?.map { reObjs ->
                                    RelateMovieBinder(
                                        RelateMovieViewBean(
                                            reObjs.roMovie?.id.orZero(),
                                            if (TextUtils.isEmpty(reObjs.roMovie?.name)) reObjs.roMovie?.nameEn.orEmpty() else reObjs.roMovie?.name.orEmpty()
                                        )
                                    )
                                }
                        ){
                            updateSelectMovieIvVisible(getMovieListCount())
                        }

                        //选择分类
                        (this[0] as? ContentInit)?.apply {
                            movieClassAdapter?.notifyAdapterDataSetChanged(this.classifies?.map { value ->
                                RelateMovieClassBinder(
                                    RelateMovieViewBean(
                                        value.key,
                                        value.value.orEmpty(),
                                        content.classifies?.contains(value.key).orFalse()
                                    )
                                )
                            }?.toMutableList())
                            addSaveDraftButton()//判断是否显示存草稿按钮
                        }


                    } ?: mBinding?.stateView?.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }
                netError?.apply {
                    mBinding?.stateView?.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }
                error?.apply {
                    mBinding?.stateView?.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }
            }
        })
    }


    private fun movieClassObserve() {
        mViewModel?.classState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    val isNotEmpty = this.classifies?.isNotEmpty().orFalse()
                    mBinding?.classSelectOptionTitleTv?.visible(isNotEmpty)
                    mBinding?.movieClassRv?.visible(isNotEmpty)
                    mBinding?.relatedMovieLine?.visible(isNotEmpty)
                    movieClassAdapter?.notifyAdapterDataSetChanged(this.classifies?.map { value ->
                        RelateMovieClassBinder(
                            RelateMovieViewBean(value.key, value.value.orEmpty())
                        )
                    }?.toMutableList())
                    //只有是新发布的时候才走此逻辑
                    mViewModel?.applyUpload(videoPath)
                }
                netError?.apply {
                    mBinding?.stateView?.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }
                error?.apply {
                    mBinding?.stateView?.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 添加电影
        if (resultCode == Search.SEARCH_MOVIE_RESULT_CODE) {
            addMovie(data)
            return
        }
        // 添加影人
        if (resultCode == Search.SEARCH_PERSON_RESULT_CODE) {
            addPerson(data)
            return
        }
        // 视频回调
        onSelectVideoResult(requestCode, resultCode, data)
    }

    private fun addMovie(data: Intent?) {
        data?.apply {
            val movie = getSerializableExtra(Search.KEY_SEARCH_DATA_MOVIE) as? Movie
            movie?.apply {
                val list = relateMovieAdapter?.getList()
                if (list?.isNotEmpty().orFalse() && list?.filter { it is RelateMovieBinder }
                        ?.filter { (it as? RelateMovieBinder)?.bean?.id == this.movieId }
                        ?.isNotEmpty()
                        .orFalse()
                ) {
                    return
                }
                updateSelectMovieIvVisible(getMovieListCount() + 1)
                relateMovieAdapter?.notifyAdapterAdded(
                    RelateMovieBinder(
                        RelateMovieViewBean(
                            id = this.movieId.orZero(),
                            name = if (TextUtils.isEmpty(name)) nameEn.orEmpty() else name.orEmpty()
                        )
                    )
                )
            }
        }
    }

    private fun getMovieListCount(): Int = relateMovieAdapter?.getList()?.size.orZero()
    private fun updateSelectMovieIvVisible(count: Int) {
        mBinding?.selectMovieIv?.visible(count < RELATE_MOVIE_MAX_COUNT)//最多选择十部电影
    }

    /**
     * 添加影人
     */
    private fun addPerson(data: Intent?) {
        data?.apply {
            val person = getSerializableExtra(Search.KEY_SEARCH_DATA_PERSON) as? Person
            person?.apply {
                showToast(this.name)
//                    addPersonItem(this)
            }
        }
    }

    private fun buildPostContent(isPublish: Boolean): PostContent {
        var covers = mutableListOf<Image>()
        videoImageBean?.apply {
            covers.add(this)
        }
        val title = if (mBinding?.titleEt?.text?.trim() == null) {
            ""
        } else {
            mBinding?.titleEt?.text?.trim().toString()
        }
        val des = if (mBinding?.videoDesEt?.text?.trim() == null) {
            ""
        } else {
            mBinding?.videoDesEt?.text?.trim().toString()
        }
        return PostContent(
            saveAction = if (isPublish) 2L else 1L,//1草稿 2发布
            contentId = if (contentId <= 0) null else contentId,
            title = title,
            author = UserManager.instance.nickname,
            type = CONTENT_TYPE_VIDEO,
            classifies = movieClassAdapter?.getList()
                ?.filter { (it as? RelateMovieClassBinder)?.bean?.isSelected.orFalse() }
                ?.map { (it as? RelateMovieClassBinder)?.bean?.id.orZero() }?.toMutableList(),
            body = des,
            video =
            Videos(
                mVideoId, videoImageBean?.imageUrl.orEmpty(),
                VIDEO_SOURCE_MEDIA_VIDEO, uploadUrl
            ),
            covers = covers,//视频封面
            reObjs = relateMovieAdapter?.getList()?.map {
                val bean = (it as? RelateMovieBinder)?.bean
                ReObjs(bean?.id.orZero(), RELATION_TYPE_MOVIE)
            }?.toMutableList()
        )
    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            DataInter.ReceiverEvent.EVENT_REQUEST_TOGGLE_SCREEN_STATE -> {
                orientationHelper?.toggleScreen()
                "横竖屏切换".e()
//                videoFragment?.dismissAllowingStateLoss()
//                PreviewVideoPlayer.get()?.logicPause()
//                setStatusBar()
            }
            DataInter.ReceiverEvent.EVENT_REQUEST_BACK -> {
                setStatusBar()
            }
            DataInter.ReceiverEvent.EVENT_CODE_ERROR_FEED_BACK -> {
//                showToast("反馈错误")
                getProvider(IUgcProvider::class.java) {
                    launchDetail(VariateExt.feedbackPostId, CommConstant.PRAISE_OBJ_TYPE_POST)
                }
            }


        }
    }

    private var isFullScreen = false
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        hideSoftInput()
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            isFullScreen = false
//            PlayerHelper.portraitMatchWidth_16_9(this, videoView, null)
//            恢复状态栏颜色
//            immersive().statusBarColor(
//                albumTitleView?.getTitleBackgroundColor() ?: getColor(R.color.transparent)
//            )
//            notifyAdapterData()
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//状态栏设置为透明色
            isFullScreen = true
//            PlayerHelper.landscapeMatchWidthHeight(this, videoView, null)
            immersive().statusBarColor(getColor(R.color.transparent))
        }
//        handleVideoMarginTop(isFullScreen)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        if (PreviewVideoPlayer.get()?.isLandScape(this@VideoPublishActivity) == true){
                    orientationHelper?.toggleScreen()
        }
        PreviewVideoPlayer.get()?.logicPause()
        setStatusBar()
    }

}