package com.skileld.android.themoviedbtestapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
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
import java.text.SimpleDateFormat


class MoviesAdapter(var movieViewModel: MovieViewModel) :
    RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>() {


    inner class MoviesViewHolder(binding: ItemMoveisCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        public var moviesImage = binding.moviesImage
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


            when (movies.release_date) {
                "" -> holder.moviesReleaseDate.text = "NaN"
                else -> {
                    val parser = SimpleDateFormat("yyyy-mm-dd")
                    val formater = SimpleDateFormat("yyyy")
                    val output = formater.format(parser.parse(movies.release_date))
                    holder.moviesReleaseDate.text = output
                }
            }




            holder.moviesPopularity.text = if (movies.vote_average.toString() != "0.0") {
                movies.vote_average.toString()
            } else "NaN"
            setOnClickListener {
                val navController = it.findNavController()
                navController.navigate(R.id.movieFragment)
                movieViewModel.moviesId = movies.id
            }
        }
    }
    override fun getItemCount(): Int = differ.currentList.size
}
