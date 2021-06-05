package com.timgortworst.cleanarchitecture.presentation.features.movie.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.TransitionInflater
import com.timgortworst.cleanarchitecture.domain.model.state.State
import com.timgortworst.cleanarchitecture.presentation.R
import com.timgortworst.cleanarchitecture.presentation.databinding.FragmentMovieListBinding
import com.timgortworst.cleanarchitecture.presentation.extension.snackbar
import com.timgortworst.cleanarchitecture.presentation.model.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieListFragment : Fragment() {
    private val listViewModel by viewModels<MovieListViewModel>()
    private lateinit var adapter: MovieListAdapter
    private lateinit var binding: FragmentMovieListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(android.R.transition.fade)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieListBinding.inflate(layoutInflater, container, false)
        sharedElementReturnTransition = TransitionInflater.from(context)
                .inflateTransition(R.transition.shared_element_transition)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()

        val navController = findNavController()
        binding.collapsingToolbarLayout.setupWithNavController(
            binding.toolbar,
            navController,
            AppBarConfiguration(navController.graph)
        )

        setupMovieList()
        observeUI()
    }

    private fun observeUI() {
        listViewModel.movies.observe(viewLifecycleOwner, { response ->
            response?.let {
                if (it is Error) {
                    binding.noResults.visibility = View.VISIBLE
                } else if(it is State.Success) {
                    binding.noResults.visibility = View.GONE
                    adapter.addMoviesToList(it.data.toMutableList())
                }
            }
            binding.recyclerView.doOnPreDraw { startPostponedEnterTransition() }
        })

        listViewModel.loading.observe(viewLifecycleOwner, {
//            swiperefresh.isRefreshing = it
        })

        listViewModel.error.observe(viewLifecycleOwner, EventObserver {
            view?.snackbar(it)
        })
    }

    private fun setupMovieList() {
        val columns = resources.getInteger(R.integer.gallery_columns)
        val orientation = resources.getInteger(R.integer.gallery_orientation)
        val padding = resources.getDimension(R.dimen.default_padding).toInt()

        adapter = MovieListAdapter { movie, imageView ->
            val directions =
                MovieListFragmentDirections.showMovieDetails(movie.title, movie.id, movie.highResImage)
            val extras = FragmentNavigatorExtras(imageView to movie.highResImage)
            findNavController().navigate(directions, extras)
        }

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(activity, columns, orientation, false)
            adapter = this@MovieListFragment.adapter
            addItemDecoration(GridMarginDecoration(columns, padding))
        }
    }
}
