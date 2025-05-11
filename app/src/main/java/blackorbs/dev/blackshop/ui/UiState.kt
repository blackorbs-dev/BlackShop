package blackorbs.dev.blackshop.ui

import java.lang.Exception

sealed class UiState {
    data object Idle : UiState()
    data object Loading : UiState()
    data object Success : UiState()
    data class Error(val exception: Exception?) : UiState()
}