package com.candyspace.stackoverflowusers.navigation

import kotlinx.serialization.Serializable

@Serializable
object SearchUserRoute
@Serializable
data class UserProfileRoute(val userId: String)