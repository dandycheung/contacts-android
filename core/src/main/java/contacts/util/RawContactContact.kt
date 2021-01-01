package contacts.util

import android.content.Context
import contacts.entities.Contact
import contacts.entities.RawContactEntity
import contacts.entities.custom.CustomCommonDataRegistry
import contacts.entities.custom.GlobalCustomCommonDataRegistry

/**
 * Returns the [Contact] with the [RawContactEntity.contactId].
 *
 * This may return null if the [Contact] no longer exists or if [RawContactEntity.contactId] is null
 * (which is the case for manually constructed entities).
 *
 * Supports profile and non-profile Contacts and RawContacts.
 *
 * ## Permissions
 *
 * The [contacts.ContactsPermissions.READ_PERMISSION] is required. Otherwise, null will be returned
 * if the permission is not granted.
 *
 * ## Thread Safety
 *
 * This should be called in a background thread to avoid blocking the UI thread.
 */
// [ANDROID X] @WorkerThread (not using annotation to avoid dependency on androidx.annotation)
@JvmOverloads
fun RawContactEntity.contact(
    context: Context,
    customDataRegistry: CustomCommonDataRegistry = GlobalCustomCommonDataRegistry,
    cancel: () -> Boolean = { false }
): Contact? =
    contactId?.let { contactId ->
        context.findFirstContactWithId(contactId, customDataRegistry, cancel)
    }