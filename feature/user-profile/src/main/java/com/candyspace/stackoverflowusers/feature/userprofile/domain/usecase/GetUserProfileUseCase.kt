package com.candyspace.stackoverflowusers.feature.userprofile.domain.usecase

import com.candyspace.stackoverflowusers.common.Result
import com.candyspace.stackoverflowusers.domain.model.User
import com.candyspace.stackoverflowusers.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(userId: Long): Result<User?> {
        return repository.getUserById(userId)
    }
}
