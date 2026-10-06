package com.candyspace.stackoverflowusers.feature.usersearch.presentation

import com.candyspace.stackoverflowusers.domain.model.User

sealed interface UserSearchUiState {
    data object Loading : UserSearchUiState
    data class Success(val users: List<User>) : UserSearchUiState
    data class Error(val message: String) : UserSearchUiState
}
