package com.candyspace.stackoverflowusers.domain.model

data class User(
    val id: Long,
    val displayName: String,
    val reputation: Int,
    val profileImage: String?,
    val location: String?,
    val creationDate: Long,
    val badgeCounts: BadgeCounts,
)

data class BadgeCounts(
    val bronze: Int,
    val silver: Int,
    val gold: Int,
)
