package com.candyspace.stackoverflowusers.feature.usersearch.presentation

import com.candyspace.stackoverflowusers.common.Result
import com.candyspace.stackoverflowusers.domain.model.BadgeCounts
import com.candyspace.stackoverflowusers.domain.model.TopTag
import com.candyspace.stackoverflowusers.domain.model.User
import com.candyspace.stackoverflowusers.domain.repository.UserRepository
import com.candyspace.stackoverflowusers.feature.usersearch.domain.usecase.GetUserListUseCase
import com.candyspace.stackoverflowusers.telemetry.FakeTelemetryLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserSearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeTelemetryLogger = FakeTelemetryLogger()

    private val fakeRepository = object : UserRepository {
        var shouldReturnError = false
        var lastQueryReceived: String? = null

        override suspend fun getUsers(query: String?, page: Int): Result<List<User>> {
            lastQueryReceived = query
            return if (shouldReturnError) {
                Result.Error("Failed to load")
            } else {
                Result.Success(
                    listOf(
                        User(
                            id = 100L,
                            displayName = "Alice",
                            reputation = 500,
                            profileImage = null,
                            location = "NYC",
                            creationDate = 0L,
                            badgeCounts = BadgeCounts(5, 10, 15),
                        ),
                    ),
                )
            }
        }

        override suspend fun getUserById(userId: Long): Result<User?> = Result.Success(null)

        override suspend fun getTopTags(userId: Long): Result<List<TopTag>> = Result.Success(emptyList())
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onSearchQueryChanged updates searchQuery in uiState and triggers debounced search after 300ms`() = runTest(testDispatcher) {
        val useCase = GetUserListUseCase(fakeRepository)
        val viewModel = UserSearchViewModel(fakeRepository, useCase, fakeTelemetryLogger)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.usersPagingDataFlow.collect()
        }
        testScheduler.advanceUntilIdle()

        viewModel.onSearchQueryChanged("Alice")
        assertEquals("Alice", viewModel.searchQuery.value)

        testScheduler.advanceTimeBy(300L)
        testScheduler.advanceUntilIdle()

        assertEquals("Alice", fakeRepository.lastQueryReceived)
        assertTrue(fakeTelemetryLogger.hasEvent("search_debounced"))
    }

    @Test
    fun `onSearchClick invokes getUsersUseCase with query and logs telemetry`() = runTest(testDispatcher) {
        val useCase = GetUserListUseCase(fakeRepository)
        val viewModel = UserSearchViewModel(fakeRepository, useCase, fakeTelemetryLogger)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.usersPagingDataFlow.collect()
        }
        testScheduler.advanceUntilIdle()

        viewModel.onSearchQueryChanged("Jon")
        viewModel.onSearchClick()
        testScheduler.advanceUntilIdle()

        assertEquals("Jon", fakeRepository.lastQueryReceived)
        assertTrue(fakeTelemetryLogger.hasEvent("search_performed"))
    }

    @Test
    fun `loadUsers updates uiState with users on success and logs event`() = runTest(testDispatcher) {
        fakeRepository.shouldReturnError = false
        val useCase = GetUserListUseCase(fakeRepository)
        val viewModel = UserSearchViewModel(fakeRepository, useCase, fakeTelemetryLogger)
        viewModel.loadUsers()
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value as UserSearchUiState.Success
        assertEquals(1, state.users.size)
        assertEquals("Alice", state.users[0].displayName)
        assertTrue(fakeTelemetryLogger.hasEvent("users_screen_state_success"))
    }

    @Test
    fun `loadUsers updates uiState with errorMessage on failure and logs error`() = runTest(testDispatcher) {
        fakeRepository.shouldReturnError = true
        val useCase = GetUserListUseCase(fakeRepository)
        val viewModel = UserSearchViewModel(fakeRepository, useCase, fakeTelemetryLogger)
        viewModel.loadUsers()
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value as UserSearchUiState.Error
        assertEquals("Failed to load", state.message)
        assertTrue(fakeTelemetryLogger.hasError("users_screen_state_error"))
    }
}
