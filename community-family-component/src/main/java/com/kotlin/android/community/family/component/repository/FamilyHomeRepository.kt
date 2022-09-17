package com.kotlin.android.community.family.component.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.community.family.component.ui.clazz.bean.FamilyClassItem
import com.kotlin.android.community.family.component.ui.clazz.bean.FamilyItem
import com.kotlin.android.community.family.component.ui.home.adapter.FamilyClassItemBinder
import com.kotlin.android.app.data.entity.CommHasMoreList
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import org.jetbrains.anko.collections.forEachWithIndex

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/10
 */
class FamilyHomeRepository : BaseRepository() {

    /**
     * 加载家族分类数据，并把api数据转换成UI Binder
     */
    suspend fun loadFamilyClass(): ApiResult<List<FamilyClassItemBinder>> {
        return request(
            converter = {
                val list = mutableListOf<FamilyClassItemBinder>()
                it.list?.forEachWithIndex { i, item ->
                    if (i < 5) {
                        list.add(
                            FamilyClassItemBinder(
                                FamilyClassItem(
                                    id = item.primaryCategoryId,
                                    name = item.primaryCategoryName ?: "",
                                    pic = item.logo ?: ""
                                )
                            )
                        )
                    }
                }
                list
            },
            api = {
                apiMTime.getCommunityFamilyClass()
            })
    }

    /**
     * 获取热门家族数据，并把api实体转换成UI Binder
     */
    suspend fun loadHotFamilyData(
        pageIndex: Long,
        pageSize: Long
    ): ApiResult<CommHasMoreList<MultiTypeBinder<*>>> {
        return request(
            converter = {
                val list = mutableListOf<MultiTypeBinder<*>>()
                it.list?.forEach { group ->
                    list.add(FamilyItem.converter2Binder(group, 1))
                }
                CommHasMoreList(hasMore = it.hasMore, list = list)
            },
            api = {
                apiMTime.getCommunityHotFamily(pageIndex, pageSize)
            })
    }
}