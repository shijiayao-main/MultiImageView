package com.jiaoay.multiimageview.widget

import android.graphics.RectF
import android.util.Log
import kotlin.math.ceil
import kotlin.math.sqrt

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
    private var columnCount: Int = 0
    private var rowCount: Int = 0

    override fun measureAreaSize(
        width: Int,
        height: Int,
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int,
        imageListSize: Int,
    ): Pair<Float, Float> {
        val rectWidth = calculateRectWidth(
            width = width,
            height = height,
            paddingLeft = paddingLeft,
            paddingTop = paddingTop,
            paddingRight = paddingRight,
            paddingBottom = paddingBottom,
            imageListSize = imageListSize,
        )
        this.rectWidth = rectWidth

        val paddingHorizontalSize = paddingLeft + paddingRight
        val paddingVerticalSize = paddingTop + paddingBottom

        val rowCount = getRowCount(imageListSize = imageListSize)
        this.rowCount = rowCount
        val columnCount = getColumnCount(imageListSize = imageListSize)
        this.columnCount = columnCount

        val areaWidth: Float =
            rectWidth * columnCount + (config.spaceWidth * (columnCount - 1)).coerceAtLeast(0f) + paddingHorizontalSize
        val areaHeight: Float =
            rectWidth * rowCount + (config.spaceWidth * (rowCount - 1)).coerceAtLeast(0f) + paddingVerticalSize

        return areaWidth to areaHeight
    }

    open fun getColumnCount(imageListSize: Int): Int {
        return if (imageListSize >= config.maxColumnCount) {
            config.maxColumnCount
        } else {
            imageListSize % config.maxColumnCount
        }
    }

    open fun getRowCount(imageListSize: Int): Int {
        return ceil(imageListSize / config.maxColumnCount.toDouble())
            .toInt()
            .coerceAtMost(
                config.maxRowCount
            )
    }

    open fun calculateRectWidth(
        width: Int,
        height: Int,
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int,
        imageListSize: Int,
    ): Float {
        val spaceColumnSumWidth: Float = config.spaceWidth * (config.maxColumnCount - 1)
        val paddingHorizontalSize = paddingLeft + paddingRight

        return (width - spaceColumnSumWidth - paddingHorizontalSize) / config.maxColumnCount
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
        val imageListSize = imageInfoList.size
        imageInfoList.forEachIndexed { index, imageInfo ->
            calculateRectF(
                index = index,
                imageInfo = imageInfo,
                paddingLeft = paddingLeft,
                paddingTop = paddingTop,
                paddingRight = paddingRight,
                paddingBottom = paddingBottom,
                imageListSize = imageListSize,
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
        imageListSize: Int,
    ) {

        val column = getColumnIndex(
            index = index,
            imageListSize = imageListSize
        )
        val row = getRowIndex(
            index = index,
            imageListSize = imageListSize
        )

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

    open fun getColumnIndex(
        index: Int,
        imageListSize: Int,
    ): Int {
        return index % columnCount
    }

    open fun getRowIndex(
        index: Int,
        imageListSize: Int,
    ): Int {
        return index / columnCount
    }
}