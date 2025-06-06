package com.whakaara.core

import android.content.BroadcastReceiver
import com.whakaara.core.LogUtils.logE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException

// https://github.com/androidx/androidx/blob/a00488668925d695a6ae0d6168d33fdd619c0b31/glance/glance-appwidget/src/main/java/androidx/glance/appwidget/CoroutineBroadcastReceiver.kt#L35
fun BroadcastReceiver.goAsync(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    block: suspend CoroutineScope.() -> Unit
) {
    val coroutineScope = CoroutineScope(SupervisorJob() + coroutineContext)
    val pendingResult = goAsync()

    coroutineScope.launch {
        try {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (t: Throwable) {
                logE(message = "BroadcastReceiver execution failed", throwable = t)
            } finally {
                // Nothing can be in the `finally` block after this, as this throws a
                // `CancellationException`
                coroutineScope.cancel()
            }
        } finally {
            // This must be the last call, as the process may be killed after calling this.
            try {
                pendingResult.finish()
            } catch (exception: IllegalStateException) {
                // On some OEM devices, this may throw an error about "Broadcast already finished".
                // See b/257513022.
                logE(message = "Error thrown when trying to finish broadcast", throwable = exception)
            }
        }
    }
}
