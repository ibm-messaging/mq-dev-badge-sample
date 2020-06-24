package com.ibm.mq.exceptions;

/**
 * The main purpose is to bundle up many exceptions within the attempt
 * of saving and altering data for when a set amount of tickets is
 * requested.
 * @author Benjamin Brunyee
 * @version 1.0
 * @see com.ibm.mq.badge.EventManager
 * @see com.ibm.mq.events.Event
 */
public class CouldNotDeductTicketsException extends Exception {
    private static final long serialVersionUID = 1L;

    public CouldNotDeductTicketsException(String message) {
        super(message);
    }
}