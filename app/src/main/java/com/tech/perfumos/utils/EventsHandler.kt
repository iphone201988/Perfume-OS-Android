package com.tech.perfumos.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

object EventsHandler {

    private val _events = MutableSharedFlow<Any>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    suspend fun publish(event: Any) {
        _events.emit(event)
    }

    inline fun <reified T> subscribe(scope: CoroutineScope, crossinline onEvent: (T) -> Unit) {
        scope.launch {
            events.filterIsInstance<T>().collectLatest { event -> onEvent(event) }
        }
    }

    private val _collectionEvent = MutableSharedFlow<Any>(extraBufferCapacity = 1)
    val collectionEvent = _collectionEvent.asSharedFlow()

    suspend fun addCollection(event: Any) {
        _collectionEvent.emit(event)
    }

    inline fun <reified T> getCollection(scope: CoroutineScope, crossinline onEvent: (T) -> Unit) {
        scope.launch {
            collectionEvent.filterIsInstance<T>().collectLatest { event -> onEvent(event) }
        }
    }

    private val _wishlistEvent = MutableSharedFlow<Any>(extraBufferCapacity = 1)
    val wishlistEvent = _wishlistEvent.asSharedFlow()

    suspend fun addWishList(event: Any) {
        _wishlistEvent.emit(event)
    }

    inline fun <reified T> getWishList(scope: CoroutineScope, crossinline onEvent: (T) -> Unit) {
        scope.launch {
            wishlistEvent.filterIsInstance<T>().collectLatest { event -> onEvent(event) }
        }
    }
}