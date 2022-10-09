package com.muath.myswiperefresh.jellyrefresh

import kotlin.jvm.JvmOverloads
import android.widget.FrameLayout
import com.muath.myswiperefresh.jellyrefresh.JellyLayout.DrawingState
import android.animation.ValueAnimator
import androidx.annotation.ColorInt
import android.view.ViewOutlineProvider
import com.muath.myswiperefresh.jellyrefresh.JellyLayout
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.graphics.minus
import kotlin.math.sin

internal class JellyLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(
    context!!, attrs, defStyleAttr
) {
    internal enum class DrawingState {
        PULLING_STATE, LOADING_STATE
    }

    fun setDrawingState(mDrawingState: DrawingState?) {
        this.mDrawingState = mDrawingState
    }
    private var wavePaint = Paint()
    private var mDrawingState: DrawingState? = null
    private val amplitude = 20f // scale
    var waveAnimation: ValueAnimator? = null
    fun setSpeed(speed: Float) {
        this.speed = speed
    }

    private var speed = 0f
    private var mPaint: Paint? = null
    private var mPath: Path? = null

    @ColorInt
    private var mColor = Color.GRAY
    private var mViewOutlineProvider: ViewOutlineProvider? = null
    private var mPointX = 0f
    @JvmField
    var mHeaderHeight = 0f
    @JvmField
    var mPullHeight = 0f
    private var pathWave: Path? = null
    private fun init() {
        setWillNotDraw(false)
        pathWave = Path()
        this.wavePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        wavePaint.color = Color.GREEN
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.style = Paint.Style.FILL
        waveAnimation = createAnimator()
        mPath = Path()
        mDrawingState = DrawingState.PULLING_STATE
        mViewOutlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                if (mPath!!.isConvex) outline.setConvexPath(mPath!!)
            }
        }
    }

    fun setColor(color: Int) {
        mColor = color
    }

    fun setPointX(pointX: Float) {
        val needInvalidate = pointX != mPointX
        mPointX = pointX
        if (needInvalidate) invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mDrawingState == DrawingState.LOADING_STATE) {
            createWave()
            //mPath = mPath!!.minus(pathWave!!)
            canvas.drawPath(mPath!!, mPaint!!)
            canvas.drawPath(pathWave!!, mPaint!!)

        } else {
            drawPulling(canvas)
            canvas.drawPath(mPath!!, mPaint!!)
        }
    }

    private fun drawPulling(canvas: Canvas) {
        val width = canvas.width
        val mDisplayX = (mPointX - width / 2f) * 0.5f + width / 2f
        mPaint!!.color = mColor
        val headerHeight = mHeaderHeight.toInt()
        val pullHeight = mPullHeight.toInt()
        mPath!!.rewind()
        mPath!!.moveTo(0f, 0f)
        mPath!!.lineTo(0f, headerHeight.toFloat())
        mPath!!.quadTo(mDisplayX, pullHeight.toFloat(), width.toFloat(), headerHeight.toFloat())
        //case2
        mPath!!.lineTo(width.toFloat(), 0f)
        mPath!!.close()
        //outlineProvider = mViewOutlineProvider
    }

    private fun createWave() {
        pathWave!!.reset()
        pathWave!!.moveTo(0f, mHeaderHeight)
        pathWave!!.lineTo(0f, amplitude)
        pathWave!!.lineTo(0f, amplitude - 10)
        var i = 0
        while (i < width + 10) {
            val wx = i.toFloat()
            val wy =
                (amplitude * 2 + amplitude * sin((i + 10) * Math.PI / WAVE_AMOUNT_ON_SCREEN + speed) + 200).toFloat()
            pathWave!!.lineTo(wx, wy)
            i += 10
        }
        pathWave!!.lineTo(width.toFloat(), mHeaderHeight)
        pathWave!!.close()
    }


    fun createAnimator(): ValueAnimator {
        return ValueAnimator.ofFloat(0f, Float.MAX_VALUE).apply {
            repeatCount = ValueAnimator.INFINITE
            this.repeatMode = ValueAnimator.RESTART
            addUpdateListener {
                speed -= WAVE_SPEED
                createWave()
                Log.d("TAG", "anim value : ${it.values}")
                Log.d("TAG", "anim value : $speed")
                invalidate()
            }

        }
    }

    /*

        private fun createAnimator(): ValueAnimator {
        return ValueAnimator.ofFloat(0f, Float.MAX_VALUE).apply {
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                speed -= WAVE_SPEED
                createPath()
                invalidate()
            }
        }
    }


        private fun createPath() {
        pathWave.reset()
        paint.color = Color.WHITE//parseColor("#203354")
        pathWave.moveTo(0f, height.toFloat())
        pathWave.lineTo(0f, amplitude)
        pathWave.lineTo(0f, amplitude - 10)
        var i = 0
        while (i < width + 10) {
            val wx = i.toFloat()
            val wy =
                amplitude * 2 + amplitude * kotlin.math.sin((i + 10) * Math.PI / WAVE_AMOUNT_ON_SCREEN + speed) + 200
            pathWave.lineTo(wx, wy.toFloat())
            i += 10
        }
        pathWave.lineTo(width.toFloat(), height.toFloat())
        pathWave.close()

    }
     */
    fun setHeaderHeight(headerHeight: Float) {
        mHeaderHeight = headerHeight
    }

    companion object {
        private const val WAVE_AMOUNT_ON_SCREEN = 350
        private const val WAVE_SPEED = 0.25f
    }

    init {
        init()
    }
}