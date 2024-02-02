package com.jiaoay.multiimageview.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class MultiImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var factory: AbstractImageFactory<*>? = null

    private val controller = object : MultiImageViewController {
        override fun notifyInvalidate() {
            this@MultiImageView.invalidate()
        }

        private val paddingRect = Rect()
        override fun getPaddingRect(): Rect {
            paddingRect.set(
                paddingLeft,
                paddingTop,
                paddingRight,
                paddingBottom
            )
            return paddingRect
        }
    }

    private val imageInfoList: MutableList<ImageInfo> = ArrayList()

    fun setData(
        imageUrlList: List<String>,
        factory: AbstractImageFactory<*>,
    ) {
        factory.controller = controller

        this.factory = factory
        imageInfoList.clear()

        val list = imageUrlList.filter {
            it.isNotBlank()
        }
        if (list.isEmpty()) {
            requestLayout()
            return
        }

        val imageInfoList = factory.processData(imageUrlList = imageUrlList)
        this.imageInfoList.addAll(imageInfoList)

        requestLayout()
        post {
            factory.loadImageDrawable(
                context = context,
                imageInfoList = imageInfoList,
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val areaSizePair = factory?.measureAreaSize(
            width = width,
            height = height,
            imageListSize = imageInfoList.size
        )

        val areaWidth = areaSizePair?.first ?: 0f
        val areaHeight = areaSizePair?.second ?: 0f

        factory?.measureImageRectF(
            imageInfoList = imageInfoList,
        )

        setMeasuredDimension(
            areaWidth.toInt(),
            areaHeight.toInt()
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (imageInfoList.isEmpty()) {
            return
        }
        factory?.drawImage2Canvas(
            canvas = canvas,
            imageInfoList = imageInfoList
        )
    }
}