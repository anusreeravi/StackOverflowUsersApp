package com.candyspace.stackoverflowusers.feature.userprofile.domain.usecase

import com.candyspace.stackoverflowusers.common.Result
import com.candyspace.stackoverflowusers.domain.model.TopTag
import com.candyspace.stackoverflowusers.domain.repository.UserRepository
import javax.inject.Inject

class GetTopTagsUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(userId: Long): Result<List<TopTag>> {
        return repository.getTopTags(userId)
    }
}
