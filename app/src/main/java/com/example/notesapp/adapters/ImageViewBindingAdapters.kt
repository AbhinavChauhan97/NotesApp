package com.example.notesapp.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

object ImageViewBindingAdapters {

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setSrc(imageView: ImageView,imageUrl:String?){
        if(imageUrl != null) {
            imageView.load(imageUrl)
        }
    }
}