package com.kotlin.android.core

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * 创建者: zl
 * 创建时间: 2020/6/11 9:42 AM
 * 描述:
 */
abstract class BaseFragment : Fragment() {

    protected lateinit var mContext: Context
    protected lateinit var mActivity: FragmentActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mActivity = requireActivity()
    }
}