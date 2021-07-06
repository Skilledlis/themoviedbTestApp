package com.skileld.android.themoviedbtestapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skileld.android.themoviedbtestapp.adapter.MoviesAdapter
import com.skileld.android.themoviedbtestapp.databinding.SearchMoviesFragmentBinding
import com.skileld.android.themoviedbtestapp.ui.viewModels.ViewModel
import com.skileld.android.themoviedbtestapp.util.ConnectionLiveData
import com.skileld.android.themoviedbtestapp.util.Constants
import com.skileld.android.themoviedbtestapp.util.Resource

class SearchMoviesFragment : Fragment() {

    companion object {
        fun newInstance() = SearchMoviesFragment()
    }

    private lateinit var viewModel: ViewModel
    private lateinit var moviesAdapter: MoviesAdapter

    private var _binding: SearchMoviesFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchMoviesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)
        setupRecyclerView()


        val connectionLiveData = ConnectionLiveData(requireContext())
        connectionLiveData.observe(viewLifecycleOwner, { isNetworkAvailable ->
            if (isNetworkAvailable) {
                viewModel.requestSearchMovies(binding.searchView.query.toString())
            } else {
                Log.e("networkAvailable", "Error $isNetworkAvailable")
            }
        })

        viewModel.searchMovies.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        moviesAdapter.differ.submitList(it.results)
                        val totalPages = it.total_results / Constants.PAGE_SIZE + 2
                        isLastPage = viewModel.searchMoviesPage == totalPages
                    }
                }
                is Resource.Error -> {
                    response.message.let {
                        Log.e("SearchFragment", "Error $it")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    viewModel.requestSearchMovies(newText)
                }
                return false
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

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount


            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.requestSearchMovies(binding.searchView.query.toString())
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }


    private fun setupRecyclerView() {
        val movieViewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        moviesAdapter = MoviesAdapter(movieViewModel)
        binding.recyclerView.apply {
            adapter = moviesAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(scrollListener)
        }
    }
}

