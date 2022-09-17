package com.kotlin.android.community.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.community.group.MyGroupList
import com.kotlin.android.community.ui.home.adapter.JoinFamilyItemBinder
import com.kotlin.android.community.ui.home.adapter.MyFamilyItemBinder
import com.kotlin.android.community.ui.home.bean.MyFamilyItem
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.getCacheFamilyPostCount
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/17
 */
class CommunityHomeRepository : BaseRepository() {

    suspend fun loadMyFamily(): ApiResult<List<MultiTypeBinder<*>>> {
        return request(
            converter = { myGroupList ->
                val list = mutableListOf<MultiTypeBinder<*>>()
                myGroupList.managedGroupList?.run {
                    list.addAll(map {
                        converter2Binder(it)
                    })
                }
                myGroupList.joinedGroupList?.run {
                    list.addAll(map {
                        converter2Binder(it)
                    })
                }
                if (list.isEmpty()) {
                    // 如果数据为空则显示加入item
                    list.add(JoinFamilyItemBinder())
                }
                //多于10条，只显示前10条
                if (list.size > 10) list.subList(0, 10) else list
            },
            api = {
                apiMTime.getCommunityMyFamily(1L, 10L)
            })
    }

    /**
     * 将API实体转换成UI Binder
     */
    private fun converter2Binder(group: MyGroupList.Group): MyFamilyItemBinder {
        return MyFamilyItemBinder(
            MyFamilyItem(
                id = group.groupId,
                pic = group.groupImg ?: "",
                name = group.groupName ?: "",
                updateCount = formatCount(
                    group.groupPostsCount -
                            getCacheFamilyPostCount(group.groupId)
                ), //这里本地计算更新条数
            )
        )
    }
}