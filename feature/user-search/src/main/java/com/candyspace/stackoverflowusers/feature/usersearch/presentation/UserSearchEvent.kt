package com.candyspace.stackoverflowusers.feature.usersearch.presentation

import com.candyspace.stackoverflowusers.domain.model.User

sealed interface UserSearchEvent {
    data class SearchQueryChanged(val query: String) : UserSearchEvent
    data object SearchClicked : UserSearchEvent
    data object RetryClicked : UserSearchEvent
    data class UserClicked(val user: User) : UserSearchEvent
    data object ExitClicked : UserSearchEvent
}

sealed interface UserSearchNavEvent {
    data class NavigateToProfile(val userId: String) : UserSearchNavEvent
    data object ExitApp : UserSearchNavEvent
}
