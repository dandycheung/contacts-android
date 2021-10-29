package contacts.core.util

import android.content.Context
import contacts.core.entities.CommonDataEntity
import contacts.core.entities.RawContact
import contacts.core.entities.custom.CustomDataRegistry
import contacts.core.entities.custom.GlobalCustomDataRegistry

/**
 * Returns the [RawContact] with the [CommonDataEntity.rawContactId].
 *
 * This may return null if the [RawContact] no longer exists or if [CommonDataEntity.rawContactId] is null
 * (which is the case for manually constructed entities).
 *
 * Supports profile/non-profile RawContacts with native/custom data.
 *
 * ## Permissions
 *
 * The [contacts.core.ContactsPermissions.READ_PERMISSION] is required. Otherwise, null will be
 * returned if the permission is not granted.
 *
 * ## Thread Safety
 *
 * This should be called in a background thread to avoid blocking the UI thread.
 */
// [ANDROID X] @WorkerThread (not using annotation to avoid dependency on androidx.annotation)
@JvmOverloads
fun CommonDataEntity.rawContact(
    context: Context,
    customDataRegistry: CustomDataRegistry = GlobalCustomDataRegistry,
    cancel: () -> Boolean = { false }
): RawContact? =
    rawContactId?.let { rawContactId ->
        context.findFirstRawContactWithId(rawContactId, customDataRegistry, cancel)
    }