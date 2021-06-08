package com.example.drawnote

import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*


class ImageSaver(private val context: Context) {
    private var fileName = UUID.randomUUID().toString()
    private val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
    private lateinit var folderName: String

    init {
        if (!directory.exists()) {
            directory.mkdirs()
        }
    }

    fun setFileName(fileName: String) {
        this.fileName = fileName
    }

    fun setFolderName(folderName: String) {
        this.folderName = folderName
    }

    fun getPath(): String {
        return directory.absolutePath
    }

    private fun getUri(): Uri {
        return File(directory, fileName).toUri()
    }

    @TargetApi(Build.VERSION_CODES.Q)
    fun saveImageToGallery(bitmap: Bitmap) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$folderName")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.IS_PENDING, true)
        }

        val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            context.contentResolver.openOutputStream(uri).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }

            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, false)
            context.contentResolver.update(uri, values, null, null)
        }
    }

    fun saveImageToStorage(bitmap: Bitmap) {
        // Make sure the directory "Android/data/com.mypackage.etc/files/Pictures" exists
        try {
            val out = FileOutputStream(File(directory, fileName))
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadImage(path: String, canvas: Canvas) {
        try {
            val f = File(path, fileName)
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            canvas.setBitmap(b)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    fun shareImage(bitmap: Bitmap) {
        saveImageToStorage(bitmap)
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "image/*"
        sharingIntent.putExtra(Intent.EXTRA_STREAM, getUri())
        context.startActivities(arrayOf(Intent.createChooser(sharingIntent, "Share with")))
    }
}