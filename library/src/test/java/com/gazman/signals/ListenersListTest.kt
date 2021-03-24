package com.gazman.signals

import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Test

class ListenersListTest {

    private lateinit var list: ListenersList<String>

    @Before
    fun init() {
        list = ListenersList()
        list.add("a")
        list.add("b")
        list.add("c")
    }

    private fun listToString(): String {
        var items = ""

        for (item in list) {
            // null is valid return value and should be taken into account
            if (item != null) {
                items += item
            }
        }

        return items
    }

    @Test
    fun add() {
        assertEquals("abc", listToString())
    }

    @Test
    fun remove() {
        list.remove("b")
        assertEquals("ac", listToString())
    }

    @Test
    fun clear() {
        list.clear()
        assertEquals("", listToString())
    }

    @Test
    fun isNotEmpty() {
        assertTrue(list.isNotEmpty())
        list.clear()
        assertFalse(list.isNotEmpty())
    }

    @Test
    fun removeAndAddSameItem() {
        list.remove("b")
        list.add("b")
        assertEquals("acb", listToString())
    }
}