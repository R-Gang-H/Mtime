package com.kotlin.tablet.ui.success

import androidx.lifecycle.MutableLiveData
import com.kotlin.android.app.data.entity.filmlist.FilmListCreateResult
import com.kotlin.android.core.BaseViewModel

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/14
 * 描述:片单创建成功ViewModel
 **/
class FilmListCreateSuccessViewModel : BaseViewModel() {
    val mDataLd: MutableLiveData<FilmListCreateResult.SimpleFilmListInfo> by lazy {
        MutableLiveData<FilmListCreateResult.SimpleFilmListInfo>()
    }

    /**
     * 设置界面数据
     */
    fun setData(data: FilmListCreateResult.SimpleFilmListInfo) {
        mDataLd.postValue(data)
    }
}