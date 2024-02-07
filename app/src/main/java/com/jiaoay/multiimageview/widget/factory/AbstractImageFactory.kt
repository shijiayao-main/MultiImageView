package com.jiaoay.multiimageview.widget.factory

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import com.jiaoay.multiimageview.loadImageUri
import com.jiaoay.multiimageview.scope
import com.jiaoay.multiimageview.widget.MultiImageViewController
import com.jiaoay.multiimageview.widget.data.ImageFactoryConfig
import com.jiaoay.multiimageview.widget.data.ImageInfo
import com.jiaoay.multiimageview.widget.data.ImageSizeConfig
import com.jiaoay.multiimageview.widget.factory.provider.SizeProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

abstract class AbstractImageFactory<T : ImageSizeConfig>(
    val config: ImageFactoryConfig,
    protected val sizeProvider: SizeProvider<T>
) {

    protected val TAG = "AbstractImageFactory"

    protected val sizeConfig: T = sizeProvider.config

    var controller: MultiImageViewController? = null

    protected fun getPaddingRect(): Rect {
        return controller?.getPaddingRect() ?: Rect()
    }

    abstract fun processData(imageUrlList: List<String>): List<ImageInfo>

    abstract fun measureAreaSize(
        width: Int,
        height: Int,
        imageListSize: Int
    ): Pair<Float, Float>

    abstract fun measureImageRectF(
        imageInfoList: List<ImageInfo>,
    )

    private var imageLoadJob: Job? = null

    open fun loadImageDrawable(
        context: Context,
        imageInfoList: List<ImageInfo>,
    ) {
        loadImageDrawable(
            context = context,
            imageInfoList = imageInfoList,
            endProcess = endProcess@{ imageInfo ->
                val drawable = imageInfo.drawable ?: return@endProcess

                val imageRectF = imageInfo.imageAreaRectF
                val imageWidth = drawable.intrinsicWidth.toFloat()
                val imageHeight = drawable.intrinsicHeight.toFloat()

                val areaWidth: Float = imageRectF.width()
                val areaHeight: Float = imageRectF.height()
                val areaLeft: Float = imageRectF.left
                val areaTop: Float = imageRectF.top

                val viewRatio: Float = safeGetRatio(width = areaWidth, height = areaHeight)
                val imageRatio: Float = safeGetRatio(width = imageWidth, height = imageHeight)

                val imageAdaptWidth: Float
                val imageAdaptHeight: Float

                if (imageRatio >= viewRatio) {
                    imageAdaptHeight = areaHeight
                    imageAdaptWidth = imageAdaptHeight * imageRatio
                } else {
                    imageAdaptWidth = areaWidth
                    imageAdaptHeight = if (imageRatio > 0) {
                        imageAdaptWidth / imageRatio
                    } else {
                        imageHeight
                    }
                }

                val imageLeft: Float = areaLeft + (areaWidth - imageAdaptWidth) / 2f
                val imageTop: Float = areaTop + (areaHeight - imageAdaptHeight) / 2f
                val imageRight: Float = imageLeft + imageAdaptWidth
                val imageBottom: Float = imageTop + imageAdaptHeight

                imageInfo.drawableBoundsRectF.set(
                    imageLeft.toInt(),
                    imageTop.toInt(),
                    imageRight.toInt(),
                    imageBottom.toInt()
                )
            },
        )
    }

    protected fun loadImageDrawable(
        context: Context,
        imageInfoList: List<ImageInfo>,
        endProcess: (ImageInfo) -> Unit
    ) {
        if (imageInfoList.isEmpty()) {
            imageLoadJob?.cancel()
            controller?.notifyInvalidate()
            return
        }
        imageLoadJob?.cancel()

        val flow = flow {
            imageInfoList.forEachIndexed { index, imageInfo ->
                if (imageInfo.drawable != null) {
                    emit(index)
                    return@forEachIndexed
                }
                val url = imageInfo.url
                val drawable = context.loadImageUri(
                    uri = Uri.parse(url),
                    width = imageInfo.imageAreaRectF.width().toInt(),
                    height = imageInfo.imageAreaRectF.height().toInt(),
                )
                imageInfo.drawable = drawable
                endProcess.invoke(imageInfo)
                emit(index)
            }
        }

        imageLoadJob = context.scope.launch {
            flow.collectLatest {
                if (it != 0) {
                    delay(8)
                }
                controller?.notifyInvalidate()
            }
        }
    }

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun drawImage2Canvas(canvas: Canvas, imageInfoList: List<ImageInfo>) {
        imageInfoList.forEach {
            canvas.drawImageInfo(
                imageInfo = it,
                imageListSize = imageInfoList.size
            )
        }
    }

    open fun Canvas.drawImageInfo(
        imageInfo: ImageInfo,
        imageListSize: Int
    ) {
        val imageRectF = imageInfo.imageAreaRectF
        val imageDrawable = imageInfo.drawable ?: let {
            this.save()
            this.clipRoundRectByRectF(rectF = imageRectF)
            this.drawPlaceholderByRectF(rectF = imageRectF)
            this.restore()
            return
        }
        this.save()
        this.clipRoundRectByRectF(rectF = imageRectF)
        this.drawDrawable2Canvas(
            drawable = imageDrawable,
            drawableBoundsRect = imageInfo.drawableBoundsRectF
        )
        this.restore()
    }

    open fun Canvas.drawDrawable2Canvas(
        drawable: Drawable?,
        drawableBoundsRect: Rect,
    ) {
        drawable ?: return
        drawable.bounds = drawableBoundsRect
        drawable.draw(this)
    }

    protected fun safeGetRatio(width: Float, height: Float): Float {
        return if (width > 0 && height > 0) {
            width / height
        } else {
            1f
        }
    }

    private val clipPath = Path()

    protected fun Canvas.clipRoundRectByRectF(rectF: RectF) {
        clipPath.reset()
        clipPath.addRoundRect(
            rectF,
            config.roundRadius,
            config.roundRadius,
            Path.Direction.CCW
        )
        this.clipPath(clipPath)
    }

    protected fun Canvas.drawPlaceholderByRectF(rectF: RectF) {
        paint.reset()
        paint.color = config.placeholderColorInt
        this.drawRect(rectF, paint)
    }

}