package com.kotlin.android.publish.component.widget.article.footer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.entity.community.publish.Group
import com.kotlin.android.app.data.entity.community.publish.RecommendType
import com.kotlin.android.app.data.entity.community.record.Image
import com.kotlin.android.app.data.entity.community.record.Vote
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.core.ext.showDialogFragment
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.databinding.EditorFooterLayoutPostBinding
import com.kotlin.android.publish.component.widget.article.view.EditorLayout
import com.kotlin.android.publish.component.widget.dialog.InputPkLabelDialog

/**
 * 发帖子/种草 footerView
 *
 * Created on 2022/4/20.
 *
 * @author o.s
 */
class EditorFooterPostView : LinearLayout {

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

    private var mBinding: EditorFooterLayoutPostBinding? = null
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

    var isAllowChanged: Boolean = true
        set(value) {
            field = value
            mBinding?.groupOptionView?.isAllowChanged = value
        }

    /**
     * 编辑模式：回显内容
     */
    var content: CommunityContent? = null
        set(value) {
            field = value
            // 图集
            mBinding?.addImagesView?.content = value
            // 家族
            mBinding?.groupOptionView?.content = value
            // PK
            mBinding?.pkOptionView?.content = value
        }

    /**
     * 家族ID
     */
    val familyId: Long?
        get() = mBinding?.groupOptionView?.selectedGroup?.groupId

    /**
     * 家族分类ID
     */
    val sectionId: Long?
        get() = mBinding?.groupOptionView?.selectedType?.subTypeId

    /**
     * 图集
     */
    val images: List<Image>?
        get() = mBinding?.addImagesView?.images

    /**
     * PK
     */
    val vote: Vote?
        get() {
            val a = mBinding?.pkOptionView?.viewpointA
            val b = mBinding?.pkOptionView?.viewpointB
            return if (a != null && b!= null) {
                Vote(
                    multiple = false,
                    opts = listOf(
                        Vote.Opts(optId = 1, optDesc = a.toString()),
                        Vote.Opts(optId = 2, optDesc = b.toString()),
                    )
                )
            } else {
                null
            }
        }

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

    var action: (() -> Unit)? = null

    var actionGroup: ((Group) -> Unit)? = null

    var actionImages: (() -> Unit)? = null
        set(value) {
            field = value
            mBinding?.addImagesView?.action = value
        }

    fun addGroup(group: Group) {
        mBinding?.groupOptionView?.addGroup(group)
    }

    fun setGroups(groups: List<Group>) {
        mBinding?.groupOptionView?.setGroups(groups)
    }

    fun setTypes(types: List<RecommendType>?) {
        mBinding?.groupOptionView?.setTypes(types)
    }

    fun selectTypeById(id: String?) {
        mBinding?.groupOptionView?.selectTypeById(id = id)
    }

    private fun initView() {
        orientation = VERTICAL
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        mBinding = EditorFooterLayoutPostBinding.inflate(LayoutInflater.from(context))
        addView(mBinding?.root)
        mBinding?.apply {
            groupOptionView.apply {
                isAllowChanged = this@EditorFooterPostView.isAllowChanged
                action = {
                    this@EditorFooterPostView.action?.invoke()
                }
                selectGroupAction = {
                    if (it != null) {
                        getGroupData(it)?.apply {
                            this@EditorFooterPostView.actionGroup?.invoke(this)
                        }
                    }
                }
                selectTypeAction = {

                }
            }

            addImagesView.apply {
                action = actionImages
            }

            pkOptionView.apply {
                titleRes = R.string.publish_component_title_input_add_pk
                action = {
                    clearEditFocus?.invoke()
                    showDialogFragment(
                        InputPkLabelDialog::class.java,
                    )?.apply {
                        title = getString(R.string.publish_component_title_input_add_pk)
                        event = { a, b ->
                            add(viewpointA = a, viewpointB = b)
                        }
                    }
                }
            }
        }
    }
}