package com.kotlin.android.ktx

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.lifecycle.ProcessLifecycleOwner
import com.kotlin.android.ktx.ext.log.d
import com.kotlin.android.ktx.lifecycle.KtxActivityLifecycleCallback
import com.kotlin.android.ktx.lifecycle.KtxAppLifecycleObserver

/**
 * Ktx应用启动时被加载，
 * 管理 app 和 Activity 的生命周期
 *
 * Created on 2020/4/21.
 *
 * @author o.s
 */
class Ktx : ContentProvider() {

    companion object {
        lateinit var app: Application
        var appLifecycle = true
        var activityLifecycle = true
    }


    override fun onCreate(): Boolean {
        val application = context!!.applicationContext as Application
        install(application)
        return true
    }

    private fun install(application: Application) {
        "Ktx :: install".d()
//        toast(context, "Ktx :: install")
        app = application
        if (activityLifecycle) {
            app.registerActivityLifecycleCallbacks(KtxActivityLifecycleCallback())
        }
        if (appLifecycle) {
            ProcessLifecycleOwner.get().lifecycle.addObserver(KtxAppLifecycleObserver())
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun getType(uri: Uri): String? = null

}