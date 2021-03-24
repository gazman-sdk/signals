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
            val node = map.remove(listener)
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

            override fun hasNext() =
                    // in case during the n-2 item iteration the n-1 item was removed, the iteration should stop
                    // in such case the node will be equal to the tail
                    //
                    // in case during the n-1 iteration the n-1 item added new items the iteration should not stop
                    // in such case the node will be null
                    node != null && node != tail &&
                            head != null // handle the clear case

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