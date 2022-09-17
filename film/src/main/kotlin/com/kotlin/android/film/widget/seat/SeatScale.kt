package com.kotlin.android.film.widget.seat

import android.content.Context
import android.view.ScaleGestureDetector

/**
 * 座位图缩放手势
 *
 * Created on 2022/2/17.
 *
 * @author o.s
 */
internal class SeatScale(
    private val context: Context,
    private val seatChart: SeatChart,
    val refresh: () -> Unit,
) {
    var isScaling = false
    private var firstScale = true
    var scaleX = 1F
    var scaleY = 1F

    fun reset() {
        firstScale = true
    }

    val gestureDetector by lazy {
        ScaleGestureDetector(context, object : ScaleGestureDetector.OnScaleGestureListener {
            /**
             * Responds to scaling events for a gesture in progress.
             * Reported by pointer motion.
             *
             * @param detector The detector reporting the event - use this to
             * retrieve extended info about event state.
             * @return Whether or not the detector should consider this event
             * as handled. If an event was not handled, the detector
             * will continue to accumulate movement until an event is
             * handled. This can be useful if an application, for example,
             * only wants to update scaling factors if the change is
             * greater than 0.01.
             */
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                isScaling = true
                detector?.apply {
                    if (firstScale) {
                        scaleX = currentSpanX
                        scaleY = currentSpanY
                        firstScale = false
                    }
                    seatChart.postMatrixScale(scaleFactor, scaleFactor, scaleX, scaleY)
                    refresh()
                }
                return true
            }

            /**
             * Responds to the beginning of a scaling gesture. Reported by
             * new pointers going down.
             *
             * @param detector The detector reporting the event - use this to
             * retrieve extended info about event state.
             * @return Whether or not the detector should continue recognizing
             * this gesture. For example, if a gesture is beginning
             * with a focal point outside of a region where it makes
             * sense, onScaleBegin() may return false to ignore the
             * rest of the gesture.
             */
            override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean = true

            /**
             * Responds to the end of a scale gesture. Reported by existing
             * pointers going up.
             *
             * Once a scale has ended, [ScaleGestureDetector.getFocusX]
             * and [ScaleGestureDetector.getFocusY] will return focal point
             * of the pointers remaining on the screen.
             *
             * @param detector The detector reporting the event - use this to
             * retrieve extended info about event state.
             */
            override fun onScaleEnd(detector: ScaleGestureDetector?) {
                isScaling = false
                firstScale = true
            }

        })
    }
}