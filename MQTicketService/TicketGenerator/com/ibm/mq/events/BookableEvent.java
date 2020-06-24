package com.ibm.mq.events;

import java.util.logging.Logger;

/**
 * NOT BEING USED
 */
public class BookableEvent implements Advertisable {
    private static final Logger logger = Logger.getLogger("com.ibm.mq.badge");
    private Venue venue = null;
    private Integer ticketQuantity = null;
    private String eventName = null;
    private Integer eventID = null;

    public BookableEvent(Integer eventID, String eventName, Venue venue, Integer ticketList) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.venue = venue;
        this.ticketQuantity = ticketList;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return this.eventName;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public Venue getVenue() {
        return this.venue;
    }

    public void setTicketQuantity(Integer ticketList) {
        this.ticketQuantity = ticketList;
    }

    public Integer getTicketQuantity() {
        return this.ticketQuantity;
    }

    public Integer getEventID() {
        return eventID;
    }

    public void setEventID(Integer eventID) {
        this.eventID = eventID;
    }

    public Event toAdvert() {
        Event advert = new Event();
        advert.setEventID(getEventID());
        advert.setTitle(getEventName());
        advert.setLocation(getVenue().getLocation());
        advert.setTime(String.valueOf(getVenue().getTime()));
        advert.setDate(String.valueOf(getVenue().getDate()));
        advert.setCapacity(ticketQuantity);
        logger.fine("Advert: " + advert.getTitle());
        return advert;
    }
}