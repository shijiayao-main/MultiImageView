package com.jiaoay.multiimageview.widget.factory.image.split

import com.jiaoay.multiimageview.widget.SplitImageFactoryConfig
import com.jiaoay.multiimageview.widget.factory.provider.FillMinAreaSizeProvider

class SplitFillMinAreaImageFactory(
    config: SplitImageFactoryConfig
) : AbstractSplitImageFactory(
    config = config,
    sizeProvider = FillMinAreaSizeProvider(config = config)
)