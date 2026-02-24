package ru.citeck.idea.http

class UnauthorizedException(message: String, body: String? = null) : HttpRequestFailedException(message, 401, body)
