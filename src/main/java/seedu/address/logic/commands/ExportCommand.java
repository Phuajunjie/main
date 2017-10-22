package seedu.address.logic.commands;

import seedu.address.commons.GoogleContactsBuilder;
import seedu.address.commons.core.Messages;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.GoogleID;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import com.google.api.services.people.v1.model.*;


/**
 * Finds and lists all persons in address book whose name contains any of the argument keywords.
 * Keyword matching is case sensitive.
 */
public class ExportCommand extends Command {

    public static final String COMMAND_WORD = "export";
    public static final String COMMAND_ALIAS = "export";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Export contacts to google"
            + "process\n"
            + "Parameters: KEYWORD\n"
            + "Example: " + COMMAND_WORD;
    private String commandMessage = "";

    private int contactsExportedCount = 0;
    private int errorExportCount = 0;

    @Override
    public CommandResult execute() throws CommandException {
        List<ReadOnlyPerson> personList = model.getAddressBook().getPersonList();
        GoogleContactsBuilder builder;

        try {
            builder = new GoogleContactsBuilder();
        } catch (IOException e) {
            throw new CommandException("Authentication Failed. Please login again.");
        }

        Person createdContact;

        if (personList.isEmpty()) {
            throw new CommandException("No contacts in addressbook to export");
        }

        for(ReadOnlyPerson contact : personList) {
            if (contact.getGoogleId().value.equals("not GoogleContact")) {
                try {
                    createdContact = createGoogleContact(contact);
                    createdContact = builder.getPeopleService().people().createContact(createdContact).execute();
                    model.updatePerson(contact, getNewAddressBookContact(contact, createdContact));
                    contactsExportedCount++;
                } catch (IOException | NullPointerException e) {
                    throw new CommandException("Authentication Failed. Please login again.");
                } catch (IllegalValueException | PersonNotFoundException e) {
                    errorExportCount++;
                }
            }
        }
        commandMessage = setCommandMessage(contactsExportedCount,errorExportCount);
        return new CommandResult(commandMessage);
}

    /**
     * Creates a Person to add in google contact
     */
    public Person createGoogleContact(ReadOnlyPerson person)throws IOException {
        Person contactToCreate = new Person();
        List names = new ArrayList();
        List email = new ArrayList();
        List phone = new ArrayList();
        List address = new ArrayList();

        names.add(new Name().setGivenName(person.getName().fullName));
        email.add(new EmailAddress().setValue(person.getEmail().value));
        phone.add(new PhoneNumber().setValue(person.getPhone().value));
        address.add(new Address().setStreetAddress(person.getAddress().value));

        return contactToCreate.setNames(names).setEmailAddresses(email).setPhoneNumbers(phone).setAddresses(address);
    }

    /**
     * Creates a Person to update in the addressbook.
     * To update new attributes : google ID and google contact tag
     */
    public ReadOnlyPerson getNewAddressBookContact(ReadOnlyPerson contact, Person createdContact)
            throws IllegalValueException {

        GoogleID Id = new GoogleID(createdContact.getResourceName().substring(8));
        Tag tag = new Tag("GoogleContact");
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);

        return new seedu.address.model.person.Person(contact.getName(), contact.getPhone(),
                contact.getEmail(), contact.getAddress(), contact.getBirthday(), contact.getFacebookAddress(), tags, Id);
    }
    /**
     * Creates a detailed message on the status of the sync
     */
    public String setCommandMessage(int contactsExportedCount, int errorExportCount) {

        commandMessage = String.format(Messages.MESSAGE_EXPORT_CONTACT, contactsExportedCount);
        if (errorExportCount == 0) {
            commandMessage += "All contacts can be now found in google contact";
        } else {
            commandMessage += String.format(Messages.MESSAGE_EXPORT_ERROR, errorExportCount);
        }
        return commandMessage;
    }
}
