package com.gazman.signals

import java.util.*

internal class ListenersList<T> : Iterable<T?> {
    private val none = Node<T>(null)
    private var head: Node<T>? = null
    private var tail: Node<T>? = null
    private var map = IdentityHashMap<T, Node<T>>()

    /**
     * is number of listeners > 0
     */
    fun isNotEmpty(): Boolean {
        return map.isNotEmpty()
    }

    /**
     * Add a listener the tail
     */
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

    /**
     * Remove a listener it exists or ignore otherwise
     */
    fun remove(listener: T) {
        synchronized(this) {
            val node = map[listener]
            if (node == head || node == tail) {
                if (node == head) {
                    head = head?.next
                }
                if (node == tail) { // No else here, as head can be equal to tail
                    tail = tail?.previous
                }
            } else {
                node?.previous?.next = node?.next
                node?.next?.previous = node?.previous
            }
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

    /**
     * Remove all the listeners
     */
    fun clear() {
        synchronized(this) {
            head = null
            tail = null
            map.clear()
        }
    }
}