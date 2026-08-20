package com.twohorse.app.data.api

class ApiException(
    val statusCode: Int,
    val apiCode: String?,
    override val message: String
) : IllegalStateException(message)
