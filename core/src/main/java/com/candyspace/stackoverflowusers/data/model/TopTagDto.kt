package com.candyspace.stackoverflowusers.data.model

import com.google.gson.annotations.SerializedName

data class TopTagsResponseDto(
    @SerializedName("items")
    val items: List<TopTagDto>,
)

data class TopTagDto(
    @SerializedName("tag_name")
    val tagName: String?,
    @SerializedName("answer_count")
    val answerCount: Int?,
    @SerializedName("score")
    val score: Int?,
)
