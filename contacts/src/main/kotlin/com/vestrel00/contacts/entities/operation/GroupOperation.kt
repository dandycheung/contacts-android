package com.vestrel00.contacts.entities.operation

import android.content.ContentProviderOperation
import com.vestrel00.contacts.Fields
import com.vestrel00.contacts.entities.MutableGroup
import com.vestrel00.contacts.entities.table.Table
import com.vestrel00.contacts.equalTo

private val TABLE = Table.GROUPS

/**
 * Builds [ContentProviderOperation]s for [Table.GROUPS].
 */
internal class GroupOperation {

    fun insert(group: MutableGroup): ContentProviderOperation = newInsert(TABLE)
        .withValue(Fields.Groups.Title, group.title)
        .withValue(Fields.Groups.AccountName, group.account.name)
        .withValue(Fields.Groups.AccountType, group.account.type)
        // Setting favorites and auto add has no effect. The Contacts Provider will routinely set
        // them to false for all user-created groups.
        // .withValue(Fields.Group.Favorites, it.favorites.toSqlValue())
        // .withValue(Fields.Group.AutoAdd, it.autoAdd.toSqlValue())
        .build()

    fun update(group: MutableGroup): ContentProviderOperation? = group.id?.let { groupId ->
        newUpdate(TABLE)
            .withSelection("${Fields.Groups.Id equalTo groupId}", null)
            .withValue(Fields.Groups.Title, group.title)
            .build()
    }

    fun delete(groupId: Long): ContentProviderOperation = newDelete(TABLE)
        .withSelection("${Fields.Groups.Id equalTo groupId}", null)
        .build()
}