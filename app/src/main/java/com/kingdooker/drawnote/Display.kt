package com.kingdooker.drawnote

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


@Suppress("DEPRECATION")
class Display : View {
    private lateinit var btmBackground: Bitmap; private lateinit var btmView: Bitmap
    private var addTextSelected = false
    private var mPaint: Paint = Paint()
    private var mPath: Path = Path()
    private var mCanvas: Canvas = Canvas()
    private var sizeBrush: Int = 12
    private var sizeEraser: Int = 12
    private var styleBrush: Paint.Style = Paint.Style.STROKE
    private var styleEraser: Paint.Style = Paint.Style.STROKE
    private var text: String = "Add text"
    private var eraserEnabled = false
    private var textColor: Int = Color.BLACK
    private var brushColor: Int = Color.BLACK
    private var colorBackground: Int = Color.WHITE
    private var textSize: Float = 50f
    private var mX: Float = 0.0f; private var mY: Float = 0.0f
    private var listAction = ArrayList<Bitmap>()
    private val deferenceSpace: Int = 4
    private val imgSaver = ImageSaver(context)

    constructor (context: Context?): super(context)
    constructor (context: Context?, attrs: AttributeSet): super(context, attrs)
    constructor (context: Context?, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle)

    init {
        mPaint.color = Color.BLACK
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.style = styleBrush
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeWidth = toPx(sizeBrush)
        imgSaver.setFolderName(resources.getString(R.string.app_name))

        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                mainHandler.postDelayed(this, 30000)
                if (isNotEmpty()) imgSaver.saveImageToStorage(getBitmap())
            }
        })
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        btmBackground = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        btmView = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(btmView)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawColor(colorBackground)
        canvas?.drawBitmap(btmBackground, 0f, 0f, null)
        canvas?.drawBitmap(btmView, 0f, 0f, null)
        super.onDraw(canvas)
    }

    fun setSizeBrush(s: Int) {
        sizeBrush = s
        mPaint.strokeWidth = toPx(s)
    }

    fun setBrushColor(color: Int) {
        brushColor = color
        mPaint.color = color
    }

    fun setSizeEraser(s: Int) {
        sizeEraser = s
        mPaint.strokeWidth = toPx(s)
    }

    fun enableEraser() {
        eraserEnabled = true
        mPaint.strokeWidth = toPx(sizeEraser)
        mPaint.color = colorBackground
    }

    fun disableEraser() {
        eraserEnabled = false
        mPaint.color = brushColor
        mPaint.strokeWidth = toPx(sizeBrush)
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

    fun setPaintStyle(style: Paint.Style, isBrush: Boolean) {
        if (isBrush) styleBrush = style
        else styleEraser = style
    }

    fun clearCanvas() {
        mCanvas.drawColor(colorBackground)
        invalidate()
        addLastAction(getBitmap())
    }

    fun saveImage() {
        imgSaver.saveImageToStorage(getBitmap())
        imgSaver.saveImageToGallery(getBitmap())
    }

    fun shareImage() {
        imgSaver.shareImage(getBitmap(), isNotEmpty())
    }

    fun loadImage(bitmap: Bitmap, fileName: String) {
        btmView = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        mCanvas = Canvas(btmView)
        mCanvas.drawBitmap(btmView, 0f, 0f, null)
        invalidate()
        listAction.clear()
        imgSaver.setFileName(fileName)
    }

    fun deleteImage() {
        File(imgSaver.getPath(), imgSaver.getFileName()).delete()
        newImage()
    }

    fun newImage() {
        mCanvas = Canvas()
        btmView = Bitmap.createBitmap(getBitmap().width, getBitmap().height, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(btmView)
        invalidate()
        listAction.clear()
        imgSaver.setFileName(UUID.randomUUID().toString())
    }

    private fun addLastAction(bitmap: Bitmap) {
        listAction.add(bitmap)
    }

    fun returnLastAction() {
        if (listAction.size > 0) {
            listAction.removeAt(listAction.size-1)
            btmView = if (listAction.size > 0) {
                listAction[listAction.size-1]
            } else {
                Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            }

            mCanvas = Canvas(btmView)
            invalidate()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x
        val y = event?.y

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
        val bounds = Rect()
        mPaint.getTextBounds(text, 0, text.length, bounds)
        mPaint.textSize = textSize
        mPaint.color = textColor
        mPaint.style = Paint.Style.FILL
        mCanvas.drawText(text, -bounds.right/2 + x!!, -bounds.top/2 + y!!, mPaint)
        invalidate()
    }

    private fun touchMove(x: Float?, y: Float?) {
        val dx = abs(x!!-mX)
        val dy = abs(y!!-mY)

        if (dx >= deferenceSpace || dy >= deferenceSpace) {
            mPath.quadTo(x, y, (x+mX)/2, (y+mY)/2)
            mY = y; mX = x
            mCanvas.drawPath(mPath, mPaint)
            invalidate()
        }
    }

    private fun touchStart(x: Float?, y: Float?) {
        if (eraserEnabled) mPaint.style = styleEraser
        else mPaint.style = styleBrush
        mPath.moveTo(x!!, y!!)
        mX = x; mY = y
    }

    private fun getBitmap(): Bitmap {
        this.isDrawingCacheEnabled = true
        this.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(this.drawingCache)
        this.isDrawingCacheEnabled = false
        return bitmap
    }

    private fun toPx(sizeBrush: Int): Float {
        return sizeBrush*(resources.displayMetrics.density)
    }

    fun getPath(): String {
        return imgSaver.getPath()
    }

    fun getFileName(): String {
        return imgSaver.getFileName()
    }
    fun fileExists(): Boolean {
        return File(imgSaver.getPath(), imgSaver.getFileName()).exists()
    }

    fun isNotEmpty(): Boolean {
        return !getBitmap().sameAs(Bitmap.createBitmap(getBitmap().width, getBitmap().height, getBitmap().config))
                && listAction.size > 0
    }
}