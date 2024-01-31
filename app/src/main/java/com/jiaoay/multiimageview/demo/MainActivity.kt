package com.jiaoay.multiimageview.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jiaoay.multiimageview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val defaultList = listOf(
        "https://variety.com/wp-content/uploads/2018/11/star-citizen.jpg?resize=681",
        "https://www.pcgamesn.com/wp-content/uploads/2018/01/star-citizen.jpg",
        "https://i0.wp.com/twinfinite.net/wp-content/uploads/2020/09/Star-Citizen-6-scaled.jpg?resize=2048%2C1229&ssl=1",
        "https://hdqwalls.com/wallpapers/star-citizen-2019-4k-ze.jpg",
        "https://tse4-mm.cn.bing.net/th/id/OIP-C.wbwTJUZR3M4k0qS-50kBpgHaEJ?rs=1&pid=ImgDetMain",
        "https://starcitizen.tools/images/thumb/c/cc/C2_Feature_Style_clouds.jpg/1200px-C2_Feature_Style_clouds.jpg",
        "https://tse1-mm.cn.bing.net/th/id/OIP-C.M-VOoGyrApVOtfbygUxoCQHaDk?rs=1&pid=ImgDetMain",
        "https://www.wallpapertip.com/wmimgs/100-1002102_star-citizen-wallpaper-planet.jpg",
        "https://wallpaperaccess.com/full/756688.jpg"
    )

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

        val list: MutableList<ImageData> = ArrayList()
        repeat(9) {
            val data = createData(imageCount = it + 1)
            list.add(data)
        }
        list.add(
            createData(imageCount = 20)
        )
        adapter.itemReset(list)
    }

    private fun createData(imageCount: Int): ImageData {
        val imageList: MutableList<String> = ArrayList()
        repeat(imageCount) {
            imageList.add(defaultList[it % defaultList.size])
        }

        return ImageData(
            imageUrlList = imageList
        )
    }
}