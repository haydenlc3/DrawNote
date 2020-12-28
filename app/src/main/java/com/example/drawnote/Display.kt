package com.example.drawnote

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class Display : View {
    private lateinit var btmBackground: Bitmap; private lateinit var btmView: Bitmap
    private var addTextSelected = false
    private var mPaint: Paint = Paint()
    private var mPath: Path = Path()
    private var mCanvas: Canvas = Canvas()
    private var sizeBrush: Int = 12
    private var sizeEraser: Int = 12
    private var text: String = "Add text"
    private var textColor: Int = Color.BLACK
    private var textSize: Float = 50f
    private var mX: Float = 0.0f; private var mY: Float = 0.0f
    private val DEFERENCE_SPACE: Int = 4
    private var listAction = ArrayList<Bitmap>()

    constructor (context: Context?): super(context)
    constructor (context: Context?, attrs: AttributeSet): super(context, attrs)
    constructor (context: Context?, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle)

    init {
        mPaint.color = Color.BLACK
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeWidth = toPx(sizeBrush)
    }

    private fun toPx(sizeBrush: Int): Float {
        return sizeBrush*(resources.displayMetrics.density)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        btmBackground = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        btmView = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(btmView)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(Color.WHITE)
        canvas?.drawBitmap(btmBackground, 0f, 0f, null)
        canvas?.drawBitmap(btmView, 0f, 0f, null)
    }

    fun setSizeBrush(s: Int) {
        sizeBrush = s
        mPaint.strokeWidth = sizeBrush.toFloat()
    }

    fun setBrushColor(color: Int) {
        mPaint.color = color
    }

    fun setSizeEraser(s: Int) {
        sizeEraser = s
        mPaint.strokeWidth = toPx(s)
    }

    fun enableEraser() {
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    fun disableEraser() {
        mPaint.xfermode = null
        mPaint.shader = null
        mPaint.maskFilter = null
    }

    fun setText(s: String) {
        text = s
    }

    fun setTextColor(color: Int) {
        textColor = color
    }

    fun setTextSize(size: Float) {
        textSize = size
    }

    fun setAddTextSelected(b: Boolean) {
        addTextSelected = b
    }

    private fun addLastAction(bitmap: Bitmap) {
        listAction.add(bitmap)
    }

    fun returnLastAction() {
        if (listAction.size > 0) {
            listAction.removeAt(listAction.size-1) // may not work
            btmView = if (listAction.size > 0) {
                listAction[listAction.size-1]
            } else {
                Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            }

            mCanvas = Canvas(btmView)
            invalidate()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var x = event?.x
        var y = event?.y

        if (addTextSelected) {
            when (event?.action) {
                MotionEvent.ACTION_UP -> {
                    addText(x, y)
                    addLastAction(getBitmap())
                }
            }
        } else {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> touchStart(x, y)
                MotionEvent.ACTION_MOVE -> touchMove(x, y)
                MotionEvent.ACTION_UP -> {
                    mPath.reset()
                    addLastAction(getBitmap())
                }
            }
        }

        return true
    }

    private fun addText(x: Float?, y: Float?) {
        mPaint.textSize = textSize
        mPaint.color = textColor
        mPaint.style = Paint.Style.FILL
        mCanvas.drawText(text, x!!, y!!, mPaint)
        invalidate()
    }

    private fun touchMove(x: Float?, y: Float?) {
        var dx = abs(x!!-mX)
        var dy = abs(y!!-mY)

        if (dx >= DEFERENCE_SPACE || dy >= DEFERENCE_SPACE) {
            mPath.quadTo(x, y, (x+mX)/2, (y+mY)/2)
            mY = y; mX = x
            mCanvas.drawPath(mPath, mPaint)
            invalidate()
        }
    }

    private fun touchStart(x: Float?, y: Float?) {
        mPath.moveTo(x!!, y!!)
        mX = x; mY = y
    }

    private fun getBitmap(): Bitmap {
        this.isDrawingCacheEnabled = true
        this.buildDrawingCache()
        var bitmap = Bitmap.createBitmap(this.drawingCache)
        this.isDrawingCacheEnabled = false
        return bitmap
    }
}

