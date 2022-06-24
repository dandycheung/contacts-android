# Android Contacts, Reborn (CHEATSHEET)

This page gives you basic sample copy-paste code showcasing how to use all of the **core APIs**
provided in this library in both **Kotlin** and **Java**!

The examples provided here show the most basic usage of each **`core` API**. Click on the section
heading explore each API in full detail. You may also find these samples in the `sample` module's
`contacts.sample.cheatsheet` package.

> ⚠️ Executing `find()` and `commit()` functions in the UI thread may result in choppy UI. Those
> should be invoked in background threads instead.
> For more info, read [Execute work outside of the UI thread using coroutines](./async/async-execution-coroutines.md).

> ℹ️ This is the only documentation page that contains Java samples. I want to keep the rest of the
> documentation Kotlin-only in order to keep the markdown files clean and free of non-markdown 
> markups from MkDocs. Also, most of the code is the same so it would just add clutter.

----------------------------------------------------------------------------------------------------

## Basics

### [Query contacts](./basics/query-contacts.md)

=== "Kotlin"

    ```kotlin
    import android.app.Activity
    import contacts.core.*
    import contacts.core.entities.Contact
    
    class QueryContactsActivity : Activity() {
    
        fun getAllContacts(): List<Contact> = Contacts(this).broadQuery().find()
    
        fun getAllContactsWithFavoritesFirstOrderedByDisplayName(): List<Contact> = Contacts(this)
            .broadQuery()
            .orderBy(
                ContactsFields.Options.Starred.desc(),
                ContactsFields.DisplayNamePrimary.asc(ignoreCase = true)
            )
            .find()
    
        fun getContactsWithEmailOrDisplayNameThatPartiallyMatches(text: String?): List<Contact> =
            Contacts(this)
                .broadQuery()
                .match(BroadQuery.Match.EMAIL)
                .wherePartiallyMatches(text)
                .find()
    
        fun getContactsWithPhoneOrDisplayNameThatPartiallyMatches(text: String?): List<Contact> =
            Contacts(this)
                .broadQuery()
                .match(BroadQuery.Match.PHONE)
                .wherePartiallyMatches(text)
                .find()
    
        fun getAllContactsIncludingOnlyDisplayNameAndEmailAddresses(): List<Contact> = Contacts(this)
            .broadQuery()
            .include(
                Fields.Contact.DisplayNamePrimary,
                Fields.Email.Address
            )
            .find()
    
        fun get25Contacts(): List<Contact> = Contacts(this)
            .broadQuery()
            .limit(25)
            .find()
    
        fun get25ContactsSkippingTheFirst25(): List<Contact> = Contacts(this)
            .broadQuery()
            .offset(25)
            .limit(25)
            .find()
    }
    ```

=== "Java"

    ```java
    import static contacts.core.OrderByKt.*;
    
    import android.app.Activity;
    
    import java.util.List;
    
    import contacts.core.BroadQuery;
    import contacts.core.ContactsFactory;
    import contacts.core.ContactsFields;
    import contacts.core.Fields;
    import contacts.core.entities.Contact;
    
    public class QueryContactsActivity extends Activity {
    
        List<Contact> getAllContacts() {
            return ContactsFactory.create(this).broadQuery().find();
        }
    
        List<Contact> getAllContactsWithFavoritesFirstOrderedByDisplayName() {
            return ContactsFactory.create(this)
                    .broadQuery()
                    .orderBy(
                            desc(ContactsFields.Options.Starred),
                            asc(ContactsFields.DisplayNamePrimary, true)
                    )
                    .find();
        }
    
        List<Contact> getContactsWithAnyDataThatPartiallyMatches(String text) {
            return ContactsFactory.create(this)
                    .broadQuery()
                    .match(BroadQuery.Match.ANY)
                    .wherePartiallyMatches(text)
                    .find();
        }
    
        List<Contact> getContactsWithEmailOrDisplayNameThatPartiallyMatches(String text) {
            return ContactsFactory.create(this)
                    .broadQuery()
                    .match(BroadQuery.Match.EMAIL)
                    .wherePartiallyMatches(text)
                    .find();
        }
    
        List<Contact> getContactsWithPhoneOrDisplayNameThatPartiallyMatches(String text) {
            return ContactsFactory.create(this)
                    .broadQuery()
                    .match(BroadQuery.Match.PHONE)
                    .wherePartiallyMatches(text)
                    .find();
        }
    
        List<Contact> getAllContactsIncludingOnlyDisplayNameAndEmailAddresses() {
            return ContactsFactory.create(this)
                    .broadQuery()
                    .include(
                            Fields.Contact.DisplayNamePrimary,
                            Fields.Email.Address
                    )
                    .find();
        }
    
        List<Contact> get25Contacts() {
            return ContactsFactory.create(this)
                    .broadQuery()
                    .limit(25)
                    .find();
        }
    
        List<Contact> get25ContactsSkippingTheFirst25() {
            return ContactsFactory.create(this)
                    .broadQuery()
                    .offset(25)
                    .limit(25)
                    .find();
        }
    }
    ```

### [Query contacts (advanced)](./basics/query-contacts-advanced.md)

=== "Kotlin"

    ```kotlin
    import android.accounts.Account
    import android.app.Activity
    import contacts.core.*
    import contacts.core.entities.Contact
    import contacts.core.util.lookupKeyIn
    
    class QueryContactsAdvancedActivity : Activity() {
    
        fun getContactById(contactId: Long): Contact? = Contacts(this)
            .query()
            .where { Contact.Id equalTo contactId }
            .find()
            .firstOrNull()
    
        fun getContactByLookupKey(lookupKey: String): List<Contact> = Contacts(this)
            .query()
            .where { Contact.lookupKeyIn(lookupKey) }
            .find()
    
        fun getAllContactsForAGoogleAccount(): List<Contact> = Contacts(this)
            .query()
            .accounts(Account("email@gmail.com", "com.google"))
            .find()
    
        fun getOnlyFavoriteContacts(): List<Contact> = Contacts(this)
            .query()
            .where {
                Contact.Options.Starred equalTo true
            }
            .find()
    
        fun getContactsPartiallyMatchingDisplayName(): List<Contact> = Contacts(this)
            .query()
            .where {
                Contact.DisplayNamePrimary contains "alex"
            }
            .find()
    
        fun getContactsWithAtLeastOneGmailEmail(): List<Contact> = Contacts(this)
            .query()
            .where {
                Email.Address endsWith "@gmail.com"
            }
            .find()
    
        fun getContactsWithAtLeastOnePhoneNumber(): List<Contact> = Contacts(this)
            .query()
            .where {
                Phone.Number.isNotNullOrEmpty()
                // or Contact.HasPhoneNumber equalTo true
            }
            .find()
    
        fun getContactsWithAtLeastOnePhoneNumberAndEmail(): List<Contact> = Contacts(this)
            .query()
            .where {
                Phone.Number.isNotNullOrEmpty() and Email.Address.isNotNullOrEmpty()
                // or Contact.HasPhoneNumber equalTo true and Email.Address.isNotNullOrEmpty()
            }
            .find()
    }
    ```

=== "Java"

    ```java
    import static contacts.core.WhereKt.*;
    import static contacts.core.util.ContactLookupKeyKt.lookupKeyIn;
    
    import android.accounts.Account;
    import android.app.Activity;
    
    import java.util.List;
    
    import contacts.core.ContactsFactory;
    import contacts.core.Fields;
    import contacts.core.entities.Contact;
    
    public class QueryContactsAdvancedActivity extends Activity {
    
        Contact getContactById(Long contactId) {
            return ContactsFactory.create(this)
                    .query()
                    .where(
                            equalTo(Fields.Contact.Id, contactId)
                    )
                    .find()
                    .get(0);
        }
    
        List<Contact> getContactByLookupKey(String lookupKey) {
            return ContactsFactory.create(this)
                    .query()
                    .where(
                            lookupKeyIn(Fields.Contact, lookupKey)
                    )
                    .find();
        }
    
        List<Contact> getAllContactsForAGoogleAccount() {
            return ContactsFactory.create(this)
                    .query()
                    .accounts(new Account("email@gmail.com", "com.google"))
                    .find();
        }
    
        List<Contact> getOnlyFavoriteContacts() {
            return ContactsFactory.create(this)
                    .query()
                    .where(
                            equalTo(Fields.Contact.Options.Starred, true)
                    )
                    .find();
        }
    
        List<Contact> getContactsPartiallyMatchingDisplayName() {
            return ContactsFactory.create(this)
                    .query()
                    .where(
                            contains(Fields.Contact.DisplayNamePrimary, "alex")
                    )
                    .find();
        }
    
        List<Contact> getContactsWithAtLeastOneGmailEmail() {
            return ContactsFactory.create(this)
                    .query()
                    .where(
                            endsWith(Fields.Email.Address, "@gmail.com")
                    )
                    .find();
        }
    
        List<Contact> getContactsWithAtLeastOnePhoneNumber() {
            return ContactsFactory.create(this)
                    .query()
                    .where(
                            isNotNullOrEmpty(Fields.Phone.Number)
                            // or equalTo(Fields.Contact.HasPhoneNumber, true)
                    )
                    .find();
        }
    
        List<Contact> getContactsWithAtLeastOnePhoneNumberAndEmail() {
            return ContactsFactory.create(this)
                    .query()
                    .where(
                            and(
                                    isNotNullOrEmpty(Fields.Phone.Number),
                                    // or equalTo(Fields.Contact.HasPhoneNumber, true),
                                    isNotNullOrEmpty(Fields.Email.Address)
                            )
                    )
                    .find();
        }
    }
    ```

### [Insert contacts](./basics/insert-contacts.md)

=== "Kotlin"

    ```kotlin
    import android.accounts.Account
    import android.app.Activity
    import contacts.core.Contacts
    import contacts.core.Insert
    import contacts.core.entities.*
    import contacts.core.util.*
    
    class InsertContactsActivity : Activity() {
    
        fun insertContact(account: Account?, groupMembership: NewGroupMembership?): Insert.Result =
            Contacts(this)
                .insert()
                .forAccount(account)
                .rawContact {
                    addAddress {
                        street = "123 Abc street"
                        city = "Brooklyn"
                        region = "New York"
                        postcode = "11207"
                        country = "US"
                        type = AddressEntity.Type.WORK
                    }
                    addEmail {
                        address = "123@abc.com"
                        type = EmailEntity.Type.WORK
                    }
                    addEvent {
                        date = EventDate.from(1990, 0, 1)
                        type = EventEntity.Type.BIRTHDAY
                    }
                    if (groupMembership != null) {
                        addGroupMembership(groupMembership)
                    }
                    addIm {
                        data = "im@aol.com"
                        protocol = ImEntity.Protocol.CUSTOM
                        customProtocol = "AOL"
                    }
                    setName {
                        prefix = "Mr."
                        givenName = "Big"
                        middleName = "Bad"
                        familyName = "Fox"
                        suffix = "Jr"
                    }
                    setNickname {
                        name = "BIG BAD FOX"
                    }
                    setNote {
                        note = "This is one big bad fox!"
                    }
                    setOrganization {
                        company = "Bad company"
                        title = "Boss"
                        department = "The bad one"
                        jobDescription = "Be a big bad boss"
                        officeLocation = "It's a secret"
                    }
                    addPhone {
                        number = "(888) 123-4567"
                        type = PhoneEntity.Type.WORK
                    }
                    addRelation {
                        name = "Bro"
                        type = RelationEntity.Type.BROTHER
                    }
                    setSipAddress {
                        sipAddress = "sip:user@domain:port"
                    }
                    addWebsite {
                        url = "www.bigbadfox.com"
                    }
                }
                .commit()
    }
    ```

=== "Java"

    ```java
    import android.accounts.Account;
    import android.app.Activity;
    
    import contacts.core.ContactsFactory;
    import contacts.core.Insert;
    import contacts.core.entities.*;

    public class InsertContactsActivity extends Activity {
    
        Insert.Result insertContact(Account account, NewGroupMembership groupMembership) {
            NewAddress address = new NewAddress();
            address.setStreet("123 Abc street");
            address.setCity("Brooklyn");
            address.setRegion("New York");
            address.setPostcode("11207");
            address.setCountry("US");
            address.setType(AddressEntity.Type.WORK);
    
            NewEmail email = new NewEmail();
            email.setAddress("123@abc.com");
            email.setType(EmailEntity.Type.WORK);
    
            NewEvent event = new NewEvent();
            event.setDate(EventDate.from(1990, 0, 1));
            event.setType(EventEntity.Type.BIRTHDAY);
    
            NewIm im = new NewIm();
            im.setData("im@aol.com");
            im.setProtocol(ImEntity.Protocol.CUSTOM);
            im.setCustomProtocol("AOL");
    
            NewName name = new NewName();
            name.setPrefix("Mr.");
            name.setGivenName("Big");
            name.setMiddleName("Bad");
            name.setFamilyName("Fox");
            name.setSuffix("Jr");
    
            NewNickname nickname = new NewNickname();
            nickname.setName("BIG BAD FOX");
    
            NewNote note = new NewNote();
            note.setNote("This is one big bad fox!");
    
            NewOrganization organization = new NewOrganization();
            organization.setCompany("Bad company");
            organization.setTitle("Boss");
            organization.setDepartment("The bad one");
            organization.setJobDescription("Be a big bad boss");
            organization.setOfficeLocation("It's a secret");
    
            NewPhone phone = new NewPhone();
            phone.setNumber("(888) 123-4567");
            phone.setType(PhoneEntity.Type.WORK);
    
            NewRelation relation = new NewRelation();
            relation.setName("Bro");
            relation.setType(RelationEntity.Type.BROTHER);
    
            NewSipAddress sipAddress = new NewSipAddress();
            sipAddress.setSipAddress("sip:user@domain:port");
    
            NewWebsite website = new NewWebsite();
            website.setUrl("www.bigbadfox.com");
    
            NewRawContact rawContact = new NewRawContact();
            rawContact.getAddresses().add(address);
            rawContact.getEmails().add(email);
            rawContact.getEvents().add(event);
            if (groupMembership != null) {
                rawContact.getGroupMemberships().add(groupMembership);
            }
            rawContact.getIms().add(im);
            rawContact.setName(name);
            rawContact.setNickname(nickname);
            rawContact.setNote(note);
            rawContact.setOrganization(organization);
            rawContact.getPhones().add(phone);
            rawContact.getRelations().add(relation);
            rawContact.setSipAddress(sipAddress);
            rawContact.getWebsites().add(website);
    
            return ContactsFactory.create(this)
                    .insert()
                    .forAccount(account)
                    .rawContacts(rawContact)
                    .commit();
        }
    }
    ```

### [Update contacts](./basics/update-contacts.md)

=== "Kotlin"

    ```kotlin
    import android.app.Activity
    import contacts.core.Contacts
    import contacts.core.Update
    import contacts.core.entities.*
    import contacts.core.util.*
    
    class UpdateContactsActivity : Activity() {
    
        fun addEmail(contact: Contact): Update.Result =
            Contacts(this)
                .update()
                .contacts(contact.mutableCopy {
                    addEmail {
                        address = "321@xyz.com"
                        type = EmailEntity.Type.CUSTOM
                        label = "Personal"
                    }
                })
                .commit()
    
        fun addEmail(rawContact: RawContact): Update.Result =
            Contacts(this)
                .update()
                .rawContacts(rawContact.mutableCopy {
                    addEmail {
                        address = "321@xyz.com"
                        type = EmailEntity.Type.CUSTOM
                        label = "Personal"
                    }
                })
                .commit()
    
        fun addAnniversary(contact: Contact): Update.Result =
            Contacts(this)
                .update()
                .contacts(contact.mutableCopy {
                    addEvent {
                        date = EventDate.from(2016, 6, 14)
                        type = EventEntity.Type.ANNIVERSARY
                    }
                })
                .commit()
    
        fun setFullName(rawContact: RawContact): Update.Result =
            Contacts(this)
                .update()
                .rawContacts(rawContact.mutableCopy {
                    setName {
                        prefix = "Mr."
                        givenName = "Small"
                        middleName = "Bald"
                        familyName = "Eagle"
                        suffix = "Sr"
                    }
                })
                .commit()
    
        fun setGivenName(rawContact: RawContact): Update.Result =
            Contacts(this)
                .update()
                .rawContacts(rawContact.mutableCopy {
                    name = (name ?: NewName()).also { it.givenName = "Greg" }
                })
                .commit()
    
        fun removeGmailEmails(contact: Contact): Update.Result =
            Contacts(this)
                .update()
                .contacts(contact.mutableCopy {
                    emails()
                        .filter { it.address?.endsWith("@gmail.com", ignoreCase = true) == true }
                        .forEach { removeEmail(it) }
                })
                .commit()
    
        fun removeEmailsAndPhones(contact: Contact): Update.Result =
            Contacts(this)
                .update()
                .contacts(contact.mutableCopy {
                    removeAllEmails()
                    removeAllPhones()
                })
                .commit()
    }
    ```

=== "Java"

    ```java
    import android.app.Activity;
    
    import contacts.core.ContactsFactory;
    import contacts.core.Update;
    import contacts.core.entities.*;
    import contacts.core.util.ContactDataKt;
    
    public class UpdateContactsActivity extends Activity {
    
        Update.Result addEmail(Contact contact) {
            MutableContact mutableContact = contact.mutableCopy();
            ContactDataKt.addEmail(mutableContact, new NewEmail(
                    EmailEntity.Type.CUSTOM,
                    "Personal",
                    "321@xyz.com"
            ));
    
            return ContactsFactory.create(this)
                    .update()
                    .contacts(mutableContact)
                    .commit();
        }
    
        Update.Result addEmail(RawContact rawContact) {
            MutableRawContact mutableRawContact = rawContact.mutableCopy();
            mutableRawContact.getEmails().add(new NewEmail(
                    EmailEntity.Type.CUSTOM,
                    "Personal",
                    "321@xyz.com"
            ));
    
            return ContactsFactory.create(this)
                    .update()
                    .rawContacts(mutableRawContact)
                    .commit();
        }
    
        Update.Result addAnniversary(Contact contact) {
            MutableContact mutableContact = contact.mutableCopy();
            ContactDataKt.addEvent(mutableContact, new NewEvent(
                    EventEntity.Type.ANNIVERSARY,
                    null,
                    EventDate.from(2016, 6, 14)
            ));
    
            return ContactsFactory.create(this)
                    .update()
                    .contacts(mutableContact)
                    .commit();
        }
    
        Update.Result setFullName(RawContact rawContact) {
            NewName name = new NewName();
            name.setPrefix("Mr.");
            name.setGivenName("Small");
            name.setMiddleName("Bald");
            name.setFamilyName("Eagle");
            name.setSuffix("Sr");
    
            MutableRawContact mutableRawContact = rawContact.mutableCopy();
            mutableRawContact.setName(name);
    
            return ContactsFactory.create(this)
                    .update()
                    .rawContacts(mutableRawContact)
                    .commit();
        }
    
        Update.Result setGivenName(RawContact rawContact) {
            MutableRawContact mutableRawContact = rawContact.mutableCopy();
            if (mutableRawContact.getName() != null) {
                mutableRawContact.getName().setGivenName("Greg");
            } else {
                NewName name = new NewName();
                name.setGivenName("Greg");
                mutableRawContact.setName(name);
            }
    
            return ContactsFactory.create(this)
                    .update()
                    .rawContacts(mutableRawContact)
                    .commit();
        }
    
        Update.Result removeGmailEmails(Contact contact) {
            MutableContact mutableContact = contact.mutableCopy();
            for (MutableEmailEntity email : ContactDataKt.emailList(mutableContact)) {
                String emailAddress = email.getAddress();
                if (emailAddress != null && emailAddress.toLowerCase().endsWith("@gmail.com")) {
                    ContactDataKt.removeEmail(mutableContact, email);
                }
            }
    
            return ContactsFactory.create(this)
                    .update()
                    .contacts(mutableContact)
                    .commit();
        }
    
        Update.Result removeEmailsAndPhones(Contact contact) {
            MutableContact mutableContact = contact.mutableCopy();
            ContactDataKt.removeAllEmails(mutableContact);
            ContactDataKt.removeAllPhones(mutableContact);
    
            return ContactsFactory.create(this)
                    .update()
                    .contacts(mutableContact)
                    .commit();
        }
    }
    ```

### [Delete contacts](./basics/delete-contacts.md)

=== "Kotlin"

    ```kotlin
    import android.app.Activity
    import contacts.core.*
    import contacts.core.entities.Contact
    import contacts.core.entities.RawContact
    
    class DeleteContactsActivity : Activity() {
    
        fun deleteContact(contact: Contact): Delete.Result = Contacts(this)
            .delete()
            .contacts(contact)
            .commit()
    
        fun deleteContactWithId(contactId: Long): Delete.Result = Contacts(this)
            .delete()
            .contactsWithId(contactId)
            .commit()
    
        fun deleteNonFavoriteContactsThatHaveANote(): Delete.Result = Contacts(this)
            .delete()
            .contactsWhereData {
                (Contact.Options.Starred equalTo false) and Note.Note.isNotNullOrEmpty()
            }
            .commit()
    
        fun deleteRawContact(rawContact: RawContact): Delete.Result = Contacts(this)
            .delete()
            .rawContacts(rawContact)
            .commit()
    
        fun deleteRawContactWithId(rawContactId: Long): Delete.Result = Contacts(this)
            .delete()
            .rawContactsWithId(rawContactId)
            .commit()
    
        fun deleteRawContactsInTheSetThatHaveANote(rawContactIds: Set<Long>): Delete.Result =
            Contacts(this)
                .delete()
                .rawContactsWhereData {
                    (RawContact.Id `in` rawContactIds) and Note.Note.isNotNullOrEmpty()
                }
                .commit()
    }
    ```

=== "Java"

    ```java
    import static contacts.core.WhereKt.*;
    
    import android.app.Activity;
    
    import java.util.Set;
    
    import contacts.core.ContactsFactory;
    import contacts.core.Delete;
    import contacts.core.Fields;
    import contacts.core.entities.Contact;
    import contacts.core.entities.RawContact;
    
    public class DeleteContactsActivity extends Activity {
    
        Delete.Result deleteContact(Contact contact) {
            return ContactsFactory.create(this)
                    .delete()
                    .contacts(contact)
                    .commit();
        }
    
        Delete.Result deleteContactWithId(Long contactId) {
            return ContactsFactory.create(this)
                    .delete()
                    .contactsWithId(contactId)
                    .commit();
        }
    
        Delete.Result deleteNonFavoriteContactsThatHaveANote() {
            return ContactsFactory.create(this)
                    .delete()
                    .contactsWhereData(
                            and(
                                    equalTo(Fields.Contact.Options.Starred, false),
                                    isNotNullOrEmpty(Fields.Note.Note)
                            )
                    )
                    .commit();
        }
    
        Delete.Result deleteRawContact(RawContact rawContact) {
            return ContactsFactory.create(this)
                    .delete()
                    .rawContacts(rawContact)
                    .commit();
        }
    
        Delete.Result deleteRawContactWithId(Long rawContactId) {
            return ContactsFactory.create(this)
                    .delete()
                    .rawContactsWithId(rawContactId)
                    .commit();
        }
    
        Delete.Result deleteRawContactsInTheSetThatHaveANote(Set<Long> rawContactIds) {
            return ContactsFactory.create(this)
                    .delete()
                    .rawContactsWhereData(
                            and(
                                    in(Fields.RawContact.Id, rawContactIds),
                                    isNotNullOrEmpty(Fields.Note.Note)
                            )
                    )
                    .commit();
        }
    }
    ```

----------------------------------------------------------------------------------------------------

## Data

### [Query specific data kinds](./data/query-data-sets.md)

=== "Kotlin"

    ```kotlin
    import android.accounts.Account
    import android.app.Activity
    import contacts.core.*
    import contacts.core.entities.*
    
    class QueryDataActivity : Activity() {
    
        fun getAllEmails(): List<Email> = Contacts(this).data().query().emails().find()
    
        fun getEmailsForAccount(account: Account): List<Email> =
            Contacts(this).data().query().emails().accounts(account).find()
    
        fun getGmailEmailsInDescendingOrder(): List<Email> = Contacts(this)
            .data()
            .query()
            .emails()
            .where { Email.Address endsWith "@gmail.com" }
            .orderBy(Fields.Email.Address.desc(ignoreCase = true))
            .find()
    
        fun getWorkPhones(): List<Phone> = Contacts(this)
            .data()
            .query()
            .phones()
            .where { Phone.Type equalTo PhoneEntity.Type.WORK }
            .find()
    
        fun getUpTo10Mothers(): List<Relation> = Contacts(this)
            .data()
            .query()
            .relations()
            .where { Relation.Type equalTo RelationEntity.Type.MOTHER }
            .limit(10)
            .find()
    
        fun getContactBirthday(contactId: Long): Event? = Contacts(this)
            .data()
            .query()
            .events()
            .where { (Contact.Id equalTo contactId) and (Event.Type equalTo EventEntity.Type.BIRTHDAY) }
            .find()
            .firstOrNull()
    }
    ```

=== "Java"

    ```java
    import static contacts.core.OrderByKt.desc;
    import static contacts.core.WhereKt.*;
    
    import android.accounts.Account;
    import android.app.Activity;
    
    import java.util.List;
    
    import contacts.core.ContactsFactory;
    import contacts.core.Fields;
    import contacts.core.entities.*;
    
    public class QueryDataActivity extends Activity {
    
        List<Email> getAllEmails() {
            return ContactsFactory.create(this).data().query().emails().find();
        }
    
        List<Email> getEmailsForAccount(Account account) {
            return ContactsFactory.create(this).data().query().emails().accounts(account).find();
        }
    
        List<Email> getGmailEmailsInDescendingOrder() {
            return ContactsFactory.create(this)
                    .data()
                    .query()
                    .emails()
                    .where(endsWith(Fields.Email.Address, "@gmail.com"))
                    .orderBy(desc(Fields.Email.Address, true))
                    .find();
        }
    
        List<Phone> getWorkPhones() {
            return ContactsFactory.create(this)
                    .data()
                    .query()
                    .phones()
                    .where(equalTo(Fields.Phone.Type, PhoneEntity.Type.WORK))
                    .find();
        }
    
        List<Relation> getUpTo10Mothers() {
            return ContactsFactory.create(this)
                    .data()
                    .query()
                    .relations()
                    .where(equalTo(Fields.Relation.Type, RelationEntity.Type.MOTHER))
                    .limit(10)
                    .find();
        }
    
        Event getContactBirthday(Long contactId) {
            return ContactsFactory.create(this)
                    .data()
                    .query()
                    .events()
                    .where(
                            and(
                                    equalTo(Fields.Contact.Id, contactId),
                                    equalTo(Fields.Event.Type, EventEntity.Type.BIRTHDAY)
                            )
                    )
                    .find()
                    .get(0);
        }
    }
    ```

### [Update existing sets of data](./data/update-data-sets.md)

=== "Kotlin"

    ```kotlin
    import android.app.Activity
    import contacts.core.Contacts
    import contacts.core.Fields
    import contacts.core.data.DataUpdate
    import contacts.core.entities.*
    
    class UpdateDataActivity : Activity() {
    
        fun updateData(data: ExistingDataEntity): DataUpdate.Result =
            Contacts(this).data().update().data(data).commit()
    
        fun updateEmailAndPhone(email: Email, phone: Phone): DataUpdate.Result = Contacts(this)
            .data()
            .update()
            .data(
                email.mutableCopy {
                    address = "myemail@email.com"
                },
                phone.mutableCopy {
                    number = "(555) 555-5555"
                }
            )
            .commit()
    
        fun updateOnlyMiddleName(changedName: MutableName): DataUpdate.Result = Contacts(this)
            .data()
            .update()
            .data(changedName)
            .include(Fields.Name.MiddleName)
            .commit()
    }
    ```

=== "Java"

    ```java
    import android.app.Activity;
    
    import contacts.core.ContactsFactory;
    import contacts.core.Fields;
    import contacts.core.data.DataUpdate;
    import contacts.core.entities.*;
    
    public class UpdateDataActivity extends Activity {
    
        DataUpdate.Result updateData(ExistingDataEntity data) {
            return ContactsFactory.create(this).data().update().data(data).commit();
        }
    
        DataUpdate.Result updateEmailAndPhone(Email email, Phone phone) {
            MutableEmail mutableEmail = email.mutableCopy();
            mutableEmail.setAddress("myemail@email.com");
    
            MutablePhone mutablePhone = phone.mutableCopy();
            mutablePhone.setNumber("(555) 555-5555");
    
            return ContactsFactory.create(this)
                    .data()
                    .update()
                    .data(mutableEmail, mutablePhone)
                    .commit();
        }
    
        DataUpdate.Result updateOnlyMiddleName(MutableName changedName) {
            return ContactsFactory.create(this)
                    .data()
                    .update()
                    .data(changedName)
                    .include(Fields.Name.MiddleName)
                    .commit();
        }
    }
    ```

### [Delete existing sets of data](./data/delete-data-sets.md)

=== "Kotlin"

    ```kotlin
    import android.app.Activity
    import contacts.core.Contacts
    import contacts.core.data.DataDelete
    import contacts.core.entities.*
    import contacts.core.equalTo
    
    class DeleteDataActivity : Activity() {
    
        fun deleteData(data: ExistingDataEntity): DataDelete.Result =
            Contacts(this).data().delete().data(data).commit()
    
        fun deleteEmailsAndPhones(emails: Set<Email>, phones: Set<Phone>): DataDelete.Result =
            Contacts(this)
                .data()
                .delete()
                .data(emails + phones)
                .commit()
    
        fun deleteDataWithId(dataId: Long): DataDelete.Result =
            Contacts(this).data().delete().dataWithId(dataId).commit()
    
        fun deleteAllWorkEmails(): DataDelete.Result =
            Contacts(this)
                .data()
                .delete()
                .dataWhere {
                    Email.Type equalTo EmailEntity.Type.WORK
                }
                .commit()
    }
    ```

=== "Java"

    ```java
    import static contacts.core.WhereKt.equalTo;
    
    import android.app.Activity;
    
    import java.util.ArrayList;
    import java.util.List;
    
    import contacts.core.ContactsFactory;
    import contacts.core.Fields;
    import contacts.core.data.DataDelete;
    import contacts.core.entities.*;
    
    public class DeleteActivity extends Activity {
    
        DataDelete.Result deleteData(ExistingDataEntity data) {
            return ContactsFactory.create(this).data().delete().data(data).commit();
        }
    
        DataDelete.Result deleteEmailsAndPhones(List<Email> emails, List<Phone> phones) {
            List<ExistingDataEntity> dataSet = new ArrayList<>();
            dataSet.addAll(emails);
            dataSet.addAll(phones);
    
            return ContactsFactory.create(this)
                    .data()
                    .delete()
                    .data(dataSet)
                    .commit();
        }
    
        DataDelete.Result deleteDataWithId(Long dataId) {
            return ContactsFactory.create(this).data().delete().dataWithId(dataId).commit();
        }
    
        DataDelete.Result deleteAllWorkEmails() {
            return ContactsFactory.create(this)
                    .data()
                    .delete()
                    .dataWhere(
                            equalTo(Fields.Email.Type, EmailEntity.Type.WORK)
                    )
                    .commit();
        }
    }
    ```

----------------------------------------------------------------------------------------------------

## Custom data

### [Integrate the Google Contacts custom data](./customdata/integrate-googlecontacts-custom-data.md)

=== "Kotlin"

    ```kotlin
    import android.app.Activity
    import contacts.core.*
    import contacts.core.data.*
    import contacts.core.entities.*
    import contacts.core.entities.custom.CustomDataRegistry
    import contacts.entities.custom.googlecontacts.*
    import contacts.entities.custom.googlecontacts.fileas.*
    import contacts.entities.custom.googlecontacts.userdefined.*
    
    class IntegrateGoogleContactsCustomDataActivity : Activity() {
    
        val contacts = Contacts(this, CustomDataRegistry().register(GoogleContactsRegistration()))
    
        fun getContactsWithGoogleContactsCustomData(): List<Contact> = contacts
            .query()
            .where {
                GoogleContactsFields.FileAs.Name.isNotNull()
                    .or(GoogleContactsFields.UserDefined.Field.isNotNull())
            }
            .find()
    
        fun insertRawContactWithGoogleContactsCustomData(): Insert.Result = contacts
            .insert()
            .rawContact {
                setFileAs(contacts) {
                    name = "Lucky"
                }
                addUserDefined(contacts) {
                    field = "Lucky Field"
                    label = "Lucky Label"
                }
            }
            .commit()
    
        fun updateRawContactGoogleContactsCustomData(rawContact: RawContact): Update.Result = contacts
            .update()
            .rawContacts(
                rawContact.mutableCopy {
                    fileAs(contacts)?.name = "Unfortunate"
                    userDefined(contacts).firstOrNull()?.apply {
                        field = "Unfortunate Field"
                        label = "Unfortunate Label"
                    }
                }
            )
            .commit()
    
        fun deleteGoogleContactsCustomDataFromRawContact(rawContact: RawContact): Update.Result =
            contacts
                .update()
                .rawContacts(
                    rawContact.mutableCopy {
                        setFileAs(contacts, null)
                        removeAllUserDefined(contacts)
                    }
                )
                .commit()
    
        fun getAllFileAs(): List<FileAs> = contacts.data().query().fileAs().find()
    
        fun getAllUserDefined(): List<UserDefined> = contacts.data().query().userDefined().find()
    
        fun updateFileAsAndUserDefined(
            fileAs: MutableFileAs, userDefined: MutableUserDefined
        ): DataUpdate.Result = contacts.data().update().data(fileAs, userDefined).commit()
    
        fun deleteFileAsAndUserDefined(fileAs: FileAs, userDefined: UserDefined): DataDelete.Result =
            contacts.data().delete().data(fileAs, userDefined).commit()
    }
    ```

=== "Java"

    ```java
    import static contacts.core.WhereKt.*;
    
    import android.app.Activity;
    
    import java.util.List;
    
    import contacts.core.*;
    import contacts.core.data.*;
    import contacts.core.entities.*;
    import contacts.core.entities.custom.CustomDataRegistry;
    import contacts.entities.custom.googlecontacts.*;
    import contacts.entities.custom.googlecontacts.fileas.*;
    import contacts.entities.custom.googlecontacts.userdefined.*;
    
    public class IntegrateGoogleContactsCustomDataActivity extends Activity {
    
        Contacts contacts = ContactsFactory.create(
                this, new CustomDataRegistry().register(new GoogleContactsRegistration())
        );
    
        List<Contact> getContactsWithGoogleContactsCustomData() {
            return contacts
                    .query()
                    .where(
                            or(
                                    isNotNull(GoogleContactsFields.FileAs.Name),
                                    isNotNull(GoogleContactsFields.UserDefined.Field)
                            )
                    )
                    .find();
        }
    
        Insert.Result insertRawContactWithGoogleContactsCustomData() {
            NewFileAs newFileAs = new NewFileAs("Lucky");
            NewUserDefined newUserDefined = new NewUserDefined("Lucky Field", "Lucky Label");
    
            NewRawContact newRawContact = new NewRawContact();
            RawContactFileAsKt.setFileAs(newRawContact, contacts, newFileAs);
            RawContactUserDefinedKt.addUserDefined(newRawContact, contacts, newUserDefined);
    
            return contacts
                    .insert()
                    .rawContacts(newRawContact)
                    .commit();
        }
    
        Update.Result updateRawContactGoogleContactsCustomData(RawContact rawContact) {
            MutableRawContact mutableRawContact = rawContact.mutableCopy();
            MutableFileAsEntity mutableFileAs = RawContactFileAsKt.fileAs(mutableRawContact, contacts);
            MutableUserDefinedEntity mutableUserDefined = 
                    RawContactUserDefinedKt.userDefinedList(mutableRawContact, contacts).get(0);
    
            if (mutableFileAs != null) {
                mutableFileAs.setName("Unfortunate");
            }
            if (mutableUserDefined != null) {
                mutableUserDefined.setField("Unfortunate Field");
                mutableUserDefined.setLabel("Unfortunate Label");
            }
    
            return contacts
                    .update()
                    .rawContacts(mutableRawContact)
                    .commit();
        }
    
        Update.Result deleteGoogleContactsCustomDataFromRawContact(RawContact rawContact) {
            MutableRawContact mutableRawContact = rawContact.mutableCopy();
            RawContactFileAsKt.setFileAs(mutableRawContact, contacts, (MutableFileAsEntity) null);
            RawContactUserDefinedKt.removeAllUserDefined(mutableRawContact, contacts);
    
            return contacts
                    .update()
                    .rawContacts(mutableRawContact)
                    .commit();
        }
    
        List<FileAs> getAllFileAs() {
            return FileAsDataQueryKt.fileAs(contacts.data().query()).find();
        }
    
        List<UserDefined> getAllUserDefined() {
            return UserDefinedDataQueryKt.userDefined(contacts.data().query()).find();
        }
    
        DataUpdate.Result updateFileAsAndUserDefined(
                MutableFileAs fileAs, MutableUserDefined userDefined
        ) {
            return contacts.data().update().data(fileAs, userDefined).commit();
        }
    
        DataDelete.Result deleteFileAsAndUserDefined(
                FileAs fileAs, UserDefined userDefined
        ) {
            return contacts.data().delete().data(fileAs, userDefined).commit();
        }
    }
    ```

### [Integrate the Gender custom data](./customdata/integrate-gender-custom-data.md)

=== "Kotlin"

    ```kotlin
    import android.app.Activity
    import contacts.core.*
    import contacts.core.data.*
    import contacts.core.entities.*
    import contacts.core.entities.custom.CustomDataRegistry
    import contacts.entities.custom.gender.*
    
    class IntegrateGenderCustomDataActivity : Activity() {
    
        val contacts = Contacts(this, CustomDataRegistry().register(GenderRegistration()))
    
        fun getContactsWithGenderCustomData(): List<Contact> = contacts
            .query()
            .where { GenderFields.Type.isNotNull() }
            .find()
    
        fun insertRawContactWithGenderCustomData(): Insert.Result = contacts
            .insert()
            .rawContact {
                setGender(contacts) {
                    type = GenderEntity.Type.MALE
                }
            }
            .commit()
    
        fun updateRawContactGenderCustomData(rawContact: RawContact): Update.Result = contacts
            .update()
            .rawContacts(
                rawContact.mutableCopy {
                    gender(contacts)?.type = GenderEntity.Type.FEMALE
                }
            )
            .commit()
    
        fun deleteGenderCustomDataFromRawContact(rawContact: RawContact): Update.Result =
            contacts
                .update()
                .rawContacts(
                    rawContact.mutableCopy {
                        setGender(contacts, null)
                    }
                )
                .commit()
    
        fun getAllGender(): List<Gender> = contacts.data().query().genders().find()
    
        fun updateGender(gender: MutableGender): DataUpdate.Result =
            contacts.data().update().data(gender).commit()
    
        fun deleteGender(gender: Gender): DataDelete.Result =
            contacts.data().delete().data(gender).commit()
    }
    ```

=== "Java"

    ```java
    import static contacts.core.WhereKt.isNotNull;
    
    import android.app.Activity;
    
    import java.util.List;
    
    import contacts.core.*;
    import contacts.core.data.*;
    import contacts.core.entities.*;
    import contacts.core.entities.custom.CustomDataRegistry;
    import contacts.entities.custom.gender.*;
    
    public class IntegrateGenderCustomDataActivity extends Activity {
    
        Contacts contacts = ContactsFactory.create(
                this, new CustomDataRegistry().register(new GenderRegistration())
        );
    
        List<Contact> getContactsWithGenderCustomData() {
            return contacts
                    .query()
                    .where(isNotNull(GenderFields.Type))
                    .find();
        }
    
        Insert.Result insertRawContactWithGenderCustomData() {
            NewGender newGender = new NewGender(GenderEntity.Type.MALE);
    
            NewRawContact newRawContact = new NewRawContact();
            RawContactGenderKt.setGender(newRawContact, contacts, newGender);
    
            return contacts
                    .insert()
                    .rawContacts(newRawContact)
                    .commit();
        }
    
        Update.Result updateRawContactGenderCustomData(RawContact rawContact) {
            MutableRawContact mutableRawContact = rawContact.mutableCopy();
            MutableGenderEntity mutableGender = RawContactGenderKt.gender(mutableRawContact, contacts);
            if (mutableGender != null) {
                mutableGender.setType(GenderEntity.Type.FEMALE);
            }
    
            return contacts
                    .update()
                    .rawContacts(mutableRawContact)
                    .commit();
        }
    
        Update.Result deleteGenderCustomDataFromRawContact(RawContact rawContact) {
            MutableRawContact mutableRawContact = rawContact.mutableCopy();
            RawContactGenderKt.setGender(mutableRawContact, contacts, (MutableGenderEntity) null);
    
            return contacts
                    .update()
                    .rawContacts(mutableRawContact)
                    .commit();
        }
    
        List<Gender> getAllGenders() {
            return GenderDataQueryKt.genders(contacts.data().query()).find();
        }
    
        DataUpdate.Result updateGender(MutableGender gender) {
            return contacts.data().update().data(gender).commit();
        }
    
        DataDelete.Result deleteGender(Gender gender) {
            return contacts.data().delete().data(gender).commit();
        }
    }
    ```

### [Integrate the Handle Name custom data](./customdata/integrate-handlename-custom-data.md)

=== "Kotlin"

    ```kotlin
    import android.app.Activity
    import contacts.core.*
    import contacts.core.data.*
    import contacts.core.entities.*
    import contacts.core.entities.custom.CustomDataRegistry
    import contacts.entities.custom.handlename.*
    
    class IntegrateHandleNameCustomDataActivity : Activity() {
    
        val contacts = Contacts(this, CustomDataRegistry().register(HandleNameRegistration()))
    
        fun getContactsWithHandleNameCustomData(): List<Contact> = contacts
            .query()
            .where { HandleNameFields.Handle.isNotNull() }
            .find()
    
        fun insertRawContactWithHandleNameCustomData(): Insert.Result = contacts
            .insert()
            .rawContact {
                addHandleName(contacts) {
                    handle = "The Beauty"
                }
            }
            .commit()
    
        fun updateRawContactHandleNameCustomData(rawContact: RawContact): Update.Result = contacts
            .update()
            .rawContacts(
                rawContact.mutableCopy {
                    handleNames(contacts).firstOrNull()?.handle = "The Beast"
                }
            )
            .commit()
    
        fun deleteHandleNameCustomDataFromRawContact(rawContact: RawContact): Update.Result =
            contacts
                .update()
                .rawContacts(
                    rawContact.mutableCopy {
                        removeAllHandleNames(contacts)
                    }
                )
                .commit()
    
        fun getAllHandleName(): List<HandleName> = contacts.data().query().handleNames().find()
    
        fun updateHandleName(handleName: MutableHandleName): DataUpdate.Result =
            contacts.data().update().data(handleName).commit()
    
        fun deleteHandleName(handleName: HandleName): DataDelete.Result =
            contacts.data().delete().data(handleName).commit()
    }
    ```

=== "Java"

    ```java
    import static contacts.core.WhereKt.isNotNull;
    
    import android.app.Activity;
    
    import java.util.List;
    
    import contacts.core.*;
    import contacts.core.data.*;
    import contacts.core.entities.*;
    import contacts.core.entities.custom.CustomDataRegistry;
    import contacts.entities.custom.handlename.*;
    
    public class IntegrateHandleNameCustomDataActivity extends Activity {
    
        Contacts contacts = ContactsFactory.create(
                this, new CustomDataRegistry().register(new HandleNameRegistration())
        );
    
        List<Contact> getContactsWithHandleNameCustomData() {
            return contacts
                    .query()
                    .where(isNotNull(HandleNameFields.Handle))
                    .find();
        }
    
        Insert.Result insertRawContactWithHandleNameCustomData() {
            NewHandleName newHandleName = new NewHandleName("The Beauty");
    
            NewRawContact newRawContact = new NewRawContact();
            RawContactHandleNameKt.addHandleName(newRawContact, contacts, newHandleName);
    
            return contacts
                    .insert()
                    .rawContacts(newRawContact)
                    .commit();
        }
    
        Update.Result updateRawContactHandleNameCustomData(RawContact rawContact) {
            MutableRawContact mutableRawContact = rawContact.mutableCopy();
            MutableHandleNameEntity mutableHandleName =
                    RawContactHandleNameKt.handleNameList(mutableRawContact, contacts).get(0);
            if (mutableHandleName != null) {
                mutableHandleName.setHandle("The Beast");
            }
    
            return contacts
                    .update()
                    .rawContacts(mutableRawContact)
                    .commit();
        }
    
        Update.Result deleteHandleNameCustomDataFromRawContact(RawContact rawContact) {
            MutableRawContact mutableRawContact = rawContact.mutableCopy();
            RawContactHandleNameKt.removeAllHandleNames(mutableRawContact, contacts);
    
            return contacts
                    .update()
                    .rawContacts(mutableRawContact)
                    .commit();
        }
    
        List<HandleName> getAllHandleNames() {
            return HandleNameDataQueryKt.handleNames(contacts.data().query()).find();
        }
    
        DataUpdate.Result updateHandleName(MutableHandleName handleName) {
            return contacts.data().update().data(handleName).commit();
        }
    
        DataDelete.Result deleteHandleName(HandleName handleName) {
            return contacts.data().delete().data(handleName).commit();
        }
    }
    ```

### [Integrate the Pokemon custom data](./customdata/integrate-pokemon-custom-data.md)

=== "Kotlin"

    ```kotlin
    import android.app.Activity
    import contacts.core.*
    import contacts.core.data.*
    import contacts.core.entities.*
    import contacts.core.entities.custom.CustomDataRegistry
    import contacts.entities.custom.pokemon.*
    
    class IntegratePokemonCustomDataActivity : Activity() {
    
        val contacts = Contacts(this, CustomDataRegistry().register(PokemonRegistration()))
    
        fun getContactsWithPokemonCustomData(): List<Contact> = contacts
            .query()
            .where { PokemonFields.Name.isNotNull() or PokemonFields.PokeApiId.isNotNull() }
            .find()
    
        fun insertRawContactWithPokemonCustomData(): Insert.Result = contacts
            .insert()
            .rawContact {
                addPokemon(contacts) {
                    name = "ditto"
                    nickname = "copy-cat"
                    level = 24
                    pokeApiId = 132
                }
            }
            .commit()
    
        fun updateRawContactPokemonCustomData(rawContact: RawContact): Update.Result = contacts
            .update()
            .rawContacts(
                rawContact.mutableCopy {
                    pokemons(contacts).firstOrNull()?.apply {
                        nickname = "OP"
                        level = 99
                    }
                }
            )
            .commit()
    
        fun deletePokemonCustomDataFromRawContact(rawContact: RawContact): Update.Result =
            contacts
                .update()
                .rawContacts(
                    rawContact.mutableCopy {
                        removeAllPokemons(contacts)
                    }
                )
                .commit()
    
        fun getAllPokemon(): List<Pokemon> = contacts.data().query().pokemons().find()
    
        fun updatePokemon(pokemon: MutablePokemon): DataUpdate.Result =
            contacts.data().update().data(pokemon).commit()
    
        fun deletePokemon(pokemon: Pokemon): DataDelete.Result =
            contacts.data().delete().data(pokemon).commit()
    }
    ```

=== "Java"

    ```java
    import static contacts.core.WhereKt.*;
    
    import android.app.Activity;
    
    import java.util.List;
    
    import contacts.core.*;
    import contacts.core.data.*;
    import contacts.core.entities.*;
    import contacts.core.entities.custom.CustomDataRegistry;
    import contacts.entities.custom.pokemon.*;
    
    public class IntegratePokemonCustomDataActivity extends Activity {
    
        Contacts contacts = ContactsFactory.create(
                this, new CustomDataRegistry().register(new PokemonRegistration())
        );
    
        List<Contact> getContactsWithPokemonCustomData() {
            return contacts
                    .query()
                    .where(or(isNotNull(PokemonFields.Name), isNotNull(PokemonFields.PokeApiId)))
                    .find();
        }
    
        Insert.Result insertRawContactWithPokemonCustomData() {
            NewPokemon newPokemon = new NewPokemon();
            newPokemon.setName("ditto");
            newPokemon.setNickname("copy-cat");
            newPokemon.setLevel(24);
            newPokemon.setPokeApiId(132);
    
            NewRawContact newRawContact = new NewRawContact();
            RawContactPokemonKt.addPokemon(newRawContact, contacts, newPokemon);
    
            return contacts
                    .insert()
                    .rawContacts(newRawContact)
                    .commit();
        }
    
        Update.Result updateRawContactPokemonCustomData(RawContact rawContact) {
            MutableRawContact mutableRawContact = rawContact.mutableCopy();
            MutablePokemonEntity mutablePokemon =
                    RawContactPokemonKt.pokemonList(mutableRawContact, contacts).get(0);
            if (mutablePokemon != null) {
                mutablePokemon.setNickname("OP");
                mutablePokemon.setLevel(99);
            }
    
            return contacts
                    .update()
                    .rawContacts(mutableRawContact)
                    .commit();
        }
    
        Update.Result deletePokemonCustomDataFromRawContact(RawContact rawContact) {
            MutableRawContact mutableRawContact = rawContact.mutableCopy();
            RawContactPokemonKt.removeAllPokemons(mutableRawContact, contacts);
    
            return contacts
                    .update()
                    .rawContacts(mutableRawContact)
                    .commit();
        }
    
        List<Pokemon> getAllPokemons() {
            return PokemonDataQueryKt.pokemons(contacts.data().query()).find();
        }
    
        DataUpdate.Result updatePokemon(MutablePokemon pokemon) {
            return contacts.data().update().data(pokemon).commit();
        }
    
        DataDelete.Result deletePokemon(Pokemon pokemon) {
            return contacts.data().delete().data(pokemon).commit();
        }
    }
    ```

### [Integrate the RPG custom data](./customdata/integrate-rpg-custom-data.md)

=== "Kotlin"

    ```kotlin
    import android.app.Activity
    import contacts.core.*
    import contacts.core.data.*
    import contacts.core.entities.*
    import contacts.core.entities.custom.CustomDataRegistry
    import contacts.entities.custom.rpg.*
    import contacts.entities.custom.rpg.profession.*
    import contacts.entities.custom.rpg.stats.*
    
    class IntegrateRpgCustomDataActivity : Activity() {
    
        val contacts = Contacts(this, CustomDataRegistry().register(RpgRegistration()))
    
        fun getContactsWithRpgCustomData(): List<Contact> = contacts
            .query()
            .where {
                RpgFields.Profession.Title.isNotNull() or RpgFields.Stats.Level.isNotNull()
            }
            .find()
    
        fun insertRawContactWithRpgCustomData(): Insert.Result = contacts
            .insert()
            .rawContact {
                setRpgProfession(contacts) {
                    title = "Berserker"
                }
                setRpgStats(contacts) {
                    level = 78
                    speed = 500
                    strength = 789
                    intelligence = 123
                    luck = 369
                }
            }
            .commit()
    
        fun updateRawContactRpgCustomData(rawContact: RawContact): Update.Result = contacts
            .update()
            .rawContacts(
                rawContact.mutableCopy {
                    rpgProfession(contacts)?.title = "Mage"
                    rpgStats(contacts)?.apply {
                        speed = 250
                        strength = 69
                        intelligence = 863
                    }
                }
            )
            .commit()
    
        fun deleteRpgCustomDataFromRawContact(rawContact: RawContact): Update.Result =
            contacts
                .update()
                .rawContacts(
                    rawContact.mutableCopy {
                        setRpgProfession(contacts, null)
                        setRpgStats(contacts, null)
                    }
                )
                .commit()
    
        fun getAllRpgProfessions(): List<RpgProfession> = contacts.data().query().rpgProfession().find()
    
        fun getAllRpgStats(): List<RpgStats> = contacts.data().query().rpgStats().find()
    
        fun updateRpgProfessionAndStats(
            profession: RpgProfession, rpgStats: RpgStats
        ): DataUpdate.Result = contacts.data().update().data(profession, rpgStats).commit()
    
        fun deleteFileAsAndUserDefined(
            profession: RpgProfession, rpgStats: RpgStats
        ): DataDelete.Result = contacts.data().delete().data(profession, rpgStats).commit()
    }
    ```

=== "Java"

    ```java
    import static contacts.core.WhereKt.*;
    
    import android.app.Activity;
    
    import java.util.List;
    
    import contacts.core.*;
    import contacts.core.data.*;
    import contacts.core.entities.*;
    import contacts.core.entities.custom.CustomDataRegistry;
    import contacts.entities.custom.rpg.*;
    import contacts.entities.custom.rpg.profession.*;
    import contacts.entities.custom.rpg.stats.*;
    
    public class IntegrateRpgCustomDataActivity extends Activity {
    
        Contacts contacts = ContactsFactory.create(
                this, new CustomDataRegistry().register(new RpgRegistration())
        );
    
        List<Contact> getContactsWithRpgCustomData() {
            return contacts
                    .query()
                    .where(or(isNotNull(RpgFields.Profession.Title), isNotNull(RpgFields.Stats.Level)))
                    .find();
        }
    
        Insert.Result insertRawContactWithRpgCustomData() {
            NewRpgProfession newRpgProfession = new NewRpgProfession("Berserker");
            NewRpgStats newRpgStats = new NewRpgStats();
            newRpgStats.setLevel(78);
            newRpgStats.setSpeed(500);
            newRpgStats.setStrength(789);
            newRpgStats.setIntelligence(123);
            newRpgStats.setLuck(369);
    
            NewRawContact newRawContact = new NewRawContact();
            RawContactRpgProfessionKt.setRpgProfession(newRawContact, contacts, newRpgProfession);
            RawContactRpgStatsKt.setRpgStats(newRawContact, contacts, newRpgStats);
    
            return contacts
                    .insert()
                    .rawContacts(newRawContact)
                    .commit();
        }
    
        Update.Result updateRawContactRpgCustomData(RawContact rawContact) {
            MutableRawContact mutableRawContact = rawContact.mutableCopy();
            MutableRpgProfessionEntity mutableRpgProfession =
                    RawContactRpgProfessionKt.rpgProfession(mutableRawContact, contacts);
            MutableRpgStatsEntity mutableRpgStats =
                    RawContactRpgStatsKt.rpgStats(mutableRawContact, contacts);
    
            if (mutableRpgProfession != null) {
                mutableRpgProfession.setTitle("Mage");
            }
            if (mutableRpgStats != null) {
                mutableRpgStats.setSpeed(250);
                mutableRpgStats.setStrength(69);
                mutableRpgStats.setIntelligence(863);
            }
    
            return contacts
                    .update()
                    .rawContacts(mutableRawContact)
                    .commit();
        }
    
        Update.Result deleteRpgCustomDataFromRawContact(RawContact rawContact) {
            MutableRawContact mutableRawContact = rawContact.mutableCopy();
            RawContactRpgProfessionKt.setRpgProfession(mutableRawContact, contacts, (MutableRpgProfession) null);
            RawContactRpgStatsKt.setRpgStats(mutableRawContact, contacts, (MutableRpgStats) null);
    
            return contacts
                    .update()
                    .rawContacts(mutableRawContact)
                    .commit();
        }
    
        List<RpgProfession> getAllRpgProfessions() {
            return RpgProfessionDataQueryKt.rpgProfession(contacts.data().query()).find();
        }
    
        List<RpgStats> getAllRpgStats() {
            return RpgStatsDataQueryKt.rpgStats(contacts.data().query()).find();
        }
    
        DataUpdate.Result updateRpgProfessionAndRpgStats(
                MutableRpgProfession rpgProfession, MutableRpgStats rpgStats
        ) {
            return contacts.data().update().data(rpgProfession, rpgStats).commit();
        }
    
        DataDelete.Result deleteRpgProfessionAndRpgStats(
                RpgProfession rpgProfession, RpgStats rpgStats
        ) {
            return contacts.data().delete().data(rpgProfession, rpgStats).commit();
        }
    }
    ```

----------------------------------------------------------------------------------------------------

## Groups

### [Query groups](./groups/query-groups.md)

=== "Kotlin"

    ```kotlin
    import android.accounts.Account
    import android.app.Activity
    import contacts.core.*
    import contacts.core.entities.*
    
    class QueryGroupsActivity : Activity() {
    
        fun getAllGroupsFromAllAccounts(): List<Group> = Contacts(this).groups().query().find()
    
        fun getGroupsFromAccount(account: Account): List<Group> =
            Contacts(this).groups().query().accounts(account).find()
    
        fun getGroupsById(groupsIds: List<Long>): List<Group> = Contacts(this)
            .groups()
            .query()
            .where { Id `in` groupsIds }
            .find()
    
        fun getGroupsOfGroupMemberships(groupMemberships: List<GroupMembership>): List<Group> =
            Contacts(this)
                .groups()
                .query()
                .where { Id `in` groupMemberships.mapNotNull { it.groupId } }
                .find()
    
        fun getFavoritesGroups(account: Account): List<Group> = Contacts(this)
            .groups()
            .query()
            .accounts(account)
            .where { Favorites equalTo true }
            .find()
    
        fun getSystemGroups(account: Account): List<Group> = Contacts(this)
            .groups()
            .query()
            .accounts(account)
            .where { SystemId.isNotNull() }
            .find()
    
        fun getUserCreatedGroups(account: Account): List<Group> = Contacts(this)
            .groups()
            .query()
            .accounts(account)
            .find()
            .filter { !it.isSystemGroup }
    }
    ```

=== "Java"

    ```java
    import static contacts.core.WhereKt.*;
    
    import android.accounts.Account;
    import android.app.Activity;
    
    import java.util.ArrayList;
    import java.util.List;
    
    import contacts.core.*;
    import contacts.core.entities.*;
    
    public class QueryGroupsActivity extends Activity {
    
        List<Group> getAllGroupsFromAllAccounts() {
            return ContactsFactory.create(this).groups().query().find();
        }
    
        List<Group> getGroupsFromAccount(Account account) {
            return ContactsFactory.create(this).groups().query().accounts(account).find();
        }
    
        List<Group> getGroupsById(List<Long> groupsIds) {
            return ContactsFactory.create(this)
                    .groups()
                    .query()
                    .where(in(GroupsFields.Id, groupsIds))
                    .find();
        }
    
        List<Group> getGroupsByGroupMembership(List<GroupMembership> groupMemberships) {
            List<Long> groupsIds = new ArrayList<>();
            for (GroupMembership groupMembership : groupMemberships) {
                if (groupMembership.getGroupId() != null) {
                    groupsIds.add(groupMembership.getGroupId());
                }
            }
    
            return ContactsFactory.create(this)
                    .groups()
                    .query()
                    .where(in(GroupsFields.Id, groupsIds))
                    .find();
        }
    
        List<Group> getFavoritesGroups(Account account) {
            return ContactsFactory.create(this)
                    .groups()
                    .query()
                    .accounts(account)
                    .where(equalTo(GroupsFields.Favorites, true))
                    .find();
        }
    
        List<Group> getSystemGroups(Account account) {
            return ContactsFactory.create(this)
                    .groups()
                    .query()
                    .accounts(account)
                    .where(isNotNull(GroupsFields.SystemId))
                    .find();
        }
    
        List<Group> getUserCreatedGroups(Account account) {
            List<Group> groups = ContactsFactory.create(this)
                    .groups()
                    .query()
                    .accounts(account)
                    .find();
    
            List<Group> userCreatedGroups = new ArrayList<>();
            for (Group group : groups) {
                if (!group.isSystemGroup()) {
                    userCreatedGroups.add(group);
                }
            }
    
            return userCreatedGroups;
        }
    }
    ```

### [Insert groups](./groups/insert-groups.md)

=== "Kotlin"

    ```kotlin
    import android.accounts.Account
    import android.app.Activity
    import contacts.core.Contacts
    import contacts.core.entities.NewGroup
    import contacts.core.groups.GroupsInsert
    
    class InsertGroupsActivity : Activity() {
    
        fun insertGroup(title: String, account: Account): GroupsInsert.Result =
            Contacts(this).groups().insert().group(title, account).commit()
    
        fun insertGroups(groups: List<NewGroup>): GroupsInsert.Result =
            Contacts(this).groups().insert().groups(groups).commit()
    }
    ```

=== "Java"

    ```java
    import contacts.core.ContactsFactory;
    import contacts.core.entities.NewGroup;
    import contacts.core.groups.GroupsInsert;
    
    public class InsertGroupsActivity extends Activity {
    
        GroupsInsert.Result insertGroup(String title, Account account) {
            return ContactsFactory.create(this).groups().insert().group(title, account).commit();
        }
    
        GroupsInsert.Result insertGroups(List<NewGroup> groups) {
            return ContactsFactory.create(this).groups().insert().groups(groups).commit();
        }
    }
    ```

### [Update groups](./groups/update-groups.md)

=== "Kotlin"

    ```kotlin
    import android.app.Activity
    import contacts.core.Contacts
    import contacts.core.entities.Group
    import contacts.core.groups.GroupsUpdate
    
    class UpdateGroupsActivity : Activity() {
    
        fun updateGroup(group: Group): GroupsUpdate.Result = Contacts(this)
            .groups()
            .update()
            .groups(
                group.mutableCopy {
                    title = "Bad love"
                }
            )
            .commit()
    }
    ```

=== "Java"

    ```java
    import android.app.Activity;
    
    import contacts.core.ContactsFactory;
    import contacts.core.entities.*;
    import contacts.core.groups.GroupsUpdate;
    
    public class UpdateGroupsActivity extends Activity {
    
        GroupsUpdate.Result updateGroup(Group group) {
            MutableGroup mutableGroup = group.mutableCopy();
            if (mutableGroup != null) {
                mutableGroup.setTitle("Bad love");
            }
    
            return ContactsFactory.create(this)
                    .groups()
                    .update()
                    .groups(mutableGroup)
                    .commit();
        }
    }
    ```

### [Delete groups](./groups/delete-groups.md)

=== "Kotlin"

    ```kotlin
    import android.app.Activity
    import contacts.core.Contacts
    import contacts.core.entities.Group
    import contacts.core.groups.GroupsDelete
    
    class DeleteGroupsActivity : Activity() {
    
        fun deleteGroups(groups: List<Group>): GroupsDelete.Result =
            Contacts(this).groups().delete().groups(groups).commit()
    }
    ```

=== "Java"

    ```java
    import android.app.Activity;
    
    import java.util.List;
    
    import contacts.core.ContactsFactory;
    import contacts.core.entities.Group;
    import contacts.core.groups.GroupsDelete;
    
    public class DeleteGroupsActivity extends Activity {
    
        GroupsDelete.Result deleteGroups(List<Group> groups) {
            return ContactsFactory.create(this).groups().delete().groups(groups).commit();
        }
    }
    ```

----------------------------------------------------------------------------------------------------

## Profile

### [Query device owner Contact profile](./profile/query-profile.md)

=== "Kotlin"

    ```kotlin
    import android.app.Activity
    import contacts.core.Contacts
    import contacts.core.entities.Contact
    
    class QueryProfileActivity : Activity() {
    
        fun getProfile(): Contact? = Contacts(this).profile().query().find().contact
    }
    ```

=== "Java"

    ```java
    import android.app.Activity;
    
    import contacts.core.ContactsFactory;
    import contacts.core.entities.Contact;
    
    public class QueryProfileActivity extends Activity {
    
        Contact getProfile() {
            return ContactsFactory.create(this).profile().query().find().getContact();
        }
    }
    ```

### [Insert device owner Contact profile](./profile/insert-profile.md)

=== "Kotlin"

    ```kotlin
    import android.app.Activity
    import contacts.core.Contacts
    import contacts.core.entities.*
    import contacts.core.profile.ProfileInsert
    import contacts.core.util.*
    
    class InsertProfileActivity : Activity() {
    
        fun insertProfile(): ProfileInsert.Result = Contacts(this)
            .profile()
            .insert()
            .rawContact {
                addAddress {
                    street = "321 Xyz street"
                    city = "Brooklyn"
                    region = "New York"
                    postcode = "11207"
                    country = "US"
                    type = AddressEntity.Type.WORK
                }
                addEmail {
                    address = "321@xyz.com"
                    type = EmailEntity.Type.WORK
                }
                addEvent {
                    date = EventDate.from(1990, 0, 1)
                    type = EventEntity.Type.BIRTHDAY
                }
                addIm {
                    data = "im@aol.com"
                    protocol = ImEntity.Protocol.CUSTOM
                    customProtocol = "AOL"
                }
                setName {
                    prefix = "Mr."
                    givenName = "Small"
                    middleName = "Goody"
                    familyName = "Dog"
                    suffix = "Jr"
                }
                setNickname {
                    name = "TINY DOMESTICATED ANIMAL"
                }
                setNote {
                    note = "This is one furry friend!"
                }
                setOrganization {
                    company = "Good company"
                    title = "Teammate"
                    department = "The good one"
                    jobDescription = "Be a good citizen"
                    officeLocation = "It's public"
                }
                addPhone {
                    number = "(888) 321-7654"
                    type = PhoneEntity.Type.WORK
                }
                addRelation {
                    name = "Bro"
                    type = RelationEntity.Type.BROTHER
                }
                setSipAddress {
                    sipAddress = "sip:user@domain:port"
                }
                addWebsite {
                    url = "www.smalltinycompany.com"
                }
            }
            .commit()
    }
    ```

=== "Java"

    ```java
    import android.app.Activity;
    
    import contacts.core.ContactsFactory;
    import contacts.core.entities.*;
    import contacts.core.profile.ProfileInsert;
    
    public class InsertProfileActivity extends Activity {
    
        ProfileInsert.Result insertProfile() {
            NewAddress address = new NewAddress();
            address.setStreet("Xyz Abc street");
            address.setCity("Brooklyn");
            address.setRegion("New York");
            address.setPostcode("11207");
            address.setCountry("US");
            address.setType(AddressEntity.Type.WORK);
    
            NewEmail email = new NewEmail();
            email.setAddress("321@xyz.com");
            email.setType(EmailEntity.Type.WORK);
    
            NewEvent event = new NewEvent();
            event.setDate(EventDate.from(1990, 0, 1));
            event.setType(EventEntity.Type.BIRTHDAY);
    
            NewIm im = new NewIm();
            im.setData("im@aol.com");
            im.setProtocol(ImEntity.Protocol.CUSTOM);
            im.setCustomProtocol("AOL");
    
            NewName name = new NewName();
            name.setPrefix("Mr.");
            name.setGivenName("Small");
            name.setMiddleName("Goody");
            name.setFamilyName("Dog");
            name.setSuffix("Jr");
    
            NewNickname nickname = new NewNickname();
            nickname.setName("TINY DOMESTICATED ANIMAL");
    
            NewNote note = new NewNote();
            note.setNote("This is one furry friend!");
    
            NewOrganization organization = new NewOrganization();
            organization.setCompany("Good company");
            organization.setTitle("Teammate");
            organization.setDepartment("The good one");
            organization.setJobDescription("Be a good citizen");
            organization.setOfficeLocation("It's public");
    
            NewPhone phone = new NewPhone();
            phone.setNumber("(888) 321-7654");
            phone.setType(PhoneEntity.Type.WORK);
    
            NewRelation relation = new NewRelation();
            relation.setName("Bro");
            relation.setType(RelationEntity.Type.BROTHER);
    
            NewSipAddress sipAddress = new NewSipAddress();
            sipAddress.setSipAddress("sip:user@domain:port");
    
            NewWebsite website = new NewWebsite();
            website.setUrl("www.smalltinycompany.com");
    
            NewRawContact rawContact = new NewRawContact();
            rawContact.getAddresses().add(address);
            rawContact.getEmails().add(email);
            rawContact.getEvents().add(event);
            rawContact.getIms().add(im);
            rawContact.setName(name);
            rawContact.setNickname(nickname);
            rawContact.setNote(note);
            rawContact.setOrganization(organization);
            rawContact.getPhones().add(phone);
            rawContact.getRelations().add(relation);
            rawContact.setSipAddress(sipAddress);
            rawContact.getWebsites().add(website);
    
            return ContactsFactory.create(this)
                    .profile()
                    .insert()
                    .rawContact(rawContact)
                    .commit();
        }
    }
    ```

### [Update device owner Contact profile](./profile/update-profile.md)

=== "Kotlin"

    ```kotlin
    import android.app.Activity
    import contacts.core.Contacts
    import contacts.core.entities.*
    import contacts.core.profile.ProfileUpdate
    import contacts.core.util.*
    
    class UpdateProfileActivity : Activity() {
    
        fun updateProfile(profile: Contact): ProfileUpdate.Result = Contacts(this)
            .profile()
            .update()
            .contact(profile.mutableCopy {
                setName {
                    displayName = "I am the phone owner"
                }
                addEmail {
                    type = EmailEntity.Type.CUSTOM
                    label = "Profile Email"
                    address = "phone@owner.com"
                }
                removeAllPhones()
                setOrganization(null)
            })
            .commit()
    }
    ```

=== "Java"

    ```java
    import android.app.Activity;
    
    import contacts.core.ContactsFactory;
    import contacts.core.entities.*;
    import contacts.core.profile.ProfileUpdate;
    import contacts.core.util.ContactDataKt;
    
    public class UpdateProfileActivity extends Activity {
    
        ProfileUpdate.Result updateProfile(Contact profile) {
            MutableContact mutableProfile = profile.mutableCopy();
            ContactDataKt.setName(mutableProfile, new NewName("I am the phone owner"));
            ContactDataKt.addEmail(mutableProfile, new NewEmail(
                    EmailEntity.Type.CUSTOM,
                    "Profile Email",
                    "phone@owner.com"
            ));
            ContactDataKt.removeAllPhones(mutableProfile);
            ContactDataKt.setOrganization(mutableProfile, (MutableOrganizationEntity) null);
    
            return ContactsFactory.create(this)
                    .profile()
                    .update()
                    .contact(mutableProfile)
                    .commit();
        }
    }
    ```

### [Delete device owner Contact profile](./profile/delete-profile.md)

=== "Kotlin"

    ```kotlin
    import android.app.Activity
    import contacts.core.Contacts
    import contacts.core.profile.ProfileDelete
    
    class DeleteProfileActivity : Activity() {
    
        fun deleteProfile(): ProfileDelete.Result = Contacts(this).profile().delete().contact().commit()
    }
    ```

=== "Java"

    ```java
    import android.app.Activity;
    
    import contacts.core.ContactsFactory;
    import contacts.core.profile.ProfileDelete;
    
    public class DeleteProfileActivity extends Activity {
    
        ProfileDelete.Result deleteProfile() {
            return ContactsFactory.create(this).profile().delete().contact().commit();
        }
    }
    ```

----------------------------------------------------------------------------------------------------

## Accounts

### [Query for Accounts](./accounts/query-accounts.md)

=== "Kotlin"

    ```kotlin
    import android.accounts.Account
    import android.app.Activity
    import contacts.core.Contacts
    import contacts.core.entities.RawContact
    
    class QueryAccountsActivity : Activity() {
    
        fun getAllAccounts(): List<Account> = Contacts(this).accounts().query().find()
    
        fun getAllGoogleAccounts(): List<Account> = Contacts(this)
            .accounts()
            .query()
            .withTypes("com.google")
            .find()
    
        fun getRawContactAccount(rawContact: RawContact): Account? = Contacts(this)
            .accounts()
            .query()
            .associatedWith(rawContact)
            .find()
            .firstOrNull()
    }
    ```

=== "Java"

    ```java
    import android.accounts.Account;
    import android.app.Activity;
    
    import java.util.List;
    
    import contacts.core.ContactsFactory;
    import contacts.core.entities.RawContact;
    
    public class QueryAccountsActivity extends Activity {
    
        List<Account> getAllAccounts() {
            return ContactsFactory.create(this).accounts().query().find();
        }
    
        List<Account> getAllGoogleAccounts() {
            return ContactsFactory.create(this)
                    .accounts()
                    .query()
                    .withTypes("com.google")
                    .find();
        }
    
        Account getRawContactAccount(RawContact rawContact) {
            return ContactsFactory.create(this)
                    .accounts()
                    .query()
                    .associatedWith(rawContact)
                    .find()
                    .get(0);
        }
    }
    ```

### [Query for RawContacts](./accounts/query-raw-contacts.md)

=== "Kotlin"

    ```kotlin
    import android.accounts.Account
    import android.app.Activity
    import contacts.core.Contacts
    import contacts.core.entities.BlankRawContact
    import contacts.core.equalTo
    
    class QueryAccountsRawContactsActivity : Activity() {
    
        fun getAllRawContacts(): List<BlankRawContact> =
            Contacts(this).accounts().queryRawContacts().find()
    
        fun getRawContactsForAccount(account: Account): List<BlankRawContact> =
            Contacts(this)
                .accounts()
                .queryRawContacts()
                .accounts(account)
                .find()
    
        fun getRawContactsForAllGoogleAccounts(): List<BlankRawContact> =
            Contacts(this)
                .accounts()
                .queryRawContacts()
                .where { AccountType equalTo "com.google" }
                .find()
    
        fun getRawContactById(rawContactId: Long): BlankRawContact? =
            Contacts(this)
                .accounts()
                .queryRawContacts()
                .where { Id equalTo rawContactId }
                .find()
                .firstOrNull()
    }
    ```

=== "Java"

    ```java
    import static contacts.core.WhereKt.equalTo;
    
    import android.accounts.Account;
    import android.app.Activity;
    
    import java.util.List;
    
    import contacts.core.*;
    import contacts.core.entities.BlankRawContact;
    
    public class QueryAccountsRawContactsActivity extends Activity {
    
        List<BlankRawContact> getAllRawContacts() {
            return ContactsFactory.create(this).accounts().queryRawContacts().find();
        }
    
        List<BlankRawContact> getRawContactsForAccount(Account account) {
            return ContactsFactory.create(this)
                    .accounts()
                    .queryRawContacts()
                    .accounts(account)
                    .find();
        }
    
        List<BlankRawContact> getRawContactsForAllGoogleAccounts() {
            return ContactsFactory.create(this)
                    .accounts()
                    .queryRawContacts()
                    .where(equalTo(RawContactsFields.AccountType, "com.google"))
                    .find();
        }
    
        BlankRawContact getRawContactById(Long rawContactId) {
            return ContactsFactory.create(this)
                    .accounts()
                    .queryRawContacts()
                    .where(equalTo(RawContactsFields.Id, rawContactId))
                    .find()
                    .get(0);
        }
    }
    ```

### [Associate a local RawContact to an Account](./accounts/associate-device-local-raw-contacts-to-an-account.md)

=== "Kotlin"

    ```kotlin
    import android.accounts.Account
    import android.app.Activity
    import contacts.core.Contacts
    import contacts.core.accounts.AccountsLocalRawContactsUpdate
    import contacts.core.entities.RawContact
    
    class UpdateLocalRawContactsAccountsActivity : Activity() {
    
        fun associateLocalRawContactToAccount(
            localRawContact: RawContact, account: Account
        ): AccountsLocalRawContactsUpdate.Result = Contacts(this)
            .accounts()
            .updateLocalRawContactsAccount()
            .addToAccount(account)
            .localRawContacts(localRawContact)
            .commit()
    }
    ```

=== "Java"

    ```java
    import android.accounts.Account;
    import android.app.Activity;
    
    import contacts.core.ContactsFactory;
    import contacts.core.accounts.AccountsLocalRawContactsUpdate;
    import contacts.core.entities.RawContact;
    
    public class UpdateLocalRawContactsAccountsActivity extends Activity {
    
        AccountsLocalRawContactsUpdate.Result associateLocalRawContactToAccount(
                RawContact localRawContact, Account account
        ) {
            return ContactsFactory.create(this)
                    .accounts()
                    .updateLocalRawContactsAccount()
                    .addToAccount(account)
                    .localRawContacts(localRawContact)
                    .commit();
        }
    }
    ```

----------------------------------------------------------------------------------------------------

## SIM card

### [Query contacts in SIM card](./sim/query-sim-contacts.md)

=== "Kotlin"

    ```kotlin
    import android.app.Activity
    import contacts.core.Contacts
    import contacts.core.entities.SimContact
    
    class QuerySimContactsActivity : Activity() {
    
        fun getAllSimContacts(): List<SimContact> = Contacts(this).sim().query().find()
    
        fun getAllSimContactsWithPhoneNumber(): List<SimContact> = Contacts(this)
            .sim()
            .query()
            .find()
            .filter { !it.number.isNullOrEmpty() }
    }
    ```

=== "Java"

    ```java
    import android.app.Activity;
    
    import java.util.ArrayList;
    import java.util.List;
    
    import contacts.core.ContactsFactory;
    import contacts.core.entities.SimContact;
    
    public class QuerySimContactsActivity extends Activity {
    
        List<SimContact> getAllSimContacts() {
            return ContactsFactory.create(this).sim().query().find();
        }
    
        List<SimContact> getAllSimContactsWithPhoneNumber() {
            List<SimContact> simContacts = ContactsFactory.create(this).sim().query().find();
            List<SimContact> simContactsWithPhoneNumber = new ArrayList<>();
            for (SimContact simContact : simContacts) {
                if (simContact.getNumber() != null && !simContact.getNumber().isEmpty()) {
                    simContactsWithPhoneNumber.add(simContact);
                }
            }
            return simContactsWithPhoneNumber;
        }
    }
    ```

### [Insert contacts into SIM card](./sim/insert-sim-contacts.md)

=== "Kotlin"

    ```kotlin
    import android.app.Activity
    import contacts.core.Contacts
    import contacts.core.sim.SimContactsInsert
    
    class InsertSimContactsActivity : Activity() {
    
        fun insertSimContact(): SimContactsInsert.Result = Contacts(this)
            .sim()
            .insert()
            .simContact {
                name = "Mr. Joe"
                number = "5555555555"
            }
            .commit()
    }
    ```

=== "Java"

    ```java
    import android.app.Activity;
    
    import contacts.core.ContactsFactory;
    import contacts.core.entities.NewSimContact;
    import contacts.core.sim.SimContactsInsert;
    
    public class InsertSimContactsActivity extends Activity {
    
        SimContactsInsert.Result insertSimContact() {
            return ContactsFactory.create(this)
                    .sim()
                    .insert()
                    .simContacts(new NewSimContact("Mr. Joe", "5555555555"))
                    .commit();
        }
    }
    ```

### [Update contacts in SIM card](./sim/update-sim-contacts.md)

=== "Kotlin"

    ```kotlin
    import android.app.Activity
    import contacts.core.Contacts
    import contacts.core.entities.SimContact
    import contacts.core.sim.SimContactsUpdate
    
    class UpdateSimContactsActivity : Activity() {
    
        fun updateSimContact(simContact: SimContact): SimContactsUpdate.Result = Contacts(this)
            .sim()
            .update()
            .simContact(simContact, simContact.mutableCopy {
                name = "Vandolf"
                number = "1234567890"
            })
            .commit()
    }
    ```

=== "Java"

    ```java
    import android.app.Activity;
    
    import contacts.core.ContactsFactory;
    import contacts.core.entities.*;
    import contacts.core.sim.SimContactsUpdate;
    
    public class UpdateSimContactsActivity extends Activity {
    
        SimContactsUpdate.Result updateSimContact(SimContact simContact) {
            MutableSimContact mutableSimContact = simContact.mutableCopy();
            mutableSimContact.setName("Vandolf");
            mutableSimContact.setNumber("1234567890");
    
            return ContactsFactory.create(this)
                    .sim()
                    .update()
                    .simContact(simContact, mutableSimContact)
                    .commit();
        }
    }
    ```

### [Delete contacts from SIM card](./sim/delete-sim-contacts.md)

=== "Kotlin"

    ```kotlin
    TODO
    ```

=== "Java"

    ```java
    TODO
    ```

----------------------------------------------------------------------------------------------------

## Blocked numbers

### [Query blocked numbers](./blockednumbers/query-blocked-numbers.md)

=== "Kotlin"

    ```kotlin
    TODO
    ```

=== "Java"

    ```java
    TODO
    ```

### [Insert blocked numbers](./blockednumbers/insert-blocked-numbers.md)

=== "Kotlin"

    ```kotlin
    TODO
    ```

=== "Java"

    ```java
    TODO
    ```

### [Delete blocked numbers](./blockednumbers/delete-blocked-numbers.md)

=== "Kotlin"

    ```kotlin
    TODO
    ```

=== "Java"

    ```java
    TODO
    ```

----------------------------------------------------------------------------------------------------

## Other

### [Get set remove full-sized and thumbnail contact photos](./other/get-set-remove-contact-raw-contact-photo.md)

=== "Kotlin"

    ```kotlin
    TODO
    ```

=== "Java"

    ```java
    TODO
    ```

### [Get set Contact options](./other/get-set-clear-contact-raw-contact-options.md)

=== "Kotlin"

    ```kotlin
    TODO
    ```

=== "Java"

    ```java
    TODO
    ```

### [Get set clear default Contact data](./other/get-set-clear-default-data.md)

=== "Kotlin"

    ```kotlin
    TODO
    ```

=== "Java"

    ```java
    TODO
    ```

### [Link unlink Contacts](./other/link-unlink-contacts.md)

=== "Kotlin"

    ```kotlin
    TODO
    ```

=== "Java"

    ```java
    TODO
    ```

### [Share Contacts vCard (.VCF)](./other/share-contacts-vcard.md)

=== "Kotlin"

    ```kotlin
    TODO
    ```

=== "Java"

    ```java
    TODO
    ```

### [Convenience functions](./other/convenience-functions.md)

=== "Kotlin"

    ```kotlin
    TODO
    ```

=== "Java"

    ```java
    TODO
    ```

----------------------------------------------------------------------------------------------------

## Logging

### [Log API input and output](./logging/log-api-input-output.md)

=== "Kotlin"

    ```kotlin
    TODO
    ```

=== "Java"

    ```java
    TODO
    ```

----------------------------------------------------------------------------------------------------

## Testing

### [Contacts API Testing](./testing/test-contacts-api.md)

=== "Kotlin"

    ```kotlin
    TODO
    ```

=== "Java"

    ```java
    TODO
    ```

----------------------------------------------------------------------------------------------------

## Debug

### [Debug the Contacts Provider tables](./debug/debug-contacts-provider-tables.md)

=== "Kotlin"

    ```kotlin
    TODO
    ```

=== "Java"

    ```java
    TODO
    ```

### [Debug the BlockedNumber Provider tables](./debug/debug-blockednumber-provider-tables.md)

=== "Kotlin"

    ```kotlin
    TODO
    ```

=== "Java"

    ```java
    TODO
    ```

### [Debug the Sim Contacts table](./debug/debug-sim-contacts-tables.md)

=== "Kotlin"

    ```kotlin
    TODO
    ```

=== "Java"

    ```java
    TODO
    ```