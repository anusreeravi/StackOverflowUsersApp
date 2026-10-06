package com.candyspace.stackoverflowusers.feature.usersearch.domain.usecase

import com.candyspace.stackoverflowusers.common.Result
import com.candyspace.stackoverflowusers.domain.model.BadgeCounts
import com.candyspace.stackoverflowusers.domain.model.TopTag
import com.candyspace.stackoverflowusers.domain.model.User
import com.candyspace.stackoverflowusers.domain.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class GetUserListUseCaseTest {

    private val fakeRepository = object : UserRepository {
        var shouldReturnError = false
        var lastQueryPassed: String? = null

        override suspend fun getUsers(query: String?, page: Int): Result<List<User>> {
            lastQueryPassed = query
            return if (shouldReturnError) {
                Result.Error("Network Error")
            } else {
                Result.Success(
                    listOf(
                        User(
                            id = 1L,
                            displayName = "John Doe",
                            reputation = 100,
                            profileImage = null,
                            location = "London",
                            creationDate = 0L,
                            badgeCounts = BadgeCounts(1, 2, 3),
                        ),
                    ),
                )
            }
        }

        override suspend fun getUserById(userId: Long): Result<User?> = Result.Success(null)

        override suspend fun getTopTags(userId: Long): Result<List<TopTag>> = Result.Success(emptyList())
    }

    private val getUserListUseCase = GetUserListUseCase(fakeRepository)

    @Test
    fun `invoke passes search query to repository`() = runBlocking {
        fakeRepository.shouldReturnError = false
        val result = getUserListUseCase(query = "John")

        assert(result is Result.Success)
        Assert.assertEquals("John", fakeRepository.lastQueryPassed)
    }

    @Test
    fun `invoke returns success when repository returns users`() = runBlocking {
        fakeRepository.shouldReturnError = false
        val result = getUserListUseCase()

        assert(result is Result.Success)
        @Suppress("UNCHECKED_CAST")
        val users = (result as Result.Success<List<User>>).data
        Assert.assertEquals(1, users.size)
        Assert.assertEquals("John Doe", users[0].displayName)
    }

    @Test
    fun `invoke returns error when repository fails`() = runBlocking {
        fakeRepository.shouldReturnError = true
        val result = getUserListUseCase()

        assert(result is Result.Error)
        val message = (result as Result.Error).message
        Assert.assertEquals("Network Error", message)
    }
}
