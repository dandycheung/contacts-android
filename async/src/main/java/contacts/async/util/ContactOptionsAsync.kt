package contacts.async.util

import android.content.Context
import contacts.async.ASYNC_DISPATCHER
import contacts.entities.ContactEntity
import contacts.entities.MutableOptions
import contacts.entities.Options
import contacts.util.options
import contacts.util.setOptions
import contacts.util.updateOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

// region WITH CONTEXT

/**
 * Suspends the current coroutine, performs the operation in background, then returns the control
 * flow to the calling coroutine scope.
 *
 * See [ContactEntity.options].
 */
suspend fun ContactEntity.optionsWithContext(
    context: Context,
    coroutineContext: CoroutineContext = ASYNC_DISPATCHER
): Options = withContext(coroutineContext) { options(context) }

/**
 * Suspends the current coroutine, performs the operation in background, then returns the control
 * flow to the calling coroutine scope.
 *
 * See [ContactEntity.setOptions].
 */
suspend fun ContactEntity.setOptionsWithContext(
    context: Context,
    options: MutableOptions,
    coroutineContext: CoroutineContext = ASYNC_DISPATCHER
): Boolean = withContext(coroutineContext) { setOptions(context, options) }

/**
 * Suspends the current coroutine, performs the operation in background, then returns the control
 * flow to the calling coroutine scope.
 *
 * See [ContactEntity.updateOptions].
 */
suspend fun ContactEntity.updateOptionsWithContext(
    context: Context,
    coroutineContext: CoroutineContext = ASYNC_DISPATCHER,
    update: MutableOptions.() -> Unit
): Boolean = withContext(coroutineContext) { updateOptions(context, update) }

// endregion

// region ASYNC

/**
 * Creates a [CoroutineScope] with the given [coroutineContext], performs the operation in that
 * scope, then returns the [Deferred] result.
 *
 * See [ContactEntity.options].
 */
fun ContactEntity.optionsAsync(
    context: Context,
    coroutineContext: CoroutineContext = ASYNC_DISPATCHER
): Deferred<Options> = CoroutineScope(coroutineContext).async { options(context) }

/**
 * Creates a [CoroutineScope] with the given [coroutineContext], performs the operation in that
 * scope, then returns the [Deferred] result.
 *
 * See [ContactEntity.setOptions].
 */
fun ContactEntity.setOptionsAsync(
    context: Context,
    options: MutableOptions,
    coroutineContext: CoroutineContext = ASYNC_DISPATCHER
): Deferred<Boolean> = CoroutineScope(coroutineContext).async { setOptions(context, options) }

/**
 * Creates a [CoroutineScope] with the given [coroutineContext], performs the operation in that
 * scope, then returns the [Deferred] result.
 *
 * See [ContactEntity.updateOptions].
 */
fun ContactEntity.updateOptionsAsync(
    context: Context,
    coroutineContext: CoroutineContext = ASYNC_DISPATCHER,
    update: MutableOptions.() -> Unit
): Deferred<Boolean> = CoroutineScope(coroutineContext).async { updateOptions(context, update) }

// endregion