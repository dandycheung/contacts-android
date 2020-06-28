package com.vestrel00.contacts.entities.mapper

import android.database.Cursor
import com.vestrel00.contacts.entities.CommonDataEntity
import com.vestrel00.contacts.entities.MimeType

@Suppress("UNCHECKED_CAST")
internal fun <T : CommonDataEntity> Cursor.entityMapperFor(mimeType: MimeType): EntityMapper<T> =
    when (mimeType) {
        MimeType.ADDRESS -> addressMapper()
        MimeType.EMAIL -> emailMapper()
        MimeType.EVENT -> eventMapper()
        MimeType.GROUP_MEMBERSHIP -> groupMembershipMapper()
        MimeType.IM -> imMapper()
        MimeType.NAME -> nameMapper()
        MimeType.NICKNAME -> nicknameMapper()
        MimeType.NOTE -> noteMapper()
        MimeType.ORGANIZATION -> organizationMapper()
        MimeType.PHONE -> phoneMapper()
        MimeType.RELATION -> relationMapper()
        MimeType.SIP_ADDRESS -> sipAddressMapper()
        MimeType.WEBSITE -> websiteMapper()
        MimeType.PHOTO, MimeType.UNKNOWN -> throw UnsupportedOperationException(
            "No entity mapper for mime type $mimeType"
        )
    } as EntityMapper<T>