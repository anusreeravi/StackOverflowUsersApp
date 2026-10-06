package com.candyspace.stackoverflowusers.domain.repository

import com.candyspace.stackoverflowusers.common.Result
import com.candyspace.stackoverflowusers.domain.model.TopTag
import com.candyspace.stackoverflowusers.domain.model.User

interface UserRepository {
    suspend fun getUsers(query: String? = null, page: Int = 1): Result<List<User>>
    suspend fun getUserById(userId: Long): Result<User?>
    suspend fun getTopTags(userId: Long): Result<List<TopTag>>
}
