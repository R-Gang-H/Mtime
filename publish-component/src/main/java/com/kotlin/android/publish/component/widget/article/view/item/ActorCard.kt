//package com.kotlin.android.publish.component.widget.article.view.item
//
//import android.content.Context
//import android.util.AttributeSet
//import android.widget.RelativeLayout
//import com.kotlin.android.publish.component.widget.article.view.entity.ActorElementData
//import com.kotlin.android.publish.component.widget.article.view.entity.IElementData
//import com.kotlin.android.publish.component.widget.article.xml.entity.Element
//
///**
// * 电影卡片
// *
// * Created on 2022/3/30.
// *
// * @author o.s
// */
//class ActorCard : RelativeLayout, IItemView {
//    constructor(context: Context?) : super(context)
//    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
//    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
//        context,
//        attrs,
//        defStyleAttr
//    )
//
//    constructor(
//        context: Context?,
//        attrs: AttributeSet?,
//        defStyleAttr: Int,
//        defStyleRes: Int
//    ) : super(context, attrs, defStyleAttr, defStyleRes)
//
//    override var element: Element
//        get() = elementData.element
//        set(value) {
//            elementData.element = value
//        }
//
//    override var elementData: IElementData = ActorElementData()
//}