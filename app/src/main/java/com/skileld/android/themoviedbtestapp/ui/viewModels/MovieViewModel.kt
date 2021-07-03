package com.skileld.android.themoviedbtestapp.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel

class MovieViewModel(app:Application) : AndroidViewModel(app) {

    var title: String? = null

    fun requestContext(): String? {
        return title
    }
}