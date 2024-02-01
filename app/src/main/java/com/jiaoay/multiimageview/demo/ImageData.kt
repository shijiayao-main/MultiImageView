package com.jiaoay.multiimageview.demo

data class ImageData(
    val imageUrlList: List<String>,
    val showType: ImageDataShowType,
)

enum class ImageDataShowType {
    Simple,
    SimpleFillArea,
    SimpleMinArea,
    SimpleFillMinArea,
    Split
}

