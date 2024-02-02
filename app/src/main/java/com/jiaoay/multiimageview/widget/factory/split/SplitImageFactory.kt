package com.jiaoay.multiimageview.widget.factory.split

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.Log
import com.jiaoay.multiimageview.widget.AbstractImageFactory
import com.jiaoay.multiimageview.widget.ImageInfo
import com.jiaoay.multiimageview.widget.SplitImageFactoryConfig
import kotlin.math.abs
import kotlin.math.ceil

/**
 * 将单图裁切成多份去展示
 */
open class SplitImageFactory(
    config: SplitImageFactoryConfig
) : AbstractImageFactory<SplitImageFactoryConfig>(config) {

    private var drawable: Drawable? = null

    override fun processData(imageUrlList: List<String>): List<ImageInfo> {
        val imageUrl = imageUrlList.first {
            it.isNotBlank()
        }

        val maxSize = config.splitCount.coerceAtMost(config.maxColumnCount * config.maxColumnCount)

        val imageInfoList: MutableList<ImageInfo> = ArrayList()
        for (index in 0 until maxSize) {
            val imageInfo = ImageInfo(
                index = index,
                url = imageUrl,
                drawable = null,
                imageAreaRectF = RectF(),
                drawableBoundsRectF = Rect()
            )
            imageInfoList.add(imageInfo)
        }
        return imageInfoList
    }

    private var rectWidth: Float = 0f

    override fun measureAreaSize(
        width: Int,
        height: Int,
        imageListSize: Int
    ): Pair<Float, Float> {
        val paddingLeft = getPaddingRect().left
        val paddingTop = getPaddingRect().top
        val paddingRight = getPaddingRect().right
        val paddingBottom = getPaddingRect().bottom

        val rectWidth = calculateRectWidth(
            width = width,
            height = height,
            imageListSize = imageListSize,
        )
        this.rectWidth = rectWidth

        val paddingHorizontalSize = paddingLeft + paddingRight
        val paddingVerticalSize = paddingTop + paddingBottom

        val rowCount = getRowCount(imageListSize = imageListSize)
        val columnCount = getColumnCount(imageListSize = imageListSize)

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
        return ceil(imageListSize / config.maxColumnCount.toDouble()).toInt()
    }

    open fun calculateRectWidth(
        width: Int,
        height: Int,
        imageListSize: Int,
    ): Float {
        val paddingLeft = getPaddingRect().left
        val paddingRight = getPaddingRect().right

        val spaceColumnSumWidth: Float = config.spaceWidth * (config.maxColumnCount - 1)
        val paddingHorizontalSize = paddingLeft + paddingRight
        return (width - spaceColumnSumWidth - paddingHorizontalSize) / config.maxColumnCount
    }

    override fun measureImageRectF(
        imageInfoList: List<ImageInfo>
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

        val columnIndex = getColumnIndex(
            index = index,
            imageListSize = imageListSize
        )
        val rowIndex = getRowIndex(
            index = index,
            imageListSize = imageListSize
        )

        val left: Float = rectWidth * columnIndex + config.spaceWidth * columnIndex + paddingLeft
        val top: Float = rectWidth * rowIndex + config.spaceWidth * rowIndex + paddingTop
        val right: Float = left + rectWidth
        val bottom: Float = top + rectWidth

        imageInfo.imageAreaRectF.set(
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
        return index % getColumnCount(imageListSize = imageListSize)
    }

    open fun getRowIndex(
        index: Int,
        imageListSize: Int,
    ): Int {
        return index / getColumnCount(imageListSize = imageListSize)
    }

    override fun loadImageDrawable(
        context: Context,
        imageInfoList: List<ImageInfo>
    ) {
        val imageListSize = imageInfoList.size
        val areaWidth = getImageAreaWidth(columnCount = getColumnCount(imageListSize))
        val areaHeight = getImageAreaHeight(rowCount = getRowCount(imageListSize))
        val singleImageList: List<ImageInfo> = imageInfoList.firstOrNull()?.let {
            listOf(
                ImageInfo(
                    index = 0,
                    url = it.url,
                    drawable = it.drawable,
                    imageAreaRectF = RectF(
                        0f,
                        0f,
                        areaWidth,
                        areaHeight
                    ),
                    drawableBoundsRectF = Rect()
                )
            )
        } ?: emptyList()

        loadImageDrawable(
            context = context,
            imageInfoList = singleImageList,
            endProcess = endProcess@{
                onEndProcess(
                    imageInfo = it,
                    originImageInfoList = imageInfoList
                )
            },
        )
    }

    private fun onEndProcess(
        imageInfo: ImageInfo,
        originImageInfoList: List<ImageInfo>
    ) {
        val imageListSize = originImageInfoList.size

        val drawable = imageInfo.drawable ?: return
        this.drawable = drawable

        val imageWidth: Float = drawable.intrinsicWidth.toFloat()
        val imageHeight: Float = drawable.intrinsicHeight.toFloat()
        val imageRatio = safeGetRatio(
            width = imageWidth,
            height = imageHeight
        )

        val rowCount = getRowCount(imageListSize = imageListSize)
        val columnCount = getColumnCount(imageListSize = imageListSize)

        val spaceSumWidth = config.spaceWidth * (columnCount - 1).coerceAtLeast(0)
        val spaceSumHeight = config.spaceWidth * (rowCount - 1).coerceAtLeast(0)
        val areaWidth = getImageAreaWidth(columnCount = columnCount)
        val areaHeight = getImageAreaHeight(rowCount = rowCount)

        val imageAdaptHeight: Float
        val imageAdaptWidth: Float
        val imageSplitLength: Float

        val spaceX: Float
        val spaceY: Float

        if (imageRatio > 1) {
            val splitWidth: Float = imageWidth / columnCount
            val splitHeight: Float = imageHeight / rowCount
            imageSplitLength = splitWidth.coerceAtMost(splitHeight)
            imageAdaptHeight = (rectWidth * imageHeight) / imageSplitLength
            imageAdaptWidth = (rectWidth * imageWidth) / imageSplitLength
            spaceX = abs(areaWidth - imageAdaptWidth - spaceSumWidth) * 0.5f
            spaceY = abs(areaHeight - imageAdaptHeight - spaceSumHeight) * 0.5f
        } else {
            val splitWidth = imageWidth / rowCount
            val splitHeight = imageHeight / columnCount
            imageSplitLength = splitWidth.coerceAtMost(splitHeight)
            imageAdaptHeight = (rectWidth * imageHeight) / imageSplitLength
            imageAdaptWidth = (rectWidth * imageWidth) / imageSplitLength

            spaceX = abs(areaHeight - imageAdaptWidth - spaceSumHeight) * 0.5f
            spaceY = abs(areaWidth - imageAdaptHeight - spaceSumWidth) * 0.5f
        }

        // 竖图四图情况下展示位置x,y需要互换
        originImageInfoList.forEachIndexed { index, info ->
            if (imageRatio <= 1) {
                calculateRectF(
                    index = processSplitVerticalIndex(
                        imageListSize = imageListSize,
                        index = index
                    ),
                    imageInfo = info,
                    imageListSize = imageListSize,
                )
            }

            val imageRectF = info.imageAreaRectF

            val imageAreaWidth: Float = imageRectF.width()
            val imageAreaHeight: Float = imageRectF.height()
            val imageAreaLeft: Float = imageRectF.left
            val imageAreaTop: Float = imageRectF.top

            val columnIndex = getColumnIndex(
                index = index,
                imageListSize = imageListSize
            )

            val rowIndex = getRowIndex(
                index = index,
                imageListSize = imageListSize
            )

            val imageLeft: Float
            val imageTop: Float
            val imageRight: Float
            val imageBottom: Float

            if (imageRatio > 1) {
                imageLeft = imageAreaLeft - columnIndex * imageAreaWidth - spaceX
                imageTop = imageAreaTop - rowIndex * imageAreaHeight - spaceY
                imageRight = imageLeft + imageAdaptWidth
                imageBottom = imageTop + imageAdaptHeight
            } else {
                imageLeft = imageAreaLeft - rowIndex * imageAreaWidth - spaceX
                imageTop = imageAreaTop - columnIndex * imageAreaHeight - spaceY
                imageRight = imageLeft + imageAdaptWidth
                imageBottom = imageTop + imageAdaptHeight
            }
            info.drawableBoundsRectF.set(
                imageLeft.toInt(),
                imageTop.toInt(),
                imageRight.toInt(),
                imageBottom.toInt()
            )
        }
    }

    private fun processSplitVerticalIndex(
        imageListSize: Int,
        index: Int
    ): Int {
        val rowIndex: Int = getRowIndex(
            index = index,
            imageListSize = imageListSize
        )
        val columnIndex: Int = getColumnIndex(
            index = index,
            imageListSize = imageListSize
        )
        val columnCount = getColumnCount(imageListSize = imageListSize)
        val newIndex = rowIndex + columnIndex * columnCount
        return if (newIndex >= imageListSize) {
            index
        } else {
            newIndex
        }
    }

    override fun Canvas.drawImageInfo(
        imageInfo: ImageInfo,
        imageListSize: Int
    ) {
        val imageRectF = imageInfo.imageAreaRectF
        drawable ?: let {
            this.save()
            this.clipRoundRectByRectF(rectF = imageRectF)
            this.drawPlaceholderByRectF(rectF = imageRectF)
            this.restore()
            return
        }
        this.save()
        this.clipRoundRectByRectF(rectF = imageRectF)
        this.drawDrawable2Canvas(
            drawable = drawable,
            drawableBoundsRect = imageInfo.drawableBoundsRectF
        )
        this.restore()
    }

    private fun getImageAreaWidth(
        columnCount: Int
    ): Float {
        return columnCount * rectWidth + config.spaceWidth * (columnCount - 1).coerceAtLeast(0)
    }

    private fun getImageAreaHeight(
        rowCount: Int
    ): Float {
        return rowCount * rectWidth + config.spaceWidth * (rowCount - 1).coerceAtLeast(0)
    }
}