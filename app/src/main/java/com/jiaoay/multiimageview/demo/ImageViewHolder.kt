package com.jiaoay.multiimageview.demo

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jiaoay.multiimageview.databinding.ViewHolderImageBinding
import com.jiaoay.multiimageview.dp2px
import com.jiaoay.multiimageview.widget.MultiImageFactoryConfig
import com.jiaoay.multiimageview.widget.factory.SimpleMultiImageFactory

class ImageViewHolder(
    val binding: ViewHolderImageBinding
) : ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bindData(data: ImageData) {
        val imageList = data.imageUrlList
        binding.multiImageView.setData(
            imageUrlList = imageList,
            factory = SimpleMultiImageFactory(
                config = MultiImageFactoryConfig(
                    maxColumnCount = 3,
                    maxRowCount = 3,
                    spaceWidth = 3f.dp2px,
                    placeholderColorInt = Color.parseColor("#f3f3f3"),
                    roundRadius = 6f.dp2px,
                )
            )
        )
        binding.text.text = "list中有: ${imageList.size} 个图片"
    }
}