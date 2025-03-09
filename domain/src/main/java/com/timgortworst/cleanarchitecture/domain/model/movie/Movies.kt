package com.timgortworst.cleanarchitecture.domain.model.movie

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Movies(
    val page: Int,
    val results: List<Movie>,
    val totalPages: Int,
    val totalResults: Int,
)
