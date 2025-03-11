package ru.citeck.idea.http

open class HttpRequestFailedException(
    message: String,
    statusCode: Int,
    body: String?
) : RuntimeException(
    "[$statusCode] $message -- ${(body ?: "").ifBlank { "NO_BODY" }}"
)
