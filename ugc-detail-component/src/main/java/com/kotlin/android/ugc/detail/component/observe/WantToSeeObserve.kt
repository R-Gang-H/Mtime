package com.kotlin.android.ugc.detail.component.observe

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.comment.component.bean.DetailBaseExtend
import com.kotlin.android.app.data.entity.common.WantToSeeResult
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.ugc.detail.component.binder.MovieBinder
import com.kotlin.android.ugc.detail.component.binderadapter.MOVIE_STATE_HSA_LIKE
import com.kotlin.android.ugc.detail.component.binderadapter.MOVIE_STATE_LIKE

/**
 * create by lushan on 2020/9/29
 * description: 电影卡片想看
 */
open class WantToSeeObserve(var activity:FragmentActivity): Observer<BaseUIModel<DetailBaseExtend<Any>>> {
    override fun onChanged(t: BaseUIModel<DetailBaseExtend<Any>>?) {
        t?.apply {
            activity.showOrHideProgressDialog(showLoading)
            success?.apply {
                if (extend is MovieBinder){//电影卡片
                    if ((result as? WantToSeeResult)?.isSuccess() == true){
                        val movieBinder = extend as MovieBinder
                        movieBinder.movieBean.movieStatus =if (movieBinder.movieBean.isWanna()){
                            MOVIE_STATE_HSA_LIKE
                        }else{
                            MOVIE_STATE_LIKE
                        }
                        movieBinder.notifyAdapterSelfChanged()
                    }
                }
            }
        }
    }
}