package com.skileld.android.themoviedbtestapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.skileld.android.themoviedbtestapp.databinding.MovieFragmentBinding
import com.skileld.android.themoviedbtestapp.ui.viewModels.ViewModel
import com.skileld.android.themoviedbtestapp.util.Constants
import com.skileld.android.themoviedbtestapp.util.Resource
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat

class MovieFragment : Fragment() {

    companion object {
        fun newInstance() = MovieFragment()
    }

    private lateinit var viewModel: ViewModel

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
        viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        viewModel.requestMovie()

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)


        viewModel.movie.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                   response.data?.let {
                       Picasso.get().load(Constants.IMAGE_URL + it.backdrop_path).into(binding.backdropImageView)
                       Picasso.get().load(Constants.IMAGE_URL + it.poster_path).into(binding.poster)
                       binding.collapsingToolbar.title = it.title
                       binding.genre.text = it.genres[0].name

                       val parser = SimpleDateFormat("yyyy-mm-dd")
                       val formatter = SimpleDateFormat("yyyy")
                       val output = formatter.format(parser.parse(it.release_date))
                       binding.releaseDate.text = output

                       binding.status.text = it.status
                       binding.budget.text = "$${it.budget}"
                       binding.revenue.text = "$${it.revenue}"

                       binding.description.text = it.overview


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