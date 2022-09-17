package com.kotlin.android.home.ui.main

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kotlin.android.core.BaseViewModel

/**
 *
 * Created on 2020/6/15.
 *
 * @author o.s
 */
class MainViewModel : BaseViewModel() {

    private val _uiState = MutableLiveData<String>()

    val uiState: LiveData<String>
        get() = _uiState

    var userDetailBundle: Bundle? = null
}