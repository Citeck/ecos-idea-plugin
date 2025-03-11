package ru.citeck.idea.authentication.oidc

data class OidcAuthParams(
    val redirectUri: String,
    val clientId: String,
    val clientSecret: String,
    val callbackPort: Int
)
