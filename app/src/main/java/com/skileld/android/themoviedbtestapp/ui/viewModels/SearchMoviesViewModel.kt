package com.skileld.android.themoviedbtestapp.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.skileld.android.themoviedbtestapp.models.MovieResponse
import com.skileld.android.themoviedbtestapp.models.ResultSearch
import com.skileld.android.themoviedbtestapp.repository.Repository
import com.skileld.android.themoviedbtestapp.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class SearchMoviesViewModel(app: Application) : AndroidViewModel(app), CoroutineScope {

    private val movieRepository: Repository = Repository()
    val movie: MutableLiveData<Resource<ResultSearch>> = MutableLiveData()



    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val job = Job()

    fun requestMovies(query:String) {
        launch(Dispatchers.Main) {
            movie.postValue(Resource.Loading())
            val response = movieRepository.getSearch(query)
            movie.postValue(handlerNewsResponse(response))
        }
    }

    private fun handlerNewsResponse(response: Response<ResultSearch>): Resource<ResultSearch> {
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