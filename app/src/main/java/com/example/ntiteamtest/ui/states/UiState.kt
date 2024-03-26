package com.example.ntiteamtest.ui.states

sealed class UiState {
    data class Error(
        val throwable: Throwable
    ) : UiState()

    data class Success<T>(
        val result: T
    ): UiState()

    class Loading : UiState()
}