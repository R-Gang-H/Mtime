package com.kotlin.android.app.data.entity.family

import com.kotlin.android.app.data.ProguardRule

/**
 * @author zhangjian
 * @date 2022/3/18 10:06
 */
data class FindFamily(
    //家族总数
    var groupTotalCount: Long? = 0,
    //家族分类列表
    var groupCategoryList: ArrayList<FindFamilyCategory>? = null,
    //推荐家族列表
    var rcmdGroupList: ArrayList<FindFamilyRecommend>? = null
) : ProguardRule

data class FindFamilyCategory(
    //家族分类Id
    var categoryId: Long? = 0L,
    //家族分类名称
    var name: String? = "",
    //封面图
    var logo: String? = ""
) : ProguardRule

data class FindFamilyRecommend(
    var groupId: Long? = 0L,
    var name: String? = "",
    var logo: String? = "",
    var memberCount: Long? = 0L,
    var postCount: Long? = 0L,
    var newPost: Article? = null,
    var hotPost: Article? = null,
    //当前用户是否加入此家族：0-未加入，1-已加入成功，2-加入中（待审核），3-黑名单人员
    var hasJoin: Long? = 0L,
    var rcmdGroupList: ArrayList<FindFamilyRecommend>? = null
) : ProguardRule

data class Article(
    var postId: Long? = 0L,
    var title: String? = ""
) : ProguardRule