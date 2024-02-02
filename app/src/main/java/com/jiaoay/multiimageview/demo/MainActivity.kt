package com.jiaoay.multiimageview.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jiaoay.multiimageview.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

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

    private val currentListStateFlow: MutableStateFlow<List<ImageData>?> = MutableStateFlow(null)

    private val adapter by lazy {
        MainPageAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL,
            false
        )
        binding.recyclerView.adapter = adapter

        binding.sampleType1.setOnClickListener {
            currentListStateFlow.value = createImageList(
                showType = ImageDataShowType.Simple
            )
        }

        binding.sampleType2.setOnClickListener {
            currentListStateFlow.value = createImageList(
                showType = ImageDataShowType.SimpleFillArea
            )
        }

        binding.sampleType3.setOnClickListener {
            currentListStateFlow.value = createImageList(
                showType = ImageDataShowType.SimpleMinArea
            )
        }

        binding.sampleType4.setOnClickListener {
            currentListStateFlow.value = createImageList(
                showType = ImageDataShowType.SimpleFillMinArea
            )
        }

        binding.sampleType5.setOnClickListener {
            currentListStateFlow.value = createImageList(
                showType = ImageDataShowType.Split
            )
        }

        binding.sampleType6.setOnClickListener {
            currentListStateFlow.value = createImageList(
                showType = ImageDataShowType.SplitFillArea
            )
        }

        binding.sampleType7.setOnClickListener {
            currentListStateFlow.value = createImageList(
                showType = ImageDataShowType.SplitMinArea
            )
        }

        binding.sampleType8.setOnClickListener {
            currentListStateFlow.value = createImageList(
                showType = ImageDataShowType.SplitFillMinArea
            )
        }

        initCollector()
    }

    private fun initCollector() {
        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.RESUMED) {
                currentListStateFlow.collectLatest {
                    if (it == null) {
                        return@collectLatest
                    }
                    adapter.itemReset(it)
                }
            }
        }
    }

    private fun createImageList(
        showType: ImageDataShowType
    ): List<ImageData> {
        val list: MutableList<ImageData> = ArrayList()
        repeat(16) {
            val data = createData(
                imageCount = it + 1,
                showType = showType
            )
            list.add(data)
        }
        list.add(
            createData(
                imageCount = 20,
                showType = showType
            )
        )
        return list
    }

    private fun createData(
        imageCount: Int,
        showType: ImageDataShowType
    ): ImageData {
        val imageList: MutableList<String> = ArrayList()
        repeat(imageCount) {
            imageList.add(defaultList[it % defaultList.size])
        }

        return ImageData(
            imageUrlList = imageList,
            showType = showType
        )
    }
}