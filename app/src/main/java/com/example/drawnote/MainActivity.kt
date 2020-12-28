package com.example.drawnote

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener


class MainActivity : AppCompatActivity() {
    private lateinit var display: Display
    private var brushSize = 12; private var eraserSize = 12

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.display)
        val mainToolbar = findViewById<View>(R.id.toolbar_main)
        val brushToolbar = findViewById<View>(R.id.toolbar_modify_brush)
        val eraserToolbar = findViewById<View>(R.id.toolbar_modify_eraser)
        val textToolbar = findViewById<View>(R.id.toolbar_modify_text)
        val openMenu = findViewById<ImageButton>(R.id.openMenu)
        val openBrush = findViewById<ImageButton>(R.id.paint)
        val addText = findViewById<ImageButton>(R.id.addText)
        val openEraser = findViewById<ImageButton>(R.id.eraser)
        val delete = findViewById<ImageButton>(R.id.delete)
        val undo = findViewById<ImageButton>(R.id.undo)
        val seekBrush = findViewById<SeekBar>(R.id.seekBrush)
        val seekEraser = findViewById<SeekBar>(R.id.seekEraser)
        val goBackBrush = findViewById<ImageButton>(R.id.returnMain)
        val goBackEraser = findViewById<ImageButton>(R.id.returnMain1)
        val goBackText = findViewById<ImageButton>(R.id.returnMain2)
        val colorLayout = findViewById<LinearLayout>(R.id.colorlayout)
        val colorLayout1 = findViewById<LinearLayout>(R.id.colorlayout1)
        val editText = findViewById<EditText>(R.id.editText)
        val editFontSize = findViewById<EditText>(R.id.editFontSize)
        val tools = arrayListOf<ImageButton>(openBrush, addText, openEraser)
        val brushColors = ArrayList<ImageButton>()
        val textColors = ArrayList<ImageButton>()

        for (i: Int in 1 until colorLayout.childCount) {
            brushColors.add(colorLayout[i] as ImageButton)
            colorLayout[i].setOnClickListener() {
                clearSelections(brushColors, colorLayout[i] as ImageButton)
                display.setBrushColor(Color.parseColor(colorLayout[i].contentDescription.toString()))
            }
        }

        for (i: Int in 0 until colorLayout1.childCount) {
            if (colorLayout1[i] is ImageButton) {
                textColors.add(colorLayout1[i] as ImageButton)
                colorLayout1[i].setOnClickListener() {
                    clearSelections(textColors, colorLayout1[i] as ImageButton)
                    display.setTextColor(Color.parseColor(colorLayout1[i].contentDescription.toString()))
                }
            }
        }

        openMenu.setOnClickListener() {
            //
        }

        openBrush.setOnClickListener() {
            clearSelections(tools, openBrush)
            display.setAddTextSelected(false)
            mainToolbar.isGone = true
            brushToolbar.isVisible = true
            display.disableEraser()
        }

        addText.setOnClickListener() {
            clearSelections(tools, addText)
            display.setAddTextSelected(true)
            mainToolbar.isGone = true
            textToolbar.isVisible = true
        }

        openEraser.setOnClickListener() {
            clearSelections(tools, openEraser)
            display.setAddTextSelected(false)
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

        goBackText.setOnClickListener() {
            textToolbar.isGone = true
            mainToolbar.isVisible = true
        }

        editText.addTextChangedListener() {
            if (editText.text.toString().isEmpty()) {
                Toast.makeText(this, "Text is empty",
                        Toast.LENGTH_SHORT).show()
            } else {
                display.setText(editText.text.toString())
            }
        }

        editFontSize.addTextChangedListener() {
            if (editFontSize.text.toString().isNotEmpty() && editFontSize.text.toString().toInt() > 0) {
                display.setTextSize(editFontSize.text.toString().toFloat())
            } else {
                Toast.makeText(this, "Size must be greater than zero",
                        Toast.LENGTH_SHORT).show()
            }
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