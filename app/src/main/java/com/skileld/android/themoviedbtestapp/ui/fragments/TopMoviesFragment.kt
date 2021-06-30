package com.skileld.android.themoviedbtestapp.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.skileld.android.themoviedbtestapp.R
import com.skileld.android.themoviedbtestapp.ui.viewModels.TopMoviesViewModel

class TopMoviesFragment : Fragment() {

    companion object {
        fun newInstance() = TopMoviesFragment()
    }

    private lateinit var viewModel: TopMoviesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.top_movies_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TopMoviesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}