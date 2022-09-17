package com.kotlin.android.image.component.photo

import androidx.lifecycle.MutableLiveData
import com.kotlin.android.app.data.entity.image.PhotoInfo

/**
 *
 * Created on 2022/5/14.
 *
 * @author o.s
 */
class PhotoData private constructor() {

    companion object {
        val instance by lazy { PhotoData() }
    }

    fun clear() {
        selectedLiveData.value = ArrayList()
        originalLiveData.value = false
    }

    /**
     * 选中的照片列表
     */
    val selectedLiveData: MutableLiveData<ArrayList<PhotoInfo>> by lazy { MutableLiveData() }

    /**
     * 原图状态
     */
    val originalLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData() }

    fun add(photo: PhotoInfo) {
        var list = selectedLiveData.value
        if (list == null) {
            list = ArrayList()
        }
        list.add(photo)
        selectedLiveData.postValue(list)
    }

    fun remove(photo: PhotoInfo) {
        var list = selectedLiveData.value
        if (list == null) {
            list = ArrayList()
        }
        list.remove(photo)
        selectedLiveData.postValue(list)
    }

    fun syncPhoto(photo: PhotoInfo) {
        var list = selectedLiveData.value
        if (list == null) {
            list = ArrayList()
        }
        if (list.contains(photo)) {
            if ((!photo.isCheck)) {
                val position = list.indexOf(photo)
                list.removeAt(position)
//                notifyItemRemoved(position)
//                notifyItemRangeChanged(position, itemCount - 1)
            }
        } else {
            if (photo.isCheck) {
                list.add(photo)
//                notifyItemChanged(itemCount - 1)
            }
        }
        selectedLiveData.postValue(list)
    }

    /**
     * 同步原图状态
     */
    fun syncOriginalState(isSelected: Boolean) {
        originalLiveData.postValue(isSelected)
    }
}