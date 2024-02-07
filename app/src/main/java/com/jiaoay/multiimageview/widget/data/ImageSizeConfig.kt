package com.jiaoay.multiimageview.widget.data

sealed class ImageSizeConfig(
    open val spaceWidth: Float,
    open val maxColumnCount: Int,
)

data class MultiImageSizeConfig(
    val maxRowCount: Int,
    override val spaceWidth: Float,
    override val maxColumnCount: Int
) : ImageSizeConfig(
    spaceWidth = spaceWidth,
    maxColumnCount = maxColumnCount
)

data class SplitImageSizeConfig(
    val splitCount: Int,
    override val spaceWidth: Float,
    override val maxColumnCount: Int
) : ImageSizeConfig(
    spaceWidth = spaceWidth,
    maxColumnCount = maxColumnCount,
)