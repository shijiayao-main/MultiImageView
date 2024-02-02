package com.jiaoay.multiimageview.widget

import androidx.annotation.ColorInt

sealed class ImageFactoryConfig(
    @ColorInt val placeholderColorInt: Int,
    val roundRadius: Float,
    val spaceWidth: Float,
    val maxColumnCount: Int,
)

class MultiImageFactoryConfig(
    val maxRowCount: Int,
    maxColumnCount: Int,
    spaceWidth: Float,
    placeholderColorInt: Int,
    roundRadius: Float
) : ImageFactoryConfig(
    placeholderColorInt = placeholderColorInt,
    roundRadius = roundRadius,
    spaceWidth = spaceWidth,
    maxColumnCount = maxColumnCount
)

class SplitImageFactoryConfig(
    val splitCount: Int,
    maxColumnCount: Int,
    spaceWidth: Float,
    placeholderColorInt: Int,
    roundRadius: Float
) : ImageFactoryConfig(
    placeholderColorInt = placeholderColorInt,
    roundRadius = roundRadius,
    spaceWidth = spaceWidth,
    maxColumnCount = maxColumnCount
)