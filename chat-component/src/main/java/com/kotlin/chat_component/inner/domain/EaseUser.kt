package com.kotlin.chat_component.inner.domain

import android.text.TextUtils
import com.hyphenate.chat.EMUserInfo
import com.hyphenate.chat.EMClient
import com.kotlin.chat_component.inner.utils.HanziToPinyin
import java.io.Serializable
import java.util.*

class EaseUser : Serializable {
    /**
     * \~chinese
     * 此用户的唯一标示名, 即用户的环信id
     *
     * \~english
     * the user name assigned from app, which should be unique in the application
     */
    private var username: String? = null
    private var nickname: String? = null

    /**
     * initial letter from nickname
     */
    private var initialLetter: String? = null

    /**
     * user's avatar
     */
    private var avatar: String? = null

    /**
     * contact 0: normal, 1: black ,3: no friend
     */
    private var contact = 0

    /**
     * the timestamp when last modify
     */
    var lastModifyTimestamp: Long = 0

    /**
     * the timestamp when set initialLetter
     */
    var modifyInitialLetterTimestamp: Long = 0

    /**
     * user's email;
     */
    private var email: String? = null

    /**
     * user's phone;
     */
    private var phone: String? = null

    /**
     * user's gender;
     */
    private var gender = 0

    /**
     * user's birth;
     */
    private var sign: String? = null

    /**
     * user's birth;
     */
    private var birth: String? = null

    /**
     * user's ext;
     */
    private var ext: String? = null
    fun getUsername(): String? {
        return username
    }

    fun setUsername(username: String) {
        this.username = username
        lastModifyTimestamp = System.currentTimeMillis()
        lastModifyTimestamp = lastModifyTimestamp
    }

    fun getNickname(): String? {
        return if (TextUtils.isEmpty(nickname)) username else nickname
    }

    fun setNickname(nickname: String?) {
        this.nickname = nickname
        lastModifyTimestamp = System.currentTimeMillis()
        lastModifyTimestamp = lastModifyTimestamp
    }

    fun getInitialLetter(): String? {
        return if (initialLetter == null || lastModifyTimestamp > modifyInitialLetterTimestamp) {
            if (!TextUtils.isEmpty(nickname)) {
                getInitialLetter(nickname)
            } else getInitialLetter(username)
        } else initialLetter
    }

    fun setInitialLetter(initialLetter: String?) {
        this.initialLetter = initialLetter
        modifyInitialLetterTimestamp = System.currentTimeMillis()
    }

    fun getAvatar(): String? {
        return avatar
    }

    fun setAvatar(avatar: String?) {
        this.avatar = avatar
        lastModifyTimestamp = System.currentTimeMillis()
        lastModifyTimestamp = lastModifyTimestamp
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email = email
        lastModifyTimestamp = System.currentTimeMillis()
        lastModifyTimestamp = lastModifyTimestamp
    }

    fun getPhone(): String? {
        return phone
    }

    fun setPhone(phone: String?) {
        this.phone = phone
        lastModifyTimestamp = System.currentTimeMillis()
        lastModifyTimestamp = lastModifyTimestamp
    }

    fun getGender(): Int {
        return gender
    }

    fun setGender(gender: Int) {
        this.gender = gender
        lastModifyTimestamp = System.currentTimeMillis()
        lastModifyTimestamp = lastModifyTimestamp
    }

    fun getSign(): String? {
        return sign
    }

    fun setSign(sign: String?) {
        this.sign = sign
        lastModifyTimestamp = System.currentTimeMillis()
        lastModifyTimestamp = lastModifyTimestamp
    }

    fun getBirth(): String? {
        return birth
    }

    fun setBirth(birth: String?) {
        this.birth = birth
        lastModifyTimestamp = System.currentTimeMillis()
        lastModifyTimestamp = lastModifyTimestamp
    }

    fun getExt(): String? {
        return ext
    }

    fun setExt(ext: String?) {
        this.ext = ext
        lastModifyTimestamp = System.currentTimeMillis()
        lastModifyTimestamp = lastModifyTimestamp
    }

    fun getContact(): Int {
        return contact
    }

    fun setContact(contact: Int) {
        this.contact = contact
        lastModifyTimestamp = System.currentTimeMillis()
        lastModifyTimestamp = lastModifyTimestamp
    }

    private fun getInitialLetter(name: String?): String? {
        return GetInitialLetter().getLetter(name)
    }

    constructor() {}
    constructor(username: String) {
        this.username = username
    }

    override fun toString(): String {
        return "EaseUser{" +
                "username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", initialLetter='" + initialLetter + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", sign='" + sign + '\'' +
                ", birth='" + birth + '\'' +
                ", ext='" + ext + '\'' +
                ", contact=" + contact +
                '}'
    }

    inner class GetInitialLetter {
        private val defaultLetter = "#"

        /**
         * 获取首字母
         * @param name
         * @return
         */
        fun getLetter(name: String?): String? {
            if (name.isNullOrEmpty()) {
                return defaultLetter
            }
            val char0 = name.lowercase(Locale.getDefault())[0]
            if (Character.isDigit(char0)) {
                return defaultLetter
            }
            val l = HanziToPinyin.getInstance()[name.substring(0, 1)]
            if (l != null && l.isNotEmpty() && l[0].target.isNotEmpty()) {
                val token = l[0]
                val letter = token.target.substring(0, 1).uppercase(Locale.getDefault())
                val c = letter[0]
                return if (c < 'A' || c > 'Z') {
                    defaultLetter
                } else letter
            }
            return defaultLetter
        }
    }

    companion object {
        fun parse(ids: List<String>?): List<EaseUser> {
            val users: MutableList<EaseUser> = ArrayList()
            if (ids == null || ids.isEmpty()) {
                return users
            }
            var user: EaseUser
            for (id in ids) {
                user = EaseUser(id)
                users.add(user)
            }
            return users
        }

        fun parse(ids: Array<String>?): List<EaseUser> {
            val users: MutableList<EaseUser> = ArrayList()
            if (ids == null || ids.size == 0) {
                return users
            }
            var user: EaseUser
            for (id in ids) {
                user = EaseUser(id)
                users.add(user)
            }
            return users
        }

        fun parseUserInfo(userInfos: Map<String?, EMUserInfo?>?): List<EaseUser> {
            val users: MutableList<EaseUser> = ArrayList()
            if (userInfos == null || userInfos.isEmpty()) {
                return users
            }
            var user: EaseUser
            val userSet = userInfos.keys
            val it = userSet.iterator()
            while (it.hasNext()) {
                val userId = it.next()
                val info = userInfos[userId]
                user = EaseUser(info!!.userId)
                user.setNickname(info.nickName)
                user.setAvatar(info.avatarUrl)
                user.setEmail(info.email)
                user.setGender(info.gender)
                user.setBirth(info.birth)
                user.setSign(info.signature)
                user.setExt(info.ext)
                if (info.userId != EMClient.getInstance().currentUser) {
                    users.add(user)
                }
            }
            return users
        }
    }
}