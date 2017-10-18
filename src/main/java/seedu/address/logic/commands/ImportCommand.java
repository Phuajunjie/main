package seedu.address.logic.commands;

import com.google.api.services.people.v1.model.Person;
import seedu.address.commons.GoogleAuthenticator;
import seedu.address.commons.GoogleContactsBuilder;
import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.core.Messages;
import seedu.address.commons.events.ui.ExitAppRequestEvent;
import seedu.address.commons.events.ui.JumpToListRequestEvent;
import seedu.address.commons.events.ui.LoadLoginEvent;
import seedu.address.commons.events.ui.NewResultAvailableEvent;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.Logic;
import seedu.address.model.person.ReadOnlyPerson;

import java.io.IOException;
import java.util.*;


/**
 * Finds and lists all persons in address book whose name contains any of the argument keywords.
 * Keyword matching is case sensitive.
 */
public class ImportCommand extends Command {

    public static final String COMMAND_WORD = "import";
    public static final String COMMAND_ALIAS = "import";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Import contacts from google"
            + "process\n"
            + "Parameters: KEYWORD\n"
            + "Example: " + COMMAND_WORD;
    private String CommandMessage = "";
    private String ErrorMessage;

    private int contactsNotImported = 0;

    @Override
    public CommandResult execute() {

        GoogleContactsBuilder builder = new GoogleContactsBuilder();
        Logic logic = new LogicManager(model);
        List<Person> connections = null;
        boolean contactAlreadyExists;

        try {
            connections = builder.getPersonlist();
        } catch (IOException E){
            ErrorMessage = "Authentication Failed. Please login again.";

        }
        List<ReadOnlyPerson> personList = model.getAddressBook().getPersonList();


        if ((connections != null) && (connections.size() > 0)) {
            for (Person person : connections) {
                contactAlreadyExists = false;
                for (ReadOnlyPerson contact : personList) {
                    if (person.getResourceName().substring(8).equals(contact.getGoogleID().value)) {
                        contactAlreadyExists = true;
                    }
                }
                if (!contactAlreadyExists) {
                    try {
                        logic.execute("add " + "n/" + person.getNames().get(0).getDisplayName() + " " +
                                "p/" + person.getPhoneNumbers().get(0).getValue().replace(" ", "") + " " +
                                "e/" + person.getEmailAddresses().get(0).getValue() + " " +
                                "a/" + person.getAddresses().get(0).getStreetAddress() + " " +
                                "t/" + "GoogleContact" + " " +
                                "c/" + person.getResourceName().substring(8));
                    } catch (CommandException | ParseException | NullPointerException e) {
                        this.contactsNotImported += 1;
                    }
                }
                else {
                    this.contactsNotImported += 1;
                }
            }
            this.CommandMessage = String.format(Messages.MESSAGE_IMPORT_CONTACT, connections.size() - contactsNotImported, contactsNotImported);
        }
        else{
            this.CommandMessage = "No Contacts Found!";
        }
        if(ErrorMessage!=null){
            CommandMessage = ErrorMessage;
        }

        return new CommandResult(CommandMessage);
    }
}