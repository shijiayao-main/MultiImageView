package com.jiaoay.multiimageview.widget.factory.image.multi

import com.jiaoay.multiimageview.widget.MultiImageFactoryConfig
import com.jiaoay.multiimageview.widget.factory.provider.FillMinAreaSizeProvider

class MultiFillMinAreaImageFactory(
    config: MultiImageFactoryConfig
) : AbstractMultiImageFactory(
    config = config,
    sizeProvider = FillMinAreaSizeProvider(config = config)
)