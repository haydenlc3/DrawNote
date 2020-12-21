package com.example.drawnote

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.core.view.get
import androidx.core.view.isGone
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {
    private lateinit var display: Display
    private var colorBackground = Color.WHITE
    private var brushSize = 12; private var eraserSize = 12

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.display)
        val mainToolbar = findViewById<View>(R.id.toolbar_main)
        val brushToolbar = findViewById<View>(R.id.toolbar_modify_brush)
        val eraserToolbar = findViewById<View>(R.id.toolbar_modify_eraser)
        val openMenu = findViewById<ImageButton>(R.id.openMenu)
        val openBrush = findViewById<ImageButton>(R.id.paint)
        val addText = findViewById<ImageButton>(R.id.colorBlack)
        val openEraser = findViewById<ImageButton>(R.id.eraser)
        val delete = findViewById<ImageButton>(R.id.delete)
        val undo = findViewById<ImageButton>(R.id.undo)
        val seekBrush = findViewById<SeekBar>(R.id.seekBrush)
        val seekEraser = findViewById<SeekBar>(R.id.seekEraser)
        val goBackBrush = findViewById<ImageButton>(R.id.returnMain)
        val goBackEraser = findViewById<ImageButton>(R.id.returnMain1)
        val colorLayout = findViewById<LinearLayout>(R.id.colorlayout)
        val tools = arrayListOf<ImageButton>(openBrush, addText, openEraser)
        val colors = ArrayList<ImageButton>()

        for (i: Int in 1 until colorLayout.childCount) {
            colors.add(colorLayout[i] as ImageButton)
            colorLayout[i].setOnClickListener() {
                clearSelections(colors, colorLayout[i] as ImageButton)
                display.setBrushColor(Color.parseColor(colorLayout[i].contentDescription.toString()))
            }
        }

        openMenu.setOnClickListener() {
            //
        }

        openBrush.setOnClickListener() {
            clearSelections(tools, openBrush)
            mainToolbar.isGone = true
            brushToolbar.isVisible = true
            display.disableEraser()
        }

        addText.setOnClickListener() {
            //
        }

        openEraser.setOnClickListener() {
            clearSelections(tools, openEraser)
            mainToolbar.isGone = true
            eraserToolbar.isVisible = true
            display.enableEraser()
        }

        delete.setOnClickListener() {
            //
        }

        undo.setOnClickListener() {
            display.returnLastAction()
        }

        goBackBrush.setOnClickListener() {
            brushToolbar.isGone = true
            mainToolbar.isVisible = true
        }

        goBackEraser.setOnClickListener() {
            eraserToolbar.isGone = true
            mainToolbar.isVisible = true
        }

        val i = object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (brushToolbar.isVisible) {
                    brushSize = p1 + 5
                    display.setSizeBrush(brushSize)
                } else if (eraserToolbar.isVisible) {
                    eraserSize = p1 + 5
                    display.setSizeEraser(eraserSize)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        }; seekBrush.setOnSeekBarChangeListener(i); seekEraser.setOnSeekBarChangeListener(i)
    }

    private fun clearSelections(array: ArrayList<ImageButton>, button: ImageButton) {
        for (i in array.indices) {
            array[i].isSelected = false
        }

        button.isSelected = true
    }
}