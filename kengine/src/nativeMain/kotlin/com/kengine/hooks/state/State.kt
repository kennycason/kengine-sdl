package com.kengine.hooks.state

class State<T>(initialValue: T) {
    private var value: T = initialValue
    private val subscribers = mutableListOf<(T) -> Unit>()

    fun get(): T = value

    fun set(newValue: T) {
        if (value != newValue) {
            value = newValue
            notifySubscribers()
        }
    }

    fun subscribe(callback: (T) -> Unit) {
        subscribers.add(callback)
    }

    fun unsubscribe(callback: (T) -> Unit) {
        subscribers.remove(callback)
    }

    private fun notifySubscribers() {
        subscribers
            .toList() // to prevent concurrent modification. TODO determine if this acceptable for frequent notification
            .forEach { it(value) }
    }
}