@file:OptIn(ExperimentalCoroutinesApi::class)

package com.jiaoay.multiimageview.demo

import androidx.lifecycle.ViewModel
import com.jiaoay.multiimageview.dp2px
import com.jiaoay.multiimageview.widget.data.ImageSizeConfig
import com.jiaoay.multiimageview.widget.data.MultiImageSizeConfig
import com.jiaoay.multiimageview.widget.data.SplitImageSizeConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach

class MainViewModel : ViewModel() {

    private val defaultList = listOf(
        "https://variety.com/wp-content/uploads/2018/11/star-citizen.jpg?resize=681",
        "https://ts1.cn.mm.bing.net/th/id/R-C.815839bff5fd65d0451a8db1d052ce13?rik=WIy2O9aWDkvtQw&riu=http%3a%2f%2fww1.sinaimg.cn%2flarge%2f844f612dgw1eoik7jwe4lj21kw29xdmq.jpg&ehk=bEB6BcOrRyIutMgqIIGB%2f5gkag9y0J777r80wJ%2bTAKM%3d&risl=&pid=ImgRaw&r=0",
        "https://www.pcgamesn.com/wp-content/uploads/2018/01/star-citizen.jpg",
        "https://i0.wp.com/twinfinite.net/wp-content/uploads/2020/09/Star-Citizen-6-scaled.jpg?resize=2048%2C1229&ssl=1",
        "https://hdqwalls.com/wallpapers/star-citizen-2019-4k-ze.jpg",
        "https://tse4-mm.cn.bing.net/th/id/OIP-C.wbwTJUZR3M4k0qS-50kBpgHaEJ?rs=1&pid=ImgDetMain",
        "https://starcitizen.tools/images/thumb/c/cc/C2_Feature_Style_clouds.jpg/1200px-C2_Feature_Style_clouds.jpg",
        "https://tse1-mm.cn.bing.net/th/id/OIP-C.M-VOoGyrApVOtfbygUxoCQHaDk?rs=1&pid=ImgDetMain",
        "https://www.wallpapertip.com/wmimgs/100-1002102_star-citizen-wallpaper-planet.jpg",
        "https://wallpaperaccess.com/full/756688.jpg"
    )

    private val currentImageSize: MutableStateFlow<Int> = MutableStateFlow(1)
    private val randomImageIndex: MutableStateFlow<Int> = MutableStateFlow(0)

    private val columnCountStateFlow: MutableStateFlow<Int> = MutableStateFlow(3)
    private val rowCountStateFlow: MutableStateFlow<Int> = MutableStateFlow(3)
    private val splitNumStateFlow: MutableStateFlow<Int> = MutableStateFlow(2)

    val typeStateFlow: MutableStateFlow<Type> = MutableStateFlow(Type.Multi)

    val showTypeStateFlow: MutableStateFlow<ImageShowType> =
        MutableStateFlow(ImageShowType.Simple)

    private val sizeConfigStateFlow: Flow<ImageSizeConfig> =
        combine(
            typeStateFlow,
            columnCountStateFlow,
            rowCountStateFlow,
            splitNumStateFlow
        ) { type, columnCount, rowCount, splitNum ->
            if (type == Type.Multi) {
                MultiImageSizeConfig(
                    maxRowCount = rowCount,
                    maxColumnCount = columnCount,
                    spaceWidth = 6f.dp2px,
                )
            } else {
                SplitImageSizeConfig(
                    splitCount = splitNum,
                    maxColumnCount = columnCount,
                    spaceWidth = 6f.dp2px,
                )
            }
        }.onEach {
            delay(100)
        }.buffer(
            capacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )


//        MutableStateFlow(
//        MultiImageSizeConfig(
//            maxRowCount = 3,
//            maxColumnCount = 3,
//            spaceWidth = 6f.dp2px,
//        )
//    )

    private val currentListStateFlow: Flow<List<String>> =
        combine(currentImageSize, randomImageIndex) { size, randomIndex ->
            size to randomIndex
        }.onEach {
            delay(100)
        }.buffer(
            capacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        ).mapLatest {
            val size = it.first
            val randomIndex = it.second
            val resultList: MutableList<String> = ArrayList()
            if (typeStateFlow.value == Type.Split) {
                val url = defaultList.getOrNull(randomIndex % defaultList.size) ?: ""
                if (url.isNotBlank()) {
                    resultList.add(url)
                }
            } else {
                for (index in 0 until size) {
                    val url = defaultList.getOrNull(index % defaultList.size) ?: ""
                    if (url.isNotBlank()) {
                        resultList.add(url)
                    }
                }
            }

            resultList
        }

    val dataFlow = combine(
        showTypeStateFlow,
        sizeConfigStateFlow,
        currentListStateFlow
    ) { showType, sizeConfig, list ->
        (showType to sizeConfig) to list
    }.onEach {
        delay(100)
    }.buffer(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    fun changeTypeStateType(type: Type) {
        typeStateFlow.value = type
    }

    fun changeShowType(showType: ImageShowType) {
        this.showTypeStateFlow.value = showType
    }

    fun addColumn() {
        columnCountStateFlow.value = columnCountStateFlow.value + 1
    }

    fun reduceColumn() {
        columnCountStateFlow.value = (columnCountStateFlow.value - 1).coerceAtLeast(1)
    }

    fun addRow() {
        rowCountStateFlow.value = rowCountStateFlow.value + 1
    }

    fun reduceRow() {
        rowCountStateFlow.value = (rowCountStateFlow.value - 1).coerceAtLeast(1)
    }

    fun addSplitNum() {
        splitNumStateFlow.value = splitNumStateFlow.value + 1
    }

    fun reduceSplitNum() {
        splitNumStateFlow.value = (splitNumStateFlow.value - 1).coerceAtLeast(1)
    }

    fun addImage() {
        currentImageSize.value = currentImageSize.value + 1
    }

    fun reduceImage() {
        currentImageSize.value = (currentImageSize.value - 1).coerceAtLeast(1)
    }

    fun changeSplitImage() {
        randomImageIndex.value = (0..9).randomOrNull() ?: 0
    }

    enum class Type {
        Multi,
        Split
    }

    enum class ImageShowType {
        Simple,
        FillArea,
        MinArea,
        FillMinArea,
    }

}