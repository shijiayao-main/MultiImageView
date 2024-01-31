package com.jiaoay.multiimageview.demo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jiaoay.multiimageview.databinding.ViewHolderImageBinding

class MainPageAdapter : RecyclerView.Adapter<ImageViewHolder>() {

    val list: MutableList<ImageData> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageViewHolder {
        val context = parent.context
        val binding = ViewHolderImageBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ImageViewHolder(
            binding = binding
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val data = list.getOrNull(position) ?: return
        holder.bindData(data = data)
    }

    fun itemReset(list: List<ImageData>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }
}