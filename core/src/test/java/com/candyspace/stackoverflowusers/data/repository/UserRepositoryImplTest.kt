package com.candyspace.stackoverflowusers.data.repository

import com.candyspace.stackoverflowusers.common.Result
import com.candyspace.stackoverflowusers.data.api.StackUsersApi
import com.candyspace.stackoverflowusers.data.model.BadgeCountsDto
import com.candyspace.stackoverflowusers.data.model.StackUsersResponseDto
import com.candyspace.stackoverflowusers.data.model.TopTagDto
import com.candyspace.stackoverflowusers.data.model.TopTagsResponseDto
import com.candyspace.stackoverflowusers.data.model.UserDto
import com.candyspace.stackoverflowusers.telemetry.FakeTelemetryLogger
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UserRepositoryImplTest {

    private val fakeTelemetryLogger = FakeTelemetryLogger()
    private val userDto = UserDto(
        userId = 999L,
        displayName = "Anu User",
        reputation = 500,
        profileImage = null,
        location = "London",
        creationDate = 0L,
        badgeCounts = BadgeCountsDto(1, 2, 3),
    )
    private val fakeApi = object : StackUsersApi {
        var requestedInname: String? = null
        var requestedUserIdForUser: Long? = null

        override suspend fun getUsers(
            page: Int,
            inname: String?,
            order: String,
            sort: String,
            site: String,
        ): StackUsersResponseDto {
            requestedInname = inname
            return StackUsersResponseDto(items = listOf(userDto), hasMore = false)
        }

        override suspend fun getUserById(userId: Long, site: String): StackUsersResponseDto {
            requestedUserIdForUser = userId
            return StackUsersResponseDto(items = listOf(userDto), hasMore = false)
        }

        override suspend fun getUserTopTags(userId: Long, site: String): TopTagsResponseDto {
            return TopTagsResponseDto(
                items = listOf(TopTagDto(tagName = "kotlin", answerCount = 10, score = 50)),
            )
        }
    }
    private val repository = UserRepositoryImpl(fakeApi, fakeTelemetryLogger)

    @Test
    fun `getUsers with search query caches results and getUserById returns cached user`() = runBlocking {
        val searchResult = repository.getUsers("anu")

        assertEquals("anu", fakeApi.requestedInname)
        assertTrue(searchResult is Result.Success)

        val userResult = repository.getUserById(999L)
        assertTrue(userResult is Result.Success)
        val user = (userResult as Result.Success).data
        assertEquals("Anu User", user?.displayName)
        assertTrue(fakeTelemetryLogger.hasEvent("user_by_id_fetch_cache_hit"))
    }

    @Test
    fun `getUserById fetches directly from API when user is not in memory cache`() = runBlocking {
        val userResult = repository.getUserById(999L)

        assertEquals(999L, fakeApi.requestedUserIdForUser)
        assertTrue(userResult is Result.Success)
        val user = (userResult as Result.Success).data
        assertEquals("Anu User", user?.displayName)
        assertTrue(fakeTelemetryLogger.hasEvent("user_by_id_fetch_success"))
    }
}
