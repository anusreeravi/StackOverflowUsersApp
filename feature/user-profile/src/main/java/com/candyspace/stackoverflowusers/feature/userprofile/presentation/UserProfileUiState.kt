package com.candyspace.stackoverflowusers.feature.userprofile.presentation

import com.candyspace.stackoverflowusers.domain.model.TopTag
import com.candyspace.stackoverflowusers.domain.model.User

sealed interface UserProfileUiState {
    data object Loading : UserProfileUiState

    data class Success(
        val user: User,
        val topTags: List<TopTag> = emptyList(),
    ) : UserProfileUiState

    data class Error(
        val message: String,
    ) : UserProfileUiState
}
