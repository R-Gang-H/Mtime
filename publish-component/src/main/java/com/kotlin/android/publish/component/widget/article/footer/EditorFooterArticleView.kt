package com.kotlin.android.publish.component.widget.article.footer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import com.kotlin.android.app.data.annotation.*
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.entity.community.record.Image
import com.kotlin.android.app.data.entity.community.record.ReObjs
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.app.data.entity.search.Article
import com.kotlin.android.app.data.entity.search.Movie
import com.kotlin.android.app.data.entity.search.Person
import com.kotlin.android.core.ext.showDialogFragment
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.publish.component.Publish.EDITOR_CUSTOM
import com.kotlin.android.publish.component.Publish.EDITOR_EDITOR_NONE
import com.kotlin.android.publish.component.Publish.EDITOR_SOURCE_NONE
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.databinding.EditorFooterLayoutArticleBinding
import com.kotlin.android.publish.component.scope.ArticleIDScope
import com.kotlin.android.publish.component.widget.article.label.AddCoverView
import com.kotlin.android.publish.component.widget.article.label.LabelType
import com.kotlin.android.publish.component.widget.article.view.EditorLayout
import com.kotlin.android.publish.component.widget.dialog.InputLabelDialog
import com.kotlin.android.publish.component.widget.dialog.PermissionDialog

/**
 * 发文章 footerView
 *
 * Created on 2022/4/20.
 *
 * @author o.s
 */
class EditorFooterArticleView : LinearLayout {
    enum class Action {
        MOVIE,
        ACTOR,
        ADD_COVER,
        DEL_COVER,
        IMAGES,
    }
    constructor(context: Context?) : super(context) { initView() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { initView() }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { initView() }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) { initView() }

    private var mBinding: EditorFooterLayoutArticleBinding? = null
    private var editorLayout: EditorLayout? = null

    /**
     * 注册编辑器，自动处理获取光标问题
     */
    fun registerEditorLayout(editorLayout: EditorLayout) {
        this.editorLayout = editorLayout
    }

    /**
     * 需要清除焦点
     */
    var clearEditFocus: (() -> Unit)? = null

    /**
     * 编辑模式：回显内容
     */
    var content: CommunityContent? = null
        set(value) {
            field = value
            mBinding?.apply {
                // 文章来源
                editorOrigin.articleOriginView.content = value
                // 编辑署名
                editorOrigin.signatureOriginView.content = value
                // 封面
                addCoverView.content = value
                // 图集
                addImagesView.content = value
                // 关联影片`
                optionMovieView.content = value
                // 关联影人
                optionActorView.content = value
                // 关联文章
//                optionArticleView.content = value
                // 关联标签
                optionLabelView.content = value
                // 评论权限
                if (FORBID_ALL == value?.commentPmsn) {
                    permissionDescView.setText(R.string.publish_component_forbid_all)
                } else {
                    permissionDescView.setText(R.string.publish_component_allow_all)
                }
                // 声明
                tags = value?.tags
            }
        }

    /**
     * 编辑模式：回显内容
     */
    var articles: List<Article>? = null
        set(value) {
            field = value
            mBinding?.optionArticleView?.articles = value
        }

    /**
     * 版权声明
     */
    var hasCopyright: Boolean = false

    /**
     * 免责声明
     */
    var hasDisclaimer: Boolean = false

    /**
     * 文章来源
     */
    val source: String?
        get() = mBinding?.editorOrigin?.articleOriginView?.selectedLabel

    /**
     * 编辑署名
     */
    val editor: String?
        get() = mBinding?.editorOrigin?.signatureOriginView?.selectedLabel

    val relations: List<ReObjs>?
        get() {
            // 关联对象类型 必填 MOVIE(1, "电影"), PERSON(2, "影人"), ARTICLE(3, "文章")
            val list = ArrayList<ReObjs>().apply {
                (mBinding?.optionMovieView?.dataList as? List<Movie>)?.forEach {
                    add(ReObjs(roId = it.movieId ?: 0, roType = RELATION_TYPE_MOVIE))
                }
                (mBinding?.optionActorView?.dataList as? List<Person>)?.forEach {
                    add(ReObjs(roId = it.personId ?: 0, roType = RELATION_TYPE_PERSON))
                }
                (mBinding?.optionArticleView?.dataList as? List<Article>)?.forEach {
                    add(ReObjs(roId = it.articleId ?: 0, roType = RELATION_TYPE_ARTICLE))
                }
            }
            return if (list.isEmpty()) {
                null
            } else {
                list
            }
        }

    /**
     * 关键词
     */
    val keywords: List<String>?
        get() = mBinding?.optionLabelView?.dataList?.map {
            val key = it.toString()
            if (key.startsWith("#")) {
                key.substring(1, key.length)
            } else {
                key
            }
        }

    /**
     * 评论权限 ALLOW_ALL(1, "允许任何人"), FORBID_ALL(2, "禁止所有人")
     */
    @CommentPermission
    val permission: Long
        get() {
            return when (mBinding?.permissionDescView?.text.toString()) {
                getString(R.string.publish_component_allow_all) -> {
                    ALLOW_ALL
                }
                getString(R.string.publish_component_forbid_all) -> {
                    FORBID_ALL
                }
                else -> {
                    0
                }
            }
        }

    /**
     * 版权声明(3) / 免责声明(4)
     */
    var tags: List<Long>?
        get() {
            val list = ArrayList<Long>()
            if (hasCopyright) {
                list.add(RECORD_TAG_COPYRIGHT)
            }
            if (hasDisclaimer) {
                list.add(RECORD_TAG_DISCLAIMER)
            }
            return if (list.isEmpty()) {
                null
            } else {
                list
            }
        }
        set(value) {
            value?.apply {
                if (value.contains(RECORD_TAG_COPYRIGHT)) {
                    hasCopyright = true
                    mBinding?.copyrightView?.isSelected = true
                }
                if (value.contains(RECORD_TAG_DISCLAIMER)) {
                    hasDisclaimer = true
                    mBinding?.disclaimerView?.isSelected = true
                }
            }
        }

    /**
     * 封面图集
     */
    val covers: List<Image>?
        get() = mBinding?.addCoverView?.image?.let {
            listOf(it)
        }

    /**
     * 图集
     */
    val images: List<Image>?
        get() = mBinding?.addImagesView?.images

    /**
     * 获取封面图
     */
    val coverPhoto: PhotoInfo?
        get() = mBinding?.addCoverView?.photo

    /**
     * 获取图集/添加图集
     */
    var photos: List<PhotoInfo>?
        get() = mBinding?.addImagesView?.photos
        set(value) {
            value?.let {
                mBinding?.addImagesView?.setPhotos(it)
            }
        }

    var action: ((Action) -> Unit)? = null

    /**
     * 添加影片
     */
    fun addMovie(movie: Movie?) {
        movie?.apply {
            mBinding?.optionMovieView?.apply {
                addLabel(
                    id = movie.movieId?.toString(),
                    name = movie.name ?: movie.nameEn,
                    data = movie,
                )
            }
        }
    }

    /**
     * 添加影人
     */
    fun addActor(person: Person?) {
        person?.apply {
            mBinding?.optionActorView?.apply {
                addLabel(
                    id = personId?.toString(),
                    name = name ?: nameEn,
                    data = person
                )
            }
        }
    }

    /**
     * 添加封面
     */
    fun addCover(photo: PhotoInfo?) {
        mBinding?.addCoverView?.photo = photo
    }

    private fun initView() {
        orientation = VERTICAL
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        mBinding = EditorFooterLayoutArticleBinding.inflate(LayoutInflater.from(context))
        addView(mBinding?.root)
        mBinding?.apply {
            mBinding?.editorOrigin?.apply {
                articleOriginView.apply {
                    type = LabelType.SOURCE
                    allowInverseSelection = false
                    clickInputLabel = EDITOR_CUSTOM
                    setLabels(
                        dataList = arrayListOf(EDITOR_SOURCE_NONE, EDITOR_CUSTOM),
                        id = {
                            it
                        }
                    )
                    select(index = 0)
                    selectAction = {
                        "选中:$it".e()
                        if (EDITOR_CUSTOM == it) {
                            clearEditFocus?.invoke()
                            showDialogFragment(
                                InputLabelDialog::class.java,
                            )?.apply {
                                title = getString(R.string.publish_component_title_input_source)
                                hint = getString(R.string.publish_component_hint_input_source)
                                errorMessage = getString(R.string.publish_component_toast_input_source_error)
                                event = { label ->
                                    val id = label.toString()
                                    addLabel<String>(isFirst = true, id = id)
                                    select(id)
                                    "选中:$label".e()
                                }
                            }
                        }
                    }
                }
                signatureOriginView.apply {
                    type = LabelType.EDITOR
                    allowInverseSelection = false
                    clickInputLabel = EDITOR_CUSTOM
                    setLabels(
                        dataList = arrayListOf(EDITOR_EDITOR_NONE, EDITOR_CUSTOM),
                        id = {
                            it
                        }
                    )
                    select(index = 0)
                    selectAction = {
                        "选中:$it".e()
                        if (EDITOR_CUSTOM == it) {
                            clearEditFocus?.invoke()
                            showDialogFragment(
                                InputLabelDialog::class.java,
                            )?.apply {
                                title = getString(R.string.publish_component_title_input_editor)
                                hint = getString(R.string.publish_component_hint_input_editor)
                                errorMessage = getString(R.string.publish_component_toast_input_editor_error)
                                event = { label ->
                                    val id = label.toString()
                                    addLabel<String>(isFirst = true, id = id)
                                    select(id)
                                    "选中:$label".e()
                                }
                            }
                        }
                    }
                }
            }

            addCoverView.apply {
                action = {
                    when (it) {
                        AddCoverView.Action.ADD -> {
                            this@EditorFooterArticleView.action?.invoke(Action.ADD_COVER)
                        }
                        AddCoverView.Action.DElELT -> {
                            this@EditorFooterArticleView.action?.invoke(Action.DEL_COVER)
                        }
                    }
                }
            }

            addImagesView.apply {
                action = {
                    this@EditorFooterArticleView.action?.invoke(Action.IMAGES)
                }
            }

            optionMovieView.apply {
                type = LabelType.MOVIE
                titleRes = R.string.publish_component_title_input_movie
                action = {
//                    launchSearchMovie(isRelation = true)
                    this@EditorFooterArticleView.action?.invoke(Action.MOVIE)
                }
            }

            optionActorView.apply {
                type = LabelType.ACTOR
                titleRes = R.string.publish_component_title_input_actor
                action = {
//                    launchSearchActor(isRelation = true)
                    this@EditorFooterArticleView.action?.invoke(Action.ACTOR)
                }
            }

            optionArticleView.apply {
                type = LabelType.ARTICLE
                titleRes = R.string.publish_component_title_input_article
                action = {
                    clearEditFocus?.invoke()
                    showDialogFragment(
                        InputLabelDialog::class.java,
                    )?.apply {
                        title = getString(R.string.publish_component_title_input_article)
                        hint = getString(R.string.publish_component_hint_input_article_id)
                        inputType = EditorInfo.TYPE_CLASS_NUMBER
                        event = {
                            try {
                                val articleId = it.toString().toLong()
                                ArticleIDScope.instance.checkReleased(
                                    contentId = articleId,
                                    error = { msg ->
                                        this@EditorFooterArticleView.showToast(msg)
                                    },
                                    netError = {msg ->
                                        this@EditorFooterArticleView.showToast(msg)
                                    }
                                ) {
                                    if (isReleased == true) {
                                        addLabel(
                                            id = articleId.toString(),
                                            name = title,
                                            data = Article(articleId = articleId, articleTitle = title),
                                        )
                                    } else {
                                        this@EditorFooterArticleView.showToast("文章尚未发布")
                                    }
                                }
                            } catch (e: Exception) {
                                showToast("请填写正确的文章ID")
                            }
                        }
                    }
                }
            }

            optionLabelView.apply {
                type = LabelType.LABEL
                titleRes = R.string.publish_component_title_input_label
                action = {
                    clearEditFocus?.invoke()
                    showDialogFragment(
                        InputLabelDialog::class.java,
                    )?.apply {
                        title = getString(R.string.publish_component_title_input_label)
                        hint = getString(R.string.publish_component_hint_input_label)
                        event = {
                            val id = if (it.startsWith("#")) {
                                it.toString()
                            } else {
                                "#$it"
                            }
                            addLabel<String>(id = id)
                        }
                    }
                }
            }

            permissionView.apply {
                setOnClickListener {
                    showDialogFragment(
                        PermissionDialog::class.java,
                    )?.apply {
                        event = {
                            permissionDescView.text = it
                        }
                    }
                }
            }

            copyrightView.apply {
                setCompoundDrawables(
                    getDrawableStateList(
                        normal = getDrawable(R.drawable.ic_editor_16_check_box_normal),
                        pressed = getDrawable(R.drawable.ic_editor_16_check_box_selected),
                        selected = getDrawable(R.drawable.ic_editor_16_check_box_selected),
                    ).apply { setBounds(0, 0, 16.dp, 16.dp) },
                    null,
                    null,
                    null)
                setOnClickListener {
                    it.isSelected = !it.isSelected
                    hasCopyright = it.isSelected
                }
            }
            disclaimerView.apply {
                setCompoundDrawables(
                    getDrawableStateList(
                        normal = getDrawable(R.drawable.ic_editor_16_check_box_normal),
                        pressed = getDrawable(R.drawable.ic_editor_16_check_box_selected),
                        selected = getDrawable(R.drawable.ic_editor_16_check_box_selected),
                    ).apply { setBounds(0, 0, 16.dp, 16.dp) },
                    null,
                    null,
                    null)
                setOnClickListener {
                    it.isSelected = !it.isSelected
                    hasDisclaimer = it.isSelected
                }
            }
        }
    }
}