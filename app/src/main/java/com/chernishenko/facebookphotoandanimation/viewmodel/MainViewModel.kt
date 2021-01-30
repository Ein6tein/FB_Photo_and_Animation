package com.chernishenko.facebookphotoandanimation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chernishenko.facebookphotoandanimation.model.Album
import com.chernishenko.facebookphotoandanimation.repository.GraphApiRepository

typealias OnLoading = (Boolean) -> Unit

class MainViewModel : ViewModel() {

    lateinit var loading: OnLoading

    val url = MutableLiveData<String>()

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>>
        get() = _albums

    private val _photos = MutableLiveData<List<String>>()
    val photos: LiveData<List<String>>
        get() = _photos

    private val repository: GraphApiRepository = GraphApiRepository()

    fun retrieveImageUrl() {
        loading.invoke(true)
        repository.requestProfilePicture().subscribe {
            if (it.isNotBlank()) {
                url.postValue(it)
            }
            loading.invoke(false)
        }
    }

    fun retrieveUserAlbums() {
        loading.invoke(true)
        _albums.value = emptyList()
        repository.requestProfileAlbums().subscribe {
            if (it.isNotEmpty()) {
                _albums.value = it
            }
            loading.invoke(false)
        }
    }

    fun retrievePhotos(albumId: Long) {
        loading.invoke(true)
        _photos.value = emptyList()
        val path = "/$albumId/photos"
        repository.requestAlbumPhotos(path).subscribe {
            if (it.isNotEmpty()) {
                _photos.value = it
            }
            loading.invoke(false)
        }
    }
}