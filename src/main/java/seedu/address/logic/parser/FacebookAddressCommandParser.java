package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FACEBOOKADDRESS;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.FacebookAddressCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.FacebookAddress;

/**
 * Parses input arguments and creates a new FacebookAddressCommand object
 */
public class FacebookAddressCommandParser implements Parser<FacebookAddressCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FacebookAddressCommand
     * and returns a FacebookAddressCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FacebookAddressCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_FACEBOOKADDRESS);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT, FacebookAddressCommand.MESSAGE_USAGE));
        }

        String facebookAddressString = argMultimap.getValue(PREFIX_FACEBOOKADDRESS).orElse("");

        return new FacebookAddressCommand(index, new FacebookAddress(facebookAddressString));
    }
}

