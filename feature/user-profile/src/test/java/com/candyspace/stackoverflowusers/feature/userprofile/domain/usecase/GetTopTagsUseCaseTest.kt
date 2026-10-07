package com.candyspace.stackoverflowusers.feature.userprofile.domain.usecase

import com.candyspace.stackoverflowusers.common.Result
import com.candyspace.stackoverflowusers.domain.model.TopTag
import com.candyspace.stackoverflowusers.domain.model.User
import com.candyspace.stackoverflowusers.domain.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetTopTagsUseCaseTest {

    private val testTags = listOf(
        TopTag(tagName = "c#", answerCount = 1000, score = 5000),
        TopTag(tagName = "java", answerCount = 800, score = 4000),
    )

    private val fakeRepository = object : UserRepository {
        var shouldReturnError = false
        var requestedUserId: Long? = null

        override suspend fun getUsers(query: String?, page: Int): Result<List<User>> = Result.Success(emptyList())
        override suspend fun getUserById(userId: Long): Result<User?> = Result.Success(null)

        override suspend fun getTopTags(userId: Long): Result<List<TopTag>> {
            requestedUserId = userId
            return if (shouldReturnError) {
                Result.Error("Failed to fetch top tags")
            } else {
                Result.Success(testTags)
            }
        }
    }

    private val useCase = GetTopTagsUseCase(fakeRepository)

    @Test
    fun `invoke passes userId to repository and returns top tags`() = runBlocking {
        fakeRepository.shouldReturnError = false
        val result = useCase(123L)

        assertEquals(123L, fakeRepository.requestedUserId)
        assertTrue(result is Result.Success)
        val tags = (result as Result.Success<List<TopTag>>).data
        assertEquals(2, tags.size)
        assertEquals("c#", tags[0].tagName)
    }

    @Test
    fun `invoke returns error when repository fails`() = runBlocking {
        fakeRepository.shouldReturnError = true
        val result = useCase(123L)

        assertTrue(result is Result.Error)
        assertEquals("Failed to fetch top tags", (result as Result.Error).message)
    }
}
