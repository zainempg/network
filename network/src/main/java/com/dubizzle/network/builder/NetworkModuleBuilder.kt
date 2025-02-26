package com.dubizzle.network.builder

import okhttp3.Authenticator
import okhttp3.Cache
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * A builder class for creating and configuring a Retrofit instance.
 *
 * This class provides a fluent API for adding various components to the Retrofit instance,
 * such as interceptors, converters, call adapters, and network configurations.
 *
 * Usage:
 * ```
 * val retrofit = NetworkModuleBuilder()
 *     .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
 *     .addConverterFactory(GsonConverterFactory.create())
 *     .buildRetrofit("https://api.example.com/")
 * ```
 */
class NetworkModuleBuilder {
    /**
     * The builder for the OkHttpClient used for network requests.
     *
     * This builder can be customized to configure various aspects of the HTTP client,
     * such as timeouts, interceptors, and caching.
     *
     * By default, it's initialized with a basic OkHttpClient.Builder instance.
     * You can access and modify this builder to apply your custom settings.
     */
    private var okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()

    /**
     * A list of [CallAdapter.Factory] instances used to create call adapters.
     *
     * Call adapters adapt the return type of the service interface methods to the expected type.
     * This allows using different return types like `Flow`, `Deferred`, etc. instead of `Call`.
     *
     * This list is initialized as an empty ArrayList. Call adapters can be added to this list
     * using the `addCallAdapterFactory` method.
     */
    private val callAdapterFactory = arrayListOf<CallAdapter.Factory>()

    /**
     * A list of [Converter.Factory] instances used to create call adapters.
     *
     * Call adapters are responsible for converting the return type of a Retrofit service method
     * into a [Call] object which can be executed to make the network request.
     *
     * This list can be modified to add or remove custom call adapter factories.
     */
    private val callConverterFactory = arrayListOf<Converter.Factory>()

    /**
     * Adds a [CookieJar] to the OkHttpClient being built.
     *
     * This allows the network client to automatically manage cookies for requests and responses.
     *
     * @param cookieJar The [CookieJar] to use for managing cookies.
     * @return The [NetworkModuleBuilder] instance for chaining method calls.
     */
    fun addCookieJar(cookieJar: CookieJar): NetworkModuleBuilder {
        okHttpClientBuilder = okHttpClientBuilder.cookieJar(cookieJar)
        return this
    }

    /**
     * Adds an [Authenticator] to the OkHttpClient being built.
     *
     * The authenticator is used to automatically re-authenticate requests
     * when a 401 (Unauthorized) response is received.
     *
     * @param authInterceptor The [Authenticator] to be added.
     * @return This [NetworkModuleBuilder] instance for chaining calls.
     */
    fun authenticator(authInterceptor: Authenticator): NetworkModuleBuilder {
        okHttpClientBuilder = okHttpClientBuilder.authenticator(authInterceptor)
        return this
    }

    /**
     * Adds a network interceptor to the OkHttpClient.
     *
     * Network interceptors are invoked for every network request, regardless of whether it is served from the cache.
     * This allows them to monitor, rewrite, and retry calls. They have access to both the request and response.
     *
     * **Note:** Unlike application interceptors, network interceptors do not have access to request body for POST requests that are cached.
     *
     * @param interceptor The [okhttp3.Interceptor] instance to add.
     * @return This NetworkModuleBuilder instance to facilitate method chaining.
     */
    fun addNetworkInterceptor(interceptor: okhttp3.Interceptor): NetworkModuleBuilder {
        okHttpClientBuilder = okHttpClientBuilder.addNetworkInterceptor(interceptor)
        return this
    }

    /**
     * Adds an interceptor to the OkHttpClient builder.
     *
     * Interceptors are a powerful mechanism that can monitor, rewrite, and retry calls.
     * They are called in the order they are added.
     *
     * @param interceptor The interceptor to add.
     * @return This NetworkModuleBuilder instance for method chaining.
     */
    fun addInterceptor(interceptor: okhttp3.Interceptor): NetworkModuleBuilder {
        okHttpClientBuilder = okHttpClientBuilder.addInterceptor(interceptor)
        return this
    }

    /**
     * Adds a [CallAdapter.Factory] to the list of call adapter factories used by Retrofit.
     *
     * Call adapter factories are responsible for adapting the return type of a Retrofit service method
     * to a type that can be used by the calling code. For example, a call adapter factory might be used
     * to adapt a `Call<T>` to a `Flow<T>` or a `Deferred<T>`.
     *
     * @param callAdapterFactory The call adapter factory to add.
     * @return This builder instance, allowing for chaining of method calls.
     */
    fun addCallAdapterFactory(callAdapterFactory: CallAdapter.Factory): NetworkModuleBuilder {
        this.callAdapterFactory.add(callAdapterFactory)
        return this
    }

    /**
     * Adds a [Converter.Factory] to the list of converter factories used by Retrofit.
     *
     * Converter factories are responsible for converting HTTP responses into Java objects
     * and vice versa. This method allows you to add custom converter factories to handle
     * specific data formats or serialization libraries.
     *
     * @param converterFactory The [Converter.Factory] to add.
     * @return This [NetworkModuleBuilder] instance to allow for method chaining.
     */
    fun addConverterFactory(converterFactory: Converter.Factory): NetworkModuleBuilder {
        this.callConverterFactory.add(converterFactory)
        return this
    }

    /**
     * Builds a Retrofit instance using the provided base URL and pre-configured
     * OkHttp client, call adapter factories, and call converter factories.
     *
     * @param stringUrl The base URL for the API endpoints.
     * @return A configured Retrofit instance ready to create API services.
     */
    fun buildRetrofit(stringUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(stringUrl)
            .client(okHttpClientBuilder.build()).apply {
                callAdapterFactory.forEach { this.addCallAdapterFactory(it) }
                callConverterFactory.forEach { this.addConverterFactory(it) }
            }.build()
    }

    /**
     * Configures the [OkHttpClient] to use the provided [Cache] for caching network responses.
     *
     * This function allows you to specify a custom cache implementation for your network requests.
     * The cache will be used to store and retrieve responses, potentially reducing network traffic
     * and improving performance.
     *
     * @param cache The [Cache] instance to use for caching.
     * @return This [NetworkModuleBuilder] instance, allowing for method chaining.
     */
    fun cache(cache: Cache): NetworkModuleBuilder {
        okHttpClientBuilder = okHttpClientBuilder.cache(cache)
        return this
    }

    /**
     * Sets the call timeout for the OkHttpClient.
     *
     * This function configures the maximum time allowed for the entire HTTP call to complete,
     * including connection establishment, request transmission, and response reception.
     *
     * @param i The timeout duration.
     * @param unit The time unit for the timeout duration (e.g., TimeUnit.SECONDS).
     * @return This NetworkModuleBuilder instance for method chaining.
     */
    fun callTimeout(i: Long, unit: TimeUnit): NetworkModuleBuilder {
        okHttpClientBuilder = okHttpClientBuilder.callTimeout(i, unit)
        return this
    }

    /**
     * Sets the connect timeout for the OkHttpClient.
     *
     * @param i The timeout value.
     * @param unit The unit of the timeout value.
     * @return This NetworkModuleBuilder instance to allow for chaining of calls.
     */
    fun connectTimeout(i: Long, unit: TimeUnit): NetworkModuleBuilder {
        okHttpClientBuilder = okHttpClientBuilder.connectTimeout(i, unit)
        return this
    }

    /**
     * Sets the read timeout for the OkHttpClient.
     *
     * This function configures the maximum time allowed for reading data from the server.
     * If the read operation takes longer than the specified timeout, a SocketTimeoutException will be thrown.
     *
     * @param i The timeout value.
     * @param unit The time unit of the timeout value.
     * @return This NetworkModuleBuilder instance, allowing for chained method calls.
     */
    fun readTimeout(i: Long, unit: TimeUnit): NetworkModuleBuilder {
        okHttpClientBuilder = okHttpClientBuilder.readTimeout(i, unit)
        return this
    }

    /**
     * Sets the write timeout for the OkHttpClient.
     *
     * This function configures the maximum time allowed for writing data to the server.
     * If the write operation takes longer than the specified timeout, a SocketTimeoutException will be thrown.
     *
     * @param i The timeout value.
     * @param unit The time unit of the timeout value (e.g., TimeUnit.SECONDS).
     * @return This NetworkModuleBuilder instance, allowing for chained calls.
     */
    fun writeTimeout(i: Long, unit: TimeUnit): NetworkModuleBuilder {
        okHttpClientBuilder = okHttpClientBuilder.writeTimeout(i, unit)
        return this
    }


}