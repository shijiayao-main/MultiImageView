package com.jiaoay.multiimageview.demo

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jiaoay.multiimageview.databinding.ViewHolderImageBinding
import com.jiaoay.multiimageview.dp2px
import com.jiaoay.multiimageview.widget.MultiImageFactoryConfig
import com.jiaoay.multiimageview.widget.SplitImageFactoryConfig
import com.jiaoay.multiimageview.widget.factory.multi.MultiFillAreaImageFactory
import com.jiaoay.multiimageview.widget.factory.multi.MultiFillMinAreaImageFactory
import com.jiaoay.multiimageview.widget.factory.multi.MultiImageFactory
import com.jiaoay.multiimageview.widget.factory.multi.MultiMinAreaImageFactory
import com.jiaoay.multiimageview.widget.factory.split.SplitFillAreaImageFactory
import com.jiaoay.multiimageview.widget.factory.split.SplitFillMinAreaImageFactory
import com.jiaoay.multiimageview.widget.factory.split.SplitImageFactory
import com.jiaoay.multiimageview.widget.factory.split.SplitMinAreaImageFactory

class ImageViewHolder(
    val binding: ViewHolderImageBinding
) : ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bindData(data: ImageData) {
        val size = data.imageUrlList.size
        val multiConfig = MultiImageFactoryConfig(
            maxColumnCount = 3,
            maxRowCount = 3,
            spaceWidth = 3f.dp2px,
            placeholderColorInt = Color.parseColor("#f3f3f3"),
            roundRadius = 6f.dp2px,
        )

        val splitConfig = SplitImageFactoryConfig(
            maxColumnCount = 4,
            splitCount = size,
            spaceWidth = 3f.dp2px,
            placeholderColorInt = Color.parseColor("#f3f3f3"),
            roundRadius = 6f.dp2px,
        )

        val factory = when (data.showType) {
            ImageDataShowType.Simple -> {
                MultiImageFactory(
                    config = multiConfig
                )
            }

            ImageDataShowType.SimpleFillArea -> {
                MultiFillAreaImageFactory(
                    config = multiConfig
                )
            }

            ImageDataShowType.SimpleMinArea -> {
                MultiMinAreaImageFactory(
                    config = multiConfig
                )
            }

            ImageDataShowType.SimpleFillMinArea -> {
                MultiFillMinAreaImageFactory(
                    config = multiConfig
                )
            }

            ImageDataShowType.Split -> {
                SplitImageFactory(
                    config = splitConfig
                )
            }

            ImageDataShowType.SplitFillArea -> {
                SplitFillAreaImageFactory(
                    config = splitConfig
                )
            }

            ImageDataShowType.SplitMinArea -> {
                SplitMinAreaImageFactory(
                    config = splitConfig
                )
            }

            ImageDataShowType.SplitFillMinArea -> {
                SplitFillMinAreaImageFactory(
                    config = splitConfig
                )
            }
        }

        val imageList = data.imageUrlList

        binding.multiImageView.setData(
            imageUrlList = imageList,
            factory = factory
        )
        binding.text.text = "list中有: $size 个图片"
    }
}