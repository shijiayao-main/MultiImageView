package com.jiaoay.multiimageview.widget.factory.provider

import android.graphics.Rect
import com.jiaoay.multiimageview.cacheSqrt
import com.jiaoay.multiimageview.isPerfectSquare
import com.jiaoay.multiimageview.sqrt
import com.jiaoay.multiimageview.widget.data.ImageFactoryConfig
import com.jiaoay.multiimageview.widget.data.ImageSizeConfig
import kotlin.math.ceil

class FillMinAreaSizeProvider<T : ImageSizeConfig>(
    config: T
) : SizeProvider<T>(
    config = config
) {

    override fun calculateRectWidth(
        width: Int,
        height: Int,
        imageListSize: Int,
        paddingRect: Rect
    ): Float {
        val paddingLeft = paddingRect.left
        val paddingRight = paddingRect.right

        val columnCount = getColumnCount(imageListSize = imageListSize)
        val spaceColumnSumWidth: Float = config.spaceWidth * (columnCount - 1)
        val paddingHorizontalSize = paddingLeft + paddingRight

        return (width - spaceColumnSumWidth - paddingHorizontalSize) / columnCount
    }

    override fun getColumnCount(imageListSize: Int): Int {
        if (imageListSize.isPerfectSquare().not()) {
            val sqrtSize = imageListSize.sqrt()
            if (sqrtSize >= config.maxColumnCount) {
                return super.getColumnCount(imageListSize)
            }
            if (imageListSize >= sqrtSize * config.maxColumnCount) {
                return super.getColumnCount(imageListSize)
            }
            return ceil(imageListSize / sqrtSize.toDouble()).toInt()
        }
        return imageListSize.cacheSqrt()
    }

    override fun getRowCount(imageListSize: Int): Int {
        if (imageListSize.isPerfectSquare().not()) {
            val sqrtSize = imageListSize.sqrt()
            if (sqrtSize >= config.maxColumnCount) {
                return super.getRowCount(imageListSize)
            }
            if (imageListSize >= sqrtSize * config.maxColumnCount) {
                return super.getRowCount(imageListSize)
            }
            return sqrtSize
        }
        return imageListSize.cacheSqrt()
    }

    override fun getColumnIndex(
        index: Int,
        imageListSize: Int
    ): Int {
        if (imageListSize.isPerfectSquare().not()) {
            return super.getColumnIndex(index, imageListSize)
        }
        return index % imageListSize.cacheSqrt()
    }

    override fun getRowIndex(
        index: Int,
        imageListSize: Int
    ): Int {
        if (imageListSize.isPerfectSquare().not()) {
            return super.getRowIndex(index, imageListSize)
        }
        return index / imageListSize.cacheSqrt()
    }
}