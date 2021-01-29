package com.chernishenko.facebookphotoandanimation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chernishenko.facebookphotoandanimation.R
import com.chernishenko.facebookphotoandanimation.databinding.RowAlbumBinding
import com.chernishenko.facebookphotoandanimation.model.Album

typealias OnClick = (Long) -> Unit

class AlbumListAdapter(
    private val list: List<Album>,
    private val onClickListener: OnClick
) : RecyclerView.Adapter<AlbumListAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(RowAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.tvAlbumName.text = list[position].name
        holder.binding.tvPhotosCount.text = holder.binding.root.context.getString(R.string.count_d, list[position].count)
        holder.binding.root.setOnClickListener { onClickListener.invoke(list[position].id) }
    }

    override fun getItemCount(): Int = list.size

    inner class ItemViewHolder(val binding: RowAlbumBinding) : RecyclerView.ViewHolder(binding.root)
}