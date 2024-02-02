package com.jiaoay.multiimageview.widget.factory.image.split

import com.jiaoay.multiimageview.widget.SplitImageFactoryConfig
import com.jiaoay.multiimageview.widget.factory.provider.SizeProvider

class SplitSampleImageFactory(
    config: SplitImageFactoryConfig
) : AbstractSplitImageFactory(
    config = config,
    sizeProvider = SizeProvider(config = config)
)