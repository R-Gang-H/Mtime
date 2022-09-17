package com.kotlin.android.mine.binder

import android.view.View
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.CollectionPersonViewBean
import com.kotlin.android.mine.databinding.ItemCollectionPersonBinding

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/9/14
 * description:收藏影人binder
 */
class CollectionPersonBinder(var bean:CollectionPersonViewBean):MultiTypeBinder<ItemCollectionPersonBinding>() {
    override fun layoutId(): Int  = R.layout.item_collection_person

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CollectionPersonBinder && other.bean != bean
    }

    override fun onClick(view: View) {
        if(view.id == R.id.personRootView){
//
            val instance:IMainProvider? = getProvider(IMainProvider::class.java)
            instance?.startActorViewActivity(bean.personId,bean.name)
        }else {
            super.onClick(view)
        }
    }
}