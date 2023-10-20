package com.example.adzunajobs.domain

sealed class Data<T>(open val data: T? = null) {
    class Success<T>(override val data: T) : Data<T>(data)
    class Failure<T>(val error: Throwable) : Data<T>()
    class Loading<T> : Data<T>()

    val isSuccess: Boolean
        get() = this is Success

    val failed: Boolean
        get() = this is Failure

    val loading: Boolean
        get() = this is Loading

    companion object {
        fun <T> success(data: T) = Success(data)
        fun <T> failure(error: Throwable) = Failure<T>(error)
        fun <T> loading() = Loading<T>()
    }
}
