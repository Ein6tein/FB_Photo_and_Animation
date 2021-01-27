package com.chernishenko.facebookphotoandanimation.model

data class Album(
    val id: Long,
    var name: String,
    val count: Int,
    val type: String
)