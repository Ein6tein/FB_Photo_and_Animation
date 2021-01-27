package com.chernishenko.facebookphotoandanimation.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chernishenko.facebookphotoandanimation.model.Album
import com.facebook.AccessToken
import com.facebook.GraphRequest
import org.json.JSONObject

typealias OnRequestComplete = () -> Unit
typealias OnLoading = (Boolean) -> Unit

class MainViewModel : ViewModel() {

    companion object {
        private val ALBUM_TYPES = listOf("normal", "profile")
    }

    var url: String? = null

    private var _albums = mutableListOf<Album>()
    val albums: List<Album>
        get() = _albums

    lateinit var loading: OnLoading

    fun retrieveImageUrl(accessToken: AccessToken, onRequestComplete: OnRequestComplete) {
        loading.invoke(true)
        val request = GraphRequest.newMeRequest(accessToken) { jsonObject, _ ->
            jsonObject
                .optJSONObject("picture")
                ?.optJSONObject("data")
                ?.optString("url")
                ?.let { url ->
                    if (url.isNotBlank()) { this.url = url }
                    onRequestComplete.invoke()
                } ?: loading.invoke(false)
        }
        val parameters = Bundle()
        parameters.putString("fields", "picture.width(800).height(800)")
        request.parameters = parameters
        request.executeAsync()
    }

    fun retrieveUserAlbums(accessToken: AccessToken, onRequestComplete: OnRequestComplete) {
        loading.invoke(true)
        val request = GraphRequest.newGraphPathRequest(accessToken, "/${accessToken.userId}/albums") { response ->
            response.jsonObject?.let {
                it.optJSONArray("data")?.let { data ->
                    _albums.clear()
                    for (i in 0 until data.length()) {
                        val obj = data[i] as JSONObject
                        val album = Album(
                            obj.optLong("id"),
                            obj.optString("name"),
                            obj.optInt("count"),
                            obj.optString("type")
                        )
                        album.apply {
                            if (type == "profile") name = "Photos of me"
                        }
                        _albums.add(album)
                    }
                    _albums = _albums
                        .filter { album -> ALBUM_TYPES.contains(album.type) }
                        .toMutableList()
                        .moveToFirst(_albums.find { album -> album.type == "profile" })
                    onRequestComplete.invoke()
                } ?: loading.invoke(false)
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,count,description,name,type")
        request.parameters = parameters
        request.executeAsync()
    }
}

private fun MutableList<Album>.moveToFirst(element: Album?): MutableList<Album> {
    element?.apply {
        remove(element)
        add(0, element)
    }
    return this
}

class MainViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel() as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}