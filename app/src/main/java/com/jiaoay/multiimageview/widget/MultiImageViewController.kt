package com.jiaoay.multiimageview.widget

import android.graphics.Rect

interface MultiImageViewController {
    fun notifyInvalidate()

    fun getPaddingRect(): Rect
}