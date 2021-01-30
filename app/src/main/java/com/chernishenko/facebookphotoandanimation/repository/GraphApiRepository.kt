package com.chernishenko.facebookphotoandanimation.repository

import android.net.Uri
import android.os.Bundle
import com.chernishenko.facebookphotoandanimation.model.Album
import com.facebook.AccessToken
import com.facebook.GraphRequest
import io.reactivex.rxjava3.core.Observable
import org.json.JSONObject

typealias OnListRetrieved<T> = (List<T>) -> Unit

class GraphApiRepository {

    companion object {
        private val ALBUM_TYPES = listOf("normal", "profile")
    }

    fun requestProfilePicture(): Observable<String> =
        Observable.create {
            val request =
                GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { jsonObject, _ ->
                    jsonObject
                        .optJSONObject("picture")
                        ?.optJSONObject("data")
                        ?.optString("url")
                        ?.let { url ->
                            it.onNext(url)
                        }
                }
            val parameters = Bundle()
            parameters.putString("fields", "picture.width(800).height(800)")
            request.parameters = parameters
            request.executeAsync()
        }

    fun requestProfileAlbums(): Observable<List<Album>> = Observable.create {
        val list = mutableListOf<Album>()
        val path = "/${AccessToken.getCurrentAccessToken().userId}/albums"
        profileAlbums(list, path) { l -> it.onNext(l) }
    }

    fun requestAlbumPhotos(path: String): Observable<List<String>> = Observable.create {
        val list = mutableListOf<String>()
        albumPhotos(list, path) { l -> it.onNext(l) }
    }

    private fun profileAlbums(list: MutableList<Album>, path: String, after: String? = null, l: OnListRetrieved<Album>) {
        val currentAccessToken = AccessToken.getCurrentAccessToken()
        val request = GraphRequest.newGraphPathRequest(currentAccessToken, path) { response ->
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
                        list.add(album)
                    }
                    val filteredList = list
                        .filter { album -> ALBUM_TYPES.contains(album.type) }
                        .toMutableList()
                        .moveToFirst(list.find { album -> album.type == "profile" })

                    val next = (it.opt("paging") as JSONObject).optString("next")
                    if (next.isNotEmpty()) {
                        profileAlbums(filteredList, path, Uri.parse(next).getQueryParameter("after"), l)
                    } else {
                        l.invoke(filteredList)
                    }
                }
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,count,description,name,type")
        after?.let { parameters.putString("after", after) }
        request.parameters = parameters
        request.executeAsync()
    }

    private fun albumPhotos(photos: MutableList<String>, path: String, after: String? = null, l: OnListRetrieved<String>) {
        val request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), path) { response ->
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
                        albumPhotos(photos, path, Uri.parse(next).getQueryParameter("after"), l)
                    } else {
                        l.invoke(photos)
                    }
                }
            }
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