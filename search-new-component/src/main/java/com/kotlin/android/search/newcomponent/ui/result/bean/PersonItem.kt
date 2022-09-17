package com.kotlin.android.search.newcomponent.ui.result.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.search.Person
import com.kotlin.android.app.data.entity.search.PersonMovie
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.search.newcomponent.ui.publish.adapter.PublishSearchPersonItemBinder
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 搜索结果页显示用影人数据
 */
data class PersonItem(
    var personId: Long = 0L,        // 影人Id
    var name: String = "",          // 影人名
    var nameEn: String = "",        // 英文名
    var img: String = "",           // 影人图片url
    var profession: String = "",    // 职业
    var loveDeep: Double = 0.0,     // 喜爱度
    var personMovies: List<String> = mutableListOf() // 热门作品列表
): ProguardRule {

    companion object {

        /**
         * 转换影人代表作
         */
        private fun convertPersonMovies(personMovies: List<PersonMovie>?): List<String> {
            return mutableListOf<String>().apply {
                personMovies?.let { list ->
                    list.map {
                        it.title?.let { title ->
                            if (title.isNotEmpty()) {
                                this.add(title)
                            }
                        }
                    }
                }
            }
        }

        /**
         * 转换ViewBean
         */
        fun objectToViewBean(bean: Person): PersonItem {
            return PersonItem(
                    personId = bean.personId.orZero(),
                    name = bean.name.orEmpty(),
                    nameEn = bean.nameEn.orEmpty(),
                    img = bean.img.orEmpty(),
                    profession = bean.profession.orEmpty(),
                    loveDeep = bean.loveDeep.orZero(),
                    personMovies = convertPersonMovies(bean.personMovies)
            )
        }

        /**
         * 发布组件-关联影人-搜索结果binders
         */
        fun buildPublishSearch(beans: List<Person>?) : MutableList<MultiTypeBinder<*>> {
            val binderList = mutableListOf<MultiTypeBinder<*>>()
            beans?.let { list ->
                list.map {
                    val viewBean = objectToViewBean(it)
                    binderList.add(
                            PublishSearchPersonItemBinder(
                                    person = it,
                                    viewBean = viewBean
                            )
                    )
                }
            }
            return binderList
        }
    }
}
