package com.jiaoay.multiimageview.widget.factory.image.multi

import com.jiaoay.multiimageview.widget.MultiImageFactoryConfig
import com.jiaoay.multiimageview.widget.factory.provider.SizeProvider

class MultiSampleImageFactory(
    config: MultiImageFactoryConfig
) : AbstractMultiImageFactory(
    config = config,
    sizeProvider = SizeProvider(config = config)
)