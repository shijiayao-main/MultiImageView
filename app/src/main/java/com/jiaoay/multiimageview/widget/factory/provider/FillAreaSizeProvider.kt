package com.jiaoay.multiimageview.widget.factory.provider

import android.graphics.Rect
import com.jiaoay.multiimageview.widget.ImageFactoryConfig

class FillAreaSizeProvider<T : ImageFactoryConfig>(
    config: T
) : SizeProvider<T>(
    config = config
) {

    override fun calculateRectWidth(
        width: Int,
        height: Int,
        imageListSize: Int,
        paddingRect: Rect,
    ): Float {
        val paddingLeft = paddingRect.left
        val paddingRight = paddingRect.right

        val columnCount = getColumnCount(imageListSize = imageListSize)
        val spaceColumnSumWidth: Float = config.spaceWidth * (columnCount - 1)
        val paddingHorizontalSize = paddingLeft + paddingRight

        return (width - spaceColumnSumWidth - paddingHorizontalSize) / columnCount
    }
}