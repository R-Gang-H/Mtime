package com.kotlin.android.community.ui.person.bean

data class WantSeeHasMoreList<T>(var totalCount:Long,var hasMore: Boolean,
                                  var list: List<T>)