package ru.citeck.idea.authentication.oidc

import ecos.com.fasterxml.jackson210.annotation.JsonProperty

data class OidcServerInfo(
    @JsonProperty("authorization_endpoint")
    val authEndpoint: String,

    @JsonProperty("token_endpoint")
    val tokenEndpoint: String,

    @JsonProperty("end_session_endpoint")
    val endSessionEndpoint: String,

    @JsonProperty("userinfo_endpoint")
    val userInfoEndpoint: String,

    @JsonProperty("introspection_endpoint")
    val introspectionEndpoint: String
)
