package com.gazman.signals

import com.gazman.signals.Signals.signal
import com.gazman.signals.SignalsTest.EatSignal
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class SignalsTest {
    @Test
    fun testDispatch() {
        val foods = AtomicInteger()
        val eatSignal: Signal<EatSignal> = signal(EatSignal::class)
        eatSignal.addListener(EatSignal { foods.incrementAndGet() })
        eatSignal.addListener(EatSignal { foods.incrementAndGet() })
        eatSignal.dispatcher.onEat()
        Assert.assertEquals(2, foods.get().toLong())
    }

    @Test
    fun testDispatchMultithreaded() {
        val executorService = Executors.newSingleThreadExecutor()
        val foods = AtomicInteger()
        val eatSignal: Signal<EatSignal> = signal(EatSignal::class)
        for (i in 0..9) {
            eatSignal.addListener(EatSignal { foods.incrementAndGet() })
        }
        for (i in 0..9) {
            eatSignal.dispatcher.onEat()
        }
        Assert.assertEquals(100, foods.get().toLong())
        for (i in 0..9) {
            executorService.execute { eatSignal.dispatcher.onEat() }
        }
        executorService.shutdown()
        executorService.awaitTermination(1, TimeUnit.HOURS)

        Assert.assertEquals(200, foods.get().toLong())
    }

    private fun interface EatSignal {
        fun onEat()
    }
}