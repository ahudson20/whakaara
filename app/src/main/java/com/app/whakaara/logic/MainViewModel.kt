package com.app.whakaara.logic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val app: Application
) : AndroidViewModel(app) {

}