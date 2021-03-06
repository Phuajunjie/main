//@@author srishag
package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import seedu.address.commons.exceptions.IllegalValueException;

/**
 * Represents a Person's birthday in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidBirthday(String)}
 */
public class Birthday {

    public static final String MESSAGE_BIRTHDAY_CONSTRAINTS =
            "Person birthday can only be digits, and should be a valid date "
                    + "represented by a 8 digit number with format ddMMyyyy";
    public static final String BIRTHDAY_VALIDATION_REGEX = "\\b\\d{8}\\b";
    public static final String DATE_FORMAT = "ddMMyyyy";
    public final String value;

    /**
     * Validates given birthday.
     *
     * @throws IllegalValueException if given birthday string is invalid.
     */
    public Birthday(String birthday) throws IllegalValueException {
        requireNonNull(birthday);
        String trimmedBirthday = birthday.trim();
        if (!isValidBirthday(trimmedBirthday)) {
            throw new IllegalValueException(MESSAGE_BIRTHDAY_CONSTRAINTS);
        }
        this.value = trimmedBirthday;
    }

    /**
     * Returns if a given string is a valid person birthday.
     */
    public static boolean isValidBirthday(String test) {
        if (test.matches(BIRTHDAY_VALIDATION_REGEX)) {
            try {
                DateFormat df = new SimpleDateFormat(DATE_FORMAT);
                df.setLenient(false);
                df.parse(test);
                return true;
            } catch (ParseException pe) {
                return false;
            }
        } else if (test.matches("")) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Birthday // instanceof handles nulls
                && this.value.equals(((Birthday) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

