package com.jiaoay.multiimageview.widget.factory.multi

import com.jiaoay.multiimageview.widget.MultiImageFactoryConfig

/**
 * 尽可能大的展示图片
 */
class MultiFillAreaImageFactory(
    config: MultiImageFactoryConfig
) : MultiImageFactory(
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