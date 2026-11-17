package tj.rsdevteam.inmuslim.feature.tasbih.testing

import android.os.SystemClock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import tj.rsdevteam.inmuslim.core.Resource

private const val TIMEOUT_MS = 5_000L
private const val POLL_INTERVAL_MS = 10L

suspend fun <T : Any> Flow<Resource<T>>.successData(): T =
    (first { it !is Resource.InProgress } as Resource.Success).data

// ViewModels collect on the main thread and Room writes on its own executor, so tests poll for the outcome.
fun <T> awaitUntil(read: () -> T, condition: (T) -> Boolean): T {
    val deadline = SystemClock.uptimeMillis() + TIMEOUT_MS
    while (true) {
        val value = read()
        if (condition(value)) return value
        check(SystemClock.uptimeMillis() < deadline) { "Timed out waiting, last value: $value" }
        Thread.sleep(POLL_INTERVAL_MS)
    }
}
