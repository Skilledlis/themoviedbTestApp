package com.skileld.android.themoviedbtestapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.skileld.android.themoviedbtestapp.databinding.MovieFragmentBinding
import com.skileld.android.themoviedbtestapp.ui.viewModels.MovieViewModel
import com.skileld.android.themoviedbtestapp.util.ConnectionLiveData
import com.skileld.android.themoviedbtestapp.util.Constants
import com.skileld.android.themoviedbtestapp.util.Resource
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat

class MovieFragment : Fragment() {

    companion object {
        fun newInstance() = MovieFragment()
    }

    private lateinit var viewModel: MovieViewModel

    private var _binding: MovieFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MovieFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MovieViewModel::class.java)
        binding.shimmerLayout.startShimmer()

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)



        val connectionLiveData = ConnectionLiveData(requireContext())
        connectionLiveData.observe(viewLifecycleOwner, { isNetworkAvailable ->
            if (isNetworkAvailable) {
                viewModel.requestMovies()
            } else {
                Log.e("networkAvailable", "Error $isNetworkAvailable")
            }
        })


        viewModel.movie.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let {
                        binding.apply {
                            collapsingToolbar.title = it.title
                            genre.text = it.genres[0].name
                            Picasso.get().load(Constants.IMAGE_URL + it.backdrop_path)
                                .into(backdropImageView)
                            Picasso.get().load(Constants.IMAGE_URL + it.poster_path).into(poster)

                            val parser = SimpleDateFormat("yyyy-mm-dd")
                            val formater = SimpleDateFormat("yyyy")
                            val output = formater.format(parser.parse(it.release_date))
                            releaseDate.text = output

                            status.text = it.status
                            budget.text = "$${it.budget}"
                            revenue.text = "$${it.revenue}"

                            description.text = it.overview

                        }
                    }
                }
                is Resource.Error -> {

                    response.message.let {
                        Log.e("MovieFragment", "Error $it")
                    }

                }
                is Resource.Loading -> {

                }
            }
        })
    }

}