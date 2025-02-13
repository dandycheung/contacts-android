package contacts.entities.custom.rpg

import contacts.core.entities.MimeType

internal sealed class RpgMimeType : MimeType.Custom() {

    internal data object Stats : RpgMimeType() {
        // Following Contacts Provider convention of "vnd.android.cursor.item/<package>.<mimetype>"
        override val value: String = "vnd.android.cursor.item/contacts.entities.custom.rpg.stats"
    }

    internal data object Profession : RpgMimeType() {
        // Following Contacts Provider convention of "vnd.android.cursor.item/<package>.<mimetype>"
        override val value: String =
            "vnd.android.cursor.item/contacts.entities.custom.rpg.profession"
    }
}