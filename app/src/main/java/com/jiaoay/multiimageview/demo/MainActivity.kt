package com.jiaoay.multiimageview.demo

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jiaoay.multiimageview.databinding.ActivityMainBinding
import com.jiaoay.multiimageview.dp2px
import com.jiaoay.multiimageview.widget.data.ImageFactoryConfig
import com.jiaoay.multiimageview.widget.data.ImageSizeConfig
import com.jiaoay.multiimageview.widget.data.MultiImageSizeConfig
import com.jiaoay.multiimageview.widget.data.SplitImageSizeConfig
import com.jiaoay.multiimageview.widget.factory.MultiImageFactory
import com.jiaoay.multiimageview.widget.factory.SplitImageFactory
import com.jiaoay.multiimageview.widget.factory.provider.FillAreaSizeProvider
import com.jiaoay.multiimageview.widget.factory.provider.FillMinAreaSizeProvider
import com.jiaoay.multiimageview.widget.factory.provider.MinAreaSizeProvider
import com.jiaoay.multiimageview.widget.factory.provider.SizeProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel: MainViewModel by viewModels()

    private val roundRadius = 6f.dp2px
    private val spaceWidth = 6f.dp2px

    private val imageConfig = ImageFactoryConfig(
        placeholderColorInt = Color.parseColor("#eebbcb"),
        roundRadius = roundRadius
    )

    private val adapter by lazy {
        MainPageAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.multiType.setOnClickListener {
            viewModel.changeTypeStateType(type = MainViewModel.Type.Multi)
        }
        binding.splitType.setOnClickListener {
            viewModel.changeTypeStateType(type = MainViewModel.Type.Split)
        }

        //
        binding.addColumn.setOnClickListener {
            viewModel.addColumn()
        }
        binding.reduceColumn.setOnClickListener {
            viewModel.reduceColumn()
        }
        binding.addRow.setOnClickListener {
            viewModel.addRow()
        }
        binding.reduceRow.setOnClickListener {
            viewModel.reduceRow()
        }

        //
        binding.addImage.setOnClickListener {
            viewModel.addImage()
        }
        binding.reduceImage.setOnClickListener {
            viewModel.reduceImage()
        }
        binding.addSplit.setOnClickListener {
            viewModel.addSplitNum()
        }
        binding.reduceSplit.setOnClickListener {
            viewModel.reduceSplitNum()
        }
        binding.changeSplitImage.setOnClickListener {
            viewModel.changeSplitImage()
        }

        binding.sample.setOnClickListener {
            viewModel.changeShowType(showType = MainViewModel.ImageShowType.Simple)
        }

        binding.fillArea.setOnClickListener {
            viewModel.changeShowType(showType = MainViewModel.ImageShowType.FillArea)
        }

        binding.fillMinArea.setOnClickListener {
            viewModel.changeShowType(showType = MainViewModel.ImageShowType.FillMinArea)
        }

        binding.minArea.setOnClickListener {
            viewModel.changeShowType(showType = MainViewModel.ImageShowType.MinArea)
        }

        initCollector()
    }

    private fun initCollector() {
        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.RESUMED) {
                launch {
                    viewModel.dataFlow.collectLatest {
                        val list = it.second
                        val first = it.first
                        val type = first.first
                        val factory = when (val config: ImageSizeConfig = first.second) {
                            is MultiImageSizeConfig -> {
                                val sizeProvider = getProvider(
                                    type = type,
                                    sizeConfig = config
                                )
                                MultiImageFactory(
                                    config = imageConfig,
                                    sizeProvider = sizeProvider
                                )
                            }

                            is SplitImageSizeConfig -> {
                                val sizeProvider = getProvider(
                                    type = type,
                                    sizeConfig = config
                                )
                                SplitImageFactory(
                                    config = imageConfig,
                                    sizeProvider = sizeProvider
                                )
                            }
                        }

                        binding.multiImageView.setData(
                            imageUrlList = list,
                            factory = factory
                        )
                    }
                }

                launch {
                    viewModel.typeStateFlow.collectLatest {
                        when(it) {
                            MainViewModel.Type.Multi -> {
                                binding.splitType.isSelected = false
                                binding.multiType.isSelected = true
                                binding.rowControlLayout.isVisible = true

                                binding.imageControlLayout.isVisible = true
                                binding.changeSplitImage.isVisible = false
                                binding.splitControlLayout.isVisible = false
                            }
                            MainViewModel.Type.Split -> {
                                binding.splitType.isSelected = true
                                binding.multiType.isSelected = false
                                binding.rowControlLayout.isVisible = false

                                binding.imageControlLayout.isVisible = false
                                binding.changeSplitImage.isVisible = true
                                binding.splitControlLayout.isVisible = true
                            }
                        }
                    }
                }

                launch {
                    viewModel.showTypeStateFlow.collectLatest {
                        when(it) {
                            MainViewModel.ImageShowType.Simple -> {
                                binding.sample.isSelected = true
                                binding.fillArea.isSelected = false
                                binding.minArea.isSelected = false
                                binding.fillMinArea.isSelected = false
                            }
                            MainViewModel.ImageShowType.FillArea -> {
                                binding.sample.isSelected = false
                                binding.fillArea.isSelected = true
                                binding.minArea.isSelected = false
                                binding.fillMinArea.isSelected = false
                            }
                            MainViewModel.ImageShowType.MinArea -> {
                                binding.sample.isSelected = false
                                binding.fillArea.isSelected = false
                                binding.minArea.isSelected = true
                                binding.fillMinArea.isSelected = false
                            }
                            MainViewModel.ImageShowType.FillMinArea -> {
                                binding.sample.isSelected = false
                                binding.fillArea.isSelected = false
                                binding.minArea.isSelected = false
                                binding.fillMinArea.isSelected = true
                            }
                        }
                    }
                }
            }
        }
    }

    fun <T : ImageSizeConfig> getProvider(
        type: MainViewModel.ImageShowType,
        sizeConfig: T
    ): SizeProvider<T> {
        return when (type) {
            MainViewModel.ImageShowType.Simple -> {
                SizeProvider(config = sizeConfig)
            }

            MainViewModel.ImageShowType.FillArea -> {
                FillAreaSizeProvider(config = sizeConfig)
            }

            MainViewModel.ImageShowType.MinArea -> {
                MinAreaSizeProvider(config = sizeConfig)
            }

            MainViewModel.ImageShowType.FillMinArea -> {
                FillMinAreaSizeProvider(config = sizeConfig)
            }
        }
    }
}