package com.kotlin.android.mine.ui.datacenter

import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.mine.R
import com.kotlin.android.mine.ui.datacenter.fragment.AnalysisFragment
import com.kotlin.android.mine.ui.datacenter.fragment.EarthFragment
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems

/**
 * @ProjectName:    MTime
 * @Package:        com.kotlin.android.mine.ui.datacenter
 * @ClassName:      DataCenterBeanViewModel
 * @Description:    数据中心ViewModel
 * @Author:         haoruigang
 * @CreateDate:     2022/3/11 09:14
 * @Version:
 */
class DataCenterBeanViewModel : BaseViewModel() {

    /**
     * 设置我的收藏tab对应fragment
     */
    fun getPageItem(creator: FragPagerItems): FragPagerItems {
        creator.add(titleRes = R.string.mine_data_center_earth, clazz = EarthFragment::class.java)
        creator.add(titleRes = R.string.mine_data_center_analysis, clazz = AnalysisFragment::class.java)
        return creator
    }

}