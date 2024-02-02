package com.jiaoay.multiimageview.widget.factory.multi

import com.jiaoay.multiimageview.widget.MultiImageFactoryConfig

class MultiFillMinAreaImageFactory(
    config: MultiImageFactoryConfig
) : MultiMinAreaImageFactory(
    config = config
) {
    override fun calculateRectWidth(
        width: Int,
        height: Int,
        imageListSize: Int,
    ): Float {
        val paddingLeft = getPaddingRect().left
        val paddingRight = getPaddingRect().right

        val columnCount = getColumnCount(imageListSize = imageListSize)
        val spaceColumnSumWidth: Float = config.spaceWidth * (columnCount - 1)
        val paddingHorizontalSize = paddingLeft + paddingRight

        return (width - spaceColumnSumWidth - paddingHorizontalSize) / columnCount
    }
}