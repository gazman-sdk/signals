package com.gazman.signals

import java.util.*

internal class ListenersList<T> : Iterable<T?> {
    private val none = Node<T>(null)
    private var head: Node<T>? = null
    private var tail: Node<T>? = null
    private var map = IdentityHashMap<T, Node<T>>()

    fun isNotEmpty(): Boolean {
        return map.isNotEmpty()
    }

    fun add(listener: T) {
        synchronized(this) {
            if (map.containsKey(listener)) {
                return
            }
            val node = Node(listener)
            map[listener] = node

            if (tail == null) {
                head = node
                tail = node
            } else {
                node.previous = tail
                tail?.next = node
                tail = node
            }
        }
    }

    fun remove(listener: T) {
        synchronized(this) {
            val node = map[listener]
            node?.previous?.next = node?.next
            node?.next?.previous = node?.previous
        }
    }

    override fun iterator(): Iterator<T?> {
        return object : Iterator<T?> {
            var node: Node<T>? = none

            override fun hasNext() = node != null && node != tail

            override fun next(): T? {
                node = if (node == none) {
                    this@ListenersList.head
                } else {
                    node?.next
                }
                return node?.value
            }

        }
    }

    fun clear() {
        synchronized(this) {
            head = null
            tail = null
            map.clear()
        }
    }
}