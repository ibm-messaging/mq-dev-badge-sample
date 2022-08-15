package com.ibm.mq.badge;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.ibm.mq.events.Event;
import com.ibm.mq.events.Venue;
import com.ibm.mq.exceptions.CouldNotDeductTicketsException;
import com.ibm.mq.exceptions.DataDidNotSaveException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Deals with the creation and alterations of {@link Event}'s.
 * The {@link EventManager} also deals with the reading of data
 * and the formatting of the data to be written.
 *
 * <p>The {@link EventManager} also deals with processing
 * requests such as ticket deductions for {@link Event}s.
 * @author Benjamin Brunyee
 * @version 1.0
 * @see Event
 */

public class EventManager {
    private static final Logger logger = Logger.getLogger("com.ibm.mq.badge");

    /**
     * {@link StorageManager} that may be used in the reading and
     * writing of data.
     */
    private StorageManager storageManager = null;

    /**
     * The list of events that are created by this
     * {@link EventManager}
     */
    private HashMap<Integer, Event> bookableEvents = new HashMap<>();

    /**
     * The initial setup from a file flag.
     */
    private boolean setup = false;

    /**
     * Used as a counter for creating event ID's
     */
    private AtomicInteger uniqueID = new AtomicInteger(0);


    /**
     * Creates an {@link EventManager} with a specified
     * {@link StorageManager} for reading and writing data
     * too.
     * @param storageManager The {@link StorageManager} that
     * will provide details such as filename and provide the
     * ability to save new data.
     */
    public EventManager(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    /**
     * Creates the base {@link EventManager} for future
     * adaptations and alterations.
     */
    public EventManager() {}

    public HashMap<Integer, Event> getBookableEvents() {
        return bookableEvents;
    }

    public void setBookableEvents(HashMap<Integer, Event> bookableEvents) {
        this.bookableEvents = bookableEvents;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    /**
     * Provides the {@link EventManager} with a {@link StorageManager}.
     * Allows for the current {@link EventManager} to setup its
     * events using the file provided by the {@link StorageManager}.
     * @param storageManager The {@link StorageManager} with a
     * specified file that would be used in reading and writing.
     */
    public void setStorageManager(StorageManager storageManager) {
        this.storageManager = storageManager;
        setup = false;
    }

    /**
     * Sets up the events from the current {@link StorageManager}
     * specified.
     * @param key The key under which the events array is stored under
     */
    public void setupAndSetEvents(String key) {
        setupAndSetEvents(storageManager, key);
    }

    /**
     * Sets up the events with a specified {@link StorageManager}.
     * @param storageManager The {@link StorageManager} which it
     * will read the data from
     * @param key The under which the events array is stored under
     */
    public void setupAndSetEvents(StorageManager storageManager, String key) {
        if (!setup) {
            JSONArray eventsArray = null;
            if (storageManager != null && storageManager.getFilename() != null) {
                eventsArray = getJsonArray(storageManager.getFilename(), key);
            }
            else {
                logger.warning("Could not get data from null storage manager or " +
                    "a null filename for specified storage manager");
            }

            // Iterating through each event
            if (eventsArray != null) {
                Iterator<?> iterator = eventsArray.iterator();
                while (iterator.hasNext()) {
                    JSONObject eventObject = (JSONObject) iterator.next();
                    createEventFromJson(eventObject);
                }
            }
            else {
                logger.finer("No storage manager was set for the event manager so " +
                    "no data could be gathered resulting in no events being set up from data");
            }
        }
        else {
            logger.warning("Setup has already taken place.");
        }
    }

    /**
     * Subtracts the tickets requested from a client and saves the data internally
     * (and to a file if there is a correctly setup {@link StorageManager} set for
     * the current {@link EventManager}).
     * @param eventID ID of the event that is being requested.
     * @param numberRequested Number of tickets that is requested for said event.
     * @throws CouldNotDeductTicketsException Thrown when tickets quantity for an
     * event could not be altered further
     */
    public void subtractTickets(Integer eventID, Integer numberRequested) throws CouldNotDeductTicketsException {
        subtractTickets(eventID, numberRequested, storageManager);
    }

    /**
     * Subtracts the tickets requested from a client and saves the data internally
     * (and to a file if there is a correctly setup {@link StorageManager}
     * specified).
     * @param eventID ID of the event that is being requested.
     * @param numberRequested Number of tickets that is requested for said event.
     * @param storageManager The {@link StorageManager} from which it will save
     * data to.
     * @throws CouldNotDeductTicketsException Thrown when tickets quantity for an
     * event could not be altered further
     */
    public void subtractTickets(Integer eventID, Integer numberRequested, StorageManager storageManager) throws CouldNotDeductTicketsException {
        if (numberRequested < 0) {
            throw new CouldNotDeductTicketsException("Number requested was a negative number");
        }
        Event bookableEvent = bookableEvents.get(eventID);
        Integer ticketsLeft = bookableEvent.getCapacity() - numberRequested;
        logger.fine("Processing request for event: " + bookableEvent.getTitle());
        logger.fine("Number requested was: " + String.valueOf(numberRequested));
        logger.fine("There would be " + String.valueOf(ticketsLeft) + " tickets left");
        if (ticketsLeft > 0) {
            if (storageManager != null) {
                try {
                    // Saving the data to a new file
                    storageManager.saveNewData(eventsToJSON(eventID, ticketsLeft));
                }
                catch (JSONException | DataDidNotSaveException e) {
                    throw new CouldNotDeductTicketsException(e.getMessage());
                }
            }
            else {
                logger.info("Storage manager has not been set so data will NOT be saved to a file");
            }
            bookableEvent.setCapacity(ticketsLeft);
            logger.info("There are now " + String.valueOf(bookableEvent.getCapacity()) + " for event: " + bookableEvent.getTitle());
        }
        else {
            throw new CouldNotDeductTicketsException("There is not enough tickets left");
        }
    }

    /**
     * Creates a JSON object with details from the events that are stored in the
     * current {@link EventManager}.
     * @param eventID ID of the event being requested
     * @param ticketsLeft Number of tickets left after the request.
     * @return A JSON object that contains all the information for the events stored.
     * @throws JSONException Thrown when an error occurred when creating JSON objects
     * and JSON Arrays.
     */
    private JSONObject eventsToJSON(Integer eventID, Integer ticketsLeft) throws JSONException {
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();
        try {
            ja = new JSONArray();
            for (Integer id : bookableEvents.keySet()) {
                Event bookableEvent = bookableEvents.get(id);
                // Creating the basic variables of each bookable event
                JSONObject parent = new JSONObject();
                parent.put("Name", bookableEvent.getTitle());
                parent.put("Location", bookableEvent.getLocation());
                parent.put("Time", String.valueOf(bookableEvent.getTime()));
                parent.put("Date", String.valueOf(bookableEvent.getDate()));

                // If the event ID is equal to the one requested
                if (eventID == id) {
                    logger.finest("Event: " + bookableEvent.getTitle() + " is being modified");
                    parent.put("Ticket Quantity", ticketsLeft);
                }
                else {
                    parent.put("Ticket Quantity", bookableEvent.getCapacity());
                }
                ja.put(parent);
            }
        }
        catch (JSONException e) {
            ja = null;
            String errorMessage = "Error when trying to create JSON objects";
            logger.warning(errorMessage);
            throw e;
        }

        if (ja != null) {
            jo.put("Events", ja);
        }
        return jo;
    }

    /**
     * Creates an event from a JSON object that contains all the details needed.
     * Each event created from this is added to the total event list for this
     * current {@link EventManager}
     * @param eventObject The JSON object that contains the event details.
     */
    private void createEventFromJson(JSONObject eventObject) {
        Integer eventID = generateID();
        String name = getValueFromJson(eventObject, "Name");
        String location = getValueFromJson(eventObject, "Location");
        LocalTime time = getTimeFromString(getValueFromJson(eventObject, "Time"));
        LocalDate date = getDateFromString(getValueFromJson(eventObject, "Date"));
        Integer ticketQuantity = null;
        try {
            ticketQuantity = Integer.parseInt(getValueFromJson(eventObject, "Ticket Quantity"));
        }
        catch (NumberFormatException e) {
            logger.warning("Event '" + String.valueOf(eventID) + "' does not have a valid ticket quantity");
        }

        if (name != null && location != null && time != null &&
        date != null && ticketQuantity != null) {
            // Creates a venue containing specific information
            Venue venue = createVenue(location, date, time);
            // Creates the event with all the properties bundled together
            addEvents(eventID, createBookableEvent(eventID, name, venue, ticketQuantity));
            logger.fine("Event Created: " + name + " ID=" + eventID);
        }
        else {
            String eventNotice = "Event not fully complete";
            if (eventID != null) {
                logger.fine(eventNotice + ": " + eventID);
            }
            else if (name != null) {
                logger.fine(eventNotice + ": " + name);
            }
            else {
                logger.fine(eventNotice);
            }
            logger.finer("Info needed for a bookable event: EventID, Name, Location, Date, Time, TicketQuantity[Standard[Price, Quantity]] ([] = JSON array)");
        }
    }

    /**
     * Adds an event ID and bookable event to the current {@code HashMap}
     * of events for the current instance of {@link EventManager}.
     * @param eventID Event ID for the event to be added.
     * @param bookableEvent Event to be added to the {@code HashMap}.
     */
    public void addEvents(Integer eventID, Event bookableEvent) {
        HashMap<Integer, Event> temp = getBookableEvents();
        temp.put(eventID, bookableEvent);
        setBookableEvents(temp);
    }

    /**
     * Adds a {@code HashMap} of events to the current {@code HashMap}
     * of events for the current instance of {@code EventManager}.
     * @param bookableEvents {@code HashMap} of events to add to
     * the {@code HashMap}.
     */
    public void addEvents(HashMap<Integer, Event> bookableEvents) {
        HashMap<Integer, Event> temp = getBookableEvents();
        for (Integer key : bookableEvents.keySet()) {
            temp.put(key, bookableEvents.get(key));
        }
        setBookableEvents(temp);
    }

    /**
     * Gets a JSON array from a file specified.
     * @param filename File to get the array from.
     * @param arrayToGet The key of which the array is under.
     * @return Returns a JSONArray or null if there was no array found.
     */
    private JSONArray getJsonArray(String filename, String arrayToGet) {
        JSONArray ja = null;
        try {
            JSONObject jo = new JSONObject(new Scanner(new File(filename)).useDelimiter("\\Z").next());
            ja = (JSONArray) jo.getJSONArray(arrayToGet);
        }
        catch (NoSuchElementException e) {
            logger.warning("There is not data in '" + filename + "'");
        }
        catch(JSONException e) {
            logger.warning("Data in file '" + filename + "' could not be parsed as a JSON Array");
            logger.warning(e.toString());
        }
        catch (FileNotFoundException e) {
            logger.warning("File: " + filename + " was not found");
        }
        return ja;
    }

    /**
     * Creates a {@link Event} from details provided.
     * @param eventID ID of the {@link Event}.
     * @param eventName Name of the {@link Event}.
     * @param venue Contains properties such as location, date and time.
     * @param ticketQuantity A number of tickets available for the
     * {@link Event}.
     * @return A {@link Event} containing all the
     * information provided.
     */
    private Event createBookableEvent(Integer eventID, String eventName, Venue venue, Integer ticketQuantity) {
        Event bookableEvent = new Event();
        bookableEvent.setEventID(eventID);
        bookableEvent.setTitle(eventName);
        bookableEvent.setTime(String.valueOf(venue.getTime()));
        bookableEvent.setDate(String.valueOf(venue.getDate()));
        bookableEvent.setLocation(venue.getLocation());
        bookableEvent.setCapacity(ticketQuantity);
        logger.finest("Bookable event created with eventID '" + eventID + "' with name of '" + eventName + "'");
        return bookableEvent;
    }

    /**
     * Creates a {@link Venue} from details provided
     * @param location Location of the {@link Venue}
     * @param date Date of the {@link Venue}
     * @param time Time of the {@link Venue}
     * @return Return a fully set Venue containing the details
     * provided.
     */
    private Venue createVenue(String location, LocalDate date, LocalTime time) {
        Venue venue = new Venue(location, date, time);
        logger.finest("Venue created at location '" + location + "' at time '" + time +
            "' at date '" + date + "'");
        return venue;
    }

    /**
     * Generates an ID that is unique to each session.
     * @return An integer that is incremented on each call.
     */
    private int generateID() {
        return uniqueID.getAndIncrement();
    }

    /**
     * Gets a date from a string, handles errors in the process.
     * @param stringHolder String containing a potential date.
     * @return A date that is gathered from the string. Can return
     * null.
     */
    private LocalDate getDateFromString(String stringHolder) {
        LocalDate date = null;
        if (!isNull(stringHolder)) {
            try {
                date = LocalDate.parse(stringHolder);
            }
            catch (DateTimeParseException e) {
                logger.warning("Date from data is not in a valid format");
                logger.warning(e.toString());
            }
        }
        else {
            logger.warning("Could not get date from null string");
        }
        return date;
    }

    /**
     * Gets a time from a string, handles errors in the process.
     * @param stringHolder String containing a potential time.
     * @return A time that is gathered from the string. Can return
     * null.
     */
    private LocalTime getTimeFromString(String stringHolder) {
        LocalTime time = null;
        if (!isNull(stringHolder)) {
            try {
                time = LocalTime.parse(stringHolder);
            }
            catch (DateTimeParseException e) {
                logger.warning("Time from data is not in a valid format");
                logger.warning(e.toString());
            }
        }
        else {
            logger.warning("Could not get time from null string");
        }
        return time;
    }

    /**
     * Return an interator specified from a JSON Object.
     * @param holderObj The object that contains the array for the
     * iterator.
     * @param iteratorToGet A key of which the array is under.
     * @return Returns an iterator for the JSON array.
     */
    private Iterator<?> getJSONIterator(JSONObject holderObj, String iteratorToGet) {
        try {
            return ((JSONArray) holderObj.get(iteratorToGet)).iterator();
        }
        catch (JSONException e) {
            logger.warning("Was not able to get an iterator for '" + iteratorToGet + "'");
            return null;
        }
    }

    /**
     * Gets a value from a JSON object with providing the key.
     * @param holderObj The JSON object that the value is gathered from.
     * @param key The key of the value requested.
     * @return A string of the key-value requested.
     */
    private String getValueFromJson(JSONObject holderObj, String key) {
        try {
            return String.valueOf(holderObj.get(key));
        }
        catch (JSONException e) {
            logger.warning("Was not able to get '" + key + "' from json");
            return null;
        }
    }

    /**
     * Checks if a string is null
     * @param str The string to check.
     * @return A true boolean if the string provided is null.
     */
    private boolean isNull(String str) {
        if (str != null && !str.isEmpty()) {
            return false;
        }
        else {
            return true;
        }
    }
}