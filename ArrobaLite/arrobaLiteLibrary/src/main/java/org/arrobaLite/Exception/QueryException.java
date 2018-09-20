package org.arrobaLite.Exception;

/**
 * Created by d070169 on 12/10/2017.
 */

public class QueryException extends DatabaseException {

     private static final String DEFAULT_MESSAGE = "An error occured during a QUERY statement.";

    public QueryException(Exception exception) { super(exception,DEFAULT_MESSAGE); }
    public QueryException(String message) { super(message); }
    public QueryException() { super(DEFAULT_MESSAGE); }
    public QueryException(Exception exception, String message) { super(exception, message); }
}
