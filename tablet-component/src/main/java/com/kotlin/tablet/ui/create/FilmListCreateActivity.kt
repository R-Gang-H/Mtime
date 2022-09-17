package com.kotlin.tablet.ui.create

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.entity.filmlist.FilmListEditInfo
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.app.data.entity.search.Movie
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.core.ext.showDialogFragment
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.image.component.PhotoAlbumDialogFragment
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.android.widget.titlebar.back
import com.kotlin.tablet.KEY_FILM_LIST_ID
import com.kotlin.tablet.KEY_FROM_EDIT
import com.kotlin.tablet.KEY_IS_EDIT
import com.kotlin.tablet.R
import com.kotlin.tablet.databinding.ActivityFilmListCreateBinding
import com.kotlin.tablet.ui.add.FilmCart

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/10
 * 描述:创建、编辑片单
 **/
@Route(path = RouterActivityPath.TABLET.FILM_LIST_CREATE)
class FilmListCreateActivity :
    BaseVMActivity<FilmListCreateViewModel, ActivityFilmListCreateBinding>() {
    private var mCoverUrl: String? = ""
    private var mCoverFieldId: String = ""
    private var mFilmListId = 0L
    private var editInfo: FilmListEditInfo? = null
        set(value) {
            field = value
            bindData(field)
        }

    private var moviesId: List<Long>? = null
        set(value) {
            field = value
            updateContainsCount(field?.size?.toLong())
        }

    companion object {
        const val TITLE_LIMIT_COUNT = 20
        const val CONTENT_LIMIT_COUNT = 60
    }

    override fun getIntentData(intent: Intent?) {
        FilmCart.isEdit = intent?.getBooleanExtra(KEY_IS_EDIT, true) ?: true
        mFilmListId = intent?.getLongExtra(KEY_FILM_LIST_ID, 0L) ?: 0L
    }

    override fun initTheme() {
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        TitleBarManager.with(this)
            .setTitle(
                title = if (FilmCart.isEdit) getString(R.string.tablet_film_list_edit) else getString(
                    R.string.tablet_film_list_create
                ),
            ).back {
                finish()
            }
    }

    override fun initView() {
        mBinding?.apply {
            mFilmListTitleLimitTv.text = String.format(
                getString(R.string.tablet_film_list_text_limit),
                0,
                TITLE_LIMIT_COUNT.toString()
            )

            mFilmListContentLimitTv.text = String.format(
                getString(R.string.tablet_film_list_text_limit),
                0,
                CONTENT_LIMIT_COUNT.toString()
            )

            mFilmListTitleEt.limit(mFilmListTitleLimitTv, TITLE_LIMIT_COUNT)
            mFilmListContentEt.limit(mFilmListContentLimitTv, CONTENT_LIMIT_COUNT)

            mFilmListCoverIv.loadImage(
                data = R.drawable.icon_film_list_bg_h,
                width = 185.dp,
                height = 100.dp,
                roundedRadius = 10.dpF
            )
            //编辑状态显示包含影片
            if (FilmCart.isEdit) {
                mContainsGroup.visible()
            } else {
                mContainsGroup.gone()
            }
        }
        initEvent()
    }

    private fun initEvent() {
        mBinding?.apply {
            //添加封面
            mFilmListCoverIv.onClick {
                takePhotos { photos ->
                    photos.firstOrNull()?.apply {
                        mCoverUrl = url
                        mCoverFieldId = fileID ?: ""
                        // 更新封面图
                        updateImg(uri)
                    }
                }
            }
            //保存片单
            mFilmListSaveBtn.onClick {
                if (mFilmListTitleEt.text.toString().isBlank()) {
                    showToast(getString(R.string.tablet_film_list_title_empty_show))
                    return@onClick
                }
                mViewModel?.save(
                    mFilmListId,
                    FilmCart.isEdit,
                    mFilmListTitleEt.text.toString(),
                    mFilmListContentEt.text.toString(),
                    mCoverUrl ?: "",
                    mCoverFieldId,
                    mOpenFilmListCb.isChecked,
                    moviesId
                )
            }
            //包含影片
            mContainsIv.onClick {
                getProvider(ITabletProvider::class.java)?.startFilmSelectedActivity(
                    mFilmListId,
                    KEY_FROM_EDIT
                )
            }
        }
    }

    /**
     * 封面
     */
    private fun takePhotos(limit: Int = 1, completed: (ArrayList<PhotoInfo>) -> Unit) {
        showDialogFragment(
            clazz = PhotoAlbumDialogFragment::class.java
        )?.apply {
            needUpload = true
            limitPhotoSelectCount = limit
            actionSuccessPhotos = {
                completed.invoke(it)
            }
        }
    }

    override fun initData() {
        if (FilmCart.isEdit) {
            mViewModel?.reqFilmListInfo(mFilmListId)
            mViewModel?.getModifyMovies(mFilmListId)
        }
    }

    override fun startObserve() {
        observeCreate()
        observeEditInfo()
        observerEditSave()
        observeModifyMovies()
    }

    private fun observerEditSave() {
        mViewModel?.editUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)
                success?.apply {
                    if (bizCode == 0L) {
                        FilmCart.instance.clear()
                        bizMsg?.showToast()
                        finish()
                    } else {
                        showToast(bizMsg ?: "编辑失败，请稍后再试")
                    }
                }
                error?.showToast()
                netError?.showToast()
            }
        }
    }

    private fun observeEditInfo() {
        mViewModel?.filmListInfoState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)
                success?.apply {
                    editInfo = this
                }
                error?.showToast()
                netError?.showToast()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindData(info: FilmListEditInfo?) {
        mBinding?.apply {
            info?.apply {
                if (mViewModel?.isFirstRefresh == true) {
                    mFilmListTitleEt.setText(title)
                    mFilmListContentEt.setText(synopsis)
                    mCoverUrl = coverUrl
                    updateImg(mCoverUrl)
                    mOpenFilmListCb.isChecked = privacyStatus == 1L
                }
                updateContainsCount(numMovie)
                mViewModel?.isFirstRefresh = false
            }
        }
    }

    private fun observeCreate() {
        mViewModel?.createUiState?.observe(this) { it ->
            it?.apply {
                showProgressDialog(it.showLoading)
                success?.apply {
                    if (bizCode == 0L) {
                        if (FilmCart.isEdit.not()) {
                            //跳转成功页
                            getProvider(ITabletProvider::class.java)
                                ?.startFilmListCreateSuccessActivity(simpleFilmList)
                        }
                        finish()
                    } else {
                        bizMsg?.showToast()
                    }
                }
                error?.showToast()
                netError?.showToast()
            }
        }
    }


    /**
     * 监听编辑状态 已选影片
     */
    private fun observeModifyMovies() {
        mViewModel?.modifyMoviesUiState?.observe(this) { it ->
            it.apply {
                showProgressDialog(showLoading)
                success?.apply {
                    movies?.forEach {
                        FilmCart.instance.update(
                            Movie(
                                img = it.imageUrl,
                                movieId = it.movieId,
                                name = it.titleCn,
                                nameEn = it.titleEn,
                                year = it.year
                            )
                        )
                    }
                    moviesId = FilmCart.instance.getSelectedIds()
                }
                error?.showToast()
                netError?.showToast()
            }
        }
    }
    /**
     * 更新封面图
     */
    private fun updateImg(path: Any?) {
        path?.let {
            // 封面图
            mBinding?.mFilmListCoverIv?.loadImage(
                data = it,
                width = 185.dp,
                height = 100.dp,
                defaultImgRes = R.drawable.icon_film_list_bg_h
            )
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateContainsCount(size: Long? = 0) {
        mBinding?.mContainsNumTv?.text = "等${size}部"
    }

    override fun onRestart() {
        super.onRestart()
        if (FilmCart.isEdit){
            if (FilmCart.isSave){
                moviesId = FilmCart.instance.getSelectedIds()
            }
        }
    }

    /**
     * 统计EditText文字输入
     */
    private fun EditText.limit(tv: TextView, maxCount: Int) {
        addTextChangedListener {
            it?.apply {
                tv.text = String.format(
                    getString(R.string.tablet_film_list_text_limit),
                    it.length.toString(),
                    maxCount.toString()
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        FilmCart.instance.clear()
    }
}