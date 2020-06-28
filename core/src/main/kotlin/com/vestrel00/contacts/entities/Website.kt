package com.vestrel00.contacts.entities

import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Website internal constructor(

    override val id: Long?,

    override val rawContactId: Long?,

    override val contactId: Long?,

    override val isPrimary: Boolean,

    override val isSuperPrimary: Boolean,

    // Type and Label are also available. However, both keep getting set to null automatically by
    // the Contacts Provider...

    /**
     * The website URL string.
     */
    val url: String?

) : CommonDataEntity {

    @IgnoredOnParcel
    override val mimeType: MimeType = MimeType.WEBSITE

    override fun isBlank(): Boolean = propertiesAreAllNullOrBlank(url)

    fun toMutableWebsite() = MutableWebsite(
        id = id,
        rawContactId = rawContactId,
        contactId = contactId,

        isPrimary = isPrimary,
        isSuperPrimary = isSuperPrimary,

        url = url
    )
}

@Parcelize
data class MutableWebsite internal constructor(

    override val id: Long?,

    override val rawContactId: Long?,

    override val contactId: Long?,

    override var isPrimary: Boolean,

    override var isSuperPrimary: Boolean,

    /**
     * See [Website.url].
     */
    var url: String?

) : MutableCommonDataEntity {

    @IgnoredOnParcel
    override val mimeType: MimeType = MimeType.WEBSITE

    constructor() : this(null, null, null, false, false, null)

    override fun isBlank(): Boolean = propertiesAreAllNullOrBlank(url)
}