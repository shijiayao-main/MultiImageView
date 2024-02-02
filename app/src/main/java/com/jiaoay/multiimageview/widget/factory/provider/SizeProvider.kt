package com.jiaoay.multiimageview.widget.factory.provider

import android.graphics.Rect
import com.jiaoay.multiimageview.widget.ImageFactoryConfig
import kotlin.math.ceil

open class SizeProvider<T : ImageFactoryConfig>(
    val config: T
) {

    open fun calculateRectWidth(
        width: Int,
        height: Int,
        imageListSize: Int,
        paddingRect: Rect
    ): Float {
        val paddingLeft = paddingRect.left
        val paddingRight = paddingRect.right

        val spaceColumnSumWidth: Float = config.spaceWidth * (config.maxColumnCount - 1)
        val paddingHorizontalSize = paddingLeft + paddingRight
        return (width - spaceColumnSumWidth - paddingHorizontalSize) / config.maxColumnCount
    }

    open fun getColumnCount(imageListSize: Int): Int {
        return if (imageListSize >= config.maxColumnCount) {
            config.maxColumnCount
        } else {
            imageListSize % config.maxColumnCount
        }
    }

    open fun getRowCount(imageListSize: Int): Int {
        return ceil(imageListSize / config.maxColumnCount.toDouble()).toInt()
    }

    open fun getColumnIndex(
        index: Int,
        imageListSize: Int,
    ): Int {
        return index % getColumnCount(imageListSize = imageListSize)
    }

    open fun getRowIndex(
        index: Int,
        imageListSize: Int,
    ): Int {
        return index / getColumnCount(imageListSize = imageListSize)
    }
}