package com.gazman.signals

internal class Node<T>(val value: T?) {
    var previous: Node<T>? = null
    var next: Node<T>? = null
}