package com.chernishenko.facebookphotoandanimation.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.facebook.AccessToken
import com.facebook.GraphRequest

typealias OnUrlRetrieved = () -> Unit

class MainViewModel : ViewModel() {

    var url: String? = null

    fun retrieveImageUrl(accessToken: AccessToken, onUrlRetrieved: OnUrlRetrieved) {
        val request = GraphRequest.newMeRequest(accessToken) { jsonObject, _ ->
            jsonObject
                .optJSONObject("picture")
                ?.optJSONObject("data")
                ?.optString("url")
                ?.let { url ->
                    if (url.isNotBlank()) { this.url = url }
                    onUrlRetrieved.invoke()
                }
        }
        val parameters = Bundle()
        parameters.putString("fields", "picture.width(800).height(800)")
        request.parameters = parameters
        request.executeAsync()
    }
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