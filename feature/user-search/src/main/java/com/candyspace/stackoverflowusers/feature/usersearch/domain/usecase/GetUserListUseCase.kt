package com.candyspace.stackoverflowusers.feature.usersearch.domain.usecase

import com.candyspace.stackoverflowusers.common.Result
import com.candyspace.stackoverflowusers.domain.model.User
import com.candyspace.stackoverflowusers.domain.repository.UserRepository
import javax.inject.Inject

class GetUserListUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(query: String? = null, page: Int = 1): Result<List<User>> {
        return repository.getUsers(query = query, page = page)
    }
}
