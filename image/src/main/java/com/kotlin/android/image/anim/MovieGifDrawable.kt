package com.kotlin.android.image.anim

import android.graphics.*
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.SystemClock
import androidx.core.graphics.withSave
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import coil.bitmap.BitmapPool
import coil.decode.DecodeUtils
import coil.size.Scale
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.log.w

/**
 * Movie GIF 处理
 *
 * Created on 2021/2/25.
 *
 * @author o.s
 */
class MovieGifDrawable @JvmOverloads constructor(
        private val movie: Movie,
        private val pool: BitmapPool = BitmapPool(0),
        val config: Bitmap.Config = Bitmap.Config.ARGB_8888,
        val scale: Scale = Scale.FIT
) : Drawable(), Animatable2Compat {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

    private val callbacks = mutableListOf<Animatable2Compat.AnimationCallback>()

    private var currentBounds: Rect? = null
    private var softwareCanvas: Canvas? = null
    private var softwareBitmap: Bitmap? = null

    private var softwareScale = 1f
    private var hardwareScale = 1f
    private var hardwareDx = 0f
    private var hardwareDy = 0f

    private var isRunning = false
    private var startTimeMillis = 0L
    private var frameTimeMillis = 0L

    private var repeatCount = REPEAT_INFINITE
    private var loopIteration = 0

    init {
        require(Build.VERSION.SDK_INT < 26 || config != Bitmap.Config.HARDWARE) { "Bitmap config must not be hardware." }
    }

    override fun draw(canvas: Canvas) {
        // onBoundsChange must be called first.
        val softwareCanvas = softwareCanvas ?: return
        val softwareBitmap = softwareBitmap ?: return

        val invalidate: Boolean
        val time: Int
        val duration = movie.duration()
        if (duration == 0) {
            invalidate = false
            time = 0
        } else {
            if (isRunning) {
                frameTimeMillis = SystemClock.uptimeMillis()
            }
            val elapsedTime = (frameTimeMillis - startTimeMillis).toInt()
            loopIteration = elapsedTime / duration
            invalidate = repeatCount == REPEAT_INFINITE || loopIteration <= repeatCount
            time = if (invalidate) elapsedTime - loopIteration * duration else duration
        }
        movie.setTime(time)

        // Clear the software canvas.
        softwareCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        // Draw onto a software canvas first.
        softwareCanvas.withSave {
            scale(softwareScale, softwareScale)
            movie.draw(this, 0f, 0f, paint)
        }

        // Draw onto the input canvas (may or may not be hardware).
        canvas.withSave {
            translate(hardwareDx, hardwareDy)
            scale(hardwareScale, hardwareScale)
            drawBitmap(softwareBitmap, 0f, 0f, paint)
        }

        "draw duration = $duration, time = $time, loopIteration = $loopIteration, (frameTimeMillis, startTimeMillis) = ($frameTimeMillis, $startTimeMillis), (hardwareDx, hardwareDy) = ($hardwareDx, $hardwareDy), softwareScale = $softwareScale, hardwareScale = $hardwareScale".e()
        if (isRunning && invalidate) {
            invalidateSelf()
        } else {
            stop()
        }
    }

    /**
     * Set the number of times to repeat the animation.
     *
     * If the animation is already running, any iterations that have already occurred will count towards the new count.
     *
     * NOTE: This method matches the behavior of [AnimatedImageDrawable.setRepeatCount]. i.e. setting [repeatCount] to 2 will
     * result in the animation playing 3 times. Setting [repeatCount] to 0 will result in the animation playing once.
     *
     * Default: [REPEAT_INFINITE]
     */
    fun setRepeatCount(repeatCount: Int) {
        require(repeatCount >= REPEAT_INFINITE) { "Invalid repeatCount: $repeatCount" }
        this.repeatCount = repeatCount
    }

    /** Get the number of times the animation will repeat. */
    fun getRepeatCount(): Int = repeatCount

    override fun setAlpha(alpha: Int) {
        require(alpha in 0..255) { "Invalid alpha: $alpha" }
        paint.alpha = alpha
    }

    override fun getOpacity(): Int {
        return if (paint.alpha == 255 && movie.isOpaque) {
            PixelFormat.OPAQUE
        } else {
            PixelFormat.TRANSLUCENT
        }
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun onBoundsChange(bounds: Rect) {
        if (currentBounds == bounds) return
        currentBounds = bounds

        val boundsWidth = bounds.width()
        val boundsHeight = bounds.height()

        // If width/height are not positive, we're unable to draw the movie.
        val movieWidth = movie.width()
        val movieHeight = movie.height()
        if (movieWidth <= 0 || movieHeight <= 0) return

        softwareScale = DecodeUtils
                .computeSizeMultiplier(movieWidth, movieHeight, boundsWidth, boundsHeight, scale)
                .toFloat()
                .coerceAtMost(1f)
        val bitmapWidth = (softwareScale * movieWidth).toInt()
        val bitmapHeight = (softwareScale * movieHeight).toInt()

        val bitmap = pool.get(bitmapWidth, bitmapHeight, config)
        softwareBitmap?.let(pool::put)
        softwareBitmap = bitmap
        softwareCanvas = Canvas(bitmap)
        "1# onBoundsChange softwareBitmap = $softwareBitmap, softwareScale = $softwareScale, hardwareScale = $hardwareScale, (hardwareDx, hardwareDy) = ($hardwareDx, $hardwareDy), (movieWidth, movieHeight) = ($movieWidth, $movieHeight), (bitmapWidth, bitmapHeight) = ($bitmapWidth, $bitmapHeight)".w()

        hardwareScale = DecodeUtils.computeSizeMultiplier(bitmapWidth, bitmapHeight, boundsWidth, boundsHeight, scale).toFloat()
        hardwareDx = bounds.left + (boundsWidth - hardwareScale * bitmapWidth) / 2
        hardwareDy = bounds.top + (boundsHeight - hardwareScale * bitmapHeight) / 2
        "2# onBoundsChange softwareBitmap = $softwareBitmap, softwareScale = $softwareScale, hardwareScale = $hardwareScale, (hardwareDx, hardwareDy) = ($hardwareDx, $hardwareDy), (movieWidth, movieHeight) = ($movieWidth, $movieHeight), (bitmapWidth, bitmapHeight) = ($bitmapWidth, $bitmapHeight)".w()
    }

    override fun getIntrinsicWidth() = movie.width()

    override fun getIntrinsicHeight() = movie.height()

    override fun isRunning() = isRunning

    override fun start() {
        if (isRunning) return
        isRunning = true

        loopIteration = 0
        startTimeMillis = SystemClock.uptimeMillis()

        for (i in callbacks.indices) {
            callbacks[i].onAnimationStart(this)
            "callbacks[i] = ${callbacks[i]}".w()
        }

        invalidateSelf()
    }

    override fun stop() {
        if (!isRunning) return
        isRunning = false

        for (i in callbacks.indices) {
            callbacks[i].onAnimationEnd(this)
        }
    }

    override fun registerAnimationCallback(callback: Animatable2Compat.AnimationCallback) {
        callbacks.add(callback)
    }

    override fun unregisterAnimationCallback(callback: Animatable2Compat.AnimationCallback): Boolean {
        return callbacks.remove(callback)
    }

    override fun clearAnimationCallbacks() = callbacks.clear()

    companion object {
        /** Pass this to [setRepeatCount] to repeat infinitely. */
        const val REPEAT_INFINITE = -1
    }
}
