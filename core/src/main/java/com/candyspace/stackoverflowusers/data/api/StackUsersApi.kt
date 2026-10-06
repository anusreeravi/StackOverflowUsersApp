package com.candyspace.stackoverflowusers.data.api

import com.candyspace.stackoverflowusers.data.model.StackUsersResponseDto
import com.candyspace.stackoverflowusers.data.model.TopTagsResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * StackUsersApi for retrieving user list details,user profile details and tag details
 */
interface StackUsersApi {
    @GET("2.3/users")
    suspend fun getUsers(
        @Query("page") page: Int = 1,
        @Query("inname") inname: String? = null,
        @Query("order") order: String = "desc",
        @Query("sort") sort: String = "reputation",
        @Query("site") site: String = "stackoverflow",
    ): StackUsersResponseDto

    @GET("2.3/users/{userId}")
    suspend fun getUserById(
        @Path("userId") userId: Long,
        @Query("site") site: String = "stackoverflow",
    ): StackUsersResponseDto

    @GET("2.3/users/{userId}/top-tags")
    suspend fun getUserTopTags(
        @Path("userId") userId: Long,
        @Query("site") site: String = "stackoverflow",
    ): TopTagsResponseDto
}
