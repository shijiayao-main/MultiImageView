package com.jiaoay.multiimageview.widget.factory.image.multi

import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import com.jiaoay.multiimageview.widget.AbstractImageFactory
import com.jiaoay.multiimageview.widget.ImageInfo
import com.jiaoay.multiimageview.widget.MultiImageFactoryConfig
import com.jiaoay.multiimageview.widget.factory.provider.SizeProvider

abstract class AbstractMultiImageFactory(
    config: MultiImageFactoryConfig,
    sizeProvider: SizeProvider<MultiImageFactoryConfig>
) : AbstractImageFactory<MultiImageFactoryConfig>(
    config = config,
    sizeProvider = sizeProvider
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
                drawable = null,
                imageAreaRectF = RectF(),
                drawableBoundsRectF = Rect()
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
        imageListSize: Int,
    ): Pair<Float, Float> {
        val rectWidth = sizeProvider.calculateRectWidth(
            width = width,
            height = height,
            imageListSize = imageListSize,
            paddingRect = getPaddingRect(),
        )
        this.rectWidth = rectWidth
        val paddingLeft = getPaddingRect().left
        val paddingTop = getPaddingRect().top
        val paddingRight = getPaddingRect().right
        val paddingBottom = getPaddingRect().bottom

        val paddingHorizontalSize = paddingLeft + paddingRight
        val paddingVerticalSize = paddingTop + paddingBottom

        val rowCount = sizeProvider.getRowCount(imageListSize = imageListSize)
        this.rowCount = rowCount
        val columnCount = sizeProvider.getColumnCount(imageListSize = imageListSize)
        this.columnCount = columnCount

        val areaWidth: Float =
            rectWidth * columnCount + (config.spaceWidth * (columnCount - 1)).coerceAtLeast(0f) + paddingHorizontalSize
        val areaHeight: Float =
            rectWidth * rowCount + (config.spaceWidth * (rowCount - 1)).coerceAtLeast(0f) + paddingVerticalSize

        return areaWidth to areaHeight
    }

//    open fun getColumnCount(imageListSize: Int): Int {
//        return if (imageListSize >= config.maxColumnCount) {
//            config.maxColumnCount
//        } else {
//            imageListSize % config.maxColumnCount
//        }
//    }
//
//    open fun getRowCount(imageListSize: Int): Int {
//        return ceil(imageListSize / config.maxColumnCount.toDouble())
//            .toInt()
//            .coerceAtMost(
//                config.maxRowCount
//            )
//    }
//
//    open fun calculateRectWidth(
//        width: Int,
//        height: Int,
//        imageListSize: Int,
//    ): Float {
//        val paddingLeft = getPaddingRect().left
//        val paddingRight = getPaddingRect().right
//
//        val spaceColumnSumWidth: Float = config.spaceWidth * (config.maxColumnCount - 1)
//        val paddingHorizontalSize = paddingLeft + paddingRight
//
//        return (width - spaceColumnSumWidth - paddingHorizontalSize) / config.maxColumnCount
//    }

    override fun measureImageRectF(
        imageInfoList: List<ImageInfo>,
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
                imageListSize = imageListSize,
            )
        }
    }

    private fun calculateRectF(
        index: Int,
        imageInfo: ImageInfo,
        imageListSize: Int,
    ) {
        val paddingLeft = getPaddingRect().left
        val paddingTop = getPaddingRect().top

        val column = sizeProvider.getColumnIndex(
            index = index,
            imageListSize = imageListSize
        )
        val row = sizeProvider.getRowIndex(
            index = index,
            imageListSize = imageListSize
        )

        val left: Float = rectWidth * column + config.spaceWidth * column + paddingLeft
        val top: Float = rectWidth * row + config.spaceWidth * row + paddingTop
        val right: Float = left + rectWidth
        val bottom: Float = top + rectWidth

        imageInfo.imageAreaRectF.set(
            left,
            top,
            right,
            bottom
        )
    }

//    open fun getColumnIndex(
//        index: Int,
//        imageListSize: Int,
//    ): Int {
//        return index % columnCount
//    }
//
//    open fun getRowIndex(
//        index: Int,
//        imageListSize: Int,
//    ): Int {
//        return index / columnCount
//    }
}