package com.kotlin.chat_component.inner.provider

import com.kotlin.chat_component.inner.domain.EaseUser

/**
 * User profile provider
 */
interface EaseUserProfileProvider {
    /**
     * return EaseUser for input username
     * @param username
     * @return
     */
    fun getUser(username: String?): EaseUser?
}