package com.kotlin.android.app.router.path

import com.kotlin.android.router.annotation.RouteGroup
import com.kotlin.android.router.annotation.RoutePath

/**
 * 创建者: zl
 * 创建时间: 2020/6/4 4:30 PM
 * 描述: 用于组件开发中，ARouter多Fragment跳转的统一路径注册
 * 在这里注册添加路由路径，需要清楚的写好注释，标明功能界面
 * 页面path路径至少两级,第一级目录代表group
 */
@RoutePath
class RouterFragmentPath {

    /**
     * 首页组件
     */
    @RouteGroup
    object Home {
        /**组件根路径*/
        private const val HOME = "/home"

        /**Home入口界面*/
        const val PAGER_HOME = "$HOME/HomeFragment"
    }


    /**
     * 用户组件
     */
    @RouteGroup
    object User {
        /**组件根路径*/
        private const val USER = "/user"

        /**我的*/
        const val PAGER_ME = "$USER/Me"
    }
}