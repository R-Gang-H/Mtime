package com.kotlin.android.community.family.component.ui.details.adapter

import android.text.TextUtils
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.ui.details.bean.FamilyDetailMember
import com.kotlin.android.ktx.ext.core.setCompoundDrawablesAndPadding
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.widget.dialog.BaseDialog

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/29
 */

// 家族简介
@BindingAdapter(value = ["binding_family_detail_des"])
fun bindingFamilyDetailDes(view: TextView, des: String?) {
    view.visible(!TextUtils.isEmpty(des))
    if (!TextUtils.isEmpty(des)) {
        view.run {
            setCompoundDrawablesAndPadding()
            text = des
            post(Runnable {
                val line: Int = lineCount
                if (line > 1) {
                    val ellipsisCount: Int = layout.getEllipsisCount(line - 1)
                    //ellipsisCount > 0 时，说明省略生效
                    if (ellipsisCount > 0) {
                        setCompoundDrawablesAndPadding(rightResId = R.drawable.ic_right_arrows, padding = 5)
                        setOnClickListener {
                            //弹框
                            BaseDialog.Builder(view.context)
                                    .setTitle(R.string.community_detail_family_des_title)
                                    .setContent(des!!)
                                    .setCanceledOnTouchOutside(true)
                                    .create()
                                    .show()
                        }
                    }
                }
            })
        }
    }
}

// 家族成员列表
@BindingAdapter(value = ["binding_family_detail_members"])
fun bindingFamilyDetailMember(view: RecyclerView, list: List<FamilyDetailMember>?) {
    if (null != list) {
        view.adapter = FamilyDetailMemberAdapter().setData(list)
    }
}