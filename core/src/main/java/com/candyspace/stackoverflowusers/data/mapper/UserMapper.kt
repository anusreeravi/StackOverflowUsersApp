package com.candyspace.stackoverflowusers.data.mapper

import com.candyspace.stackoverflowusers.data.model.BadgeCountsDto
import com.candyspace.stackoverflowusers.data.model.UserDto
import com.candyspace.stackoverflowusers.domain.model.BadgeCounts
import com.candyspace.stackoverflowusers.domain.model.User

fun UserDto.toDomain(): User {
    return User(
        id = userId,
        displayName = displayName ?: "Unknown User",
        reputation = reputation ?: 0,
        profileImage = profileImage,
        location = location,
        creationDate = creationDate ?: 0L,
        badgeCounts = badgeCounts.toDomain(),
    )
}

fun BadgeCountsDto?.toDomain(): BadgeCounts {
    return BadgeCounts(
        bronze = this?.bronze ?: 0,
        silver = this?.silver ?: 0,
        gold = this?.gold ?: 0,
    )
}
