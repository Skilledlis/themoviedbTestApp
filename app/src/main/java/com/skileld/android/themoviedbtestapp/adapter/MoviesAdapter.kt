package com.skileld.android.themoviedbtestapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.skileld.android.themoviedbtestapp.R
import com.skileld.android.themoviedbtestapp.databinding.ItemMoveisCardBinding
import com.skileld.android.themoviedbtestapp.models.Result
import com.skileld.android.themoviedbtestapp.ui.viewModels.MovieViewModel
import com.skileld.android.themoviedbtestapp.util.Constants.Companion.IMAGE_URL
import com.squareup.picasso.Picasso


class MoviesAdapter(var movieViewModel: MovieViewModel) : RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>() {
    inner class MoviesViewHolder(binding: ItemMoveisCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val moviesImage = binding.moviesImage
        val moviesTitle = binding.moviesTitle
        val moviesReleaseDate = binding.releaseDate
        val moviesPopularity = binding.popularity
    }

    private val differCallback = object : DiffUtil.ItemCallback<Result>() {
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val binding = ItemMoveisCardBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MoviesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        val movies = differ.currentList[position]
        holder.itemView.apply {
            Picasso.get().load(IMAGE_URL + movies.poster_path).into(holder.moviesImage)
            holder.moviesTitle.text = movies.title
            holder.moviesReleaseDate.text = movies.release_date
            holder.moviesPopularity.text = if (movies.vote_average.toString() != "0.0") {
                movies.vote_average.toString()
            } else "NaN"
            setOnClickListener {
                val navController = it.findNavController()
                navController.navigate(R.id.movieFragment)
                movieViewModel.title = movies.title
                Toast.makeText(context, movies.title, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size


}
