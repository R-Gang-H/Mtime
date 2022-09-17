package com.kotlin.android.community.ui.person.binder

import android.view.View

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.ItemPersonCollectionPersonBinding
import com.kotlin.android.community.ui.person.bean.CollectionPersonViewBean
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/9/14
 * description:收藏影人binder
 */
class CollectionPersonBinder(var bean: CollectionPersonViewBean):MultiTypeBinder<ItemPersonCollectionPersonBinding>() {
    override fun layoutId(): Int  = R.layout.item_person_collection_person

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