package com.skileld.android.themoviedbtestapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skileld.android.themoviedbtestapp.adapter.MoviesAdapter
import com.skileld.android.themoviedbtestapp.databinding.PopularMoviesFragmentBinding
import com.skileld.android.themoviedbtestapp.ui.viewModels.ViewModel
import com.skileld.android.themoviedbtestapp.util.ConnectionLiveData
import com.skileld.android.themoviedbtestapp.util.Constants.Companion.PAGE_SIZE
import com.skileld.android.themoviedbtestapp.util.Resource

class PopularMoviesFragment : Fragment() {

    companion object {
        fun newInstance() = PopularMoviesFragment()
    }

    lateinit var connectionLiveData: ConnectionLiveData


    private lateinit var viewModel: ViewModel
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
        viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        setupRecyclerView()
        binding.shimmerLayout.startShimmer()

        connectionLiveData = ConnectionLiveData(requireContext())
        connectionLiveData.observe(viewLifecycleOwner, { isNetworkAvailable ->
            if (isNetworkAvailable) {
                viewModel.requestPopularMovies()
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
                        val totalPages = it.total_results / PAGE_SIZE + 2
                        isLastPage = viewModel.moviesPage == totalPages
                        if(isLastPage) {
                            binding.rvPopularsMovies.setPadding(0, 0, 0, 0)
                        }
                    }
                    hideProgressBar()
                }
                is Resource.Error -> {
                    showProgressBar()
                    response.message.let {
                        Log.e("PopularFragment", "Error $it")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        isLoading = true
    }


    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate) {
                connectionLiveData.observe(viewLifecycleOwner, { isNetworkAvailable ->
                    if (isNetworkAvailable) {
                        viewModel.requestPopularMovies()
                    } else {
                        Log.e("networkAvailable", "Error $isNetworkAvailable")
                    }
                })
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setupRecyclerView() {
        val movieViewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        moviesAdapter = MoviesAdapter(movieViewModel)
        binding.rvPopularsMovies.apply {
            adapter = moviesAdapter
            layoutManager = GridLayoutManager(activity, 2)
            addOnScrollListener(scrollListener)
        }
    }

}