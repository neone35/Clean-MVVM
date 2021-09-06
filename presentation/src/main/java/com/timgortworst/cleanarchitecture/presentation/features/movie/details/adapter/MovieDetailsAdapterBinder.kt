package com.timgortworst.cleanarchitecture.presentation.features.movie.details.adapter

import android.content.Context
import androidx.recyclerview.widget.ConcatAdapter
import com.timgortworst.cleanarchitecture.domain.model.movie.Credits
import com.timgortworst.cleanarchitecture.domain.model.movie.Movie
import com.timgortworst.cleanarchitecture.domain.model.movie.MovieDetails
import com.timgortworst.cleanarchitecture.presentation.R
import com.timgortworst.cleanarchitecture.presentation.features.base.AdapterItemBinder
import com.timgortworst.cleanarchitecture.presentation.model.Margins
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class MovieDetailsAdapterBinder @Inject constructor(
    @ActivityContext private val context: Context
) : AdapterItemBinder<ConcatAdapter, MovieDetails> {
    private val relatedMoviesAdapter by lazy {
        RelatedMoviesAdapter()
    }

    private val spacing by lazy {
        context.resources.getDimension(R.dimen.keyline_16).toInt()
    }

    private val defaultMargins by lazy {
        Margins(left = spacing, top = spacing, right = spacing)
    }

    override fun addToAdapter(adapter: ConcatAdapter, item: MovieDetails) {
        with(adapter) {
            addStatistics(item.status, item.voteAverage, item.voteCount, item.popularity)
            addGenres(item.genres)
            addOverView(item.overview)
            addWatchProviders(item.watchProviders)
            addCast(item.cast)
            addRelatedMovies(item.recommendations)
        }
    }

    private fun ConcatAdapter.addStatistics(
        status: String,
        voteAverage: Double,
        voteCount: Int,
        popularity: Double
    ) {
        addAdapter(StatisticsAdapter(status, voteAverage, voteCount, popularity, defaultMargins))
    }

    private fun ConcatAdapter.addGenres(item: List<MovieDetails.Genre>) {
        addAdapter(GenresAdapter(item, defaultMargins))
    }

    private fun ConcatAdapter.addOverView(item: String) {
        addAdapter(TextAdapter(item, defaultMargins))
    }

    private fun ConcatAdapter.addWatchProviders(item: Map<String, MovieDetails.Provider>) {
        val watchProviders = item.map {
            (it.value.flatRate.orEmpty() + it.value.buy.orEmpty() + it.value.rent.orEmpty())
        }.joinToString()

        if (watchProviders.isNotBlank()) {
            addAdapter(
                TextAdapter(
                    context.resources.getString(R.string.available_watch_providers, watchProviders),
                    defaultMargins,
                )
            )
        }
    }

    private fun ConcatAdapter.addCast(item: List<Credits.Cast>) {
        addAdapter(
            TextAdapter(
                context.resources.getString(R.string.cast_and_crew),
                defaultMargins,
                R.style.TextAppearance_MyTheme_Headline5
            )
        )

        addAdapter(CastAdapter(GridSpanSizeLookup.calculateSpanWidth(2)).apply {
            submitList(item.take(15))
        })
    }

    private fun ConcatAdapter.addRelatedMovies(item: List<Movie>) {
        addAdapter(
            TextAdapter(
                context.resources.getString(R.string.also_watch),
                defaultMargins,
                R.style.TextAppearance_MyTheme_Headline5
            )
        )
        addAdapter(
            NestedRecyclerAdapter(
                item,
                relatedMoviesAdapter,
                Margins(top = spacing / 2),
                RelatedMoviesItemDecoration(spacing / 2),
            )
        )
    }
}





