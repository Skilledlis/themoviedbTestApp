package com.skileld.android.themoviedbtestapp.repository

import com.skileld.android.themoviedbtestapp.api.RetrofitInstance

class Repository {
    suspend fun getPopular(page: Int) = RetrofitInstance.api.getPopularMovies(page = page)
    suspend fun getTop(page: Int) = RetrofitInstance.api.getTopRatedMovies(page = page)
    suspend fun getDetails(id: Int) = RetrofitInstance.api.getDetails(id)

    suspend fun getSearch(
        query: String,
        page: Int
    ) = RetrofitInstance.api.searchMovies(query = query, page = page)
}