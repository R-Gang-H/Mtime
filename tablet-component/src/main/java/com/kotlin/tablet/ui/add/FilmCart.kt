package com.kotlin.tablet.ui.add

import com.kotlin.android.app.data.entity.search.Movie
import com.kotlin.tablet.FILM_LIST_CONTAINS_FILM_MAX_COUNT

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/31
 * 描述:添加电影缓存类
 **/
class FilmCart private constructor() {

    var mKeyWord: String = ""

    companion object {
        val instance by lazy { FilmCart() }
        var isEdit:Boolean = false
        //编辑状态下 是否了保存影片
        var isSave:Boolean = false
    }

    //缓存已经添加的电影
    private var mMap = LinkedHashMap<Long?, Movie>()

    private fun add(movie: Movie) {
        if (mMap.containsKey(movie.movieId).not()) {
            mMap[movie.movieId] = movie
        }
    }

    private fun remove(movie: Movie) {
        if (mMap.containsKey(movie.movieId)) {
            mMap.remove(movie.movieId)
        }
    }
    /**
     * 更新已选影片
     */
    fun update(movie: Movie): Boolean {
        if (mMap.containsKey(movie.movieId)) {
            movie.isAdd = false
            mMap.remove(movie.movieId)
        } else {
            if (mMap.size >= FILM_LIST_CONTAINS_FILM_MAX_COUNT) {
                return false
            }
            movie.isAdd = true
            mMap[movie.movieId] = movie
        }
        return true
    }
    /**
     * 已选影片集合
     */
    fun getSelectedData(): LinkedHashMap<Long?, Movie> {
        return mMap
    }
    /**
     * 已选影片size
     */
    val selectedSize:Int
        get() {
            return mMap.size
        }

    /**
     * 已选影片ids
     */
    fun getSelectedIds(): List<Long> {
        return getSelectedData().map { it.value.movieId ?: 0L }
    }
    /**
     * 清空已选影片
     * 创建状态：关闭搜索页、搜索页保存、已选影片页保存
     * 编辑状态：编辑页保存、关闭编辑页
     */
    fun clear() {
        mMap.clear()
        mKeyWord = ""
    }


}