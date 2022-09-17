package com.kotlin.android.ugc.detail.component.ui.link

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.screenHeight
import com.kotlin.android.ktx.ext.dimension.statusBarHeight
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.binder.UgcLinkActorBinder
import com.kotlin.android.ugc.detail.component.binder.UgcLinkMovieBinder
import com.kotlin.android.ugc.detail.component.binder.UgcLinkTitleBinder
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import kotlinx.android.synthetic.main.frag_ugc_link.*

/**
 * create by lushan on 2022/3/14
 * des:关联内容弹框
 **/
class UgcLinkFragment : DialogFragment() {
    private var binderList: MutableList<MultiTypeBinder<*>> = mutableListOf()
    private var adapter: MultiTypeAdapter? = null
    var ugcListener: ((MultiTypeBinder<*>) -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogFullScreen)
    }


    fun setBinderList(binderList: MutableList<MultiTypeBinder<*>>) {
        this.binderList.clear()
        this.binderList.addAll(binderList)
        adapter?.notifyAdapterDataSetChanged(this.binderList)
        if (this.binderList.isNotEmpty()){
            setTitle(binderList[0])
        }

    }

    private fun setTitle(binder:MultiTypeBinder<*>){
        linkTypeTitleTv?.text = when (binder) {
            is UgcLinkTitleBinder -> {
                binder.title
            }
            is UgcLinkMovieBinder -> {
                getString(R.string.ugc_detail_movie)
            }
            is UgcLinkActorBinder -> {
                getString(R.string.ugc_detail_actor)
            }
            else -> {
                getString(R.string.ugc_detail_recommend)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.run {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.run {
                decorView.setPadding(0, 0, 0, 0)
                attributes.run {
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                    gravity = Gravity.BOTTOM
                    windowAnimations = R.style.BottomDialogAnimation
                }
            }
        }
        val view = inflater.inflate(R.layout.frag_ugc_link, null)
        view.setBackground(
            colorRes = android.R.color.white,
            cornerRadius = 4.dpF,
            direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
        )

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linkCloseIv?.onClick { dismiss() }
        linkTitleCL?.setBackground(
            colorRes = android.R.color.white,
            cornerRadius = 4.dpF,
            direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
        )
        initRecyclerView()
        rootRl?.layoutParams?.apply {
            height = screenHeight - statusBarHeight - 60.dp
        }?.also {
            rootRl?.layoutParams = it
        }

    }

    private fun initRecyclerView() {
        adapter = createMultiTypeAdapter(linkRv, LinearLayoutManager(context))
        adapter?.setOnClickListener(::handleWannaSee)
        linkRv?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as? LinearLayoutManager?
                val index = linearLayoutManager?.findFirstVisibleItemPosition().orZero()
                if (index >= 0 && index < binderList.size) {
                    var multiTypeBinder = binderList[index]
                    setTitle(multiTypeBinder)
                }

            }
        })
    }

    private fun handleWannaSee(view: View, multiTypeBinder: MultiTypeBinder<*>) {
        if (view.id == R.id.movieBtnFl) {
            if (multiTypeBinder is UgcLinkMovieBinder) {
                ugcListener?.invoke(multiTypeBinder)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}