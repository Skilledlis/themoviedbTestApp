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
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class SearchMoviesFragment : Fragment() {

    companion object {
        fun newInstance() = SearchMoviesFragment()
    }

    lateinit var connectionLiveData: ConnectionLiveData

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



        connectionLiveData = ConnectionLiveData(requireContext())
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
                    response.data?.let {
                        moviesAdapter.differ.submitList(it.results)
                    }
                }
                is Resource.Error -> {
                    response.message.let {
                        Log.e("SearchFragment", "Error $it")
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
                    viewModel.requestSearchMovies(newText)
                }
                return false
            }
        })
    }


    private fun setupRecyclerView() {
        val movieViewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        moviesAdapter = MoviesAdapter(movieViewModel)
        binding.recyclerView.apply {
            adapter = moviesAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}

