package contacts.core.entities

import android.accounts.Account
import contacts.core.DEPRECATED_IM
import contacts.core.DEPRECATED_SIP_ADDRESS
import contacts.core.entities.custom.AbstractCustomDataEntityHolder
import contacts.core.entities.custom.CustomDataEntityHolder
import contacts.core.entities.custom.ImmutableCustomDataEntityHolder
import contacts.core.redactString
import contacts.core.redactedCopies
import contacts.core.util.PhotoDataOperation
import contacts.core.util.isProfileId
import contacts.core.util.redactedCopy
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * [Entity] that holds data modeling columns in the RawContacts table.
 *
 * ## Contact, RawContact, and Data
 *
 * A Contact may consist of one or more RawContact. A RawContact is an association between a Contact
 * and an [android.accounts.Account]. Each RawContact is associated with several pieces of Data such
 * as name, emails, phone, address, and more.
 *
 * The Contacts Provider may combine RawContacts from several different Accounts. The same effect
 * is achieved when merging / linking multiple contacts.
 *
 * It is possible for a RawContact to not be associated with an Account. Such RawContacts are local
 * to the device and are not synced.
 */
sealed interface RawContactEntity : Entity {

    /**
     * A list of [AddressEntity].
     */
    val addresses: List<AddressEntity>

    /**
     * A list of [EmailEntity].
     */
    val emails: List<EmailEntity>

    /**
     * A list of [EventEntity].
     */
    val events: List<EventEntity>

    /**
     * A list of [GroupMembershipEntity].
     */
    val groupMemberships: List<GroupMembershipEntity>

    /**
     * A list of [ImEntity].
     */
    @Deprecated(DEPRECATED_IM)
    val ims: List<@Suppress("Deprecation") ImEntity>

    /**
     * The [NameEntity].
     */
    val name: NameEntity?

    /**
     * The [NicknameEntity].
     */
    val nickname: NicknameEntity?

    /**
     * The [NoteEntity].
     */
    val note: NoteEntity?

    /**
     * The [OptionsEntity] for this raw contact.
     *
     * ## [ContactEntity.options] vs [RawContactEntity.options]
     *
     * Changes to the options of a RawContact may affect the options of the parent Contact. On the
     * other hand, changes to the options of the parent Contact will be propagated to all child
     * RawContact options.
     */
    val options: OptionsEntity?

    /**
     * The [OrganizationEntity].
     */
    val organization: OrganizationEntity?

    /**
     * A list of [PhoneEntity].
     */
    val phones: List<PhoneEntity>

    /**
     * The [Photo] does not have any real functional value. This exist only to prevent
     * RawContacts from being considered blanks, which may result in unwanted deletion in updates.
     */
    val photo: PhotoEntity?

    /**
     * A list [RelationEntity].
     */
    val relations: List<RelationEntity>

    /**
     * The [SipAddressEntity].
     */
    @Deprecated(DEPRECATED_SIP_ADDRESS)
    val sipAddress: @Suppress("Deprecation") SipAddressEntity?

    /**
     * A list [WebsiteEntity].
     */
    val websites: List<WebsiteEntity>

    /**
     * Map of custom mime type value to a [ImmutableCustomDataEntityHolder].
     */
    // This should actually be internal... if interfaces allowed for internal property declarations.
    // We can put this map as an internal property of a public class... but nah. We'll see =)
    /* internal */ val customDataEntities: Map<String, AbstractCustomDataEntityHolder>

    override val isBlank: Boolean
        get() = propertiesAreAllNullOrBlank(
            name,
            nickname,
            note,
            organization,
            @Suppress("Deprecation") sipAddress,
            addresses,
            emails,
            events,
            groupMemberships,
            @Suppress("Deprecation") ims,
            phones,
            relations,
            websites,
            customDataEntities.values.flatMap { it.entities }
            // The following are intentionally excluded as they do not constitute a row in the
            // Data table; displayNamePrimary, displayNameAlt, account, options
        )

    /**
     * True if this raw contact belongs to the user's personal profile entry.
     */
    val isProfile: Boolean
        get() = false

    /**
     * The RawContact's associated [Account]. RawContacts that are not associated with an Account
     * are local to the device and are not synced.
     *
     * Both the Account name and type must not be null. If anyone of those are null, then this will
     * be null.
     *
     * ## Inserting new RawContacts
     *
     * If an account is not provided, or null is provided, or if an incorrect account is provided,
     * the raw contacts inserted will not be associated with an account. RawContacts inserted
     * without an associated account are considered local or device-only contacts, which are not
     * synced.
     *
     * **For Lollipop (API 22) and below**
     *
     * When an Account is added, from a state where no accounts have yet been added to the system,
     * the Contacts Provider automatically sets all of the null `accountName` and `accountType` in
     * the RawContacts table to that Account's name and type.
     *
     * RawContacts inserted without an associated account will automatically get assigned to an
     * account if there are any available. This may take a few seconds, whenever the Contacts
     * Provider decides to do it.
     *
     * **For Marshmallow (API 23) and above**
     *
     * The Contacts Provider no longer associates local contacts to an account when an account is or
     * becomes available. Local contacts remain local.
     *
     * **Account removal**
     *
     * Removing the Account will delete all of the associated rows in the RawContact and Data tables.
     */
    val account: Account?

    /**
     * From the official docs at https://developer.android.com/reference/android/provider/ContactsContract.RawContacts
     *
     * > String that uniquely identifies this row to its source account. Typically it is set at the
     * > time the raw contact is inserted and never changed afterwards. The one notable exception is
     * > a new raw contact: it will have an account name and type (and possibly a data set), but no
     * > source id. This indicates to the sync adapter that a new contact needs to be created
     * > server-side and its ID stored in the corresponding SOURCE_ID field on the phone.
     *
     * Additionally, the **source ID will and should be null** if the RawContact is not
     * associated/managed by an [account] that has a sync adapter that assigns a non-null value to
     * it.
     *
     * ## Not guaranteed to be immediate!
     *
     * When a [NewRawContact] is inserted with a [NewRawContact.account] that has a sync adapter,
     * this property may be set to a non-null value. The final value of this property may be
     * assigned at a later time, when the sync adapter performs a sync. This means that the value of
     * this may be null or assigned a non-null temporary value right after insertion but may change
     * once the sync as occurred.
     *
     * ## Changing this will change the parent contact's lookup key!
     *
     * When the value of this is not null, the Contacts Provider will automatically use this as a
     * component to the parent Contact's [ExistingContactEntity.lookupKey].
     *
     * Changing this may break existing shortcuts that use the parent Contact's lookup key! This
     * should never be changed, except by the sync adapter in the aforementioned scenarios.
     *
     * ## For sync adapter use only!
     *
     * Applications should NOT set/modify the value of this property!
     *
     * As mentioned in the official docs, setting the value for this property at the time of
     * insertion or updating its value afterwards is typically only done in the context of sync
     * adapters. This is not for general app use!
     *
     * Do NOT mess with this unless you know exactly what you are doing. Otherwise, it MAY cause
     * issues with syncing with respect to the Account's sync adapter and remote servers/databases.
     *
     * Surprisingly, setting/modifying this value does not require
     * [android.provider.ContactsContract.CALLER_IS_SYNCADAPTER] to be set to true. This means that
     * regular applications can set/modify it... The best we can do is document this.
     *
     * ## The [ExistingContactEntity.lookupKey] vs [sourceId]
     *
     * RawContacts do not have a lookup key. It is exclusive to Contacts. However, RawContacts
     *  associated with an Account that have a SyncAdapter typically have a non-null value in the
     * [RawContactEntity.sourceId], which is typically used as **part** of the
     * parent [ExistingContactEntity.lookupKey]. For example, a RawContact that has a sourceId of
     * 6f5de8460f7f227e belongs to a Contact that has a lookup key of 2059i6f5de8460f7f227e. Notice
     * that the value of the sourceId is not exactly the same as the value of the lookup key!
     */
    val sourceId: String?

    // We have to cast the return type because we are not using recursive generic types.
    override fun redactedCopy(): RawContactEntity
}

/* DEV NOTES: Necessary Abstractions
 *
 * We only create abstractions when they are necessary!
 *
 * Apart from RawContactEntity, there is only one interface that extends it; ExistingRawContactEntity.
 * This interface is used for library functions that require a RawContactEntity with an ID, which means
 * that it exists in the database. There are two variants of this; RawContact and MutableRawContact.
 * With this, we can create functions (or extensions) that can take in (or have as the receiver)
 * either RawContact or MutableRawContact through the ExistingRawContactEntity abstraction/facade.
 *
 * This is why there are no interfaces for NewRawContactEntity, ImmutableRawContactEntity, and
 * MutableRawContactEntity. There are currently no library functions or constructs that require them.
 *
 * Please update this documentation if new abstractions are created.
 */

/**
 * A [RawContactEntity] that has already been inserted into the database.
 */
sealed interface ExistingRawContactEntity : RawContactEntity, ExistingEntity {

    /**
     * The id of the RawContacts row this represents.
     *
     * The value of RawContacts._ID / Data.RAW_CONTACT_ID.
     */
    // Override for documentation purposes.
    override val id: Long

    /**
     * The ID of the [Contact] that this [RawContact] is associated with.
     *
     * The value of RawContacts.CONTACT_ID / Data.CONTACT_ID.
     */
    val contactId: Long

    /**
     * The standard text shown as the raw contact's display name, based on the best available
     * information for the raw contact (for example, it might be the email address if the name is
     * not available). This may be null if the Contacts Provider cannot find a suitable display name
     * source to use.
     *
     * This is the raw contact name displayed by the Google Contacts app when viewing a raw contact.
     *
     * The contacts provider is free to choose whatever representation makes most sense for its
     * target market. For example in the default Android Open Source Project implementation, if the
     * display name is based on the [Name] and the [Name] follows the Western full-name style, then
     * this field contains the "given name first" version of the full name.
     *
     * This is a read-only attribute as the Contacts Provider automatically sets this value.
     * This is ignored for insert, update, and delete functions.
     *
     * ## [ExistingRawContactEntity.displayNamePrimary] vs [Name.displayName]
     *
     * The [ExistingRawContactEntity.displayNamePrimary] may be different than [Name.displayName].
     * If a [Name] in the Data table is not provided, then other kinds of data will be used as the
     * raw contact's display name. For example, if an [Email] is provided but no [Name] then the
     * display name will be the email. When a [Name] is inserted, the Contacts Provider
     * automatically updates the [ExistingRawContactEntity.displayNamePrimary].
     *
     * Display name sources are specified in `ContactsContract.DisplayNameSources`. In order of
     * increasing priority; [Email], [Phone], [Organization], [Nickname], and [Name].
     *
     * ## [ContactEntity.displayNamePrimary] vs [ExistingRawContactEntity.displayNamePrimary]
     *
     * The [ContactEntity.displayNamePrimary] holds the same value as **one of its** constituent
     * RawContacts.
     */
    val displayNamePrimary: String?

    /**
     * An alternative representation of the display name, such as "family name first" instead of
     * "given name first" for Western names. If an alternative is not available, the values should
     * be the same as [displayNamePrimary].
     *
     * This is a read-only attribute as the Contacts Provider automatically sets this value.
     * This is ignored for insert, update, and delete functions.
     */
    val displayNameAlt: String?

    override val isProfile: Boolean
        get() = id.isProfileId

    // We have to cast the return type because we are not using recursive generic types.
    override fun redactedCopy(): ExistingRawContactEntity
}

/**
 * An immutable [ExistingRawContactEntity].
 *
 * This can hold existing immutable data entities.
 */
@ConsistentCopyVisibility
@Parcelize
data class RawContact internal constructor(

    override val id: Long,
    override val contactId: Long,
    override val account: Account?,
    override val sourceId: String?,

    override val displayNamePrimary: String?,
    override val displayNameAlt: String?,

    override val addresses: List<Address>,
    override val emails: List<Email>,
    override val events: List<Event>,
    override val groupMemberships: List<GroupMembership>,
    @Deprecated(DEPRECATED_IM) override val ims: List<@Suppress("Deprecation") Im>,
    override val name: Name?,
    override val nickname: Nickname?,
    override val note: Note?,
    override val options: Options?,
    override val organization: Organization?,
    override val phones: List<Phone>,
    override val photo: Photo?,
    override val relations: List<Relation>,
    @Deprecated(DEPRECATED_SIP_ADDRESS) override val sipAddress: @Suppress("Deprecation") SipAddress?,
    override val websites: List<Website>,
    override val customDataEntities: Map<String, ImmutableCustomDataEntityHolder>,

    override val isRedacted: Boolean

) : ExistingRawContactEntity, ImmutableEntityWithMutableType<MutableRawContact> {

    override fun mutableCopy() = MutableRawContact(
        id = id,
        contactId = contactId,
        sourceId = sourceId,

        displayNamePrimary = displayNamePrimary,
        displayNameAlt = displayNameAlt,
        account = account,

        addresses = addresses.asSequence().mutableCopies().toMutableList(),
        emails = emails.asSequence().mutableCopies().toMutableList(),
        events = events.asSequence().mutableCopies().toMutableList(),
        groupMemberships = groupMemberships.toMutableList(),
        ims = @Suppress("Deprecation") ims.asSequence().mutableCopies().toMutableList(),
        name = name?.mutableCopy(),
        nickname = nickname?.mutableCopy(),
        note = note?.mutableCopy(),
        options = options?.mutableCopy(),
        organization = organization?.mutableCopy(),
        phones = phones.asSequence().mutableCopies().toMutableList(),
        photo = photo,
        relations = relations.asSequence().mutableCopies().toMutableList(),
        sipAddress = @Suppress("Deprecation") sipAddress?.mutableCopy(),
        websites = websites.asSequence().mutableCopies().toMutableList(),

        customDataEntities = customDataEntities
            .mapValues { it.value.mutableCopy() }
            .toMutableMap(),

        isRedacted = isRedacted
    )

    override fun redactedCopy() = copy(
        isRedacted = true,

        displayNamePrimary = displayNamePrimary?.redactString(),
        displayNameAlt = displayNameAlt?.redactString(),
        account = account?.redactedCopy(),

        addresses = addresses.redactedCopies(),
        emails = emails.redactedCopies(),
        events = events.redactedCopies(),
        groupMemberships = groupMemberships.redactedCopies(),
        ims = @Suppress("Deprecation") ims.redactedCopies(),
        name = name?.redactedCopy(),
        nickname = nickname?.redactedCopy(),
        organization = organization?.redactedCopy(),
        phones = phones.redactedCopies(),
        photo = photo?.redactedCopy(),
        relations = relations.redactedCopies(),
        sipAddress = @Suppress("Deprecation") sipAddress?.redactedCopy(),
        websites = websites.redactedCopies(),

        customDataEntities = customDataEntities.mapValues {
            it.value.redactedCopy()
        }
    )
}

/**
 * A mutable [ExistingRawContactEntity].
 *
 * This can hold new and existing mutable data entities.
 */
@ConsistentCopyVisibility
@Parcelize
data class MutableRawContact internal constructor(

    override val id: Long,
    override val contactId: Long,
    override val account: Account?,

    // Intentionally making this var so that it can be modified post-insert by sync adapters. As
    // stated in the property doc, this should only be set by sync adapters!
    override var sourceId: String?,

    override val displayNamePrimary: String?,
    override val displayNameAlt: String?,

    override var addresses: MutableList<MutableAddressEntity>,
    override var emails: MutableList<MutableEmailEntity>,
    override var events: MutableList<MutableEventEntity>,
    override var groupMemberships: MutableList<GroupMembershipEntity>,
    @Deprecated(DEPRECATED_IM) override var ims: MutableList<@Suppress("Deprecation") MutableImEntity>,
    override var name: MutableNameEntity?,
    override var nickname: MutableNicknameEntity?,
    override var note: MutableNoteEntity?,
    override var options: MutableOptionsEntity?,
    override var organization: MutableOrganizationEntity?,
    override var phones: MutableList<MutablePhoneEntity>,
    override var photo: PhotoEntity?,
    override var relations: MutableList<MutableRelationEntity>,
    @Deprecated(DEPRECATED_SIP_ADDRESS) override var sipAddress: @Suppress("Deprecation") MutableSipAddressEntity?,
    override var websites: MutableList<MutableWebsiteEntity>,

    override val customDataEntities: MutableMap<String, CustomDataEntityHolder>,

    override val isRedacted: Boolean

) : ExistingRawContactEntity, MutableEntity {

    @IgnoredOnParcel
    internal var photoDataOperation: PhotoDataOperation? = null

    override val isBlank: Boolean
        get() = super.isBlank && photoDataOperation !is PhotoDataOperation.SetPhoto

    override fun redactedCopy() = copy(
        isRedacted = true,

        displayNamePrimary = displayNamePrimary?.redactString(),
        displayNameAlt = displayNameAlt?.redactString(),
        account = account?.redactedCopy(),

        addresses = addresses.asSequence().redactedCopies().toMutableList(),
        emails = emails.asSequence().redactedCopies().toMutableList(),
        events = events.asSequence().redactedCopies().toMutableList(),
        groupMemberships = groupMemberships.asSequence().redactedCopies().toMutableList(),
        ims = @Suppress("Deprecation") ims.asSequence().redactedCopies().toMutableList(),
        name = name?.redactedCopy(),
        nickname = nickname?.redactedCopy(),
        options = options?.redactedCopy(),
        organization = organization?.redactedCopy(),
        phones = phones.asSequence().redactedCopies().toMutableList(),
        photo = photo?.redactedCopy(),
        relations = relations.asSequence().redactedCopies().toMutableList(),
        sipAddress = @Suppress("Deprecation") sipAddress?.redactedCopy(),
        websites = websites.asSequence().redactedCopies().toMutableList(),

        customDataEntities = customDataEntities.mapValues {
            it.value.redactedCopy()
        }.toMutableMap()
    )
}

/**
 * A new mutable [RawContactEntity].
 *
 * This can hold new mutable data entities.
 */
@Parcelize
data class NewRawContact @JvmOverloads constructor(

    override var account: Account? = null,
    override var sourceId: String? = null,

    override var addresses: MutableList<NewAddress> = mutableListOf(),
    override var emails: MutableList<NewEmail> = mutableListOf(),
    override var events: MutableList<NewEvent> = mutableListOf(),
    override var groupMemberships: MutableList<NewGroupMembership> = mutableListOf(),
    @Deprecated(DEPRECATED_IM) override var ims: MutableList<@Suppress("Deprecation") NewIm> = mutableListOf(),
    override var name: NewName? = null,
    override var nickname: NewNickname? = null,
    override var note: NewNote? = null,
    override var options: NewOptions? = null,
    override var organization: NewOrganization? = null,
    override var phones: MutableList<NewPhone> = mutableListOf(),
    override var photo: Photo? = null,
    override var relations: MutableList<NewRelation> = mutableListOf(),
    @Deprecated(DEPRECATED_SIP_ADDRESS) override var sipAddress: @Suppress("Deprecation") NewSipAddress? = null,
    override var websites: MutableList<NewWebsite> = mutableListOf(),

    override val customDataEntities: MutableMap<String, CustomDataEntityHolder> = mutableMapOf(),

    override val isRedacted: Boolean = false

) : RawContactEntity, NewEntity, MutableEntity {

    @IgnoredOnParcel
    internal var photoDataOperation: PhotoDataOperation? = null

    override val isBlank: Boolean
        get() = super.isBlank && photoDataOperation !is PhotoDataOperation.SetPhoto

    override fun redactedCopy() = copy(
        isRedacted = true,

        account = account?.redactedCopy(),

        addresses = addresses.asSequence().redactedCopies().toMutableList(),
        emails = emails.asSequence().redactedCopies().toMutableList(),
        events = events.asSequence().redactedCopies().toMutableList(),
        groupMemberships = groupMemberships.asSequence().redactedCopies().toMutableList(),
        ims = @Suppress("Deprecation") ims.asSequence().redactedCopies().toMutableList(),
        name = name?.redactedCopy(),
        nickname = nickname?.redactedCopy(),
        note = note?.redactedCopy(),
        options = options?.redactedCopy(),
        organization = organization?.redactedCopy(),
        phones = phones.asSequence().redactedCopies().toMutableList(),
        photo = photo?.redactedCopy(),
        relations = relations.asSequence().redactedCopies().toMutableList(),
        sipAddress = @Suppress("Deprecation") sipAddress?.redactedCopy(),
        websites = websites.asSequence().redactedCopies().toMutableList(),

        customDataEntities = customDataEntities.mapValues {
            it.value.redactedCopy()
        }.toMutableMap()
    )
}

/**
 * A temporary holder of existing immutable entities in mutable lists / attribute.
 *
 * Used internally to optimize cursor to contact mappings.
 */
@Parcelize
internal data class TempRawContact(

    override val id: Long,
    val contactId: Long,
    override val account: Account?,
    override val sourceId: String?,

    var displayNamePrimary: String?,
    var displayNameAlt: String?,

    override var addresses: MutableList<Address>,
    override var emails: MutableList<Email>,
    override var events: MutableList<Event>,
    override var groupMemberships: MutableList<GroupMembership>,
    @Deprecated(DEPRECATED_IM) override var ims: MutableList<@Suppress("Deprecation") Im>,
    override var name: Name?,
    override var nickname: Nickname?,
    override var note: Note?,
    override var options: Options?,
    override var organization: Organization?,
    override var phones: MutableList<Phone>,
    override var photo: Photo?,
    override var relations: MutableList<Relation>,
    @Deprecated(DEPRECATED_SIP_ADDRESS) override var sipAddress: @Suppress("Deprecation") SipAddress?,
    override var websites: MutableList<Website>,
    override val customDataEntities: MutableMap<String, ImmutableCustomDataEntityHolder>,

    override val isRedacted: Boolean

    // Intentionally not extending ExistingRawContactEntity to limit the amount possibilities to
    // only RawContact and MutableRawContact, which are the main consumer-facing entities.
) : RawContactEntity, ExistingEntity, MutableEntity {

    fun toRawContact() = RawContact(
        id = id,
        contactId = contactId,
        sourceId = sourceId,
        account = account,

        displayNamePrimary = displayNamePrimary,
        displayNameAlt = displayNameAlt,

        addresses = addresses.toList(),
        emails = emails.toList(),
        events = events.toList(),
        groupMemberships = groupMemberships.toList(),
        ims = @Suppress("Deprecation") ims.toList(),
        name = name,
        nickname = nickname,
        note = note,
        options = options,
        organization = organization,
        phones = phones.toList(),
        photo = photo,
        relations = relations.toList(),
        sipAddress = @Suppress("Deprecation") sipAddress,
        websites = websites.toList(),
        customDataEntities = customDataEntities.toMap(), // send a shallow copy

        isRedacted = isRedacted
    )

    override fun redactedCopy() = copy(
        isRedacted = true,

        displayNamePrimary = displayNamePrimary?.redactString(),
        displayNameAlt = displayNameAlt?.redactString(),
        account = account?.redactedCopy(),

        addresses = addresses.asSequence().redactedCopies().toMutableList(),
        emails = emails.asSequence().redactedCopies().toMutableList(),
        events = events.asSequence().redactedCopies().toMutableList(),
        groupMemberships = groupMemberships.asSequence().redactedCopies().toMutableList(),
        ims = @Suppress("Deprecation") ims.asSequence().redactedCopies().toMutableList(),
        name = name?.redactedCopy(),
        nickname = nickname?.redactedCopy(),
        options = options?.redactedCopy(),
        organization = organization?.redactedCopy(),
        phones = phones.asSequence().redactedCopies().toMutableList(),
        photo = photo?.redactedCopy(),
        relations = relations.asSequence().redactedCopies().toMutableList(),
        sipAddress = @Suppress("Deprecation") sipAddress?.redactedCopy(),
        websites = websites.asSequence().redactedCopies().toMutableList(),

        customDataEntities = customDataEntities.mapValues {
            it.value.redactedCopy()
        }.toMutableMap()
    )
}