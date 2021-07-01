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

class PopularMoviesViewModel(
    app: Application
) : AndroidViewModel(app), CoroutineScope {

    val popularRepository: Repository = Repository()

    val popularMovies: MutableLiveData<Resource<MoviesResponse>> = MutableLiveData()


    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val job = Job()

    init {
        requestPopularMovies()
    }

    fun requestPopularMovies() {
        launch(Dispatchers.Main) {
            popularMovies.postValue(Resource.Loading())
            val response = popularRepository.getPopular()
            popularMovies.postValue(handlerNewsResponse(response))
        }
    }

    private fun handlerNewsResponse(response: Response<MoviesResponse>): Resource<MoviesResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

}