package com.candyspace.stackoverflowusers.feature.usersearch.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.candyspace.stackoverflowusers.common.Result
import com.candyspace.stackoverflowusers.domain.model.User
import com.candyspace.stackoverflowusers.domain.repository.UserRepository

class UserPagingSource(
    private val userRepository: UserRepository,
    private val query: String? = null,
) : PagingSource<Int, User>() {

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        val page = params.key ?: STARTING_PAGE_INDEX
        return try {
            when (val result = userRepository.getUsers(query = query, page = page)) {
                is Result.Success -> {
                    val users = result.data
                    val nextKey = if (users.isEmpty()) null else page + 1
                    val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                    LoadResult.Page(
                        data = users,
                        prevKey = prevKey,
                        nextKey = nextKey,
                    )
                }

                is Result.Error -> {
                    LoadResult.Error(Exception(result.message))
                }

                is Result.Loading -> {
                    LoadResult.Error(IllegalStateException("Unexpected loading state"))
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        const val STARTING_PAGE_INDEX = 1
    }
}
