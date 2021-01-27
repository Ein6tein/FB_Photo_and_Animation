package com.chernishenko.facebookphotoandanimation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chernishenko.facebookphotoandanimation.R
import com.chernishenko.facebookphotoandanimation.adapter.AlbumListAdapter
import com.chernishenko.facebookphotoandanimation.databinding.FragmentAlbumSelectionBinding
import com.chernishenko.facebookphotoandanimation.viewmodel.MainViewModel
import com.chernishenko.facebookphotoandanimation.viewmodel.MainViewModelFactory

class AlbumSelectionFragment : Fragment() {

    companion object {
        const val TAG = "AlbumSelectionFragment"
    }

    private val factory by lazy { MainViewModelFactory() }
    private val viewModel by lazy { ViewModelProvider(requireActivity(), factory).get(MainViewModel::class.java) }

    private lateinit var binding: FragmentAlbumSelectionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAlbumSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvAlbums.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.rvAlbums.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
            setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)
        })
        binding.rvAlbums.adapter = AlbumListAdapter(viewModel.albums)
        viewModel.loading.invoke(false)
    }
}