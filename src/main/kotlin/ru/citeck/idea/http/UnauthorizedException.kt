package ru.citeck.idea.http

class UnauthorizedException(message: String) : HttpRequestFailedException(message, 401, "")
