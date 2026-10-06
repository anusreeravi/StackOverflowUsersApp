package com.candyspace.stackoverflowusers.domain.model

data class TopTag(
    val tagName: String,
    val answerCount: Int,
    val score: Int,
)
