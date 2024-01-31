package com.jiaoay.multiimageview.widget

import android.graphics.RectF
import android.graphics.drawable.Drawable

data class ImageInfo(
    val index: Int,
    val url: String,
    var drawable: Drawable?,
    val imageRectF: RectF,
)
