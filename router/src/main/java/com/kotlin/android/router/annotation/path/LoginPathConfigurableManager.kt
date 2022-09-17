package com.kotlin.android.router.annotation.path

/**
 * create by lushan on 2022/1/6
 * des:
 **/
class LoginPathConfigurableManager {
    companion object{
        val instance by lazy { LoginPathConfigurableManager() }
    }

    private val configurablePathMap = HashMap<String,Boolean>()

    /**
     * 设置当前路径是否可以动态配置是否可以登录
     */
    fun setPathNeedLogin(path: String, needLogin:Boolean){
        configurablePathMap[path] = needLogin
    }

    /**
     * 当前路径是否配置了需要登录
     */
    fun isPathNeedLogin(path: String): Boolean = configurablePathMap[path] == true

    /**
     * 是否包含路径
     */
    fun contains(path: String): Boolean = configurablePathMap.containsKey(path)

    /**
     * 清空当前可配置登录项
     */
    fun clear() = configurablePathMap.clear()
}