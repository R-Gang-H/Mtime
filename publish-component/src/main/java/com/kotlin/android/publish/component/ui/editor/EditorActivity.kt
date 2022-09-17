package com.kotlin.android.publish.component.ui.editor

import android.content.Intent
import android.graphics.Color
import android.view.ViewTreeObserver
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.annotation.*
import com.kotlin.android.app.data.entity.common.MovieSubItemRating
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.entity.community.publish.Group
import com.kotlin.android.app.data.entity.community.publish.RecommendType
import com.kotlin.android.app.data.entity.community.record.PostContent
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.app.data.entity.movie.LatestComment
import com.kotlin.android.app.data.entity.search.Article
import com.kotlin.android.app.data.entity.search.Movie
import com.kotlin.android.app.data.entity.search.Person
import com.kotlin.android.app.router.ext.openCommunity
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.mine.IMineProvider
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.app.router.provider.review.IReviewProvider
import com.kotlin.android.app.router.provider.search.ISearchProvider
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.bonus.scene.component.postPublishPost
import com.kotlin.android.bonus.scene.component.postPublishReview
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.core.ext.showDialogFragment
import com.kotlin.android.image.component.PhotoAlbumDialogFragment
import com.kotlin.android.image.component.getPhotoAlbumFragment
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.keyboard.setKeyboardListener
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.ktx.ext.statelist.getColorStateList
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.publish.component.Publish
import com.kotlin.android.publish.component.Publish.MAX_ADD_IMAGE_LIMIT
import com.kotlin.android.publish.component.Publish.MAX_ADD_MOVIE_LIMIT
import com.kotlin.android.publish.component.Publish.MAX_ADD_VIDEO_LIMIT
import com.kotlin.android.publish.component.Publish.MAX_ARTICLE_TEXT_COUNT_LIMIT
import com.kotlin.android.publish.component.Publish.MAX_FILM_COMMENT_TEXT_COUNT_LIMIT
import com.kotlin.android.publish.component.Publish.MAX_GROUP_COUNT_LIMIT
import com.kotlin.android.publish.component.Publish.MAX_LONG_COMMENT_TEXT_COUNT_LIMIT
import com.kotlin.android.publish.component.Publish.MAX_TITLE_TEXT_COUNT_LIMIT
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.databinding.ActEditorBinding
import com.kotlin.android.publish.component.scope.ContentInitScope
import com.kotlin.android.publish.component.ui.selectedvideo.onSelectVideoResult
import com.kotlin.android.publish.component.ui.selectedvideo.showVideoListDialog
import com.kotlin.android.publish.component.widget.article.footer.EditorFooterArticleView
import com.kotlin.android.publish.component.widget.article.xml.XmlParser
import com.kotlin.android.publish.component.widget.dialog.EditorImagesDialog
import com.kotlin.android.publish.component.widget.dialog.EditorInputLinkDialog
import com.kotlin.android.publish.component.widget.dialog.showPublishSuccessDialog
import com.kotlin.android.publish.component.widget.editor.EditorMenuAddView
import com.kotlin.android.publish.component.widget.selector.LocalMedia
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.search.newcomponent.Search
import com.kotlin.android.user.UserManager
import com.kotlin.android.widget.titlebar.*
import com.kotlin.android.widget.titlebar.item.EditorCenterTitleView

/**
 * 编辑器
 *
 * Created on 2022/4/6.
 *
 * @author o.s
 */
@Route(path = RouterActivityPath.Publish.PAGER_EDITOR_ACTIVITY)
class EditorActivity : BaseVMActivity<EditorViewModel, ActEditorBinding>() {

    private val mProvider by lazy {
        getProvider(IPublishProvider::class.java)
    }

    private val mSearchProvider by lazy {
        getProvider(ISearchProvider::class.java)
    }

    private val mTicketProvider by lazy {
        getProvider(ITicketProvider::class.java)
    }

    private var titleBar: TitleBar? = null
    private val centerView by lazy {
        EditorCenterTitleView(this).apply {
            action = {
                mTicketProvider?.startMovieDetailsActivity(mMovieId)
            }
        }
    }

    private var mLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    private var _style: EditorStyle = EditorStyle.ARTICLE

    /**
     * 编辑器样式，文章
     */
    var style: EditorStyle
        // = EditorStyle.ARTICLE
        get() = _style
        set(value) {
            _style = value
            titleBar?.post {
                notifyChangeStyle()
            }
        }

    private fun syncStyle() {
        style = when (mContentType) {
            CONTENT_TYPE_JOURNAL -> EditorStyle.JOURNAL
            CONTENT_TYPE_POST -> EditorStyle.POST
            CONTENT_TYPE_FILM_COMMENT -> EditorStyle.FILM_COMMENT
            CONTENT_TYPE_ARTICLE -> EditorStyle.ARTICLE
            CONTENT_TYPE_VIDEO -> EditorStyle.VIDEO
            CONTENT_TYPE_AUDIO -> EditorStyle.AUDIO
            else -> EditorStyle.JOURNAL
        }
    }

    /**
     * 内容类型 [ContentType]
     */
    @ContentType
    var mContentType: Long = CONTENT_TYPE_JOURNAL

    private var isFootmarks: Boolean = false // 来源于种草
    private var isLongComment: Boolean = false // 来源于长影评
        set(value) {
            field = value
            if (isFilmComment) {
                mBinding?.titleLayout?.isVisible = value
                mBinding?.divideView1?.isVisible = value
                mBinding?.filmRatingView?.isLongComment = value
                displaySubTitle(currentTextCount)
            }
        }

    /**
     * 内容ID不为null，则表示需要编辑
     */
    private var mContentId: Long? = null
    private var mDraftContentId: Long? = null // 保存草稿时返回contentId，再次保存时回带给接口
    private var mMovieId = 0L
    private var mMovieName = ""
    private var mFamilyId: Long? = null
    private var mSectionId: Long? = null // 编辑接口返回的加载分类ID

    //    private var mFamilyName = ""
    private var mRecId: Long? = null // 发布转编辑时，由上游页面或存草稿content.api接口返回，需要在发布时带入该参数。
    private var mIsShowDraft: Boolean = true // 是否显示保存草稿，默认显示。在编辑时，需要根据接口字段调整状态。

    /**
     * 编辑状态
     */
    private var isEditorState = false

    /**
     * 是否影评
     */
    private val isFilmComment: Boolean
        get() = CONTENT_TYPE_FILM_COMMENT == mContentType

    /**
     * 保存动作1：草稿 2：发布
     */
    private var mSaveAction: Long = 0L

    /**
     * 是否添加关联影片/影人
     */
    private var isRelation: Boolean = false

    /**
     * 处理影评相关：
     * 电影名称、长短影评
     */
    private var mContent: CommunityContent? = null
        set(value) {
            field = value
            isLongComment = value?.fcType == LONG_COMMENT
            syncSave()
            if (mMovieName.isEmpty()) {
                val nameCN = value?.fcMovie?.name
                val nameEN = value?.fcMovie?.nameEn
                mMovieName = if (nameCN.isNullOrEmpty()) {
                    nameEN.orEmpty()
                } else {
                    nameCN.orEmpty()
                }
                updateTitle()
            }
        }

    /**
     * 最新评论
     */
    private var latestComment: LatestComment? = null
        set(value) {
            field = value
//            syncSave(isEnabled = mRating > 0 || mCommentId > 0)
            syncCommentDelete(isShow = mRating > 0 || mCommentId > 0)
        }

    /**
     * 回显评分
     */
    private val mRating: Double
        get() = latestComment?.userInfo?.rating ?: 0.0

    /**
     * 回显分项评分
     */
    private val mSubRatings: List<MovieSubItemRating>?
        get() = latestComment?.userInfo?.userMovieSubItemRatings

    /**
     * 评论ID
     */
    private val mCommentId: Long
        get() = if (isFilmComment) {
            val longCommentId = latestComment?.longComment?.commentId
            if (!isLongComment) {
                isLongComment = longCommentId != null
            }
            longCommentId ?: latestComment?.shortComment?.commentId ?: 0L
        } else {
            0L
        }

    /**
     * 添加图片限制
     */
    private val addImageLimit: Int
        get() = mBinding?.editorLayout?.imageLimit ?: MAX_ADD_IMAGE_LIMIT

    /**
     * 添加视频限制
     */
    private val addVideoLimit: Int
        get() = mBinding?.editorLayout?.videoLimit ?: MAX_ADD_VIDEO_LIMIT

    /**
     * 添加电影限制
     */
    private val addMovieLimit: Int
        get() = mBinding?.editorLayout?.movieLimit ?: MAX_ADD_MOVIE_LIMIT

    /**
     * 当前文本字数
     */
    private var currentTextCount = 0
        set(value) {
            field = value
            isContentReady = if (isFilmComment) {
                if (isLongComment) {
                    value > 0
                } else {
                    value in 1..MAX_FILM_COMMENT_TEXT_COUNT_LIMIT
                }
            } else {
                value in 1..MAX_ARTICLE_TEXT_COUNT_LIMIT
            }
        }

    private var isTitleReady = false
        set(value) {
            if (field != value) {
                field = value
                syncTitleBar()
            }
        }

    private var isContentReady = false
        set(value) {
            if (field != value) {
                field = value
                syncTitleBar()
            }
        }

    private var isGroupReady = true
        set(value) {
            if (field != value) {
                field = value
                syncTitleBar()
            }
        }

    private var isRatingReady = false
        set(value) {
            if (field != value) {
                field = value
                syncTitleBar()
            }
        }

    private fun syncTitleBar() {
        val isEnable = when (mContentType) {
            CONTENT_TYPE_JOURNAL -> {
                isTitleReady && isContentReady
            }
            CONTENT_TYPE_POST -> {
                isTitleReady && isContentReady && isGroupReady
            }
            CONTENT_TYPE_FILM_COMMENT -> {
                if (isLongComment) {
                    isTitleReady && isContentReady && isRatingReady
                } else {
                    isRatingReady
                }
            }
            CONTENT_TYPE_ARTICLE -> {
                isTitleReady && isContentReady
            }
            else -> {
                false
            }
        }
        if (CONTENT_TYPE_FILM_COMMENT == mContentType) {
//            syncSave(isEnabled = isRatingReady || isContentReady || isTitleReady)
            syncCommentDelete(isShow = isRatingReady || isContentReady || isTitleReady)
            if (!isLongComment) {
                syncSave(isEnabled = false)
            } else {
                syncSave(isEnabled = isEnable)
            }
        } else {
            syncSave(isEnabled = isEnable)
        }
        syncPublish(isEnabled = isEnable)
    }

    override fun initTheme() {
        super.initTheme()
        immersive().transparentStatusBar()
            .statusBarDarkFont(true)
    }

    override fun getIntentData(intent: Intent?) {
        super.getIntentData(intent)
        intent?.apply {
            mContentType = getLongExtra(Publish.KEY_TYPE, 0L)
            mRecId = getLongExtra(Publish.KEY_RECORD_ID, 0L)
            isFootmarks = getBooleanExtra(Publish.KEY_IS_FOOTMARKS, false)
            isLongComment = getBooleanExtra(Publish.KEY_IS_LONG_COMMENT, false)
            mContentId = getLongExtra(Publish.KEY_CONTENT_ID, 0L)
            mMovieId = getLongExtra(Publish.KEY_MOVIE_ID, 0L)
            mMovieName = getStringExtra(Publish.KEY_MOVIE_NAME).orEmpty()
            mFamilyId = getLongExtra(Publish.KEY_FAMILY_ID, 0L)
//            mFamilyName = getStringExtra(Publish.KEY_FAMILY_NAME).orEmpty()

            mContentId = if (mContentId == 0L) null else mContentId
            mRecId = if (mRecId == 0L) null else mRecId
            mFamilyId = if (mFamilyId == 0L) null else mFamilyId
            isEditorState = mContentId != null
        }
    }

    override fun initNewData() {
//        syncData()
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        titleBar = TitleBarManager.with(this, ThemeStyle.STANDARD_STATUS_BAR)
            .addCenterView(centerView)
            .back {
                finish()
            }
            .addItem(
                isReversed = true,
                title = "发布",
                titleHeight = 26.dp,
                titleMarginEnd = 7.dp,
                isBold = true,
                colorState = getColorStateList(
                    normalColorRes = R.color.color_ffffff,
                    pressColorRes = R.color.color_ffffff,
                    disableColorRes = R.color.color_ffffff // color_8798af // color_aab7c7 // color_8a9199
                ),
                titlePaddingStart = 10.dp,
                titlePaddingEnd = 10.dp,
                bgDrawable = getDrawableStateList(
                    normal = getShapeDrawable(
                        colorRes = R.color.color_20a0da,
                        cornerRadius = 13.dpF
                    ),
                    pressed = getShapeDrawable(
                        colorRes = R.color.color_004696,
                        cornerRadius = 13.dpF
                    ),
                    disable = getShapeDrawable(
                        colorRes = R.color.color_6620a0da,
                        cornerRadius = 13.dpF
                    ),
                )
            ) {
                mSaveAction = 2L
                publish()
            }
            .addItem(
                isReversed = true,
                title = "存草稿",
                titleMarginEnd = 14.dp,
                colorState = getColorStateList(
                    normalColorRes = R.color.color_20a0da,
                    pressColorRes = R.color.color_004696,
                    disableColorRes = R.color.color_cbd0d7
                ),
            ) {
                mSaveAction = 1L
                publish()
            }
    }

    override fun initView() {
        initMenuView()
        initTipsView()
        initTitleView()
        initEditorView()
        initFooterJournalView()
        initFooterPostView()
        initFilmRatingView()
        initFooterArticleView()
        initFooterView()
    }

    override fun initData() {
//        XmlParser.parseXml(body = TestData.xmlData1) {
//            mBinding?.editorLayout?.body = it
//        }
        if (mMovieId != 0L && mMovieName.isEmpty()) {
            ContentInitScope.instance.getMovieName(movieId = mMovieId) {
                // 处理没有传过来电影名称的情况
                mMovieName = this
                updateTitle()
            }
        }
        if (isEditorState) {
            mContentId?.apply {
                mViewModel?.getContent(
                    type = mContentType,
                    contentId = this,
                    recId = mRecId
                )
            }
        } else {
            mBinding?.editorLayout?.autoAddItemWithText()
        }
        syncStyle()
        syncSave(isEnabled = false)
        syncPublish(isEnabled = false)

        when (mContentType) {
            CONTENT_TYPE_FILM_COMMENT -> {
                mViewModel?.getMovieRating(movieId = mMovieId)
            }
        }
    }

    override fun startObserve() {
        initContentObserve()
        initGroupObserve()
        initRelationObserve()
        initFilmCommentObserve()
    }

    override fun onDestroy() {
        super.onDestroy()
        mLayoutListener?.apply {
            window.decorView.viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
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
        // 选择家族回调
        if (resultCode == Publish.FAMILY_LIST_RESULT_CODE) {
            addGroup(data)
            return
        }
        // 相册回调
        getPhotoAlbumFragment()?.onActivityResult(requestCode, resultCode, data)
        // 视频回调
        onSelectVideoResult(requestCode, resultCode, data)
    }

    /**
     * 同步 存草稿 / 删除 是否可用
     */
    private fun syncSave(isEnabled: Boolean = true) {
        if (!isEnabled && isFilmComment && !isLongComment) {
            showSave(isShow = false)
        } else {
            showSave(isShow = mIsShowDraft)
            titleBar?.updateEnable(
                isReversed = true,
                index = 0,
                isEnabled = isEnabled
            )
        }
    }

    /**
     * 是否显示存草稿
     */
    private fun showSave(isShow: Boolean = true) {
        titleBar?.updateVisibility(
            isReversed = true,
            index = 0,
            isShow = isShow
        )
    }

    /**
     * 同步 发布 是否可用
     */
    private fun syncPublish(isEnabled: Boolean = true) {
        titleBar?.updateEnable(
            isReversed = true,
            index = 1,
            isEnabled = isEnabled
        )
    }

    /**
     * 同步影评删除按钮
     */
    private fun syncCommentDelete(isShow: Boolean) {
        mBinding?.deleteCommentView?.isVisible = isShow
    }

    private fun addMovieCard(movie: Movie) {
        "添加影片:$movie".e()
        mBinding?.editorLayout?.addMovieCard(movie = movie)
    }

    private fun addImageCard(photoInfo: PhotoInfo) {
        "添加图片:$photoInfo".e()
        mBinding?.editorLayout?.addImageCard(photoInfo = photoInfo)
    }

    private fun addImageCards(photos: List<PhotoInfo>) {
        "添加图片列表:$photos".e()
        mBinding?.editorLayout?.addImageCards(photos = photos)
    }

    private fun addVideoCard(data: LocalMedia) {
        "添加视频:$data".e()
        mBinding?.editorLayout?.addVideoCard(data = data)
    }

    private fun addTextCard(text: CharSequence = "") {
        "添加文本:$text".e()
        mBinding?.editorLayout?.addTextCard(text = text)
    }

    private fun addLink(title: CharSequence, url: CharSequence) {
        "添加超链接:$title -> $url".e()
        mBinding?.editorLayout?.currentTextItemLayout?.textCard?.addLink(title = title, url = url)
    }

    /**
     * 设置家族
     */
    private fun setGroupLabels(groups: List<Group>) {
        val first = groups.find { it.groupId == mFamilyId }
        val groupList = ArrayList<Group>()
        first?.apply {
            groupList.add(first)
        }
        groups.forEach {
            if (groupList.size < MAX_GROUP_COUNT_LIMIT) {
                if (it.groupId != mFamilyId) {
                    groupList.add(it)
                }
            }
        }
        mBinding?.footerPostView?.setGroups(groupList)
    }

    /**
     * 设置家族分区
     */
    private fun setTypeLabels(types: List<RecommendType>?) {
        mBinding?.footerPostView?.setTypes(types)
        if (isEditorState) {
            mBinding?.footerPostView?.selectTypeById(id = mSectionId.toString())
        }
    }

    private fun launchSearchGroup(isRelation: Boolean = false) {
        this.isRelation = isRelation
        mProvider?.startFamilyListActivity(this, isFootmarks = isFootmarks)
    }

    private fun launchSearchMovie(isRelation: Boolean = false) {
        this.isRelation = isRelation
        // 查询电影信息，生成电影卡片数据
        mSearchProvider?.startPublishSearchActivity(
            activity = this,
            searchType = SEARCH_MOVIE,
            from = Search.PUBLISH_SEARCH_FROM_PUBLISH
        )
    }

    private fun launchSearchActor(isRelation: Boolean = false) {
        this.isRelation = isRelation
        mSearchProvider?.startPublishSearchActivity(
            activity = this,
            searchType = SEARCH_PERSON,
            from = Search.PUBLISH_SEARCH_FROM_PUBLISH
        )
    }

    /**
     * 添加影片
     */
    private fun addMovie(data: Intent?) {
        data?.apply {
            val movie = getSerializableExtra(Search.KEY_SEARCH_DATA_MOVIE) as? Movie
            if (isRelation) {
                mBinding?.footerArticleView?.addMovie(movie)
            } else {
                // 添加电影
                movie?.apply {
                    addMovieCard(movie = this)
                }
            }
        }
    }

    /**
     * 添加影人
     */
    private fun addPerson(data: Intent?) {
        data?.apply {
            val person = getSerializableExtra(Search.KEY_SEARCH_DATA_PERSON) as? Person
            if (isRelation) {
                mBinding?.footerArticleView?.addActor(person)
            }
        }
    }

    /**
     * 添加家族
     */
    private fun addGroup(data: Intent?) {
        data?.apply {
            val groupId = getLongExtra(Publish.KEY_FAMILY_ID, 0)
            val groupName = getStringExtra(Publish.KEY_FAMILY_NAME).orEmpty()
            mBinding?.footerPostView?.addGroup(Group(groupId = groupId, name = groupName))
        }
    }

    private fun notifyChangeStyle() {
        updateTitle()
        mBinding?.apply {
            when (style) {
                EditorStyle.JOURNAL -> { // 发日志
                    tipsView.isVisible = false
                    filmRatingView.isVisible = false
                    titleLayout.isVisible = true
                    divideView1.isVisible = true
                    divideView2.isVisible = true
                    footerArticleView.isVisible = false
                    footerPostView.isVisible = false
                    footerImagesView.isVisible = true
                    editorMenuView.showAddView(index = 1, isShow = false)
                }
                EditorStyle.POST -> { // 发帖子
                    tipsView.isVisible = true
                    filmRatingView.isVisible = false
                    titleLayout.isVisible = true
                    divideView1.isVisible = true
                    divideView2.isVisible = true
                    footerArticleView.isVisible = false
                    footerPostView.isVisible = true
                    footerImagesView.isVisible = false
                    editorMenuView.showAddView(index = 1, isShow = true)
                    if (!isEditorState) {
                        if (isFootmarks) {
                            // 种草家族
                            mViewModel?.getGroupFootmarks()
                        } else {
                            // 帖子
                            mViewModel?.loadFamilyList()
                        }
                    } else {
                        footerPostView.isAllowChanged = false
//                        setGroupLabels(listOf(Group(groupId = mFamilyId, name = mFamilyName)))
                    }
                }
                EditorStyle.FILM_COMMENT -> { // 发影评
                    tipsView.isVisible = false
                    filmRatingView.isVisible = true
                    titleLayout.isVisible = isLongComment
                    divideView1.isVisible = isLongComment
                    divideView2.isVisible = false
                    footerArticleView.isVisible = false
                    footerPostView.isVisible = false
                    footerImagesView.isVisible = false
                    editorMenuView.showAddView(index = 1, isShow = false)
                    editorLayout.hint = getString(R.string.publish_talk_about_your_opinion_hint)
                }
                EditorStyle.ARTICLE -> { // 发文章
                    tipsView.isVisible = true
                    filmRatingView.isVisible = false
                    titleLayout.isVisible = true
                    divideView1.isVisible = true
                    divideView2.isVisible = true
                    footerArticleView.isVisible = true
                    footerPostView.isVisible = false
                    footerImagesView.isVisible = false
                    editorMenuView.showAddView(index = 1, isShow = true)
                }
                else -> {
                    tipsView.isVisible = false
                    filmRatingView.isVisible = false
                    titleLayout.isVisible = false
                    divideView1.isVisible = false
                    divideView2.isVisible = false
                    footerArticleView.isVisible = false
                    footerPostView.isVisible = false
                    footerImagesView.isVisible = false
                    editorMenuView.showAddView(index = 1, isShow = true)
                }
            }
        }
    }

    private fun updateTitle() {
        titleBar?.apply {
            when (style) {
                EditorStyle.JOURNAL -> {
                    displayTitle(title = "${titlePrefix}日志")
                    displaySubTitle()
                }
                EditorStyle.POST -> {
                    displayTitle(title = "${titlePrefix}帖子")
                    displaySubTitle()
                }
                EditorStyle.ARTICLE -> {
                    displayTitle(title = "${titlePrefix}文章")
                    displaySubTitle()
                }
                EditorStyle.FILM_COMMENT -> {
                    displayMovieTitle(title = "${titlePrefix}到影片:")
                    displaySubTitle()
                }
                else -> {
                    displayTitle()
                    displaySubTitle()
                }
            }
        }
    }

    private val titlePrefix: String
        get() = if (isEditorState) {
            "编辑"
        } else {
            "发"
        }

    private fun displayTitle(title: CharSequence = "") {
        centerView.title = title
    }

    private fun displayMovieTitle(title: CharSequence = "") {
        centerView.setMovieTitle(
            title = title,
            movieId = mMovieId,
            movieName = mMovieName,
        )
    }

    private fun displaySubTitle(count: Int = 0) {
        val maxCount = if (isFilmComment) {
            if (count > MAX_FILM_COMMENT_TEXT_COUNT_LIMIT) {
                if (!isLongComment) {
                    changeLongCommentDialog()
                    MAX_FILM_COMMENT_TEXT_COUNT_LIMIT
                } else {
                    MAX_LONG_COMMENT_TEXT_COUNT_LIMIT
                }
            } else {
                MAX_LONG_COMMENT_TEXT_COUNT_LIMIT
            }
        } else {
            MAX_ARTICLE_TEXT_COUNT_LIMIT
        }
        centerView.isSubTitleVisible = count > 0
        centerView.subTitle = if (count > maxCount) {
            "$count".toSpan().toColor(color = Color.RED)?.append("字") ?: ""
        } else {
            "${count}字"
        }
    }

    private var showChangeLongComment = false

    private fun changeLongCommentDialog() {
        if (showChangeLongComment) {
            return
        }
        if (!showChangeLongComment) {
            showChangeLongComment = true
        }
        com.kotlin.android.widget.dialog.showDialog(
            context = this,
            content = R.string.publish_too_many_words_please_change_to_long_film_review,
            positive = R.string.publish_change_to_long_film_review,
            negative = R.string.publish_deletion_content
        ) {
            isLongComment = true
            showMenuBar(isShow = true)
            currentTextCount = currentTextCount
//            syncTitleBar()
        }
    }

    /**
     * 删除标记状态，评分和影评也将被删除
     */
    private fun deleteMark() {
        com.kotlin.android.widget.dialog.showDialog(
            context = this,
            title = R.string.publish_delete_tag,
            content = R.string.publish_delete_tips,
        ) {
            when {
                mCommentId > 0L -> {
                    mViewModel?.postDeleteContent(mContentType, mCommentId)
                    if (mRating > 0) {
                        mViewModel?.movieRating(mMovieId, 0.0, "")
                    }
                }
                mRating > 0 -> {
                    mViewModel?.movieRating(mMovieId, 0.0, "")
                    finish()
                }
                else -> {
                    // 重置页面数据
                    clear()
                }
            }
        }
    }

    private fun clear() {
        if (isFilmComment) {
            mBinding?.apply {
                filmRatingView.apply {
                    mSubRatings?.forEach {
                        it.rating = 0F
                    }
                    subRatings = mSubRatings
                    rating = 0.0
                    tags = null
                }
                titleView.setText("")
                editorLayout.clear()
            }
        }
    }

    /**
     * 发布
     */
    fun publish() {
        when (mContentType) {
            CONTENT_TYPE_JOURNAL -> {
                if (!checkTitle()
                    || !checkContent()
                ) {
                    return
                }
            }
            CONTENT_TYPE_POST -> {
                if (!checkTitle()
                    || !checkContent()
                    || !checkGroup()
                    || !checkContentIsReady()
                ) {
                    return
                }
            }
            CONTENT_TYPE_FILM_COMMENT -> {
                if (!checkCommentContent()) {
                    return
                }
            }
            CONTENT_TYPE_ARTICLE -> {
                if (!checkTitle()
                    || !checkContent()
                    || !checkSource()
                    || !checkEditor()
                    || !checkContentIsReady()
                ) {
                    return
                }
            }
        }
        if (isFilmComment) {
            publishRating()
        }
        buildPostContent()?.apply {
            mViewModel?.publishContent(content = this)
        }
    }

    /**
     * 发布评分
     */
    private fun publishRating() {
        mBinding?.apply {
            if (isFilmComment && isRatingReady) {
                mViewModel?.movieRating(
                    movieId = mMovieId,
                    rating = filmRatingView.rating,
                    subRatingDesc = filmRatingView.subRatingsDesc
                )
            }
        }
    }

    /**
     * 构建发布内容
     */
    private fun buildPostContent(): PostContent? {
        return mBinding?.let {
            when (mContentType) {
                CONTENT_TYPE_JOURNAL -> {
                    PostContent(
                        saveAction = mSaveAction,
                        title = it.titleView.text.toString(),
                        author = UserManager.instance.nickname,
                        type = mContentType,
                        contentId = mContentId ?: mDraftContentId,
                        recId = mRecId,
                        body = it.editorLayout.body.body,
                        images = it.footerImagesView.images,
                    )
                }
                CONTENT_TYPE_POST -> {
                    PostContent(
                        saveAction = mSaveAction,
                        title = it.titleView.text.toString(),
                        author = UserManager.instance.nickname,
                        type = mContentType,
                        contentId = mContentId ?: mDraftContentId,
                        recId = mRecId,
                        groupId = it.footerPostView.familyId ?: mFamilyId,
                        sectionId = it.footerPostView.sectionId,
                        body = it.editorLayout.body.body,
                        images = it.footerPostView.images,
                        vote = it.footerPostView.vote,
                    )
                }
                CONTENT_TYPE_ARTICLE -> {
                    PostContent(
                        saveAction = mSaveAction,
                        title = it.titleView.text.toString(),
                        author = UserManager.instance.nickname,
                        editor = it.footerArticleView.editor,
                        source = it.footerArticleView.source,
                        type = mContentType,
                        contentId = mContentId ?: mDraftContentId,
                        recId = mRecId,
                        body = it.editorLayout.body.body,
                        covers = it.footerArticleView.covers,
                        images = it.footerArticleView.images,
                        reObjs = it.footerArticleView.relations,
                        keywords = it.footerArticleView.keywords,
                        commentPmsn = it.footerArticleView.permission,
                        tags = it.footerArticleView.tags,
                    )
                }
                CONTENT_TYPE_FILM_COMMENT -> {
                    val action = if (isLongComment) {
                        mSaveAction
                    } else {
                        null // 短影评，不传该字段
                    }
                    if (isContentReady) {
                        PostContent(
                            saveAction = action,
                            author = UserManager.instance.nickname,
                            type = mContentType,
                            fcMovie = mMovieId,
                            contentId = mContentId ?: mDraftContentId,
                            recId = mRecId,
                            body = it.editorLayout.body.body,
                        ).apply {
                            if (isLongComment) {
                                fcType = LONG_COMMENT
                                title = it.titleView.text.toString()
                                tags = it.filmRatingView.tags
                            } else {
                                fcType = SHORT_COMMENT
                            }
                        }
                    } else {
                        null
                    }
                }
                else -> null
            }
        }
    }

    private fun initMenuView() {
        mBinding?.apply {
            editorMenuView.apply {
                showMenuBar(isShow = false)
                registerEditorLayout(editorLayout)
                actionPhoto = {
                    choosePhoto()
                }
                actionAdd = {
                    when (it) {
                        EditorMenuAddView.Action.MOVIE -> {
                            if (addMovieLimit > 0) {
                                launchSearchMovie(isRelation = false)
                            } else {
                                showToast(
                                    getString(
                                        R.string.publish_only_add_movie_at_most,
                                        MAX_ADD_MOVIE_LIMIT
                                    )
                                )
                            }
                        }
                        EditorMenuAddView.Action.VIDEO -> {
                            if (addVideoLimit > 0) {
                                showVideoListDialog(toVideoPublish = false) { localMedia ->
                                    addVideoCard(data = localMedia)
                                }
                            } else {
                                showToast(
                                    getString(
                                        R.string.publish_only_add_video_at_most,
                                        MAX_ADD_VIDEO_LIMIT
                                    )
                                )
                            }
                        }
                        EditorMenuAddView.Action.LINK -> {
                            showDialogFragment(
                                EditorInputLinkDialog::class.java,
                            )?.apply {
                                event = { title, url ->
                                    addLink(title = title, url = url)
                                }
                            }
                        }
                    }
                }
            }
        }
        mLayoutListener = setKeyboardListener(
            hide = {
                "隐藏键盘:$it".e()
                mBinding?.editorMenuView?.keyboardHide(it)
            },
            show = {
                "显示键盘:$it".e()
                mBinding?.editorMenuView?.keyboardShow(it)
            }
        )
    }

    private fun initTipsView() {
        mBinding?.apply {
            tipsView.apply {
                if (CONTENT_TYPE_POST == mContentType && isFootmarks.not()) {
                    setText(R.string.publish_component_jaunary_tips)
                } else if (CONTENT_TYPE_ARTICLE == mContentType) {
                    setText(R.string.publish_component_article_tips)
                } else {
                    setText(R.string.publish_component_tips)
                }
                setOnTouchListener(
                    TextTouchListener(
                        context = this@EditorActivity,
                        textView = this,
                        action = {
                            tipsView.isVisible = false
                        }
                    )
                )
            }
        }
    }

    private fun initTitleView() {
        mBinding?.apply {
            titleView.apply {
                doAfterTextChanged {
                    var total = it?.trim()?.length ?: 0
                    if (total > 0) {
                        total = it?.length ?: 0
                    }
                    isTitleReady = total in 1..MAX_TITLE_TEXT_COUNT_LIMIT
                    if (total > MAX_TITLE_TEXT_COUNT_LIMIT) {
                        titleCountView.text = "$total".toSpan().toColor(color = Color.RED)
                            ?.append("/$MAX_TITLE_TEXT_COUNT_LIMIT")
                    } else {
                        titleCountView.text = "$total/$MAX_TITLE_TEXT_COUNT_LIMIT"
                    }
                }
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        showMenuBar(isShow = false)
                    }
                }
            }
        }
    }

    private fun initEditorView() {
        mBinding?.apply {
            editorLayout.apply {
                hint = getString(R.string.publish_input_content_hint)
                textCountChange = {
                    currentTextCount = it
                    displaySubTitle(it)
                }
                focusChanged = { _, hasFocus ->
                    if (hasFocus) {
                        // 不是短评就显示
                        showMenuBar(isShow = !(isFilmComment && !isLongComment))
                    }
                }
            }
        }
    }

    private fun initFooterJournalView() {
        mBinding?.apply {
            footerImagesView.apply {
                action = {
                    showDialogFragment(
                        EditorImagesDialog::class.java
                    )?.apply {
                        footerImagesView.photos.apply {
                            photos = this
                        }
                        event = {
                            setPhotos(photos)
                        }
                    }
                }
            }
        }
    }

    private fun initFooterPostView() {
        mBinding?.apply {
            footerPostView.apply {
                registerEditorLayout(editorLayout)
                clearEditFocus = {
                    titleView.clearFocus()
                    editorLayout.clearEditFocus()
                }
                action = {
                    launchSearchGroup(isRelation = true)
                }
                actionGroup = {
//                    if (isFootmarks) {
//                        mViewModel?.getGroupSubTypes()
//                    } else {
//                        it.groupId?.apply {
//                            mViewModel?.getGroupSectionList(groupId = this)
//                        }
//                    }
                    it.groupId?.apply {
                        mViewModel?.getGroupSectionList(groupId = this)
                    }
                }
                actionImages = {
                    showDialogFragment(
                        EditorImagesDialog::class.java
                    )?.apply {
                        footerPostView.photos?.apply {
                            photos = this
                        }
                        event = {
                            footerPostView.photos = photos
                        }
                    }
                }
            }
        }
    }

    private fun initFilmRatingView() {
        mBinding?.apply {
            filmRatingView.apply {
                ratingChange = {
                    isRatingReady = it
                }
            }
        }
    }

    private fun initFooterArticleView() {
        mBinding?.apply {
            footerArticleView.apply {
                registerEditorLayout(editorLayout)
                clearEditFocus = {
                    titleView.clearFocus()
                    editorLayout.clearEditFocus()
                }
                action = {
                    when (it) {
                        EditorFooterArticleView.Action.MOVIE -> {
                            launchSearchMovie(isRelation = true)
                        }
                        EditorFooterArticleView.Action.ACTOR -> {
                            launchSearchActor(isRelation = true)
                        }
                        EditorFooterArticleView.Action.ADD_COVER -> {
                            takePhotos(limit = 1) { photos ->
                                addCover(photo = photos.firstOrNull())
                            }
                        }
                        EditorFooterArticleView.Action.DEL_COVER -> {
                        }
                        EditorFooterArticleView.Action.IMAGES -> {
                            showDialogFragment(
                                EditorImagesDialog::class.java
                            )?.apply {
                                footerArticleView.photos?.apply {
                                    photos = this
                                }
                                event = {
                                    footerArticleView.photos = photos
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initFooterView() {
        mBinding?.apply {
            footerShadowView.setBackground(
                colorRes = R.color.color_00000000,
                endColorRes = R.color.color_1f000000
            )
        }
        mBinding?.apply {
            deleteCommentView.setOnClickListener {
                deleteMark()
            }
        }
    }

    /**
     * 内容
     */
    private fun initContentObserve() {
        mViewModel?.contentUIState?.observe(this) {
            it.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    mRecId = creatorAuthority?.btnEdit?.recId
                    mIsShowDraft = creatorAuthority?.btnEdit?.draftAble ?: true
                    showSave(isShow = mIsShowDraft)
                    dispatchContent(this)
                }
                error?.showToast()
                netError?.showToast()
            }
        }

        mViewModel?.postContentUIState?.observe(this) {
            it.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    if (bizCode == 0L) {
                        if (mSaveAction == 2L) {
                            if (isFilmComment) {
                                showToast(R.string.publish_success)
                                getProvider(IReviewProvider::class.java)?.startReviewShare(
                                    contentId = contentId,
                                    isPublished = false,
                                    isShowEditBtn = true
                                )
                                finish()
                            } else {
                                //显示发布成功弹框
                                showPublishSuccessDialog(getString(R.string.publish_success)) {
                                    //跳转到我的内容页
                                    getProvider(IMineProvider::class.java) {
                                        startMyContent(
                                            activity = this@EditorActivity,
                                            contentType = mContentType
                                        )
                                    }
                                    finish()
                                }
                            }
                            postEvent(mContentType)
                        } else {
                            mDraftContentId = contentId
                            showToast(R.string.publish_component_drawft_had_saved)
                        }
                    } else {
                        if (mSaveAction == 2L) {
                            com.kotlin.android.widget.dialog.showDialog(
                                context = this@EditorActivity,
                                content = R.string.publish_fail,
                                positive = R.string.publish_again
                            ) {
                                publish()
                            }
                        } else {
                            showToast(R.string.publish_component_drawft_save_failed)
                        }
                    }
                }
                error?.showToast()
                netError?.showToast()
            }
        }
    }

    /**
     * 家族
     */
    private fun initGroupObserve() {
        mViewModel?.familyListUIState?.observe(this) {
            it.apply {

                success?.apply {
                    setGroupLabels(this)
                    if (this.isEmpty()) {
                        showJoinGroup()
                    }
                }
            }
        }
        mViewModel?.footmarksUIState?.observe(this) {
            it.apply {

                success?.apply {
                    groupList?.apply {
                        setGroupLabels(this)
                    }
                }
            }
        }
        mViewModel?.sectionListUIState?.observe(this) {
            it.apply {

                success?.apply {
                    sectionList?.map { section ->
                        RecommendType(
                            subTypeId = section.sectionId,
                            name = section.name
                        )
                    }?.apply {
                        setTypeLabels(this)
                    } ?: setTypeLabels(null)
                }

                error?.apply {
                    setTypeLabels(null)
                }

                netError?.apply {
                    setTypeLabels(null)
                }
            }
        }
//        mViewModel?.recommendTypesUIState?.observe(this) {
//            it.apply {
//
//                success?.apply {
//                    setTypeLabels(subTypes)
//                }
//
//                error?.apply {
//                    setTypeLabels(null)
//                }
//
//                netError?.apply {
//                    setTypeLabels(null)
//                }
//            }
//        }
    }

    /**
     * 关联
     */
    private fun initRelationObserve() {
        mViewModel?.relationArticlesUIState?.observe(this) {
            it.apply {
                success?.apply {
                    mBinding?.footerArticleView?.articles = items?.map { commContent ->
                        Article(
                            articleId = commContent.contentId,
                            articleTitle = commContent.title
                        )
                    }
                }
            }
        }
    }

    /**
     * 影评
     */
    private fun initFilmCommentObserve() {
        mViewModel?.latestCommentUIState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    latestComment = this
                    showFilmRatingView()
                }
            }
        }
        mViewModel?.deleteCommentUIState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    if (isSuccess()) {
                        showToast(R.string.delete_success)
                        finish()
                    } else {
                        bizMsg?.showToast()
                    }
                }
                netError?.showToast()
                error?.showToast()
            }
        }
        mViewModel?.movieRatingUIState?.observe(this) {
            it.apply {
                // 仅有评分时需处理
                if (mCommentId <= 0L) {
                    showProgressDialog(showLoading)

                    success?.apply {
                        if (status == 1L) {
//                            mReviewProvider?.startReviewShare(recordId, false)
                            finish()
                        } else {
                            showToast(statusMsg)
                        }
                    }
                }
            }
        }
    }

    private fun showMenuBar(isShow: Boolean = true) {
        mBinding?.editorMenuView?.isShowMenuBar = isShow
    }

    /**
     * 回显电影评分视图
     */
    private fun showFilmRatingView() {
        mBinding?.apply {
            filmRatingView.rating = mRating
            filmRatingView.subRatings = mSubRatings
        }
    }

    /**
     * 分发不同类型的内容处理方案
     */
    private fun dispatchContent(content: CommunityContent) {
        mBinding?.apply {
            titleView.setText(content.title.orEmpty())
            when (mContentType) {
                CONTENT_TYPE_JOURNAL -> {
                    parserBody(content.body)
                    footerImagesView.content = content
                }
                CONTENT_TYPE_POST -> {
                    parserBody(content.body)
                    footerPostView.content = content
//                    mFamilyId = content.group?.id
                    mSectionId = content.section?.sectionId
                }
                CONTENT_TYPE_ARTICLE -> {
                    parserBody(content.body)
                    footerArticleView.content = content
                    mViewModel?.getRelationArticles(contentId = mContentId)
                }
                CONTENT_TYPE_FILM_COMMENT -> {
                    mContent = content
                    filmRatingView.tags = content.tags
                    parserBody(content.body)
                }
                else -> {

                }
            }
        }
    }

    /**
     * 解析body标签
     */
    private fun parserBody(body: String?) {
        body?.apply {
            XmlParser.parseXml(body = this) {
                mBinding?.editorLayout?.body = it
            }
        }
    }

    private fun postEvent(recordType: Long) {
        when (recordType) {
            CONTENT_TYPE_JOURNAL -> {//日志

            }
            CONTENT_TYPE_POST -> {//帖子
                postPublishPost()
            }
            CONTENT_TYPE_FILM_COMMENT -> {//影评
                postPublishReview()
            }
        }
    }

    /**
     * 选择照片
     */
    private fun choosePhoto() {
        if (addImageLimit > 0) {
            takePhotos(limit = addImageLimit) { photos ->
                addImageCards(photos)
            }
        } else {
            showToast(getString(R.string.publish_only_add_pictures_at_most, MAX_ADD_IMAGE_LIMIT))
        }
    }

    /**
     * 照片
     */
    private fun takePhotos(
        limit: Int = MAX_ADD_IMAGE_LIMIT,
        completed: (ArrayList<PhotoInfo>) -> Unit
    ) {
        showDialogFragment(
            clazz = PhotoAlbumDialogFragment::class.java
        )?.apply {
            needUpload = true
            limitPhotoSelectCount = limit
            actionSuccessPhotos = {
                completed.invoke(it)
            }
        }
    }

    /**
     * 检查标题
     */
    private fun checkTitle(): Boolean {
        return if (mBinding?.titleView?.text?.length.orZero() in 1..MAX_TITLE_TEXT_COUNT_LIMIT) {
            true
        } else {
            showToast(R.string.publish_component_toast_input_title)
            false
        }
    }

    /**
     * 检查内容
     */
    private fun checkContent(): Boolean {
        return when {
            currentTextCount in 1..MAX_ARTICLE_TEXT_COUNT_LIMIT -> {
                true
            }
            currentTextCount > MAX_ARTICLE_TEXT_COUNT_LIMIT -> {
                showToast(R.string.publish_component_toast_content_exceed)
                false
            }
            else -> {
                showToast(R.string.publish_component_toast_input_content)
                false
            }
        }
    }

    /**
     * 检查影评内容
     */
    private fun checkCommentContent(): Boolean {
        return when {
            currentTextCount in 1..MAX_FILM_COMMENT_TEXT_COUNT_LIMIT -> {
                if (isLongComment) {
                    checkContentIsReady()
                } else {
                    true
                }
            }
            currentTextCount > MAX_FILM_COMMENT_TEXT_COUNT_LIMIT -> {
                if (isLongComment) {
                    checkContentIsReady()
//                    true
                } else {
                    showToast(R.string.publish_component_toast_film_comment_content_exceed)
                    false
                }
            }
            else -> {
                // 影评可以无内容（只有评分）
                true
            }
        }
    }

    /**
     * 检查文章来源
     */
    private fun checkSource(): Boolean {
        return if (mBinding?.footerArticleView?.source.isNullOrEmpty()) {
            showToast(R.string.publish_component_toast_select_source)
            false
        } else {
            true
        }
    }

    /**
     * 检查编辑署名
     */
    private fun checkEditor(): Boolean {
        return if (mBinding?.footerArticleView?.editor.isNullOrEmpty()) {
            showToast(R.string.publish_component_toast_select_editor)
            false
        } else {
            true
        }
    }

    /**
     * 检查家族
     */
    private fun checkGroup(): Boolean {
        return if (mBinding?.footerPostView?.familyId.orZero() == 0L) {
            showToast(R.string.publish_component_toast_select_group)
            false
        } else {
            true
        }
    }

    /**
     * 检查内容是否就绪
     */
    private fun checkContentIsReady(): Boolean {
        // 即没有成功(就绪)，也没有错误，说明在上传中。在发布时需要提示
        return if (mSaveAction == 2L && mBinding?.editorLayout?.isReady == false && mBinding?.editorLayout?.isError == false) {
            showToast(R.string.publish_component_toast_content_uploading)
            false
        } else {
            true
        }
    }

    /**
     * 弹框提示未加入家族
     */
    private fun showJoinGroup() {
        com.kotlin.android.widget.dialog.showDialog(
            context = this,
            content = R.string.publish_component_tips_join_group,
            positive = R.string.publish_component_tips_join_group_go,
            negative = R.string.publish_component_tips_join_group_ok,
        ) {
            openCommunity(subPosition = 2)
        }
    }
}