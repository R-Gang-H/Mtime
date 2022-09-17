package com.kotlin.android.publish.component.ui

import android.text.TextUtils
import android.view.Gravity
import androidx.fragment.app.viewModels
import com.kotlin.android.bonus.scene.component.postPublishPost
import com.kotlin.android.bonus.scene.component.postPublishReview
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.component.showPhotoAlbumFragment
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.widget.ItemType
import com.kotlin.android.publish.component.widget.PublishStyle
import com.kotlin.android.comment.component.bar.item.BarButtonItem
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.app.data.annotation.*
import com.kotlin.android.app.data.entity.community.record.PostRecord
import com.kotlin.android.app.data.entity.movie.LatestComment
import com.kotlin.android.app.data.entity.search.Movie
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.orFalse
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.ktx.ext.span.toBold
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.ktx.ext.statelist.getColorStateList
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.publish.component.databinding.LayoutPublishBinding
import com.kotlin.android.publish.component.widget.PublishLayout
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.app.router.provider.review.IReviewProvider
import com.kotlin.android.app.router.provider.search.ISearchProvider
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.search.newcomponent.Search
import com.kotlin.android.user.UserManager
import com.kotlin.android.widget.dialog.showDialog
import com.kotlin.android.widget.titlebar.TextTouchListener
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import kotlinx.android.synthetic.main.layout_publish.*

/**
 * 发布视图 [PublishFragment]
 *
 * Created on 2020/7/10.
 *
 * @author o.s
 */
class PublishFragment : BaseVMFragment<PublishViewModel, LayoutPublishBinding>() {

    private val mProvider by lazy {
        getProvider(IPublishProvider::class.java)
    }

    private val mSearchProvider by lazy {
        getProvider(ISearchProvider::class.java)
    }

    private val mTicketProvider by lazy {
        getProvider(ITicketProvider::class.java)
    }

    private val mFamilyProvider by lazy {
        getProvider(ICommunityFamilyProvider::class.java)
    }

    private val mReviewProvider by lazy {
        getProvider(IReviewProvider::class.java)
    }

//    private var isFirst = true // 首次打开发布
    /**
     * 最新评论
     */
    private var latestComment: LatestComment? = null

    /**
     * 回显评分
     */
    private val mRating: Double
        get() = latestComment?.userInfo?.rating ?: 0.0

    /**
     * 评论ID
     */
    private val mCommentId: Long
        get() = if (mPublishType == PUBLISH_FILM_COMMENT) {
            latestComment?.shortComment?.commentId ?: 0L
        } else {
            latestComment?.longComment?.commentId ?: 0L
        }

    var style: PublishStyle = PublishStyle.FILM_COMMENT
        set(value) {
            field = value
            publishLayout?.style = value
        }

    @PublishType
    var mPublishType = PUBLISH_JOURNAL
        set(value) {
            field = value
            notifyChange()
        }

    private var mPublishState: PublishLayout.PublishState? = null

    var movieId = 0L
    var movieName = ""
    var familyId = 0L
    var familyName = ""

    private var recordId: Long = 0L

    /**
     * 发布类型 [PublishType] 转换为接口记录类型 [ContentType]
     */
    @ContentType
    val recordType: Long
        get() = when (mPublishType) {
            PUBLISH_JOURNAL -> CONTENT_TYPE_JOURNAL
            PUBLISH_POST -> CONTENT_TYPE_POST
            PUBLISH_FILM_COMMENT -> CONTENT_TYPE_FILM_COMMENT
            PUBLISH_LONG_FILM_COMMENT -> CONTENT_TYPE_FILM_COMMENT
            else -> CONTENT_TYPE_FILM_COMMENT
        }

    /**
     * 照片
     */
    private fun takePhotos(limit: Int, completed: (ArrayList<PhotoInfo>) -> Unit) {
        activity?.showPhotoAlbumFragment(
            isUploadImageInComponent = true,
            limitedCount = limit.toLong()
        )?.apply {
            actionSelectPhotos = { photos ->
                completed.invoke(photos)
            }
        }
    }

    fun addMovieItem(movie: Movie) {
        publishLayout?.addItemView(ItemType.MOVIE_CARD)?.run {
            this.movie = movie
        }
    }

    override fun initVM(): PublishViewModel = viewModels<PublishViewModel>().value

    override fun initView() {
        immersive().statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
        initTitleView()
        initPublishLayout()
        initBarButton()
        initFamilyBubble()
    }

    override fun initData() {
        if (style == PublishStyle.FILM_COMMENT
            || style == PublishStyle.LONG_COMMENT
        ) {
            mViewModel?.getMovieRating(movieId)
        }
    }

    override fun startObserve() {
        mViewModel?.recordIdUIState?.observe(this) {
            it.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    recordId = recId
                    buildPostRecord(recordId)?.apply {
                        mViewModel?.publishRecord(this)
                    }
                }
                netError?.showToast()
            }
        }
        mViewModel?.recordUIState?.observe(this) {
            it.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    if (bizCode == 0L) {
                        showToast(R.string.publish_success)
                        if (mPublishType == PUBLISH_FILM_COMMENT ||
                            mPublishType == PUBLISH_LONG_FILM_COMMENT
                        ) {
                            mReviewProvider?.startReviewShare(recordId, false, true)
                        }
                        postEvent(recordType)
                        finishActivity()
                    } else {
                        showDialog(
                            context = context,
                            content = R.string.publish_fail,
                            positive = R.string.publish_again
                        ) {
                            publish()
                        }
                    }
                }
                netError?.showToast()
            }
        }
        mViewModel?.movieRatingUIState?.observe(this) {
            it.apply {
                // 仅有评分时需处理
                if (isOnlyRating()) {
                    showProgressDialog(showLoading)

                    success?.apply {
                        if (status == 1L) {
//                            mReviewProvider?.startReviewShare(recordId, false)
                            finishActivity()
                        } else {
                            showToast(statusMsg)
                        }
                    }
                }
            }
        }
        mViewModel?.latestCommentUIState?.observe(this) {
            it.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    latestComment = this
                    publishLayout.rating = mRating
                    publishLayout.subRatings = userInfo?.userMovieSubItemRatings
                }
            }
        }
        mViewModel?.deleteCommentUIState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    if (isSuccess()) {
                        showToast(R.string.delete_success)
                        finishActivity()
                    } else {
                        bizMsg?.showToast()
                    }
                }
                netError?.showToast()
                error?.showToast()
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

//    override fun onResume() {
//        super.onResume()
//        if (isFirst) {
//            isFirst = false
//            if (style == PublishStyle.JOURNAL) {
//                publishLayout?.post {
//                    chooseCover()
//                }
//            }
//        }
//    }

    override fun destroyView() {
    }

    private fun initTitleView() {
        titleBar?.apply {
            setThemeStyle(ThemeStyle.STANDARD)
            setState(State.NORMAL)
            addItem(
                drawableRes = R.drawable.ic_title_bar_close,
                reverseDrawableRes = R.drawable.ic_title_bar_close
            ) {
        //                showDialog(
        //                        context = context,
        //                        title = R.string.publish_save_draft,
        //                        negativeAction = {
        //                            finishActivity()
        //                        }
        //                ) {
        //                    showToast(R.string.publish_save_draft)
        //                    finishActivity()
        //                }
                finishActivity()
            }
            addItem(
                titleRes = R.string.publish,
                colorRes = R.color.color_8798af,
                colorState = getColorStateList(
                    normalColor = getColor(R.color.color_8798af),
                    pressColor = getColor(R.color.color_c0c3c6),
                    disableColor = getColor(R.color.color_c0c3c6)
                ),
                textSize = 17F,
                isBold = true,
                isReversed = true,
            ) {
                publish()
            }
            updateEnable(
                isReversed = true,
                isEnabled = false,
            )
        }
        notifyChange()
    }

    private fun initPublishLayout() {
        publishLayout?.apply {
            style = this@PublishFragment.style
            changeStyle = {
                style = it
                mPublishType = when (it) {
                    PublishStyle.FILM_COMMENT -> PUBLISH_FILM_COMMENT
                    PublishStyle.LONG_COMMENT -> PUBLISH_LONG_FILM_COMMENT
                    PublishStyle.JOURNAL -> {
                        if (familyId > 0 && !TextUtils.isEmpty(familyName)) {
                            PUBLISH_POST
                        } else {
                            PUBLISH_JOURNAL
                        }
                    }
                }
            }
            changePublishState = {
                mPublishState = it
                syncPublishState()
            }
            actionLink = {
                it.movieId?.apply {
                    mTicketProvider?.startMovieDetailsActivity(this)
                }
            }
        }
    }

    private fun initBarButton() {
        barButton?.apply {
            action = { type, _ ->
                when (type) {
                    BarButtonItem.Type.PHOTO -> {
                        choosePhoto()
                    }
                    BarButtonItem.Type.MOVIE -> {
                        if ((publishLayout?.movieLimit ?: 0) > 0) {
                            // 查询电影信息，生成电影卡片数据
                            mSearchProvider?.startPublishSearchActivity(
                                    activity = activity,
                                    searchType = SEARCH_MOVIE,
                                    from = Search.PUBLISH_SEARCH_FROM_PUBLISH
                            )
                        } else {
                            showToast(
                                getString(
                                    R.string.publish_only_add_movie_at_most,
                                    publishLayout?.maxMovieLimit
                                )
                            )
                        }
                    }
                    BarButtonItem.Type.FAMILY -> {
                        mProvider?.startFamilyListActivity(activity)
                    }
                    BarButtonItem.Type.KEYBOARD -> {
                        publishLayout?.keyboard(activity)
                    }
                    BarButtonItem.Type.DELETE -> {
                        showDialog(
                            context = context,
                            title = R.string.publish_delete_tag,
                            content = R.string.publish_delete_tips,
                        ) {
                            when {
                                mCommentId > 0L -> {
                                    mViewModel?.postDeleteContent(recordType, mCommentId)
                                    if (mRating > 0) {
                                        mViewModel?.movieRating(movieId, 0.0, "")
                                    }
                                }
                                mRating > 0 -> {
                                    mViewModel?.movieRating(movieId, 0.0, "")
                                    finishActivity()
                                }
                                else -> {
                                    // 重置页面数据
                                    publishLayout?.clear()
                                }
                            }
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun initFamilyBubble() {
        familyBubbleView?.apply {
            setBackground(
                colorRes = R.color.color_20a0da,
                cornerRadius = 13.dpF
            )
        }
    }

    /**
     * 选择照片
     */
    private fun choosePhoto() {
        if ((publishLayout?.imageLimit ?: 0) > 0) {
            takePhotos(publishLayout?.imageLimit ?: 1) { photos ->
                photos.forEach { photoInfo ->
                    publishLayout?.addItemView(ItemType.IMAGE_CARD)?.run {
                        setImageDate(photoInfo)
                    }
                }
            }
        } else {
            showToast(
                getString(
                    R.string.publish_only_add_pictures_at_most,
                    publishLayout?.maxImageLimit
                )
            )
        }
    }

    /**
     * 选择封面
     * 封面：日志、帖子的情况下先弹出封面选择界面
     */
//    private fun chooseCover() {
//        activity?.showPhotoAlbumFragment(
//                isUploadImageInComponent = true,
//                limitedCount = PublishTitleView.maxImageCount
//        )?.apply {
//            actionSelectPhotos = { photos ->
//                publishLayout?.setCover(photos)
//            }
//            cancelEvent = {
//                finishActivity()
//            }
//        }
//    }

    private fun finishActivity() {
        activity?.finish()
    }

    /**
     * 发布
     */
    fun publish() {
        publishRating()
        if (!isOnlyRating()) {
            mViewModel?.loadRecordId(recordType)
        }
    }

    /**
     * 发布评分
     */
    private fun publishRating() {
        if (mPublishType == PUBLISH_FILM_COMMENT
            || mPublishType == PUBLISH_LONG_FILM_COMMENT
        ) {
            val rating = publishLayout?.rating.orZero()
            val subItemRating = publishLayout?.getSubItemRatings() ?: ""
            if (rating > 0) {
                mViewModel?.movieRating(movieId, rating, subItemRating)
            }
        }
    }

    /**
     * 短评只有评分的情况
     */
    private fun isOnlyRating(): Boolean {
        return if (PUBLISH_FILM_COMMENT == mPublishType) {
            mPublishState?.run {
                isRatingReady && !isTitleReady && !isBodyReady
            } ?: false
        } else {
            false
        }
    }

    private fun syncPublishState() {
        mPublishState?.apply {
            when (mPublishType) {
                PUBLISH_JOURNAL -> {
                    titleBar?.updateEnable(
                        isReversed = true,
                        isEnabled = isCoverReady && (isTitleReady || isBodyReady)
                    )
                    if (isTitleReady || isBodyReady) {
                        familyBubble?.gone()
                    }
                }
                PUBLISH_POST -> {
                    titleBar?.updateEnable(
                        isReversed = true,
                        isEnabled = isCoverReady && isTitleReady
                    )
                    if (isTitleReady || isBodyReady) {
                        familyBubble?.gone()
                    }
                }
                PUBLISH_FILM_COMMENT -> {
                    titleBar?.updateEnable(
                        isReversed = true,
                        isEnabled = isRatingReady
                    )
                }
                PUBLISH_LONG_FILM_COMMENT -> {
                    val rating = publishLayout?.rating.orZero()
                    val isSubRatingModel = publishLayout?.titleView?.isSubRatingModel.orFalse()
                    val isSubRatingReady = !isSubRatingModel || rating <= 0 || isRatingReady
                    titleBar?.updateEnable(
                        isReversed = true,
                        isEnabled = isTitleReady && isBodyReady && isSubRatingReady
                    )
                }
            }
        }
    }

    private fun notifyChange() {
        when (mPublishType) {
            PUBLISH_JOURNAL -> {
                journalStyle()
            }
            PUBLISH_POST -> {
                postStyle()
            }
            PUBLISH_FILM_COMMENT -> {
                filmCommentStyle()
            }
            PUBLISH_LONG_FILM_COMMENT -> {
                longFilmCommentStyle()
            }
        }
        syncPublishState()
    }

    private fun journalStyle() {
        setTitle()
        barButton?.apply {
            addItem(BarButtonItem.Type.PHOTO)
            addItem(BarButtonItem.Type.MOVIE)
            addItem(BarButtonItem.Type.FAMILY)

            addItem(BarButtonItem.Type.KEYBOARD, true)
            removeItem(BarButtonItem.Type.DELETE)
        }
        familyBubble?.visible()
    }

    private fun postStyle() {
        setPostTitle()
        barButton?.apply {
            addItem(BarButtonItem.Type.PHOTO)
            addItem(BarButtonItem.Type.MOVIE)
            addItem(BarButtonItem.Type.FAMILY)

            addItem(BarButtonItem.Type.KEYBOARD, true)
            removeItem(BarButtonItem.Type.DELETE)
        }
        familyBubble?.gone()
    }

    private fun filmCommentStyle() {
        setFilmCommentTitle()
        barButton?.apply {
            removeItem(BarButtonItem.Type.PHOTO)
            removeItem(BarButtonItem.Type.MOVIE)
            removeItem(BarButtonItem.Type.FAMILY)

            addItem(BarButtonItem.Type.KEYBOARD, true)
            addItem(BarButtonItem.Type.DELETE, true)
        }
        familyBubble?.gone()
    }

    private fun longFilmCommentStyle() {
        setFilmCommentTitle()
        barButton?.apply {
            addItem(BarButtonItem.Type.PHOTO)
            addItem(BarButtonItem.Type.MOVIE)
            removeItem(BarButtonItem.Type.FAMILY)

            addItem(BarButtonItem.Type.KEYBOARD, true)
            addItem(BarButtonItem.Type.DELETE, true)
        }
        familyBubble?.gone()
    }

    /**
     * 设置日志标题
     */
    private fun setTitle() {
        titleBar?.apply {
            setTitle()
        }
    }

    /**
     * 设置帖子标题
     */
    private fun setPostTitle() {
        if (TextUtils.isEmpty(familyName)) {
            return
        }
        titleBar?.apply {
            val span = familyName
                .toSpan()
                .toBold()
                .toColor(color = getColor(R.color.color_20a0da))

            val titleSpan = getString(R.string.publish_to_family)
                .toSpan()
                .append(span)
            setTitle(
                title = titleSpan,
                colorRes = R.color.color_8798af,
                endDrawable = getDrawable(R.drawable.ic_title_bar_center_end_close),
                textSize = 15F,
                isBold = false,
                gravity = Gravity.CENTER,
                link = familyName,
                touchClick = {
                    when (it.position) {
                        TextTouchListener.Position.START -> {
                        }
                        TextTouchListener.Position.END -> {
                            showDialog(
                                context = context,
                                content = R.string.do_you_want_delete_family
                            ) {
                                mPublishType = PUBLISH_JOURNAL
                                setTitle()
                                syncPublishState()
                            }
                        }
                        TextTouchListener.Position.CENTER -> {
                            mFamilyProvider?.startFamilyDetail(familyId)
                        }
                    }
                })
        }
    }

    /**
     * 设置影评标题
     */
    private fun setFilmCommentTitle() {
        titleBar?.apply {
            val span = movieName
                .toSpan()
                .toBold()
                .toColor(color = getColor(R.color.color_20a0da))
            val titleSpan = getString(R.string.publish_to_film)
                .toSpan()
                .append(span)
            setTitle(
                title = titleSpan,
                colorRes = R.color.color_8798af,
                textSize = 15F,
                isBold = false,
                gravity = Gravity.CENTER,
                link = movieName,
                touchClick = {
                    when (it.position) {
                        TextTouchListener.Position.START -> {
                        }
                        TextTouchListener.Position.END -> {
                        }
                        TextTouchListener.Position.CENTER -> {
                            mTicketProvider?.startMovieDetailsActivity(movieId)
                        }
                    }
                })
        }
    }

    private fun buildPostRecord(recordId: Long): PostRecord? {
        return when (recordType) {
            CONTENT_TYPE_JOURNAL -> {
                PostRecord(
                    author = UserManager.instance.nickname,
                    type = recordType,
                    recId = recordId,
                    body = publishLayout?.body,
                    covers = publishLayout?.covers,
                    images = publishLayout?.images
                ).apply {
                    val titleText = publishLayout?.title
                    if (!TextUtils.isEmpty(titleText)) {
                        title = titleText
                    }
                }
            }
            CONTENT_TYPE_POST -> {
                PostRecord(
                    title = publishLayout?.title,
                    author = UserManager.instance.nickname,
                    type = recordType,
                    recId = recordId,
                    groupId = familyId,
                    body = publishLayout?.body,
                    covers = publishLayout?.covers,
                    images = publishLayout?.images
                )
            }
            CONTENT_TYPE_FILM_COMMENT -> {
                PostRecord(
                    author = UserManager.instance.nickname,
                    type = recordType,
                    recId = recordId,
                    fcMovie = movieId,
                    body = publishLayout?.body
                ).apply {
                    if (mPublishType == PUBLISH_LONG_FILM_COMMENT) {
                        title = publishLayout?.title
                        fcType = LONG_COMMENT
                        tags = publishLayout?.tags
                    } else {
                        fcType = SHORT_COMMENT
                    }
                }
            }
            else -> null
        }
    }

    /**
     * 添加关联文章
     */
    private fun addArticle(id: Long, title: String) {
        (activity as PublishActivity).addArticle(id, title)
    }

}