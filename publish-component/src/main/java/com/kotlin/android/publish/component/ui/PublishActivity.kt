package com.kotlin.android.publish.component.ui

import android.content.Intent
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.annotation.PUBLISH_JOURNAL
import com.kotlin.android.app.data.annotation.PublishType
import com.kotlin.android.app.data.entity.search.Movie
import com.kotlin.android.app.data.entity.search.Person
import com.kotlin.android.image.component.getPhotoAlbumFragment
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.publish.component.Publish
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.databinding.ActPublishBinding
import com.kotlin.android.publish.component.showPublishFragment
import com.kotlin.android.publish.component.widget.PublishStyle
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.search.newcomponent.Search

/**
 * 发布页面：
 *
 * Created on 2020/9/15.
 *
 * @author o.s
 */
@Route(path = RouterActivityPath.Publish.PAGER_PUBLISH_ACTIVITY)
class PublishActivity : BaseVMActivity<PublishViewModel, ActPublishBinding>() {

    private var mPublishFragment: PublishFragment? = null
    private var mStyle: PublishStyle = PublishStyle.JOURNAL
    @PublishType
    private var mPublishType: Long = PUBLISH_JOURNAL
    private var mMovieId = 0L
    private var mMovieName = ""
    private var mFamilyId = 0L
    private var mFamilyName = ""

//    override fun getLayoutResId(): Int = R.layout.act_publish

    override fun initVM(): PublishViewModel = viewModels<PublishViewModel>().value

    override fun initView() {
        immersive().statusBarColor(getColor(R.color.color_ffffff))
                .statusBarDarkFont(true)
        mPublishFragment = showPublishFragment(R.id.rootView)
        syncData()
    }

    override fun getIntentData(intent: Intent?) {
        super.getIntentData(intent)
        intent?.apply {
            mPublishType = getLongExtra(Publish.KEY_TYPE, 0)
            mStyle = PublishStyle.obtain(mPublishType)
            mMovieId = getLongExtra(Publish.KEY_MOVIE_ID, 0)
            mMovieName = getStringExtra(Publish.KEY_MOVIE_NAME).orEmpty()
            mFamilyId = getLongExtra(Publish.KEY_FAMILY_ID, 0)
            mFamilyName = getStringExtra(Publish.KEY_FAMILY_NAME).orEmpty()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        syncData()
    }

    override fun initData() {
    }

    override fun startObserve() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 添加电影
        if (resultCode == Search.SEARCH_MOVIE_RESULT_CODE) {
            addMovie(data)
            return
        }
        // 添加影人
        if (resultCode == Search.SEARCH_PERSON_RESULT_CODE) {
            addPerson(data)
            return
        }
        // 选择家族回调
        if (resultCode == Publish.FAMILY_LIST_RESULT_CODE) {
            getIntentData(data)
            syncData()
            return
        }
        // 相册回调
        getPhotoAlbumFragment()?.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 同步发布数据
     */
    private fun syncData() {
        mPublishFragment?.apply {
            style = mStyle
            movieId = mMovieId
            movieName = mMovieName
            familyId = mFamilyId
            familyName = mFamilyName
            mPublishType = this@PublishActivity.mPublishType
        }
    }

    /**
     * 添加影片
     */
    private fun addMovie(data: Intent?) {
        data?.apply {
            val movie = getSerializableExtra(Search.KEY_SEARCH_DATA_MOVIE) as? Movie
            mPublishFragment?.apply {
                movie?.apply {
                    addMovieItem(this)
                }
            }
        }
    }

    /**
     * 添加影人
     */
    private fun addPerson(data: Intent?) {
        data?.apply {
            val person = getSerializableExtra(Search.KEY_SEARCH_DATA_PERSON) as? Person
            mPublishFragment?.apply {
                person?.apply {
                    // TODO: 2022/4/8 by vivian.wei
                    showToast(this.name)
//                    addPersonItem(this)
                }
            }
        }
    }

    /**
     * 添加关联文章
     */
    fun addArticle(id: Long, title: String) {
        // TODO: 2022/4/11 by vivian.wei
        showToast("id=$id  title=$title")
    }
}