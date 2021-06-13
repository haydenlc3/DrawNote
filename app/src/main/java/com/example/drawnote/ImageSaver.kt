package com.example.drawnote

import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
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

    fun getFileName(): String {
        return fileName
    }

    @TargetApi(Build.VERSION_CODES.Q)
    fun saveImageToGallery(bitmap: Bitmap): Uri? {
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

        return uri
    }

    fun saveImageToStorage(bitmap: Bitmap) {
        try {
            val out = FileOutputStream(File(directory, fileName))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareImage(bitmap: Bitmap, isNotEmpty: Boolean) {
        if (isNotEmpty) saveImageToStorage(bitmap)
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "image/*"
        sharingIntent.putExtra(Intent.EXTRA_STREAM, saveImageToGallery(bitmap))
        context.startActivities(arrayOf(Intent.createChooser(sharingIntent, "Share with")))
    }
}