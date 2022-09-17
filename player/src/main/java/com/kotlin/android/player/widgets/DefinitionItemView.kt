package com.kotlin.android.player.widgets

import android.content.Context
import android.graphics.Color
import androidx.appcompat.widget.AppCompatTextView
import com.kotlin.android.player.R

/**
 * Created by mtime on 2017/10/19.
 */
class DefinitionItemView(context: Context?) : AppCompatTextView(context!!) {
    var key = "XX"
    fun setCurrentItemKey(currentItemKey: String) {
        if (key == currentItemKey) {
            setTextColor(Color.parseColor("#FF8600"))
            setBackgroundResource(R.drawable.player_sdk_shape_definition_orange)
        } else {
            setTextColor(Color.parseColor("#FFFFFF"))
            setBackgroundResource(R.drawable.player_sdk_shape_definition_transparent)
        }
    }
}