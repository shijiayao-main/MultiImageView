package com.jiaoay.multiimageview.widget.factory.image.multi

import com.jiaoay.multiimageview.widget.MultiImageFactoryConfig
import com.jiaoay.multiimageview.widget.factory.provider.MinAreaSizeProvider

/**
 * 会尽可能的使用较小的空间去展示图片
 */
open class MultiMinAreaImageFactory(
    config: MultiImageFactoryConfig
) : AbstractMultiImageFactory(
    config = config,
    sizeProvider = MinAreaSizeProvider(config = config)
)