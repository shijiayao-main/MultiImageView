package com.jiaoay.multiimageview.widget.factory.image.split

import com.jiaoay.multiimageview.widget.SplitImageFactoryConfig
import com.jiaoay.multiimageview.widget.factory.provider.MinAreaSizeProvider

/**
 * 会尽可能的使用较小的空间去展示图片
 */
open class SplitMinAreaImageFactory(
    config: SplitImageFactoryConfig
) : AbstractSplitImageFactory(
    config = config,
    sizeProvider = MinAreaSizeProvider(config = config)
)