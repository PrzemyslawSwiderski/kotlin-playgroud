@file:Suppress("EXPERIMENTAL_API_USAGE", "UNUSED_PARAMETER")

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.system.measureTimeMillis

val launchCount = 1_000_000

suspend fun massiveLaunch(action: suspend () -> Unit) = coroutineScope {
    for (i in 1..launchCount) {
        if (i == launchCount) log("Launching: $launchCount. coroutine")
        launch { action() }
    }
}

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

// Test mutable counter inc with single thread
@ObsoleteCoroutinesApi
val counterContext = newSingleThreadContext("CounterContext")
var counterSingleThreadContext = 0

runBlocking {
    // confine everything to a single-threaded context

    val time = measureTimeMillis {
        withContext(counterContext) {
            massiveLaunch {
                counterSingleThreadContext++
            }
        }
    }
    log("Counter in single threaded context = $counterSingleThreadContext, time: $time ms") // 1000 - there is no problem with multi-threaded access
}

// Test with synchronization on a counter

var counterSynchronized = 0

runBlocking {
    val time = measureTimeMillis {
        withContext(Dispatchers.Default) {
            massiveLaunch {
                synchronized(counterSynchronized) {
                    counterSynchronized++
                }
            }
        }
    }
    log("Counter with synchronization on a mutable object = $counterSynchronized, time: $time ms")
}


var counterSyncByExternalObj = 0

// Test with synchronization on object out of coroutine scope

val obj = Any()

fun incSync(obj: Any) = synchronized(obj) {
    counterSyncByExternalObj++
}

runBlocking {
    val time = measureTimeMillis {
        withContext(Dispatchers.Default) {
            massiveLaunch {
                incSync(obj)
            }
        }
    }
    log("Counter with sync on a external obj = $counterSyncByExternalObj, time: $time ms")
}

// Test with Mutex instance

val mutex = Mutex()
var counterWithMutex = 0

runBlocking {
    val time = measureTimeMillis {
        withContext(Dispatchers.Default) {
            massiveLaunch {
                mutex.withLock {
                    counterWithMutex++
                }
            }
        }
    }
    log("Counter with mutex = $counterWithMutex, time: $time ms")
}
