package contacts.core.entities

import kotlinx.parcelize.Parcelize

/**
 * A data kind representing the contact's nickname.
 *
 * A RawContact may have 0 or 1 entry of this data kind.
 */
sealed interface NicknameEntity : DataEntity {

    // Type and Label are also available. However, the type keep getting set to default
    // automatically by the Contacts Provider...

    /**
     * The nickname
     */
    val name: String?

    /**
     * The [name].
     */
    // Delegated properties are not allowed on interfaces =(
    // override var primaryValue: String? by this::name
    override val primaryValue: String?
        get() = name

    override val mimeType: MimeType
        get() = MimeType.Nickname

    override val isBlank: Boolean
        get() = propertiesAreAllNullOrBlank(name)

    // We have to cast the return type because we are not using recursive generic types.
    override fun redactedCopy(): NicknameEntity
}

/* DEV NOTES: Necessary Abstractions
 *
 * We only create abstractions when they are necessary!
 *
 * Apart from NicknameEntity, there is only one interface that extends it; MutableNicknameEntity.
 *
 * The MutableNicknameEntity interface is used for library constructs that require an NicknameEntity
 * that can be mutated whether it is already inserted in the database or not. There are two
 * variants of this; MutableNickname and NewNickname. With this, we can create constructs that can
 * keep a reference to MutableNickname(s) or NewNickname(s) through the MutableNicknameEntity
 * abstraction/facade.
 *
 * This is why there are no interfaces for NewNicknameEntity, ExistingNicknameEntity, and
 * ImmutableNicknameEntity. There are currently no library functions or constructs that require them.
 *
 * Please update this documentation if new abstractions are created.
 */

/**
 * A mutable [NicknameEntity]. `
 */
sealed interface MutableNicknameEntity : NicknameEntity, MutableDataEntity {

    override var name: String?

    /**
     * The [name].
     */
    // Delegated properties are not allowed on interfaces =(
    // override var primaryValue: String? by this::name
    override var primaryValue: String?
        get() = name
        set(value) {
            name = value
        }

    // We have to cast the return type because we are not using recursive generic types.
    override fun redactedCopy(): MutableNicknameEntity
}

/**
 * An existing immutable [NicknameEntity].
 */
@ConsistentCopyVisibility
@Parcelize
data class Nickname internal constructor(

    override val id: Long,
    override val rawContactId: Long,
    override val contactId: Long,

    override val isPrimary: Boolean,
    override val isSuperPrimary: Boolean,

    override val name: String?,

    override val isRedacted: Boolean

) : NicknameEntity, ExistingDataEntity, ImmutableDataEntityWithMutableType<MutableNickname> {

    override fun mutableCopy() = MutableNickname(
        id = id,
        rawContactId = rawContactId,
        contactId = contactId,

        isPrimary = isPrimary,
        isSuperPrimary = isSuperPrimary,

        name = name,

        isRedacted = isRedacted
    )

    override fun redactedCopy() = copy(
        isRedacted = true,

        name = name?.redact()
    )
}

/**
 * An existing mutable [NicknameEntity].
 */
@ConsistentCopyVisibility
@Parcelize
data class MutableNickname internal constructor(

    override val id: Long,
    override val rawContactId: Long,
    override val contactId: Long,

    override val isPrimary: Boolean,
    override val isSuperPrimary: Boolean,

    override var name: String?,

    override val isRedacted: Boolean

) : NicknameEntity, ExistingDataEntity, MutableNicknameEntity {

    override fun redactedCopy() = copy(
        isRedacted = true,

        name = name?.redact()
    )
}


/**
 * A new mutable [NicknameEntity].
 */
@Parcelize
data class NewNickname @JvmOverloads constructor(

    override var name: String? = null,

    override var isReadOnly: Boolean = false,
    override val isRedacted: Boolean = false

) : NicknameEntity, NewDataEntity, MutableNicknameEntity {

    override fun redactedCopy() = copy(
        isRedacted = true,

        name = name?.redact()
    )
}