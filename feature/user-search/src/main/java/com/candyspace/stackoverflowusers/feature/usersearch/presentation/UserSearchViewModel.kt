package com.candyspace.stackoverflowusers.feature.usersearch.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.candyspace.stackoverflowusers.common.Result
import com.candyspace.stackoverflowusers.domain.model.User
import com.candyspace.stackoverflowusers.domain.repository.UserRepository
import com.candyspace.stackoverflowusers.feature.usersearch.data.paging.UserPagingSource
import com.candyspace.stackoverflowusers.feature.usersearch.domain.usecase.GetUserListUseCase
import com.candyspace.stackoverflowusers.telemetry.TelemetryLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class UserSearchViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getUserListUseCase: GetUserListUseCase,
    private val telemetryLogger: TelemetryLogger,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserSearchUiState>(UserSearchUiState.Loading)
    val uiState: StateFlow<UserSearchUiState> = _uiState.asStateFlow()

    private val _navEvent = MutableSharedFlow<UserSearchNavEvent>()
    val navEvent: SharedFlow<UserSearchNavEvent> = _navEvent.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val usersPagingDataFlow: Flow<PagingData<User>> = _searchQuery
        .debounce(300.milliseconds)
        .onEach { query ->
            if (query.isNotBlank()) {
                telemetryLogger.logEvent("search_debounced", mapOf("query" to query))
            }
        }
        .flatMapLatest { query ->
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = false,
                ),
                pagingSourceFactory = {
                    UserPagingSource(
                        userRepository = userRepository,
                        query = query.takeIf { it.isNotBlank() },
                    )
                },
            ).flow
        }
        .cachedIn(viewModelScope)

    private val _isLoading = MutableStateFlow(value = true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var searchJob: Job? = null

    init {
        telemetryLogger.logEvent("users_screen_loaded")
        _isLoading.value = false
    }

    /**
     * Handles user interaction events dispatched from [UserSearchScreen].
     *
     * @param event The [UserSearchEvent] action triggered by the UI.
     */
    fun onEvent(event: UserSearchEvent) {
        when (event) {
            is UserSearchEvent.SearchQueryChanged -> onSearchQueryChanged(event.query)
            is UserSearchEvent.SearchClicked -> onSearchClick()
            is UserSearchEvent.RetryClicked -> loadUsers()
            is UserSearchEvent.UserClicked -> {
                telemetryLogger.logEvent("user_clicked", mapOf("user_id" to event.user.id.toString()))
                viewModelScope.launch {
                    _navEvent.emit(UserSearchNavEvent.NavigateToProfile(event.user.id.toString()))
                }
            }
            is UserSearchEvent.ExitClicked -> {
                viewModelScope.launch {
                    _navEvent.emit(UserSearchNavEvent.ExitApp)
                }
            }
        }
    }

    /**
     * Updates the active search query and debounces automatic user list fetching by 300ms.
     *
     * @param newQuery The updated user search input string.
     */
    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        _uiState.value = UserSearchUiState.Loading
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300.milliseconds)
            loadUsers(query = newQuery)
        }
    }

    /**
     * Cancels pending search jobs and performs an immediate search query fetch.
     */
    fun onSearchClick() {
        searchJob?.cancel()
        val query = _searchQuery.value
        telemetryLogger.logEvent("search_performed", mapOf("query" to query))
        loadUsers(query = query)
    }

    /**
     * Fetches page 1 of users using [GetUserListUseCase] and updates [_uiState].
     *
     * @param query Optional search query; defaults to [_searchQuery].
     */
    fun loadUsers(query: String? = null) {
        val currentQuery = query ?: _searchQuery.value
        _searchQuery.value = currentQuery
        viewModelScope.launch {
            _uiState.value = UserSearchUiState.Loading
            when (val result = getUserListUseCase(query = currentQuery, page = 1)) {
                is Result.Success -> {
                    val users = (result.data as? List<*>)?.filterIsInstance<User>() ?: emptyList()
                    telemetryLogger.logEvent(
                        "users_screen_state_success",
                        mapOf("count" to users.size.toString()),
                    )
                    _uiState.value = UserSearchUiState.Success(users = users)
                }

                is Result.Error -> {
                    telemetryLogger.logError("users_screen_state_error", result.message)
                    _uiState.value = UserSearchUiState.Error(message = result.message)
                }

                is Result.Loading -> {
                    _uiState.value = UserSearchUiState.Loading
                }
            }
            _isLoading.value = false
        }
    }
}
