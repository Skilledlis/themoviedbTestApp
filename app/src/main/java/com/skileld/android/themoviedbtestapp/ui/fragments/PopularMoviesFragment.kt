package com.skileld.android.themoviedbtestapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.skileld.android.themoviedbtestapp.adapter.MoviesAdapter
import com.skileld.android.themoviedbtestapp.databinding.PopularMoviesFragmentBinding
import com.skileld.android.themoviedbtestapp.ui.viewModels.MovieViewModel
import com.skileld.android.themoviedbtestapp.ui.viewModels.PopularMoviesViewModel
import com.skileld.android.themoviedbtestapp.util.ConnectionLiveData
import com.skileld.android.themoviedbtestapp.util.Resource

class PopularMoviesFragment : Fragment() {

    companion object {
        fun newInstance() = PopularMoviesFragment()
    }

    private lateinit var viewModel: PopularMoviesViewModel
    private lateinit var moviesAdapter: MoviesAdapter

    private var _binding: PopularMoviesFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PopularMoviesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(PopularMoviesViewModel::class.java)
        setupRecyclerView()
        binding.shimmerLayout.startShimmer()

        val connectionLiveData = ConnectionLiveData(requireContext())
        connectionLiveData.observe(viewLifecycleOwner, { isNetworkAvailable ->
            if (isNetworkAvailable){
                viewModel.requestPopularMovies()
            }
            else {
                Log.e("networkAvailable", "Error $isNetworkAvailable")
            }
        })

        viewModel.popularMovies.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    binding.shimmerLayout.visibility = View.GONE
                    response.data?.let {
                        moviesAdapter.differ.submitList(it.results)
                    }
                }
                is Resource.Error -> {
                    response.message.let {
                        Log.e("PopularFragment", "Error $it")
                    }
                }
                is Resource.Loading -> {

                }
            }
        })

    }

    private fun setupRecyclerView() {
        val movieViewModel = ViewModelProvider(requireActivity()).get(MovieViewModel::class.java)
        moviesAdapter = MoviesAdapter(movieViewModel)
        binding.rvPopularsMovies.apply {
            adapter = moviesAdapter
            layoutManager = GridLayoutManager(activity, 2)
        }
    }

}