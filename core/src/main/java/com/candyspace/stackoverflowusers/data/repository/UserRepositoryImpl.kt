package com.candyspace.stackoverflowusers.data.repository

import com.candyspace.stackoverflowusers.common.Result
import com.candyspace.stackoverflowusers.data.api.StackUsersApi
import com.candyspace.stackoverflowusers.data.mapper.toDomain
import com.candyspace.stackoverflowusers.domain.model.TopTag
import com.candyspace.stackoverflowusers.domain.model.User
import com.candyspace.stackoverflowusers.domain.repository.UserRepository
import com.candyspace.stackoverflowusers.telemetry.TelemetryLogger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val stackUsersApi: StackUsersApi,
    private val telemetryLogger: TelemetryLogger,
) : UserRepository {

    private var cachedUsers: List<User> = emptyList()

    /**
     * Fetches a paginated list of users from the StackOverflow API, optionally filtered by [query].
     *
     * Successful results are cached in-memory and telemetry events are logged.
     *
     * @param query Optional name search query.
     * @param page Page index for pagination (starts at 1).
     * @return [Result.Success] containing the list of users or [Result.Error] on failure.
     */
    override suspend fun getUsers(query: String?, page: Int): Result<List<User>> {
        val params = mutableMapOf<String, String>()
        if (!query.isNullOrBlank()) {
            params[PARAM_QUERY] = query
        }
        params[PARAM_PAGE] = page.toString()
        telemetryLogger.logEvent(EVENT_USER_LIST_FETCH_START, params)

        return try {
            val response = stackUsersApi.getUsers(page = page, inname = query?.takeIf { it.isNotBlank() })
            val users = response.items.map { it.toDomain() }
            cachedUsers = (cachedUsers + users).distinctBy { it.id }

            telemetryLogger.logEvent(
                EVENT_USER_LIST_FETCH_SUCCESS,
                mapOf(
                    PARAM_USER_COUNT to users.size.toString(),
                    PARAM_QUERY to (query ?: ""),
                    PARAM_PAGE to page.toString(),
                ),
            )
            Result.Success(users)
        } catch (e: Exception) {
            telemetryLogger.logError(SCENARIO_USER_LIST_FETCH, e.message.toString(), e)
            Result.Error(
                message = e.message.toString(),
                cause = e,
            )
        }
    }

    /**
     * Fetches details for a specific user by [userId].
     *
     * Checks in-memory cache first before calling the remote StackOverflow API.
     *
     * @param userId Unique StackOverflow user ID.
     * @return [Result.Success] containing the [User] or null, or [Result.Error] on failure.
     */
    override suspend fun getUserById(userId: Long): Result<User?> {
        telemetryLogger.logEvent(EVENT_USER_BY_ID_FETCH_START, mapOf(PARAM_USER_ID to userId.toString()))
        val found = cachedUsers.find { it.id == userId }
        if (found != null) {
            telemetryLogger.logEvent(EVENT_USER_BY_ID_FETCH_CACHE_HIT, mapOf(PARAM_USER_ID to userId.toString()))
            return Result.Success(found)
        }

        return try {
            val response = stackUsersApi.getUserById(userId = userId)
            val user = response.items.firstOrNull()?.toDomain()
            if (user != null) {
                cachedUsers = (cachedUsers + user).distinctBy { it.id }
                telemetryLogger.logEvent(EVENT_USER_BY_ID_FETCH_SUCCESS, mapOf(PARAM_USER_ID to userId.toString()))
                Result.Success(user)
            } else {
                val errorMsg = "User $userId not found"
                telemetryLogger.logError(SCENARIO_USER_BY_ID_FETCH, errorMsg)
                Result.Error(errorMsg)
            }
        } catch (e: Exception) {
            telemetryLogger.logError(SCENARIO_USER_BY_ID_FETCH, e.message.toString(), e)
            Result.Error(message = e.message.toString(), cause = e)
        }
    }

    /**
     * Fetches top tags associated with a given [userId].
     *
     * @param userId Unique StackOverflow user ID.
     * @return [Result.Success] containing list of [TopTag] items or [Result.Error] on failure.
     */
    override suspend fun getTopTags(userId: Long): Result<List<TopTag>> {
        telemetryLogger.logEvent(EVENT_TOP_TAGS_FETCH_START, mapOf(PARAM_USER_ID to userId.toString()))
        return try {
            val response = stackUsersApi.getUserTopTags(userId = userId)
            val tags = response.items.map { topTagDto ->
                TopTag(
                    tagName = topTagDto.tagName ?: DEFAULT_TAG_NAME,
                    answerCount = topTagDto.answerCount ?: 0,
                    score = topTagDto.score ?: 0,
                )
            }
            telemetryLogger.logEvent(
                EVENT_TOP_TAGS_FETCH_SUCCESS,
                mapOf(
                    PARAM_USER_ID to userId.toString(),
                    PARAM_TAG_COUNT to tags.size.toString(),
                ),
            )
            Result.Success(tags)
        } catch (e: Exception) {
            telemetryLogger.logError(SCENARIO_TOP_TAGS_FETCH, e.message.toString(), e)
            Result.Error(
                message = e.message.toString(),
                cause = e,
            )
        }
    }

    companion object {
        private const val PARAM_QUERY = "query"
        private const val PARAM_PAGE = "page"
        private const val PARAM_USER_COUNT = "user_count"
        private const val PARAM_USER_ID = "user_id"
        private const val PARAM_TAG_COUNT = "tag_count"
        private const val EVENT_USER_LIST_FETCH_START = "user_list_fetch_start"
        private const val EVENT_USER_LIST_FETCH_SUCCESS = "user_list_fetch_success"
        private const val EVENT_USER_BY_ID_FETCH_START = "user_by_id_fetch_start"
        private const val EVENT_USER_BY_ID_FETCH_CACHE_HIT = "user_by_id_fetch_cache_hit"
        private const val EVENT_USER_BY_ID_FETCH_SUCCESS = "user_by_id_fetch_success"
        private const val EVENT_TOP_TAGS_FETCH_START = "top_tags_fetch_start"
        private const val EVENT_TOP_TAGS_FETCH_SUCCESS = "top_tags_fetch_success"
        private const val SCENARIO_USER_LIST_FETCH = "user_list_fetch"
        private const val SCENARIO_USER_BY_ID_FETCH = "user_by_id_fetch"
        private const val SCENARIO_TOP_TAGS_FETCH = "top_tags_fetch"
        private const val DEFAULT_TAG_NAME = "tag"
    }
}
