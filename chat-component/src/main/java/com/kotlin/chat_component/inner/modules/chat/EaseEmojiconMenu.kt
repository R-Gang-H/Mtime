package com.kotlin.chat_component.inner.modules.chat

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.kotlin.chat_component.R
import com.kotlin.chat_component.inner.domain.EaseEmojicon
import com.kotlin.chat_component.inner.domain.EaseEmojiconGroupEntity
import com.kotlin.chat_component.inner.model.EaseDefaultEmojiconDatas
import com.kotlin.chat_component.inner.modules.chat.interfaces.EaseEmojiconMenuListener
import com.kotlin.chat_component.inner.modules.chat.interfaces.IChatEmojiconMenu
import com.kotlin.chat_component.inner.widget.emojicon.EaseEmojiconIndicatorView
import com.kotlin.chat_component.inner.widget.emojicon.EaseEmojiconPagerView
import com.kotlin.chat_component.inner.widget.emojicon.EaseEmojiconPagerView.EaseEmojiconPagerViewListener
import com.kotlin.chat_component.inner.widget.emojicon.EaseEmojiconScrollTabBar
import com.kotlin.chat_component.inner.widget.emojicon.EaseEmojiconScrollTabBar.EaseScrollTabBarItemClickListener
import java.util.*

class EaseEmojiconMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), IChatEmojiconMenu {
    private var emojiconColumns = 0
    private var bigEmojiconColumns = 0
    private val tabBar: EaseEmojiconScrollTabBar
    private val indicatorView: EaseEmojiconIndicatorView
    private val pagerView: EaseEmojiconPagerView
    private val emojiconGroupList: ArrayList<EaseEmojiconGroupEntity?> = ArrayList()
    private var listener: EaseEmojiconMenuListener? = null

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.EaseEmojiconMenu)
        emojiconColumns = ta.getInt(R.styleable.EaseEmojiconMenu_emojiconColumns, defaultColumns)
        bigEmojiconColumns =
            ta.getInt(R.styleable.EaseEmojiconMenu_bigEmojiconRows, defaultBigColumns)
        ta.recycle()
    }

    companion object {
        private const val defaultColumns = 7
        private const val defaultBigColumns = 4
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.ease_widget_emojicon, this)
        pagerView = findViewById<View>(R.id.pager_view) as EaseEmojiconPagerView
        indicatorView = findViewById<View>(R.id.indicator_view) as EaseEmojiconIndicatorView
        tabBar = findViewById<View>(R.id.tab_bar) as EaseEmojiconScrollTabBar
        initAttrs(context, attrs)
    }

    @JvmOverloads
    fun init(groupEntities: MutableList<EaseEmojiconGroupEntity>? = null) {
        var groupEntities = groupEntities
        if (groupEntities == null || groupEntities.size == 0) {
            groupEntities = ArrayList()
            groupEntities.add(
                EaseEmojiconGroupEntity(
                    R.drawable.ee_1,
                    Arrays.asList(*EaseDefaultEmojiconDatas.data) as List<EaseEmojicon>?
                )
            )
        }
        for (groupEntity in groupEntities) {
            emojiconGroupList.add(groupEntity)
            tabBar.addTab(groupEntity.icon)
        }
        pagerView.setPagerViewListener(EmojiconPagerViewListener())
        pagerView.init(emojiconGroupList, emojiconColumns, bigEmojiconColumns)
        tabBar.setTabBarItemClickListener(object : EaseScrollTabBarItemClickListener {
            override fun onItemClick(position: Int) {
                pagerView.setGroupPostion(position)
            }
        })
    }

    /**
     * add emojicon group
     * @param groupEntity
     */
    override fun addEmojiconGroup(groupEntity: EaseEmojiconGroupEntity?) {
        emojiconGroupList.add(groupEntity)
        pagerView.addEmojiconGroup(groupEntity!!, true)
        tabBar.addTab(groupEntity.icon)
    }

    /**
     * add emojicon group list
     * @param groupEntitieList
     */
    override fun addEmojiconGroup(groupEntitieList: List<EaseEmojiconGroupEntity?>?) {
        for (i in groupEntitieList!!.indices) {
            val groupEntity = groupEntitieList[i]
            emojiconGroupList.add(groupEntity)
            pagerView.addEmojiconGroup(
                groupEntity!!,
                if (i == groupEntitieList.size - 1) true else false
            )
            tabBar.addTab(groupEntity.icon)
        }
    }

    /**
     * remove emojicon group
     * @param position
     */
    override fun removeEmojiconGroup(position: Int) {
        emojiconGroupList.removeAt(position)
        pagerView.removeEmojiconGroup(position)
        tabBar.removeTab(position)
    }

    override fun setTabBarVisibility(isVisible: Boolean) {
        if (!isVisible) {
            tabBar.visibility = GONE
        } else {
            tabBar.visibility = VISIBLE
        }
    }

    override fun setEmojiconMenuListener(listener: EaseEmojiconMenuListener?) {
        this.listener = listener
    }

    private inner class EmojiconPagerViewListener : EaseEmojiconPagerViewListener {
        override fun onPagerViewInited(groupMaxPageSize: Int, firstGroupPageSize: Int) {
            indicatorView.init(groupMaxPageSize)
            indicatorView.updateIndicator(firstGroupPageSize)
            tabBar.selectedTo(0)
        }

        override fun onGroupPositionChanged(groupPosition: Int, pagerSizeOfGroup: Int) {
            indicatorView.updateIndicator(pagerSizeOfGroup)
            tabBar.selectedTo(groupPosition)
        }

        override fun onGroupInnerPagePostionChanged(oldPosition: Int, newPosition: Int) {
            indicatorView.selectTo(oldPosition, newPosition)
        }

        override fun onGroupPagePostionChangedTo(position: Int) {
            indicatorView.selectTo(position)
        }

        override fun onGroupMaxPageSizeChanged(maxCount: Int) {
            indicatorView.updateIndicator(maxCount)
        }

        override fun onDeleteImageClicked() {
            if (listener != null) {
                listener!!.onDeleteImageClicked()
            }
        }

        override fun onExpressionClicked(emojicon: EaseEmojicon?) {
            if (listener != null) {
                listener!!.onExpressionClicked(emojicon)
            }
        }
    }
}