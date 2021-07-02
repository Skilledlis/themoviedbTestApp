package com.skileld.android.themoviedbtestapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.skileld.android.themoviedbtestapp.adapter.MoviesAdapter
import com.skileld.android.themoviedbtestapp.databinding.PopularMoviesFragmentBinding
import com.skileld.android.themoviedbtestapp.ui.MainActivity
import com.skileld.android.themoviedbtestapp.ui.viewModels.PopularMoviesViewModel
import com.skileld.android.themoviedbtestapp.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PopularMoviesFragment : Fragment() {

    companion object {
        fun newInstance() = PopularMoviesFragment()
    }

    private lateinit var viewModel: PopularMoviesViewModel
    lateinit var moviesAdapter: MoviesAdapter

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
        viewModel.requestPopularMovies()
        setupRecyclerView()

        viewModel.popularMovies.observe(viewLifecycleOwner, { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        moviesAdapter.differ.submitList(it.results)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
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

    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE

    }

    private fun setupRecyclerView() {
        moviesAdapter = MoviesAdapter()
        binding.rvPopularsMovies.apply {
            adapter = moviesAdapter
            layoutManager = GridLayoutManager(activity,2)
        }
    }

}