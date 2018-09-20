package org.arrobaLite.Exception;

/**
 * Created by d070169 on 12/10/2017.
 */

public class UpdateException extends DatabaseException {

    private static final String DEFAULT_MESSAGE = "An error occured during UPDATE statement.";

    public UpdateException(Exception exception) { super(exception,DEFAULT_MESSAGE); }
    public UpdateException() { super(DEFAULT_MESSAGE); }
    public UpdateException(String message) { super(message); }
    public UpdateException(Exception exception, String message) { super(exception, message); }
}
