package com.candyspace.stackoverflowusers.data.model

import com.google.gson.annotations.SerializedName

data class StackUsersResponseDto(
    @SerializedName("items")
    val items: List<UserDto>,
    @SerializedName("has_more")
    val hasMore: Boolean,
)

data class UserDto(
    @SerializedName("user_id")
    val userId: Long,
    @SerializedName("display_name")
    val displayName: String?,
    @SerializedName("reputation")
    val reputation: Int?,
    @SerializedName("profile_image")
    val profileImage: String?,
    @SerializedName("location")
    val location: String?,
    @SerializedName("creation_date")
    val creationDate: Long?,
    @SerializedName("badge_counts")
    val badgeCounts: BadgeCountsDto?,
)

data class BadgeCountsDto(
    @SerializedName("bronze")
    val bronze: Int?,
    @SerializedName("silver")
    val silver: Int?,
    @SerializedName("gold")
    val gold: Int?,
)
