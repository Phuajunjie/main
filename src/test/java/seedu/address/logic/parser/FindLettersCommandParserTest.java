//@@author PhuaJunJie
package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;

import org.junit.Test;

import seedu.address.logic.commands.FindLettersCommand;
import seedu.address.model.person.NameContainsAlphabetsPredicate;

public class FindLettersCommandParserTest {

    private FindLettersCommandParser parser = new FindLettersCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                FindLettersCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindLettersCommand expectedFindCommand = new FindLettersCommand(new
                NameContainsAlphabetsPredicate(Arrays.asList("Fiona", "Amelia")));
        assertParseSuccess(parser, "Fiona Amelia", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Fiona \n \t Amelia  \t", expectedFindCommand);
    }

}
