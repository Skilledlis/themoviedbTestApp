package com.skileld.android.themoviedbtestapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skileld.android.themoviedbtestapp.adapter.MoviesAdapter
import com.skileld.android.themoviedbtestapp.databinding.ItemMoveisCardBinding
import com.skileld.android.themoviedbtestapp.databinding.SearchMoviesFragmentBinding
import com.skileld.android.themoviedbtestapp.ui.viewModels.MovieViewModel
import com.skileld.android.themoviedbtestapp.ui.viewModels.SearchMoviesViewModel
import com.skileld.android.themoviedbtestapp.util.ConnectionLiveData
import com.skileld.android.themoviedbtestapp.util.Resource

class SearchMoviesFragment : Fragment() {

    companion object {
        fun newInstance() = SearchMoviesFragment()
    }

    private lateinit var viewModel: SearchMoviesViewModel
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
        viewModel = ViewModelProvider(this).get(SearchMoviesViewModel::class.java)

        setupRecyclerView()

        val connectionLiveData = ConnectionLiveData(requireContext())
        connectionLiveData.observe(viewLifecycleOwner, { isNetworkAvailable ->
            if (isNetworkAvailable) {
                viewModel.requestMovies(binding.searchView.query.toString())
            } else {
                Log.e("networkAvailable", "Error $isNetworkAvailable")
            }
        })

        viewModel.searchMovies.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
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



        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    viewModel.requestMovies(newText)
                }
                return false
            }
        })
    }


    private fun setupRecyclerView() {
        val movieViewModel = ViewModelProvider(requireActivity()).get(MovieViewModel::class.java)
        moviesAdapter = MoviesAdapter(movieViewModel)
        binding.recyclerView.apply {
            adapter = moviesAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }


}

