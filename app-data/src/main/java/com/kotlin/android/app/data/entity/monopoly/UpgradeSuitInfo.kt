package com.kotlin.android.app.data.entity.monopoly

/**
 * http://front-gateway.mtime.com/richman/upgradeSuit.api
 * 升级套装后套装详情浮层需要更新的信息（应app开发要求新建的字段，也可以通过调用suitDetail.api整体刷新套装详情浮层）
 *
 * Created on 2021/7/21.
 *
 * @author o.s
 */
data class UpgradeSuitInfo(
    var suitCount: Long = 0, // 当前级别拥有套装数
    var canUpgrade: Boolean = false, // 是否能升级
)
