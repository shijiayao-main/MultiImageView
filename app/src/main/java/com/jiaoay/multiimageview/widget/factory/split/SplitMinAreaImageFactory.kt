package com.jiaoay.multiimageview.widget.factory.split

import com.jiaoay.multiimageview.cacheSqrt
import com.jiaoay.multiimageview.isPerfectSquare
import com.jiaoay.multiimageview.sqrt
import com.jiaoay.multiimageview.widget.SplitImageFactoryConfig
import kotlin.math.ceil

/**
 * 会尽可能的使用较小的空间去展示图片
 */
open class SplitMinAreaImageFactory(
    config: SplitImageFactoryConfig
) : SplitImageFactory(
    config = config
) {
    override fun getColumnCount(imageListSize: Int): Int {
        if (imageListSize.isPerfectSquare().not()) {
            val sqrtSize = imageListSize.sqrt()
            if (sqrtSize >= config.maxColumnCount) {
                return super.getColumnCount(imageListSize)
            }
            if (imageListSize >= sqrtSize * config.maxColumnCount) {
                return super.getColumnCount(imageListSize)
            }
            return ceil(imageListSize / sqrtSize.toDouble()).toInt()
        }
        return imageListSize.cacheSqrt()
    }

    override fun getRowCount(imageListSize: Int): Int {
        if (imageListSize.isPerfectSquare().not()) {
            val sqrtSize = imageListSize.sqrt()
            if (sqrtSize >= config.maxColumnCount) {
                return super.getRowCount(imageListSize)
            }
            if (imageListSize >= sqrtSize * config.maxColumnCount) {
                return super.getRowCount(imageListSize)
            }
            return sqrtSize
        }
        return imageListSize.cacheSqrt()
    }

    override fun getColumnIndex(
        index: Int,
        imageListSize: Int
    ): Int {
        if (imageListSize.isPerfectSquare().not()) {
            return super.getColumnIndex(index, imageListSize)
        }
        return index % imageListSize.cacheSqrt()
    }

    override fun getRowIndex(
        index: Int,
        imageListSize: Int
    ): Int {
        if (imageListSize.isPerfectSquare().not()) {
            return super.getRowIndex(index, imageListSize)
        }
        return index / imageListSize.cacheSqrt()
    }
}