package com.kotlin.android.home.ui.recommend.adapter

import android.graphics.Typeface
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.router.ext.openFilm
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ItemShowingComingBinding
import com.kotlin.android.home.databinding.ItemShowingComingMovieBinding
import com.kotlin.android.home.ui.recommend.bean.ShowingComingCategoryItem
import com.kotlin.android.home.ui.recommend.bean.ShowingComingMovieItem
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.statelist.getColorStateList
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/10
 * 
 * 推荐页面-正在热映和待映推荐
 */
class ShowingComingBinder(val data: ShowingComingCategoryItem) :
    BaseRecommendHeadBinder<ItemShowingComingBinding>() {

    private var mAdapter: MultiTypeAdapter? = null

    override fun layoutId(): Int {
        return R.layout.item_showing_coming
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ShowingComingBinder && other.data == data
    }

    private fun setTabSelected(isSelect: Boolean, view: TextView) {
        view.apply {
            isSelected = isSelect
            typeface = if (isSelected) {
                textSize = 18f
                Typeface.defaultFromStyle(Typeface.BOLD)
            } else {
                textSize = 14f
                Typeface.defaultFromStyle(Typeface.NORMAL)
            }
        }
    }

    override fun onBindViewHolder(binding: ItemShowingComingBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.mShowingComingAllTv.onClick {
            if (binding.mShowingComingTab1.isSelected) {
                openFilm(subPosition = 0)
            } else {
                openFilm(subPosition = 2)
            }
        }

        binding.mShowingComingTab1.apply {
            isVisible = data.showingList.isNullOrEmpty().not()
        }.onClick {
            setTabSelected(true, it)
            setTabSelected(false, binding.mShowingComingTab2)
            data.showingList?.apply {
                mAdapter?.notifyAdapterDataSetChanged(binders = this, isScrollToTop = false)
            }
        }

        binding.mShowingComingTab2.apply {
            isVisible = data.comingList.isNullOrEmpty().not()
        }.onClick {
            setTabSelected(true, it)
            setTabSelected(false, binding.mShowingComingTab1)
            data.comingList?.apply {
                mAdapter?.notifyAdapterDataSetChanged(binders = this, isScrollToTop = false)
            }
        }

        setMovieAdapter(binding)

        if (!data.showingList.isNullOrEmpty()) {
            binding.mShowingComingTab1.callOnClick()
        } else if (!data.comingList.isNullOrEmpty()) {
            binding.mShowingComingTab2.callOnClick()
        }
    }

    private fun setMovieAdapter(
        binding: ItemShowingComingBinding
    ) {
        mAdapter = createMultiTypeAdapter(binding.mShowingComingMovieListRv).apply {
            mOnClickListener?.let {
                setOnClickListener(it)
            }
        }
    }
}

class ShowingComingMovieItemBinder(val movieItem: ShowingComingMovieItem) :
    MultiTypeBinder<ItemShowingComingMovieBinding>() {

    val mTicketProvider: ITicketProvider? by lazy { getProvider(ITicketProvider::class.java) }

    override fun layoutId(): Int = R.layout.item_showing_coming_movie

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean =
        other is ShowingComingMovieItemBinder
                && (other.movieItem.btnState != movieItem.btnState
                || other.movieItem.id != movieItem.id)

    override fun onPreBindViewHolder(binding: ItemShowingComingMovieBinding, position: Int) {
        super.onPreBindViewHolder(binding, position)
        binding.mMovieMtimeScoreTv.apply {
            isVisible = movieItem.showScoreOrWantSee()
            text = movieItem.getFormatScoreOrWantSee()
        }
        binding.mMovieTagsLL.apply {
            removeAllViews()
            isVisible = movieItem.tag.isNotEmpty()
            if (isVisible) {
                val tags = movieItem.tag.split(",")
                tags.forEach {
                    val textView = TextView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        marginTop = 2.dp
                        setBackground(
                            colorRes = R.color.color_99000000,
                            cornerRadius = 4.dpF
                        )
                        updatePadding(3.dp, 1.dp, 3.dp, 1.dp)
                        textSize = 9f
                        setTextColorRes(R.color.color_ffffff)
                        text = it
                    }
                    addView(textView)
                }
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mMovieBtnFl -> {
                onBtnClick(view)
            }
            else -> {
                super.onClick(view)
            }
        }
    }

    //购票、预售、想看按钮点击事件
    private fun onBtnClick(view: View) {
        when (movieItem.btnState) {
            CommConstant.MOVIE_BTN_STATE_PRESELL,
            CommConstant.MOVIE_BTN_STATE_TICKET -> { //预售、购票
                mTicketProvider?.startMovieShowtimeActivity(movieId = movieItem.id)
            }
            else -> {  //已想看 //想看
                super.onClick(view)
            }
        }
    }

    /**
     * 想看状态变动
     */
    fun wantToSeeChanged() {
        movieItem.btnState = if (movieItem.btnState
            == CommConstant.MOVIE_BTN_STATE_WANT_SEE
        ) {
            movieItem.wantSeeCount++
            CommConstant.MOVIE_BTN_STATE_WANT_SEEN
        } else {
            movieItem.wantSeeCount--
            CommConstant.MOVIE_BTN_STATE_WANT_SEE
        }
        notifyAdapterSelfChanged()
    }
}