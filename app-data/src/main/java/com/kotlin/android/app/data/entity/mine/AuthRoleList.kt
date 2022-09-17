package com.kotlin.android.app.data.entity.mine

/**
 * create by lushan on 2020/10/13
 * description:电影认证人角色列表
 */
data class AuthRoleList(var success: Boolean = false,//是否成功
                        var error: String? = "",//提示信息
                        var roleList: MutableList<String>? = mutableListOf()//认证角色列表
)