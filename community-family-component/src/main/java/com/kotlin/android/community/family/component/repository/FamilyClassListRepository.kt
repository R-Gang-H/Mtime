package com.kotlin.android.community.family.component.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.community.family.component.ui.clazz.adapter.FamilyItemBinder
import com.kotlin.android.community.family.component.ui.clazz.bean.FamilyClassItem
import com.kotlin.android.community.family.component.ui.clazz.bean.FamilyItem
import com.kotlin.android.app.data.entity.CommHasMoreList

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/10
 */
class FamilyClassListRepository : BaseRepository() {

    /**
     * 获取家族分类并把API实体转换成UI实体
     */
    suspend fun loadClassData(): ApiResult<List<FamilyClassItem>> {
        return request(
            converter = {
                val list = mutableListOf<FamilyClassItem>()
                it.list?.forEach { item ->
                    list.add(
                        FamilyClassItem(
                            id = item.primaryCategoryId,
                            name = item.primaryCategoryName ?: "",
                            pic = item.logo ?: ""
                        )
                    )
                }
                list
            },
            api = {
                apiMTime.getCommunityFamilyClass()
            })
    }

    /**
     * 根据家族分类ID获取家族列表，并把API数据转换成UI Binder
     */
    suspend fun loadFamilyList(
        categoryId: Long,
        pageIndex: Long,
        pageSize: Long
    ): ApiResult<CommHasMoreList<FamilyItemBinder>> {
        return request(
            converter = {
                val list = mutableListOf<FamilyItemBinder>()
                if (pageIndex == 1L) {
                    it.rcmdGroupList?.forEach { group ->
                        list.add(FamilyItem.converter2Binder(group))
                    }
                }
                it.groupList?.forEach { group ->
                    list.add(FamilyItem.converter2Binder(group))
                }

                if (list.isEmpty()) {
                    null
                } else {
                    CommHasMoreList(hasMore = it.hasMore, list = list)
                }
            },
            api = {
                apiMTime.getCommunityFamilyListByClass(categoryId, pageIndex, pageSize)
            })
    }
}