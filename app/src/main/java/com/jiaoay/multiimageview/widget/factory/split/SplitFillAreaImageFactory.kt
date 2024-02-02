package com.jiaoay.multiimageview.widget.factory.split

import com.jiaoay.multiimageview.widget.SplitImageFactoryConfig

/**
 * 尽可能大的展示图片
 */
class SplitFillAreaImageFactory(
    config: SplitImageFactoryConfig
) : SplitImageFactory(
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