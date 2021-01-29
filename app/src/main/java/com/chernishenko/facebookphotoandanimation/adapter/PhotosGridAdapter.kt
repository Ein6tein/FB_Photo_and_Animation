package com.chernishenko.facebookphotoandanimation.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chernishenko.facebookphotoandanimation.databinding.CellPhotoBinding
import com.chernishenko.facebookphotoandanimation.module.GlideRequests

typealias OnPhotoClick = (String) -> Unit

class PhotosGridAdapter(
    private val requests: GlideRequests,
    private val photos: List<String>,
    private val onPhotoClick: OnPhotoClick
) : RecyclerView.Adapter<PhotosGridAdapter.ItemViewHolder>(), ListPreloader.PreloadModelProvider<String> {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = CellPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        requests
            .load(photos[position])
            .placeholder(ColorDrawable(Color.LTGRAY))
            .transition(DrawableTransitionOptions.withCrossFade(300))
            .into(holder.binding.ivPhoto)
        holder.binding.ivPhoto.setOnClickListener { onPhotoClick.invoke(photos[position]) }
    }

    override fun getItemCount(): Int = photos.size

    inner class ItemViewHolder(val binding: CellPhotoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getPreloadItems(position: Int): MutableList<String> {
        return photos.subList(position, if (position + 8 < photos.size) position + 8 else photos.size).toMutableList()
    }

    override fun getPreloadRequestBuilder(item: String): RequestBuilder<*> = requests
            .load(item)
            .placeholder(ColorDrawable(Color.LTGRAY))
            .transition(DrawableTransitionOptions.withCrossFade(300))
}
