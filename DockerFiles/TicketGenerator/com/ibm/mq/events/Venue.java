package com.ibm.mq.events;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * A {@link Venue} contains important information on an
 * {@link Event} or anything that requires a <tt>location</tt>,
 * <tt>date</tt> and <tt>time</tt>. A {@link Venue} is used to
 * create an {@link Event} with the correct porperties in a
 * compressed practice.
 * @author Benjamin Brunyee
 * @version 1.0
 * @see Event
 */
public class Venue {
    private String location = null;
    private LocalDate date = null;
    private LocalTime time = null;

    /**
     * Sets the {@link Venue} with the all properties.
     * @param location Location of a event.
     * @param date Date of the event.
     * @param time Time of the event.
     */
    public Venue(String location, LocalDate date, LocalTime time) {
        this.location = location;
        this.date = date;
        this.time = time;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getLocation() {
        return this.location;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public LocalTime getTime() {
        return this.time;
    }

    /**
     * Determines whether the current instance of {@link Venue}
     * has all it's properties set correctly.
     * @return True or false depending if the {@link Venue} is
     * set up correctly.
     */
    public boolean isComplete() {
        if (location != null && date != null && time != null) {
            return true;
        }
        else {
            return false;
        }
    }
}