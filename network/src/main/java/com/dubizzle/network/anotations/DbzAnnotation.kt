package com.dubizzle.network.anotations

import okhttp3.Request
import retrofit2.Invocation

/**
 * This annotation indicates that the annotated function requires an access token.
 *
 * @param values If true, the function requires an access token. If false, the function does not require an access token. Defaults to true.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class NeedsAccessToken(val values: Boolean = true)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class LoginRequired(val values: Boolean = true)

fun Request.isLoginRequired(): Boolean {
    return this.tag(Invocation::class.java)?.method()
        ?.getAnnotation(LoginRequired::class.java)?.values == true
}

fun Request.isAccessTokenRequired(): Boolean {
    return this.tag(Invocation::class.java)?.method()
        ?.getAnnotation(NeedsAccessToken::class.java)?.values == true
}