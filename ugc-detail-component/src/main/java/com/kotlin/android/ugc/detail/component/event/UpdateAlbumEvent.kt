package com.kotlin.android.ugc.detail.component.event

import com.jeremyliao.liveeventbus.core.LiveEvent

/**
 * create by lushan on 2020/10/12
 * description:
 */
class UpdateAlbumEvent(var isUpdateSuccess:Boolean = false,//是否更新成功
                       var newAlbumName:String = ""//更新后的相册名称
):LiveEvent