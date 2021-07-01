package com.skileld.android.themoviedbtestapp.repository

import com.skileld.android.themoviedbtestapp.api.RetrofitInstance

class Repository {
    suspend fun getPopular() = RetrofitInstance.api.getPopularMovies()
    suspend fun getTop() = RetrofitInstance.api.getTopRatedMovies()
    suspend fun getDetails(
        id: Int
    ) = RetrofitInstance.api.getDetails(id)
}