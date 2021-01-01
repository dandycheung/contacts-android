package contacts.entities.mapper

import contacts.entities.Options
import contacts.entities.cursor.OptionsCursor

internal class OptionsMapper(private val optionsCursor: OptionsCursor) : EntityMapper<Options> {

    override val value: Options
        get() = Options(
            id = optionsCursor.id,

            starred = optionsCursor.starred,

            // Deprecated in API 29 - contains useless value for all Android versions in Play store.
            // timesContacted = optionsCursor.timesContacted,
            // lastTimeContacted = optionsCursor.lastTimeContacted,

            customRingtone = optionsCursor.customRingtone,

            sendToVoicemail = optionsCursor.sendToVoicemail
        )
}