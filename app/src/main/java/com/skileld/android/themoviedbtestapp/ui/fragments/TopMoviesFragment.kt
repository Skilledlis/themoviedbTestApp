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
import com.skileld.android.themoviedbtestapp.databinding.TopMoviesFragmentBinding
import com.skileld.android.themoviedbtestapp.ui.viewModels.MovieViewModel
import com.skileld.android.themoviedbtestapp.ui.viewModels.ViewModel
import com.skileld.android.themoviedbtestapp.util.ConnectionLiveData
import com.skileld.android.themoviedbtestapp.util.Resource

class TopMoviesFragment : Fragment() {

    companion object {
        fun newInstance() = TopMoviesFragment()
    }

    private lateinit var viewModel: ViewModel
    private lateinit var moviesAdapter: MoviesAdapter

    private var _binding: TopMoviesFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TopMoviesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)
        setupRecyclerView()
        binding.shimmerLayout.startShimmer()

        val connectionLiveData = ConnectionLiveData(requireContext())
        connectionLiveData.observe(viewLifecycleOwner, { isNetworkAvailable ->
            if (isNetworkAvailable) {
                viewModel.requestTopMovies()
            } else {
                Log.e("networkAvailable", "Error $isNetworkAvailable")
            }
        })

        viewModel.movies.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    binding.shimmerLayout.visibility = View.GONE
                    response.data?.let {
                        moviesAdapter.differ.submitList(it.results)
                    }
                }
                is Resource.Error -> {
                    response.message.let {
                        Log.e("TopMoviesFragment", "Error $it")
                    }
                }
                is Resource.Loading -> {
                    binding.shimmerLayout.visibility = View.VISIBLE
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