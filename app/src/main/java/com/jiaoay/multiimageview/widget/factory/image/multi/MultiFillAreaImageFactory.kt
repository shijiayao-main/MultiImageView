package com.jiaoay.multiimageview.widget.factory.image.multi

import com.jiaoay.multiimageview.widget.MultiImageFactoryConfig
import com.jiaoay.multiimageview.widget.factory.provider.FillAreaSizeProvider

/**
 * 尽可能大的展示图片
 */
class MultiFillAreaImageFactory(
    config: MultiImageFactoryConfig
) : AbstractMultiImageFactory(
    config = config,
    sizeProvider = FillAreaSizeProvider(config = config)
)