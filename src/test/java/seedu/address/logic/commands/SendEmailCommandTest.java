package seedu.address.logic.commands;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BODY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_SUBJECT;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showFirstPersonOnly;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.events.ui.JumpToListRequestEvent;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.commands.exceptions.GoogleAuthException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.ui.testutil.EventsCollectorRule;

public class SendEmailCommandTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public final EventsCollectorRule eventsCollectorRule = new EventsCollectorRule();

    private Model model;

    @Before
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    /**
     * Checks if login is authenticated. In this case it is not, GoogleAuthException is thrown.
     */
    @Before
    public void execute_require_login() throws Exception {
        thrown.expect(GoogleAuthException.class);
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Index lastPersonIndex = Index.fromOneBased(model.getFilteredPersonList().size());

        assertExecutionSuccess(INDEX_FIRST_PERSON, VALID_EMAIL_SUBJECT, VALID_EMAIL_BODY);
        assertExecutionSuccess(INDEX_THIRD_PERSON, VALID_EMAIL_SUBJECT, VALID_EMAIL_BODY);
        assertExecutionSuccess(lastPersonIndex, VALID_EMAIL_SUBJECT, VALID_EMAIL_BODY);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_failure() {
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);

        assertExecutionFailure(outOfBoundsIndex, VALID_EMAIL_SUBJECT, VALID_EMAIL_BODY,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showFirstPersonOnly(model);

        assertExecutionSuccess(INDEX_FIRST_PERSON, VALID_EMAIL_SUBJECT, VALID_EMAIL_BODY);
    }

    @Test
    public void execute_invalidIndexFilteredList_failure() {
        showFirstPersonOnly(model);

        Index outOfBoundsIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundsIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        assertExecutionFailure(outOfBoundsIndex, VALID_EMAIL_SUBJECT, VALID_EMAIL_BODY,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    //Index, Subject, Body has been specified
    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() throws Exception {
        SendEmailCommand sendEmailCommand = prepareCommand(INDEX_FIRST_PERSON, VALID_EMAIL_SUBJECT, VALID_EMAIL_BODY);
        String expectedMessage = String.format(SendEmailCommand.MESSAGE_SUCCESS);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        assertCommandSuccess(sendEmailCommand, model, expectedMessage, expectedModel);
    }

    //Index, Subject has been specified
    @Test
    public void execute_subjectFieldSpecifiedUnfilteredList_success() throws Exception {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        SendEmailCommand sendEmailCommand = prepareCommand(indexLastPerson, VALID_EMAIL_SUBJECT, "");
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        assertCommandSuccess(sendEmailCommand, model, expectedMessage, expectedModel);
    }

    //Index, Body has been specified
    @Test
    public void execute_bodyFieldSpecifiedUnfilteredList_success() throws Exception {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        SendEmailCommand sendEmailCommand = prepareCommand(indexLastPerson, "", VALID_EMAIL_BODY);
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        assertCommandSuccess(sendEmailCommand, model, expectedMessage, expectedModel);
    }

    //Only Index has been specified
    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        SendEmailCommand sendEmailCommand = prepareCommand(indexLastPerson, "", "");
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        assertCommandSuccess(sendEmailCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Executes a {@code SendEmailCommand} with the given {@code index, subject, body},
     * and checks that {@code JumpToListRequestEvent}
     * is raised with the correct index.
     */
    private void assertExecutionSuccess(Index index, String subject, String body) {
        SendEmailCommand sendEmailCommand = prepareCommand(index, subject, body);

        try {
            CommandResult commandResult = sendEmailCommand.execute();
            assertEquals(String.format(SendEmailCommand.MESSAGE_SUCCESS, index.getOneBased()),
                    commandResult.feedbackToUser);
        } catch (CommandException | GoogleAuthException e) {
            throw new IllegalArgumentException("Execution of command should not fail.", e);
        }

        JumpToListRequestEvent lastEvent = (JumpToListRequestEvent) eventsCollectorRule.eventsCollector.getMostRecent();
        assertEquals(index, Index.fromZeroBased(lastEvent.targetIndex));
    }

    /**
     * Executes a {@code SendEmailCommand} with the given {@code index, subject, body},
     * and checks that a {@code CommandException or GoogleAuthException}
     * is thrown with the {@code expectedMessage}.
     */
    private void assertExecutionFailure(Index index, String subject, String body, String expectedMessage) {
        SendEmailCommand sendEmailCommand = prepareCommand(index, subject, body);

        try {
            sendEmailCommand.execute();
            Assert.fail("The expected exception was not thrown.");
        } catch (CommandException | GoogleAuthException e) {
            assertEquals(expectedMessage, e.getMessage());
            Assert.assertTrue(eventsCollectorRule.eventsCollector.isEmpty());
        }
    }

    /**
     * Returns a {@code SendEmailCommand} with parameters {@code index, subject, body}.
     */
    private SendEmailCommand prepareCommand(Index index, String subject, String body) {
        SendEmailCommand sendEmailCommand = new SendEmailCommand(index, subject, body);
        sendEmailCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return sendEmailCommand;
    }
}

