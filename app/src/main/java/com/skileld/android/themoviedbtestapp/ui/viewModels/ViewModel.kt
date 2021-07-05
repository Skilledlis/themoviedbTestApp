package com.skileld.android.themoviedbtestapp.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.skileld.android.themoviedbtestapp.models.MoviesResponse
import com.skileld.android.themoviedbtestapp.repository.Repository
import com.skileld.android.themoviedbtestapp.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class ViewModel(app: Application) : AndroidViewModel(app), CoroutineScope {

    var moviesId: Int? = null

    private val movieRepository: Repository = Repository()
    val movies: MutableLiveData<Resource<MoviesResponse>> = MutableLiveData()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val job = Job()

    fun requestPopularMovies() {
        launch(Dispatchers.Main) {
            movies.postValue(Resource.Loading())
            val response = movieRepository.getPopular()
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

    fun requestSearchMovies(query:String) {
        launch(Dispatchers.Main) {
            movies.postValue(Resource.Loading())
            val response = movieRepository.getSearch(query)
            movies.postValue(handlerMoviesResponse(response))
        }
    }

    private fun handlerMoviesResponse(response: Response<MoviesResponse>): Resource<MoviesResponse> {
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