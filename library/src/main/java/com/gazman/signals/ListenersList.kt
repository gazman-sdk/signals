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
            val node = map.remove(listener) ?: return
            node.removed = true

            if (node == head || node == tail) {
                if (node == head) {
                    head = head?.next
                    head?.previous = null
                }
                if (node == tail) { // No else here, as head can be equal to tail
                    tail = tail?.previous
                    tail?.next = null
                }
            } else {
                node.previous?.next = node.next
                node.next?.previous = node.previous
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
                if (node == none) {
                    node = this@ListenersList.head
                } else {
                    if (node?.removed == true) {
                        while (node != null) {
                            if (node?.previous?.removed == false) {
                                node = node?.previous?.next
                                return node?.value
                            }
                            node = node?.previous
                        }
                        node = this@ListenersList.head
                    } else {
                        node = node?.next
                    }
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