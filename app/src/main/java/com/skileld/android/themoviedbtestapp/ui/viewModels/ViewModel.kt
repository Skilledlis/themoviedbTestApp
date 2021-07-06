package com.skileld.android.themoviedbtestapp.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.skileld.android.themoviedbtestapp.models.MovieResponse
import com.skileld.android.themoviedbtestapp.models.MoviesResponse
import com.skileld.android.themoviedbtestapp.repository.Repository
import com.skileld.android.themoviedbtestapp.util.Resource
import kotlinx.coroutines.*
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class ViewModel(app: Application) : AndroidViewModel(app), CoroutineScope {

    private val movieRepository: Repository = Repository()

    val movies: MutableLiveData<Resource<MoviesResponse>> = MutableLiveData()
    var moviesPage = 1
    var popularMoviesResponse: MoviesResponse? = null

    var moviesId: Int? = null
    val movie: MutableLiveData<Resource<MovieResponse>> = MutableLiveData()

    val searchMovies: MutableLiveData<Resource<MoviesResponse>> = MutableLiveData()
    var searchMoviesPage = 1
    var searchMoviesResponse: MoviesResponse? = null
    var newSearchQuery:String? = null
    var oldSearchQuery:String? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val job = Job()

    fun requestMovie() {
        launch(Dispatchers.Main) {
            movie.postValue(Resource.Loading())
            val response = movieRepository.getDetails(moviesId ?: 0)
            movie.postValue(handlerMovie(response))
        }
    }

    fun requestPopularMovies() {
        launch(Dispatchers.Main) {
            movies.postValue(Resource.Loading())
            val response = movieRepository.getPopular(moviesPage)
            movies.postValue(handlerMoviesResponse(response))

        }
    }

    fun requestTopMovies() {
        launch(Dispatchers.Main) {
            movies.postValue(Resource.Loading())
            val response = movieRepository.getTop()
            movies.postValue(handlerMoviesResponse(response))
        }
    }

    fun requestSearchMovies(query: String) {
        launch(Dispatchers.Main) {
            delay(500)
            searchMovies.postValue(Resource.Loading())
            val response = movieRepository.getSearch(query)
            searchMovies.postValue(handlerSearchMoviesResponse(response))
        }
    }

    private fun handlerMoviesResponse(response: Response<MoviesResponse>): Resource<MoviesResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                moviesPage++
                if (popularMoviesResponse == null) {
                    popularMoviesResponse = resultResponse
                }else{
                    val oldResult = popularMoviesResponse?.results
                    val newResult = resultResponse.results
                    oldResult?.addAll(newResult)
                }
                return Resource.Success(popularMoviesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handlerSearchMoviesResponse(response: Response<MoviesResponse>) : Resource<MoviesResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if(searchMoviesResponse == null || newSearchQuery != oldSearchQuery) {
                    searchMoviesPage = 1
                    oldSearchQuery = newSearchQuery
                    searchMoviesResponse = resultResponse
                } else {
                    searchMoviesPage++
                    val oldResult = searchMoviesResponse?.results
                    val newArticles = resultResponse.results
                    oldResult?.addAll(newArticles)
                }
                return Resource.Success(searchMoviesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handlerMovie(response: Response<MovieResponse>): Resource<MovieResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}