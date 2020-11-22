package com.vestrel00.contacts.entities.operation

import com.vestrel00.contacts.Field
import com.vestrel00.contacts.Fields
import com.vestrel00.contacts.entities.MimeType
import com.vestrel00.contacts.entities.MutableWebsite

internal class WebsiteOperation(isProfile: Boolean) :
    AbstractCommonDataOperation<MutableWebsite>(isProfile) {

    override val mimeType = MimeType.WEBSITE

    override fun setData(
        data: MutableWebsite, setValue: (field: Field, dataValue: Any?) -> Unit
    ) {
        setValue(Fields.Website.Url, data.url)
    }
}