package com.candyspace.stackoverflowusers.feature.usersearch.data.paging

import androidx.paging.PagingSource
import com.candyspace.stackoverflowusers.common.Result
import com.candyspace.stackoverflowusers.domain.model.BadgeCounts
import com.candyspace.stackoverflowusers.domain.model.TopTag
import com.candyspace.stackoverflowusers.domain.model.User
import com.candyspace.stackoverflowusers.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserPagingSourceTest {

    private val sampleUsers = listOf(
        User(
            id = 1L,
            displayName = "User 1",
            reputation = 100,
            profileImage = null,
            location = "London",
            creationDate = 0L,
            badgeCounts = BadgeCounts(1, 2, 3),
        ),
        User(
            id = 2L,
            displayName = "User 2",
            reputation = 200,
            profileImage = null,
            location = "NY",
            creationDate = 0L,
            badgeCounts = BadgeCounts(2, 3, 4),
        ),
    )

    private val fakeRepository = object : UserRepository {
        var shouldReturnError = false

        override suspend fun getUsers(query: String?, page: Int): Result<List<User>> {
            return if (shouldReturnError) {
                Result.Error("API Network Error")
            } else {
                Result.Success(sampleUsers)
            }
        }

        override suspend fun getUserById(userId: Long): Result<User?> = Result.Success(null)

        override suspend fun getTopTags(userId: Long): Result<List<TopTag>> = Result.Success(emptyList())
    }

    @Test
    fun `load returns Page on successful repository fetch`() = runTest {
        val pagingSource = UserPagingSource(fakeRepository, query = "android")

        val expected = PagingSource.LoadResult.Page(
            data = sampleUsers,
            prevKey = null,
            nextKey = 2,
        )

        val actual = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 30,
                placeholdersEnabled = false,
            ),
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `load returns Error on failed repository fetch`() = runTest {
        fakeRepository.shouldReturnError = true
        val pagingSource = UserPagingSource(fakeRepository, query = "android")

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 30,
                placeholdersEnabled = false,
            ),
        )

        assertTrue(result is PagingSource.LoadResult.Error)
        assertEquals("API Network Error", (result as PagingSource.LoadResult.Error).throwable.message)
    }
}
