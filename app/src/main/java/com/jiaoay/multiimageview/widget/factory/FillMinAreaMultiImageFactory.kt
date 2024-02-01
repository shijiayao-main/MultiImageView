package com.jiaoay.multiimageview.widget.factory

import com.jiaoay.multiimageview.widget.MultiImageFactoryConfig

class FillMinAreaMultiImageFactory(
    config: MultiImageFactoryConfig
) : MinAreaMultiImageFactory(
    config = config
) {
    override fun calculateRectWidth(
        width: Int,
        height: Int,
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int,
        imageListSize: Int,
    ): Float {
        val columnCount = getColumnCount(imageListSize = imageListSize)
        val spaceColumnSumWidth: Float = config.spaceWidth * (columnCount - 1)
        val paddingHorizontalSize = paddingLeft + paddingRight

        return (width - spaceColumnSumWidth - paddingHorizontalSize) / columnCount
    }
}