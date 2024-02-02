package com.jiaoay.multiimageview.widget.factory.image.split

import com.jiaoay.multiimageview.widget.SplitImageFactoryConfig
import com.jiaoay.multiimageview.widget.factory.provider.FillAreaSizeProvider

/**
 * 尽可能大的展示图片
 */
class SplitFillAreaImageFactory(
    config: SplitImageFactoryConfig
) : AbstractSplitImageFactory(
    config = config,
    sizeProvider = FillAreaSizeProvider(config)
)