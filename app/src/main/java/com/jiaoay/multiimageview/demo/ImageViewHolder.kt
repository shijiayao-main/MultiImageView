package com.jiaoay.multiimageview.demo

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jiaoay.multiimageview.databinding.ViewHolderImageBinding
import com.jiaoay.multiimageview.dp2px
import com.jiaoay.multiimageview.widget.MultiImageFactoryConfig
import com.jiaoay.multiimageview.widget.SplitMultiImageFactoryConfig
import com.jiaoay.multiimageview.widget.factory.FillAreaMultiImageFactory
import com.jiaoay.multiimageview.widget.factory.FillMinAreaMultiImageFactory
import com.jiaoay.multiimageview.widget.factory.MinAreaMultiImageFactory
import com.jiaoay.multiimageview.widget.factory.SimpleMultiImageFactory
import com.jiaoay.multiimageview.widget.factory.SplitMultiImageFactory

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

        val splitConfig = SplitMultiImageFactoryConfig(
            maxColumnCount = 3,
            splitCount = size,
            spaceWidth = 3f.dp2px,
            placeholderColorInt = Color.parseColor("#f3f3f3"),
            roundRadius = 6f.dp2px,
        )

        val factory = when (data.showType) {
            ImageDataShowType.Simple -> {
                SimpleMultiImageFactory(
                    config = multiConfig
                )
            }

            ImageDataShowType.SimpleFillArea -> {
                FillAreaMultiImageFactory(
                    config = multiConfig
                )
            }

            ImageDataShowType.SimpleMinArea -> {
                MinAreaMultiImageFactory(
                    config = multiConfig
                )
            }

            ImageDataShowType.SimpleFillMinArea -> {
                FillMinAreaMultiImageFactory(
                    config = multiConfig
                )
            }

            ImageDataShowType.Split -> {
                SplitMultiImageFactory(
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