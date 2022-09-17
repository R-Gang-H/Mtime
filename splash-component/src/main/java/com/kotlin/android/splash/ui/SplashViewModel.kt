package com.kotlin.android.splash.ui

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.splash.bean.SplashAd
import com.kotlin.android.splash.repository.SplashRepository
import kotlinx.coroutines.launch

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/11/23
 */
class SplashViewModel : BaseViewModel() {
    private val repo by lazy { SplashRepository() }

    private val mUIModel = BaseUIModel<SplashAd>()
    val uiState = mUIModel.uiState;

    fun loadSplashAd() {
        viewModelScope.launch {
            val result = withOnIO {
                repo.loadSplashAd()
            }

            mUIModel.checkResultAndEmitUIState(result)
        }
    }
}