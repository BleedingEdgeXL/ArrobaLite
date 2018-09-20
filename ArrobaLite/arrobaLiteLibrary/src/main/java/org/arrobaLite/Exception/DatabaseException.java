package org.arrobaLite.Exception;

/**
 * Created by d070169 on 12/10/2017.
 */

public class DatabaseException extends Exception {

      private static final String DEFAULT_MESSAGE = "Error executing SQL.";

    public DatabaseException(String message){ super(message); }
    public DatabaseException(Exception exception){ super(DEFAULT_MESSAGE,exception); }
    public DatabaseException(){ super(DEFAULT_MESSAGE); }
    public DatabaseException(Exception exception, String message){ super(message,exception); }
}
