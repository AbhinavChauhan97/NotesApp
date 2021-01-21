package com.example.notesapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ImagesUtil private constructor(private val context: Context){

    companion object{
        fun with(context: Context):ImagesUtil{
            return ImagesUtil(context)
        }
    }

    fun imagesDirectory(): File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

    fun getUriForImageFile(name: String): Uri {
        val picturesDirectory = imagesDirectory()
        val imageFile = File(picturesDirectory, name)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.android.fileprovider",
            imageFile
        )
    }

    suspend fun compressAndResizeImage(imageFileName: String) {
        val imagesDirectory = imagesDirectory()
        val imageFile = File(imagesDirectory, imageFileName)
        val resizedBitmap = resizeBitmapFromFile(imageFile)
        val stream = compressBitmap(resizedBitmap)
        val outputStream = FileOutputStream(imageFile)
        stream.writeTo(outputStream)
    }

    suspend fun compressBitmap(bitmap: Bitmap): ByteArrayOutputStream {

        return withContext(Dispatchers.Default) {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            outputStream
        }
    }

    suspend fun resizeBitmapFromFile(file: File): Bitmap {

        return withContext(Dispatchers.IO) {
            val options = BitmapFactory.Options()
            options.inSampleSize = 2
            options.inPreferredConfig = Bitmap.Config.RGB_565
            BitmapFactory.decodeFile(file.absolutePath, options)
        }
    }
}