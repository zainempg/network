package com.dubizzle.network.interceptor

import com.dubizzle.network.anotations.CacheRequest
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import java.util.concurrent.TimeUnit

/**
 * `CacheInterceptor` is an OkHttp [Interceptor] that adds caching directives to requests based on the presence of a [CacheRequest] annotation on the associated method.
 *
 * This interceptor inspects the method associated with a request (via the `Invocation` tag) and checks for the presence of the `@CacheRequest` annotation.
 * If the annotation is present, it extracts the `maxAge` value from it and adds a `Cache-Control` header to the request, instructing the cache to consider the response valid for the specified duration.
 * If no `CacheRequest` annotation is found, the request is passed through without modification.
 *
 * **Functionality:**
 * - **Annotation-Driven Caching:** Enables caching for specific requests by annotating the corresponding method with `@CacheRequest`.
 * - **`maxAge` Control:** Uses the `maxAge` property of the `@CacheRequest` annotation to set the maximum age of cached responses.
 * - **OkHttp Integration:** Seamlessly integrates with OkHttp's interceptor mechanism.
 * - **No-Op for Unannotated Methods:** Does not alter requests that are not associated with a method having the `@CacheRequest` annotation.
 *
 * **Usage:**
 * 1. Add this `CacheInterceptor` to your OkHttp client's interceptor chain.
 * 2. Annotate the methods that need caching with `@CacheRequest`.
 *
 *     ```kotlin
 *     @Retention(AnnotationRetention.RUNTIME)
 *     @Target(AnnotationTarget.FUNCTION)
 *     annotation class CacheRequest(val maxAge: Int)
 *
 *     interface MyService {
 *         @CacheRequest(maxAge = 300) // Cache for 5 minutes (300 seconds)
 *         @GET("/data")
 *         suspend fun getData(): Response
 *
 *         @GET("/other-data") // No caching directives
 *         suspend fun getOtherData(): Response
 *     }
 *
 */
class CacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // Get the request from the chain.
        var request = chain.request()
        val annotation = request.tag(Invocation::class.java)?.method()
            ?.getAnnotation(CacheRequest::class.java)
        annotation?.let {
            val cacheControl = CacheControl.Builder()
                .maxAge(it.maxAge, TimeUnit.SECONDS)
                .build()
            request = request.newBuilder()
                .cacheControl(cacheControl)
                .build()
        }
        return chain.proceed(request)
    }
}