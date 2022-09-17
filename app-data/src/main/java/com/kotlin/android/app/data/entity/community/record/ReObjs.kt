package com.kotlin.android.app.data.entity.community.record

import com.kotlin.android.app.data.annotation.RelationType

/**
 * 关联对象集合 目前只有文字使用
 *
 * Created on 2020/9/29.
 *
 * @author o.s
 */
data class ReObjs(
        var roId: Long = 0, // 关联对象ID 必填
        @RelationType var roType: Long = 0 // 关联对象类型 必填 MOVIE(1, "电影"), PERSON(2, "影人"), ARTICLE(3, "文章")
)
