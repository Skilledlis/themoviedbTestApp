package com.skileld.android.themoviedbtestapp.api

import com.skileld.android.themoviedbtestapp.util.Constants.Companion.API_KEY
import com.skileld.android.themoviedbtestapp.models.MoviesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("3/movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key")
        apiKey: String = API_KEY,
        @Query("language")
        language: String = "ru",
        @Query("region")
        region: String = "RU"
    ): Response<MoviesResponse>

    @GET("3/movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key")
        apiKey: String = "64967af077e0c68879a345f297fe21d7",
        @Query("language")
        language: String = "ru",
        @Query("region")
        region: String = "RU"
    ): Response<MoviesResponse>

    @GET("3/search/movie")
    suspend fun searchMovies(
        @Query("api_key")
        apiKey: String = "64967af077e0c68879a345f297fe21d7",
        @Query("language")
        language: String = "ru",
        @Query("query")
        query: String,
        @Query("include_adult")
        include_adult: String = "false"
    ): Response<MoviesResponse>
}