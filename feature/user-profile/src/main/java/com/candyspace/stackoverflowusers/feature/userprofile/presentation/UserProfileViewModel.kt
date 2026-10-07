package com.candyspace.stackoverflowusers.feature.userprofile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.candyspace.stackoverflowusers.common.Result
import com.candyspace.stackoverflowusers.feature.userprofile.domain.usecase.GetTopTagsUseCase
import com.candyspace.stackoverflowusers.feature.userprofile.domain.usecase.GetUserProfileUseCase
import com.candyspace.stackoverflowusers.telemetry.TelemetryLogger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = UserProfileViewModel.Factory::class)
class UserProfileViewModel @AssistedInject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getTopTagsUseCase: GetTopTagsUseCase,
    private val telemetryLogger: TelemetryLogger,
    @Assisted private val userId: Long,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(userId: Long): UserProfileViewModel
    }

    private val _uiState = MutableStateFlow<UserProfileUiState>(UserProfileUiState.Loading)
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    private val _navEvent = MutableSharedFlow<UserProfileNavEvent>()
    val navEvent: SharedFlow<UserProfileNavEvent> = _navEvent.asSharedFlow()

    init {
        telemetryLogger.logEvent("user_detail_screen_viewed", mapOf("user_id" to userId.toString()))
        loadUserDetail()
    }

    /**
     * Handles user interaction events dispatched from [UserProfileScreen].
     *
     * @param event The [UserProfileEvent] action triggered by the UI.
     */
    fun onEvent(event: UserProfileEvent) {
        when (event) {
            is UserProfileEvent.RetryClicked -> loadUserDetail()
            is UserProfileEvent.BackClicked -> {
                viewModelScope.launch {
                    _navEvent.emit(UserProfileNavEvent.PopBackStack)
                }
            }
        }
    }

    /**
     * Concurrently fetches user details and top tags for [userId] and updates [_uiState].
     */
    fun loadUserDetail() {
        viewModelScope.launch {
            _uiState.value = UserProfileUiState.Loading

            val userDeferred = async { getUserProfileUseCase(userId) }
            val tagsDeferred = async { getTopTagsUseCase(userId) }

            val userResult = userDeferred.await()
            val tagsResult = tagsDeferred.await()

            if (userResult is Result.Success && userResult.data != null) {
                val user = userResult.data!!
                val tags = if (tagsResult is Result.Success) tagsResult.data else emptyList()
                telemetryLogger.logEvent(
                    "user_detail_screen_state_success",
                    mapOf("user_id" to userId.toString(), "tag_count" to tags.size.toString()),
                )
                _uiState.value = UserProfileUiState.Success(
                    user = user,
                    topTags = tags,
                )
            } else {
                val error = (userResult as? Result.Error)?.message
                    ?: "User details could not be loaded."
                telemetryLogger.logError("user_detail_screen_state_error", error)
                _uiState.value = UserProfileUiState.Error(
                    message = error,
                )
            }
        }
    }
}
