package com.skileld.android.themoviedbtestapp.models

data class SearchMoviesResponse(
    val page: Int,
    val results: List<ResultSearch>,
    val total_pages: Int,
    val total_results: Int
)