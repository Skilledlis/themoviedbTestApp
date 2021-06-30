package com.skileld.android.themoviedbtestapp.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.skileld.android.themoviedbtestapp.ui.viewModels.PopularMoviesViewModel
import com.skileld.android.themoviedbtestapp.R

class PopularMoviesFragment : Fragment() {

    companion object {
        fun newInstance() = PopularMoviesFragment()
    }

    private lateinit var viewModel: PopularMoviesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popular_movies_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PopularMoviesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}