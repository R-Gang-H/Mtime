package com.kotlin.tablet

const val KEY_FILM_LIST_INFO = "key_film_list_info"
const val KEY_CONTRIBUTE_ACTIVITY_ID = "key_contribute_activity_id"
const val KEY_FILM_LIST_ID = "key_film_list_id"
const val KEY_IS_EDIT = "key_is_edit"
const val KEY_TO_SELECTED_FROM = "key_to_selected_from"//0搜索 1编辑
const val KEY_FILM_LIST_MINE_TAB = "key_film_list_mine_tab"
const val KEY_TO_DETAILS_SOURCE = "key_to_details_source"// 0:缓存5分钟，1:缓存1秒-我创建的片单列表用，
const val KEY_FILM_LIST_STATUS = "key_film_list_status" // 创建 or 编辑

const val KEY_FROM_EDIT = 1 //进入已选片单 from 编辑
const val KEY_FROM_SEARCH = 0 //进入已选片单 from 搜索
const val PAGE_SEARCH_ACTIVITY = 0 //搜索电影页
const val PAGE_SELECTED_ACTIVITY = 1 //已选电影页
const val PAGE_SUCCESS_ACTIVITY = 2 //片单创建成功页
const val PAGE_CONTRIBUTE_ACTIVITY = 3//投稿活动页
const val FILM_LIST_CONTAINS_FILM_MAX_COUNT = 500 //片单最多篇包含影片数量
const val FILM_LIST_DETAILS_DELETE_SUCCESS = 100//删除片单成功(片单详情页)

const val FILM_LIST_CREATE = 0L //创建片单
const val FILM_LIST_EDIT = 1L  //编辑片单

