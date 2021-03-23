package com.gazman.signals

import com.gazman.signals.Signals.localSignal
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

class SignalsTest {

    private fun interface EatSignal {
        fun onEat()
    }

    @Test
    fun addListenerAndDispatch() {
        val counter = AtomicInteger()
        val eatSignal: Signal<EatSignal> = localSignal(EatSignal::class)
        eatSignal.addListener { counter.incrementAndGet() }
        eatSignal.addListener { counter.incrementAndGet() }

        eatSignal.dispatcher.onEat()
        Assert.assertEquals(2, counter.get())
    }

    @Test
    fun removeListener() {
        for (i in 0..9) {
            removeListener(i)
        }
    }

    private fun removeListener(index: Int) {
        val counter = AtomicInteger()
        val eatSignal: Signal<EatSignal> = localSignal(EatSignal::class)
        val listeners = ArrayList<EatSignal>()

        for (i in 0..9) {
            listeners.add { counter.incrementAndGet() }
        }
        for (i in 0..9) {
            eatSignal.addListener(listeners[i])
        }

        eatSignal.removeListener(listeners[index])

        eatSignal.dispatcher.onEat()

        Assert.assertEquals(9, counter.get())
    }

    @Test
    fun addListenerWhileDispatching() {
        val counter = AtomicInteger()
        val eatSignal: Signal<EatSignal> = localSignal(EatSignal::class)
        eatSignal.addListener {
            counter.incrementAndGet()
            eatSignal.addListener { counter.incrementAndGet() }
        }

        eatSignal.dispatcher.onEat()
        Assert.assertEquals(2, counter.get())
    }

    @Test
    fun removeListenerWhileDispatching() {
        for (i in 0..9) {
            removeListenerWhileDispatching(i)
        }
    }

    private fun removeListenerWhileDispatching(index: Int) {
        val counter = AtomicInteger()
        val eatSignal: Signal<EatSignal> = localSignal(EatSignal::class)

        val listeners = ArrayList<EatSignal>()
        for (i in 0..9) {
            if (i == 4) {
                listeners.add {
                    counter.incrementAndGet()
                    eatSignal.removeListener(listeners[index])
                }
            } else {
                listeners.add { counter.incrementAndGet() }
            }
        }

        for (listener in listeners) {
            eatSignal.addListener(listener)
        }

        eatSignal.dispatcher.onEat()
        Assert.assertEquals(if (index <= 4) 10 else 9, counter.get())
    }

    @Test
    fun dispatchAsync() {
        val executorService = Executors.newFixedThreadPool(10)
        val counter = AtomicInteger()
        val eatSignal: Signal<EatSignal> = localSignal(EatSignal::class)
        val random = Random(123)

        for (i in 0..9) {
            // Add 10 anonymous class listeners
            eatSignal.addListener {
                counter.incrementAndGet()
                Thread.sleep(random.nextLong(3))
            }
        }

        for (i in 0..9) {
            executorService.execute { eatSignal.dispatcher.onEat() }
        }
        executorService.shutdown()
        executorService.awaitTermination(1, TimeUnit.MINUTES)

        Assert.assertEquals(100, counter.get())
    }
}