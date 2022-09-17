package com.kotlin.android.player.widgets.videodialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.player.R
import com.kotlin.android.player.splayer.PreviewVideoPlayer
import kotlinx.android.synthetic.main.frag_video_dialog.*

/**
 * create by lushan on 2022/3/21
 * des:
 **/
class VideoDialogFragment : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ImmersiveDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.run {
            window?.run {
                decorView.setPadding(0, 0, 0, 0)
                attributes.run {
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.MATCH_PARENT
                }
                setWindowAnimations(R.style.BottomDialogAnimation)
            }
        }

        return inflater.inflate(R.layout.frag_video_dialog, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.apply {
            activity?.immersive(this)
                ?.updateStatusBarColor(getColor(R.color.color_ffffff))
                ?.statusBarDarkFont(true)
        }
    }

    fun updateVideo() {
        PreviewVideoPlayer.get()?.apply {
            attachContainer(videoContainer)
        }

    }

    override fun dismissAllowingStateLoss() {
        videoContainer?.removeAllViews()
        super.dismissAllowingStateLoss()
    }


    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (activity as? DialogInterface.OnDismissListener)?.onDismiss(dialog)
    }

}