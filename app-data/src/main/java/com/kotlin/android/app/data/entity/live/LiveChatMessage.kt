package com.kotlin.android.app.data.entity.live

import com.kotlin.android.app.data.ProguardRule

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/3/15
 *
 * {"userId":25325129,"roomNum":"21031709487hdd","message":"will啊OK","clientType":1,"userName":"小强","headIcon":"http://img5.mtime.cn/up/2018/03/13/154604.32977364_o.jpg","messageId":"16218495-588e-436e-859d-e92508926cc5"}
 *
 * 直播聊天信息API数据实体
 */
data class LiveChatMessage(
        var userName: String?,
        var clientType: Long?,
        var roomNum: String?,
        var message: String?,
        var headIcon: String?,
        var userId: Long?,
        var messageId: String?,
        var chatType: Long? //1：普通消息 2：欢迎消息
) : ProguardRule
