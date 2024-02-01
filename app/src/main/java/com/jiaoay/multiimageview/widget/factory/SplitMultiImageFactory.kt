package com.jiaoay.multiimageview.widget.factory

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.Log
import com.jiaoay.multiimageview.widget.AbstractImageFactory
import com.jiaoay.multiimageview.widget.ImageInfo
import com.jiaoay.multiimageview.widget.SplitMultiImageFactoryConfig
import kotlin.math.abs
import kotlin.math.ceil

/**
 * 将单图裁切成多份去展示
 */
class SplitMultiImageFactory(
    config: SplitMultiImageFactoryConfig
) : AbstractImageFactory<SplitMultiImageFactoryConfig>(config) {

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

    private var areaWidth: Float = 0f
    private var areaHeight: Float = 0f

    private var paddingLeft: Int = 0
    private var paddingTop: Int = 0
    private var paddingRight: Int = 0
    private var paddingBottom: Int = 0

    override fun measureAreaSize(
        width: Int,
        height: Int,
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int,
        imageListSize: Int
    ): Pair<Float, Float> {
        this.paddingLeft = paddingLeft
        this.paddingTop = paddingTop
        this.paddingRight = paddingRight
        this.paddingBottom = paddingBottom

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

        this.areaWidth = areaWidth
        this.areaHeight = areaHeight

        return areaWidth to areaHeight
    }

    private fun getColumnCount(imageListSize: Int): Int {
        return if (imageListSize >= config.maxColumnCount) {
            config.maxColumnCount
        } else {
            imageListSize % config.maxColumnCount
        }
    }

    private fun getRowCount(imageListSize: Int): Int {
        return ceil(imageListSize / config.maxColumnCount.toDouble()).toInt()
    }

    private fun calculateRectWidth(
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
        paddingBottom: Int
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

    private fun getColumnIndex(
        index: Int,
        imageListSize: Int,
    ): Int {
        return index % columnCount
    }

    private fun getRowIndex(
        index: Int,
        imageListSize: Int,
    ): Int {
        return index / columnCount
    }

    override fun loadImageDrawable(
        context: Context,
        imageInfoList: List<ImageInfo>
    ) {
        val imageListSize = imageInfoList.size

        val singleImageList: List<ImageInfo> = imageInfoList.firstOrNull()?.let {
            listOf(
                ImageInfo(
                    index = 0,
                    url = it.url,
                    imageRectF = RectF(
                        0f,
                        0f,
                        areaWidth,
                        areaHeight
                    ),
                    drawable = it.drawable
                )
            )
        } ?: emptyList()

        loadImageDrawable(
            context = context,
            imageInfoList = singleImageList,
            endProcess = endProcess@{
                val drawable = it.drawable ?: return@endProcess
                this.drawable = drawable

                val imageWidth = drawable.intrinsicWidth
                val imageHeight = drawable.intrinsicHeight
                val imageRatio = safeGetRatio(
                    width = imageWidth.toFloat(),
                    height = imageHeight.toFloat()
                )
                if (imageRatio > 1) {
                    // 横图先不处理
                    return@endProcess
                }
                // 竖图四图情况下展示位置x,y需要互换
                imageInfoList.forEachIndexed { index, imageInfo ->
                    calculateRectF(
                        index = processSplitIndex(
                            imageListSize = imageListSize,
                            index = index
                        ),
                        imageInfo = imageInfo,
                        paddingLeft = paddingLeft,
                        paddingTop = paddingTop,
                        paddingRight = paddingRight,
                        paddingBottom = paddingBottom,
                        imageListSize = imageListSize,
                    )
                }
            },
        )
    }

    private fun processSplitIndex(imageListSize: Int, index: Int): Int {
        val rowIndex: Int = getRowIndex(
            index = index,
            imageListSize = imageListSize
        )
        val columnIndex: Int = getColumnIndex(
            index = index,
            imageListSize = imageListSize
        )

        val columnCount = getColumnCount(imageListSize = imageListSize)
        val rowCount: Int = getRowCount(imageListSize = imageListSize)

        return if (columnIndex >= rowCount || rowIndex >= columnCount) {
            index
        } else {
            rowIndex + columnIndex * columnCount
        }
    }

    override fun Canvas.drawImageInfo(
        imageInfo: ImageInfo,
        imageListSize: Int
    ) {
        val imageRectF = imageInfo.imageRectF
        val imageDrawable = drawable ?: let {
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
            imageIndex = imageInfo.index,
            imageListSize = imageListSize,
            imageRectF = imageRectF,
            imageWidth = imageDrawable.intrinsicWidth.toFloat(),
            imageHeight = imageDrawable.intrinsicHeight.toFloat()
        )
        this.restore()
    }

    override fun Canvas.drawDrawable2Canvas(
        drawable: Drawable?,
        imageIndex: Int,
        imageListSize: Int,
        imageRectF: RectF,
        imageWidth: Float,
        imageHeight: Float,
    ) {
        drawable ?: return

        val imageAreaWidth: Float = imageRectF.width()
        val imageAreaHeight: Float = imageRectF.height()
        val imageAreaLeft: Float = imageRectF.left
        val imageAreaTop: Float = imageRectF.top

        val imageRatio: Float = safeGetRatio(
            width = imageWidth,
            height = imageHeight,
        )

        val columnIndex = getColumnIndex(
            index = imageIndex,
            imageListSize = imageListSize
        )

        val rowIndex = getRowIndex(
            index = imageIndex,
            imageListSize = imageListSize
        )

        val imageSplitLength: Float

        val imageLeft: Float
        val imageTop: Float
        val imageRight: Float
        val imageBottom: Float

        if (imageRatio > 1) {
            val splitWidth = imageWidth / columnCount
            val splitHeight = imageHeight / rowCount
            imageSplitLength = splitWidth.coerceAtMost(splitHeight)
            val imageAdaptHeight: Float = (imageAreaHeight * imageHeight) / imageSplitLength
            val imageAdaptWidth: Float = (imageAreaWidth * imageWidth) / imageSplitLength

            val spaceX = abs(areaWidth - imageAdaptWidth) * 0.5f
            val spaceY = abs(areaHeight - imageAdaptHeight) * 0.5f

            imageLeft = imageAreaLeft - columnIndex * imageAreaWidth - spaceX
            imageTop = imageAreaTop - rowIndex * imageAreaHeight - spaceY
            imageRight = imageLeft + imageAdaptWidth
            imageBottom = imageTop + imageAdaptHeight
        } else {
            val splitWidth = imageWidth / rowCount
            val splitHeight = imageHeight / columnCount
            imageSplitLength = splitWidth.coerceAtMost(splitHeight)
            val imageAdaptHeight: Float = (imageAreaHeight * imageHeight) / imageSplitLength
            val imageAdaptWidth: Float = (imageAreaWidth * imageWidth) / imageSplitLength

            val spaceX = abs(areaWidth - imageAdaptWidth) * 0.5f
            val spaceY = abs(areaHeight - imageAdaptHeight) * 0.5f

            imageLeft = imageAreaLeft - rowIndex * imageAreaWidth - spaceX
            imageTop = imageAreaTop - columnIndex * imageAreaHeight - spaceY
            imageRight = imageLeft + imageAdaptWidth
            imageBottom = imageTop + imageAdaptHeight
        }

        drawable.setBounds(
            imageLeft.toInt(),
            imageTop.toInt(),
            imageRight.toInt(),
            imageBottom.toInt()
        )

        drawable.draw(this)
    }
}