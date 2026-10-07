package com.candyspace.stackoverflowusers.feature.userprofile.presentation

sealed interface UserProfileEvent {
    data object RetryClicked : UserProfileEvent
    data object BackClicked : UserProfileEvent
}

sealed interface UserProfileNavEvent {
    data object PopBackStack : UserProfileNavEvent
}
