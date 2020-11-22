package com.vestrel00.contacts.util

import android.content.Context
import com.vestrel00.contacts.ContactsFields
import com.vestrel00.contacts.ContactsPermissions
import com.vestrel00.contacts.Include
import com.vestrel00.contacts.entities.ContactEntity
import com.vestrel00.contacts.entities.MutableOptions
import com.vestrel00.contacts.entities.Options
import com.vestrel00.contacts.entities.mapper.optionsMapper
import com.vestrel00.contacts.entities.operation.OptionsOperation
import com.vestrel00.contacts.entities.table.Table
import com.vestrel00.contacts.equalTo

/**
 * Returns the most up-to-date [Options] of this [ContactEntity], which may be different from this
 * instance's options immutable member variable as it may be stale.
 *
 * Note that changes to the options of a RawContact may affect the options of the parent Contact.
 * On the other hand, changes to the options of the parent Contact will be propagated to all child
 * RawContact options.
 *
 * ## Permissions
 *
 * This requires the [ContactsPermissions.READ_PERMISSION].
 *
 * ## Thread Safety
 *
 * This should be called in a background thread to avoid blocking the UI thread.
 */
// [ANDROID X] @WorkerThread (not using annotation to avoid dependency on androidx.annotation)
fun ContactEntity.options(context: Context): Options {
    val contactId = id

    if (!ContactsPermissions(context).canQuery() || contactId == null) {
        return Options()
    }

    return context.contentResolver.query(
        Table.Contacts,
        Include(ContactsFields.Options),
        ContactsFields.Id equalTo contactId
    ) {
        it.getNextOrNull { it.optionsMapper().value }
    } ?: Options()
}

/**
 * Updates this [ContactEntity.options] with the given [options].
 *
 * Note that changes to the options of a RawContact may affect the options of the parent Contact.
 * On the other hand, changes to the options of the parent Contact will be propagated to all child
 * RawContact options.
 *
 * This will not change the value of this instance's options immutable member variable! You will
 * need to refresh this instance or use [ContactEntity.options] extension function to get the most
 * up-to-date options.
 *
 * ## Permissions
 *
 * This requires the [ContactsPermissions.WRITE_PERMISSION].
 *
 * ## Thread Safety
 *
 * This should be called in a background thread to avoid blocking the UI thread.
 */
// [ANDROID X] @WorkerThread (not using annotation to avoid dependency on androidx.annotation)
fun ContactEntity.setOptions(context: Context, options: MutableOptions): Boolean {
    val contactId = id

    if (!ContactsPermissions(context).canUpdateDelete() || contactId == null) {
        return false
    }

    return context.contentResolver.applyBatch(
        OptionsOperation.updateContactOptions(contactId, options)
    ) != null
}

/**
 * Updates this [ContactEntity.options] in [update]. If this contact has null options, a new blank
 * options will be used in [update].
 *
 * Note that changes to the options of a RawContact may affect the options of the parent Contact.
 * On the other hand, changes to the options of the parent Contact will be propagated to all child
 * RawContact options.
 *
 * This will not change the value of this instance's options immutable member variable! You will
 * need to refresh this instance or use [ContactEntity.options] extension function to get the most
 * up-to-date options.
 *
 * ## Permissions
 *
 * This requires the [ContactsPermissions.WRITE_PERMISSION].
 *
 * ## Thread Safety
 *
 * This should be called in a background thread to avoid blocking the UI thread.
 */
// [ANDROID X] @WorkerThread (not using annotation to avoid dependency on androidx.annotation)
fun ContactEntity.updateOptions(context: Context, update: MutableOptions.() -> Unit): Boolean {
    val optionsToUse = options(context).toMutableOptions()
    optionsToUse.update()
    return setOptions(context, optionsToUse)
}