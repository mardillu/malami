package com.mardillu.malami.ui.model

data class UiState<T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isIdle: Boolean = false,
    val error: String? = null,
) {
    val hasErrors: Boolean = error != null
}

data class UiValidationState<T>(
    val data: T? = null,
    val error: String? = null,
) {
    val hasErrors: Boolean = error != null
}