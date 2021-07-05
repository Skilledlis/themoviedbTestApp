package com.skileld.android.themoviedbtestapp.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.skileld.android.themoviedbtestapp.models.MovieResponse
import com.skileld.android.themoviedbtestapp.repository.Repository
import com.skileld.android.themoviedbtestapp.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class MovieViewModel(app: Application) : AndroidViewModel(app), CoroutineScope {

    var moviesId: Int? = null

    private val movieRepository: Repository = Repository()
    val movie: MutableLiveData<Resource<MovieResponse>> = MutableLiveData()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val job = Job()

    fun requestMovie() {
        launch(Dispatchers.Main) {
            movie.postValue(Resource.Loading())
            val response = movieRepository.getDetails(moviesId ?: 0)
            movie.postValue(handlerNewsResponse(response))
        }
    }

    private fun handlerNewsResponse(response: Response<MovieResponse>): Resource<MovieResponse> {
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