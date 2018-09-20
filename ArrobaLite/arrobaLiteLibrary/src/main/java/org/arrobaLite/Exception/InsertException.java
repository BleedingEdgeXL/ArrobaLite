package org.arrobaLite.Exception;

/**
 * Created by d070169 on 12/10/2017.
 */

public class InsertException extends DatabaseException {

  private static final String DEFAULT_MESSAGE = "An error occured during INSERT statement.";

    public InsertException(Exception exception) { super(exception,DEFAULT_MESSAGE); }
    public InsertException() { super(DEFAULT_MESSAGE); }
    public InsertException(String message) { super(message); }
    public InsertException(Exception exception, String message) { super(exception, message); }
}
