package com.kotlin.android.widget.titlebar

import android.view.View
import com.kotlin.android.widget.R

/**
 * 所有标题Icon扩展
 *
 * Created on 2022/3/18.
 *
 * @author o.s
 */

fun TitleBar.back(
    click: ((v: View) -> Unit)? = null
): TitleBar {
    addItem(
        drawableRes = R.drawable.ic_title_bar_36_back,
        reverseDrawableRes = R.drawable.ic_title_bar_36_back_reversed,
        click = click
    )
    return this
}

fun TitleBar.moreH(
    click: ((v: View) -> Unit)? = null
): TitleBar {
    addItem(
        isReversed = true,
        drawableRes = R.drawable.ic_title_bar_36_more_h,
        reverseDrawableRes = R.drawable.ic_title_bar_36_more_h_reversed,
        click = click
    )
    return this
}

fun TitleBar.share1(
    click: ((v: View) -> Unit)? = null
): TitleBar {
    addItem(
        isReversed = true,
        drawableRes = R.drawable.ic_title_bar_36_share_1,
        reverseDrawableRes = R.drawable.ic_title_bar_36_share_1_reversed,
        click = click
    )
    return this
}

fun TitleBar.share2(
    click: ((v: View) -> Unit)? = null
): TitleBar {
    addItem(
        isReversed = true,
        drawableRes = R.drawable.ic_title_bar_36_share_2,
        reverseDrawableRes = R.drawable.ic_title_bar_36_share_2_reversed,
        click = click
    )
    return this
}

fun TitleBar.search(
    click: ((v: View) -> Unit)? = null
): TitleBar {
    addItem(
        isReversed = true,
        drawableRes = R.drawable.ic_title_bar_36_search,
        reverseDrawableRes = R.drawable.ic_title_bar_36_search_reversed,
        click = click
    )
    return this
}

fun TitleBar.setting(
    click: ((v: View) -> Unit)? = null
): TitleBar {
    addItem(
        isReversed = true,
        drawableRes = R.drawable.ic_title_bar_36_setting,
        reverseDrawableRes = R.drawable.ic_title_bar_36_setting_reversed,
        click = click
    )
    return this
}

fun TitleBar.scan(
    isReversed: Boolean,
    click: ((v: View) -> Unit)? = null
): TitleBar {
    addItem(
        isReversed = isReversed,
        drawableRes = R.drawable.ic_title_bar_36_scan,
        reverseDrawableRes = R.drawable.ic_title_bar_36_scan_reversed,
        click = click
    )
    return this
}

fun TitleBar.message(
    click: ((v: View) -> Unit)? = null
): TitleBar {
    addItem(
        isReversed = true,
        drawableRes = R.drawable.ic_title_bar_36_message,
        reverseDrawableRes = R.drawable.ic_title_bar_36_message_reversed,
        click = click
    )
    return this
}

fun TitleBar.info(
    click: ((v: View) -> Unit)? = null
): TitleBar {
    addItem(
        isReversed = true,
        drawableRes = R.drawable.ic_title_bar_36_info,
        reverseDrawableRes = R.drawable.ic_title_bar_36_info_reversed,
        click = click
    )
    return this
}

fun TitleBar.fans(
    click: ((v: View) -> Unit)? = null
): TitleBar {
    addItem(
        isReversed = true,
        drawableRes = R.drawable.ic_title_bar_36_fans,
        reverseDrawableRes = R.drawable.ic_title_bar_36_fans_reversed,
        click = click
    )
    return this
}

fun TitleBar.help(
    click: ((v: View) -> Unit)? = null
): TitleBar {
    addItem(
        isReversed = true,
        drawableRes = R.drawable.ic_title_bar_36_help,
        reverseDrawableRes = R.drawable.ic_title_bar_36_help_reversed,
        click = click
    )
    return this
}

fun TitleBar.add(
    click: ((v: View) -> Unit)? = null
): TitleBar {
    addItem(
        isReversed = true,
        drawableRes = R.drawable.ic_title_bar_36_add,
        reverseDrawableRes = R.drawable.ic_title_bar_36_add_reversed,
        click = click
    )
    return this
}

fun TitleBar.filter(
    click: ((v: View) -> Unit)? = null
): TitleBar {
    addItem(
        isReversed = true,
        drawableRes = R.drawable.ic_title_bar_36_filter,
        reverseDrawableRes = R.drawable.ic_title_bar_36_filter_reversed,
        click = click
    )
    return this
}