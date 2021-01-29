package com.chernishenko.facebookphotoandanimation.viewmodel

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    val photos = mutableListOf<String>()

    val url = MutableLiveData<String>()

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
                ?.let {
                    if (it.isNotBlank()) {
                        url.postValue(it)
                        onRequestComplete.invoke()
                    }
                } ?: loading.invoke(false)
        }
        val parameters = Bundle()
        parameters.putString("fields", "picture.width(800).height(800)")
        request.parameters = parameters
        request.executeAsync()
    }

    fun retrieveUserAlbums(accessToken: AccessToken, onRequestComplete: OnRequestComplete) {
        loading.invoke(true)
        _albums.clear()
        val path = "/${accessToken.userId}/albums"
        requestAlbums(accessToken, path, onRequestComplete = onRequestComplete)
    }

    private fun requestAlbums(accessToken: AccessToken, path: String, after: String? = null, onRequestComplete: OnRequestComplete) {
        val request = GraphRequest.newGraphPathRequest(accessToken, path) { response ->
            response.jsonObject?.let {
                it.optJSONArray("data")?.let { data ->
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

                    val next = (it.opt("paging") as JSONObject).optString("next")
                    if (next.isNotEmpty()) {
                        requestAlbums(accessToken, path, Uri.parse(next).getQueryParameter("after"), onRequestComplete)
                    } else {
                        onRequestComplete.invoke()
                    }

                } ?: loading.invoke(false)
            } ?: loading.invoke(false)
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,count,description,name,type")
        after?.let { parameters.putString("after", after) }
        request.parameters = parameters
        request.executeAsync()
    }

    fun retrievePhotos(albumId: Long, accessToken: AccessToken, onRequestComplete: OnRequestComplete) {
        loading.invoke(true)
        photos.clear()
        val link = "/$albumId/photos"
        requestPhotos(accessToken, link, onRequestComplete = onRequestComplete)
    }

    private fun requestPhotos(accessToken: AccessToken, path: String, after: String? = null, onRequestComplete: OnRequestComplete) {
        val request = GraphRequest.newGraphPathRequest(accessToken, path) { response ->
            response.jsonObject?.let {
                it.optJSONArray("data")?.let { array ->
                    for (i in 0 until array.length()) {
                        val images = (array[i] as JSONObject).optJSONArray("images")
                        images?.let {
                            photos.add((images[0] as JSONObject).opt("source") as String)
                        }
                    }
                    val next = (it.opt("paging") as JSONObject).optString("next")
                    if (next.isNotEmpty()) {
                        requestPhotos(accessToken, path, Uri.parse(next).getQueryParameter("after"), onRequestComplete)
                    } else {
                        onRequestComplete.invoke()
                    }
                } ?: loading.invoke(false)
            } ?: loading.invoke(false)
        }
        val parameters = Bundle()
        parameters.putString("fields", "images")
        after?.let { parameters.putString("after", after) }
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