package com.chernishenko.facebookphotoandanimation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.chernishenko.facebookphotoandanimation.R
import com.chernishenko.facebookphotoandanimation.adapter.PhotosGridAdapter
import com.chernishenko.facebookphotoandanimation.databinding.FragmentPhotoSelectionBinding
import com.chernishenko.facebookphotoandanimation.module.GlideApp
import com.chernishenko.facebookphotoandanimation.module.GlideRequests
import com.chernishenko.facebookphotoandanimation.viewmodel.MainViewModel

class PhotoSelectionFragment : Fragment() {

    companion object {
        const val TAG = "PhotoSelectionFragment"
    }

     private val viewModel by activityViewModels<MainViewModel>()
     private lateinit var preloader: RecyclerViewPreloader<String>
     private lateinit var requests: GlideRequests

    private lateinit var binding: FragmentPhotoSelectionBinding
    private lateinit var adapter: PhotosGridAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPhotoSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requests = GlideApp.with(this)
        binding.rvPhotos.layoutManager = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
        binding.rvPhotos.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
            setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)
        })
        binding.rvPhotos.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL).apply {
            setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)
        })
        viewModel.photos.observe(viewLifecycleOwner) {
            adapter = PhotosGridAdapter(requests, it) { photo ->
                viewModel.url.postValue(photo)
                parentFragmentManager.popBackStack()
                parentFragmentManager.popBackStack()
            }
            binding.rvPhotos.adapter = adapter

            val sizeProvider = ViewPreloadSizeProvider<String>(adapter.createViewHolder(binding.rvPhotos, 0).binding.ivPhoto)
            preloader = RecyclerViewPreloader(requests, adapter, sizeProvider, 9)
            binding.rvPhotos.addOnScrollListener(preloader)
        }
    }
}