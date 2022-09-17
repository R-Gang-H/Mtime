package com.kotlin.android.player.receivers

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import com.kk.taurus.playerbase.receiver.BaseReceiver
import com.kk.taurus.playerbase.touch.OnTouchGestureListener

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/9/7
 */
class TouchGestureListenerReceiver(var ctx: Context, val listener: OnTouchGestureListener) : BaseReceiver(ctx), OnTouchGestureListener {
    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {

    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {

    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {

    }

    override fun onSingleTapConfirmed(event: MotionEvent?) {
        listener.onSingleTapConfirmed(event)
    }

    override fun onLongPress(event: MotionEvent?) {
        listener.onLongPress(event)
    }

    override fun onDoubleTap(event: MotionEvent?) {
        listener.onDoubleTap(event)
    }

    override fun onDown(event: MotionEvent?) {
        listener.onDown(event)
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float) {
        listener.onScroll(e1, e2, distanceX, distanceY)
    }

    override fun onEndGesture() {
        listener.onEndGesture()
    }
}

interface SimpleTouchGestureListener : OnTouchGestureListener {
    override fun onSingleTapConfirmed(event: MotionEvent?) {

    }

    override fun onLongPress(event: MotionEvent?) {

    }

    override fun onDoubleTap(event: MotionEvent?) {

    }

    override fun onDown(event: MotionEvent?) {

    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float) {

    }

    override fun onEndGesture() {

    }
}