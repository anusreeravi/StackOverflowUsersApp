package com.candyspace.stackoverflowusers.feature.userprofile.presentation

import com.candyspace.stackoverflowusers.common.Result
import com.candyspace.stackoverflowusers.domain.model.BadgeCounts
import com.candyspace.stackoverflowusers.domain.model.TopTag
import com.candyspace.stackoverflowusers.domain.model.User
import com.candyspace.stackoverflowusers.domain.repository.UserRepository
import com.candyspace.stackoverflowusers.feature.userprofile.domain.usecase.GetTopTagsUseCase
import com.candyspace.stackoverflowusers.feature.userprofile.domain.usecase.GetUserProfileUseCase
import com.candyspace.stackoverflowusers.telemetry.FakeTelemetryLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val fakeTelemetryLogger = FakeTelemetryLogger()

    private val testUser = User(
        id = 123L,
        displayName = "Bob Smith",
        reputation = 15000,
        profileImage = null,
        location = "Berlin",
        creationDate = 1500000000L,
        badgeCounts = BadgeCounts(2, 4, 8),
    )

    private val fakeRepository = object : UserRepository {
        var shouldFailUser = false

        override suspend fun getUsers(query: String?, page: Int): Result<List<User>> = Result.Success(listOf(testUser))

        override suspend fun getUserById(userId: Long): Result<User?> {
            return if (shouldFailUser) {
                Result.Error("User not found")
            } else {
                Result.Success(testUser)
            }
        }

        override suspend fun getTopTags(userId: Long): Result<List<TopTag>> {
            return Result.Success(
                listOf(
                    TopTag("android", 50, 100),
                    TopTag("kotlin", 30, 80),
                ),
            )
        }
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
    fun `loadUserDetail loads user and top tags on success and logs telemetry`() = runBlocking {
        fakeRepository.shouldFailUser = false
        val getUserProfileUseCase = GetUserProfileUseCase(fakeRepository)
        val getTopTagsUseCase = GetTopTagsUseCase(fakeRepository)

        val viewModel = UserProfileViewModel(
            getUserProfileUseCase = getUserProfileUseCase,
            getTopTagsUseCase = getTopTagsUseCase,
            telemetryLogger = fakeTelemetryLogger,
            userId = 123L,
        )

        val state = viewModel.uiState.value as UserProfileUiState.Success
        assertEquals("Bob Smith", state.user.displayName)
        assertEquals(2, state.topTags.size)
        assertEquals("android", state.topTags[0].tagName)
        assertTrue(fakeTelemetryLogger.hasEvent("user_detail_screen_viewed"))
        assertTrue(fakeTelemetryLogger.hasEvent("user_detail_screen_state_success"))
    }

    @Test
    fun `loadUserDetail sets errorMessage on failure and logs error`() = runBlocking {
        fakeRepository.shouldFailUser = true
        val getUserProfileUseCase = GetUserProfileUseCase(fakeRepository)
        val getTopTagsUseCase = GetTopTagsUseCase(fakeRepository)

        val viewModel = UserProfileViewModel(
            getUserProfileUseCase = getUserProfileUseCase,
            getTopTagsUseCase = getTopTagsUseCase,
            telemetryLogger = fakeTelemetryLogger,
            userId = 123L,
        )

        val state = viewModel.uiState.value as UserProfileUiState.Error
        assertEquals("User not found", state.message)
        assertTrue(fakeTelemetryLogger.hasError("user_detail_screen_state_error"))
    }
}
