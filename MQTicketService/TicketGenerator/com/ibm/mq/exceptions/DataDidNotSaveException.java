package com.ibm.mq.exceptions;

/**
 * Thrown when data could not be saved to a file in anyway.
 * @author Benjamin Brunyee
 * @version 1.0
 */
public class DataDidNotSaveException extends Exception {
    private static final long serialVersionUID = 1L;

    public DataDidNotSaveException(String message) {
        super(message);
    }

    public DataDidNotSaveException(String message, Throwable err) {
        super(message, err);
    }
}