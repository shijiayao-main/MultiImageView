package com.jiaoay.multiimageview.widget

import android.graphics.RectF
import android.util.Log
import kotlin.math.ceil

abstract class AbstractMultiImageFactory(
    config: MultiImageFactoryConfig
) : AbstractImageFactory<MultiImageFactoryConfig>(
    config = config
) {
    override fun processData(imageUrlList: List<String>): List<ImageInfo> {
        val imageInfoList: MutableList<ImageInfo> = ArrayList()
        val maxSize = imageUrlList.size.coerceAtMost(
            config.maxColumnCount * config.maxRowCount
        )
        for (index in 0 until maxSize) {
            val url = imageUrlList.getOrNull(index) ?: continue
            val imageInfo = ImageInfo(
                index = index,
                url = url,
                imageRectF = RectF(),
                drawable = null
            )
            imageInfoList.add(imageInfo)
        }
        return imageInfoList
    }

    private var rectWidth: Float = 0f

    override fun measureAreaSize(
        width: Int,
        height: Int,
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int,
        imageListSize: Int
    ): Pair<Float, Float> {
        val spaceColumnSumWidth: Float = config.spaceWidth * (config.maxColumnCount - 1)

        val paddingHorizontalSize = paddingLeft + paddingRight
        val paddingVerticalSize = paddingTop + paddingBottom

        val rectWidth =
            (width - spaceColumnSumWidth - paddingHorizontalSize) / config.maxColumnCount
        this.rectWidth = rectWidth

        val rowCount = getRowCount(size = imageListSize)
        val columnCount = getColumnCount(size = imageListSize)

        val areaWidth: Float =
            rectWidth * columnCount + (config.spaceWidth * (columnCount - 1)).coerceAtLeast(0f) + paddingHorizontalSize
        val areaHeight: Float =
            rectWidth * rowCount + (config.spaceWidth * (rowCount - 1)).coerceAtLeast(0f) + paddingVerticalSize

        return areaWidth to areaHeight
    }

    private fun getRowCount(size: Int): Int {
        return ceil(size / config.maxRowCount.toDouble())
            .toInt()
            .coerceAtMost(
                config.maxRowCount
            )
    }

    private fun getColumnCount(size: Int): Int {

        return if (size >= config.maxColumnCount) {
            config.maxColumnCount
        } else {
            size % config.maxColumnCount
        }
    }

    override fun measureImageRectF(
        imageInfoList: List<ImageInfo>,
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int,
    ) {
        if (rectWidth <= 0f) {
            Log.e(TAG, "measureImageRectF: rectWidth is <= 0, please measureShowArea before!")
            return
        }
        imageInfoList.forEachIndexed { index, imageInfo ->
            calculateRectF(
                index = index,
                imageInfo = imageInfo,
                paddingLeft = paddingLeft,
                paddingTop = paddingTop,
                paddingRight = paddingRight,
                paddingBottom = paddingBottom,
            )
        }
    }

    private fun calculateRectF(
        index: Int,
        imageInfo: ImageInfo,
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int,
    ) {

        val column = index % config.maxColumnCount
        val row = index / config.maxColumnCount
        val left: Float = rectWidth * column + config.spaceWidth * column + paddingLeft
        val top: Float = rectWidth * row + config.spaceWidth * row + paddingTop
        val right: Float = left + rectWidth
        val bottom: Float = top + rectWidth

        imageInfo.imageRectF.set(
            left,
            top,
            right,
            bottom
        )
    }
}