package com.timgortworst.cleanarchitecture.data.mapper

import com.timgortworst.cleanarchitecture.data.model.movie.NetworkMovies
import com.timgortworst.cleanarchitecture.domain.model.movie.Movie
import java.text.SimpleDateFormat
import java.util.*

fun NetworkMovies.asDomainModel(): List<Movie> = with(this) {
    results.map {
        Movie(
            it.adult,
            it.backdropPath,
            it.id,
            it.originalLanguage,
            it.originalTitle,
            it.overview,
            it.popularity,
            it.posterPath,
            if (it.releaseDate.isNotBlank()) SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).parse(it.releaseDate) else null,
            it.title,
            it.video,
            it.voteAverage,
            it.voteCount,
            "https://image.tmdb.org/t/p/w185/".plus(it.posterPath),
            "https://image.tmdb.org/t/p/original/".plus(it.posterPath),
        )
    }
}