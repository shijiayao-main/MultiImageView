package com.jiaoay.multiimageview.widget.data

import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable

data class ImageInfo(
    val index: Int,
    val url: String,
    var drawable: Drawable?,
    val imageAreaRectF: RectF,
    val drawableBoundsRectF: Rect,
)
