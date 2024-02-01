package com.jiaoay.multiimageview.widget

import androidx.annotation.ColorInt

sealed class ImageFactoryConfig(
    @ColorInt val placeholderColorInt: Int,
    val roundRadius: Float
)

class MultiImageFactoryConfig(
    val maxColumnCount: Int,
    val maxRowCount: Int,
    val spaceWidth: Float,
    placeholderColorInt: Int,
    roundRadius: Float
) : ImageFactoryConfig(
    placeholderColorInt = placeholderColorInt,
    roundRadius = roundRadius
)

class SplitMultiImageFactoryConfig(
    val splitCount: Int,
    val maxColumnCount: Int,
    val spaceWidth: Float,
    placeholderColorInt: Int,
    roundRadius: Float
) : ImageFactoryConfig(
    placeholderColorInt = placeholderColorInt,
    roundRadius = roundRadius
)