package com.dubizzle.network.anotations

/**
 * @param maxAge in seconds
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class CacheRequest(val maxAge: Int = HALF_HOUR) {
    companion object {
        const val ONE_MINUTE: Int = 60
        const val HALF_HOUR: Int = 60 * 30
        const val ONE_HOUR: Int = 60 * 60
        const val ONE_DAY: Int = 60 * 60 * 24
        const val SEVEN_DAYS: Int = 60 * 60 * 24 * 7
    }
}