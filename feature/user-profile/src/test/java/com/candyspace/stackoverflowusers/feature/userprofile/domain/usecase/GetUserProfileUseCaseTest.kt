package com.candyspace.stackoverflowusers.feature.userprofile.domain.usecase

import com.candyspace.stackoverflowusers.common.Result
import com.candyspace.stackoverflowusers.domain.model.BadgeCounts
import com.candyspace.stackoverflowusers.domain.model.TopTag
import com.candyspace.stackoverflowusers.domain.model.User
import com.candyspace.stackoverflowusers.domain.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetUserProfileUseCaseTest {

    private val testUser = User(
        id = 123L,
        displayName = "Jon Skeet",
        reputation = 1234567,
        profileImage = null,
        location = "Reading, UK",
        creationDate = 0L,
        badgeCounts = BadgeCounts(10, 20, 30),
    )

    private val fakeRepository = object : UserRepository {
        var shouldReturnError = false
        var requestedUserId: Long? = null

        override suspend fun getUsers(query: String?, page: Int): Result<List<User>> = Result.Success(emptyList())

        override suspend fun getUserById(userId: Long): Result<User?> {
            requestedUserId = userId
            return if (shouldReturnError) {
                Result.Error("User not found")
            } else {
                Result.Success(testUser)
            }
        }

        override suspend fun getTopTags(userId: Long): Result<List<TopTag>> = Result.Success(emptyList())
    }

    private val useCase = GetUserProfileUseCase(fakeRepository)

    @Test
    fun `invoke passes userId to repository and returns user`() = runBlocking {
        fakeRepository.shouldReturnError = false
        val result = useCase(123L)

        assertEquals(123L, fakeRepository.requestedUserId)
        assertTrue(result is Result.Success)
        @Suppress("UNCHECKED_CAST")
        val user = (result as Result.Success<User?>).data
        assertEquals("Jon Skeet", user?.displayName)
    }

    @Test
    fun `invoke returns error when repository fails`() = runBlocking {
        fakeRepository.shouldReturnError = true
        val result = useCase(123L)

        assertTrue(result is Result.Error)
        assertEquals("User not found", (result as Result.Error).message)
    }
}
