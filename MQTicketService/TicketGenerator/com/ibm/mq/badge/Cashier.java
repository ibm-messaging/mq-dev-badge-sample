package com.ibm.mq.badge;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.Destination;

import com.ibm.mq.exceptions.CouldNotDeductTicketsException;

/**
 * Used to connect to topics and queues to listen for messages and create
 * appropriate responses. Main use of this class is to listen to responses
 * put on a {@code purchase} queue and then send a response to a
 * {@code confirmation} queue.
 *
 * <p>Connection details are provided by the
 * {@link EnvSetter} for the current instance of {@link Cashier}.
 *
 * <p>Some default standards for the message being received should be:
 * <ul>
 * <li>XML Format
 * <li>Contains <tt>eventID</tt> tag - What eventID is being requested.\n
 * <li>Contains <tt>numberRequested</tt> tag - How many tickets for that event
 * is being requested.
 * </ul>
 * @author Benjamin Brunyee
 * @version 1.0
 * @see EnvSetter
 * @see EventManager
 */

public class Cashier implements Runnable {
    /**
     * Flag used to alert the cashier to stop listening and start closing
     * connections.
     */
    volatile boolean cancel = false;
    private static final Logger logger = Logger.getLogger("com.ibm.mq.badge");
    private boolean listeningToResponse = false;

    /**
     * {@link EventManager} is responsible for dealing with ticket requests
     * such as subtracting tickets and saving to a file.
     */
    private EventManager eventManager = null;

    /**
     * {@link EnvSetter} is responsible for settin the correct configuration
     * for the connection to MQ topics and queues.
     */
    private EnvSetter envSetter = null;

    /**
     * The Context is the current MQ context. This is set when
     * the connection is created for the {@link Cashier}.
     */
    private JMSContext context = null;

    /**
     * A JMSProducer is what is required when wanting to send messages
     * to a queue or topic.
     */
    private JMSProducer producer = null;

    /**
     * A JMSConsumer is what is required when receiving messages from
     * a queue or topic.
     */
    private JMSConsumer consumer = null;

    /**
     * Destination of where responses will be sent to.
     */
    private Destination sendTo = null;

    /**
     * This constructor will provide the {@link Cashier} with all the details
     * to make a successful pipeline of information and allow tickets to be
     * through the cashier. Without a {@link EventManager} no tickets would
     * be deducted from memory or saved. Without a {@link EnvSetter} no
     * details would be provided for the MQ connections.
     * @param eventManager The {@link EventManager} that will be dealing with
     * ticket subtractions.
     * @param envSetter The {@link EnvSetter} that will be providing MQ
     * connection details.
     */
    public Cashier(EventManager eventManager, EnvSetter envSetter) {
        this.eventManager = eventManager;
        this.envSetter = envSetter;
    }

    /**
     * Creates the base {@link Cashier}. A {@link EventManager} will be
     * needed if dealing with requests is the aim. A {@link EnvSetter}
     * will also need to be set or this instance as it will provide the
     * {@link Cashier} with sufficient details for creating MQ connections.
     */
    public Cashier() {}

    public EnvSetter getEnvSetter() {
        return envSetter;
    }

    public void setEnvSetter(EnvSetter envSetter) {
        this.envSetter = envSetter;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    /**
     * Checks whether the cashier is ready and is listening for a response
     * to be put on the queue.
     * @return True if the cashier is listening
     */
    public boolean isListeningToResponse() {
        return listeningToResponse;
    }

    /**
     * Provides the ability to start this class as a thread for background
     * processing.
     */
    public void run() {
        Thread.currentThread().setName("Cashier");
        connect("purchase", "confirmation");
        waitForRequest();
    }

    /**
     * Stops the cashier from listening to anymore messages and moves onto
     * closing the connections that are open for a clean shutdown.
     */
    public void close() {
        cancel = true;
        logger.finest("Close flag set for cashier to signal for the connections to close");
        while (true) {
            if (!isListeningToResponse()) {
                closeConnection();
                break;
            }
            else {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Interrupted sleep when waiting to close connections", e);
                }
            }
        }
    }

    /**
     * Connects to 2 queues that are specified. The {@code receiveFrom} queue
     * is where the {@link Cashier} would get its client's requests from.
     * Following from that is the {@code sendTo} queue which is the destination
     * of where a response will be sent.
     * @param receiveFrom A destination to receive messages from.
     * @param sendTo A destination to send messages to.
     */
    public void connect(String receiveFrom, String sendTo) {
        if (envSetter != null) {
            context = envSetter.connect();
            if (context != null) {
                try {
                    // Creating the consumer which messages will be received from.
                    consumer = context.createConsumer(context.createQueue(receiveFrom));

                    // Creating the producer which messages will be sent to.
                    this.sendTo = context.createQueue(sendTo);
                    producer = context.createProducer();
                    logger.finer("Connection for cashier has been created");
                }
                catch (JMSRuntimeException e) {
                    logger.log(Level.SEVERE, "An error occurred when trying to create producer/consumer", e);
                }
            }
            else {
                logger.severe("Context was not created. Exiting...");
            }
        }
        else {
            logger.severe("No EnvSetter set for the Cashier. Could not create MQ connections");
        }
    }

    /**
     * Listens to a queue and waits for a message to be received. When a message
     * is received then it will process the message and create a response to be
     * published to another queue. Desintations are specified in the creation of
     * te Cashier connections.
     */
    public void waitForRequest() {
        if (consumer != null && producer != null && context != null) {
            cancel = false;
            logger.info("Starting to listen for ticket requests");
            while (!cancel) {
                listeningToResponse = true;
                Message message = null;
                try {
                    message = consumer.receive(10000); // Wait 10 seconds for a message. If no message is received, the method returns null.
                }
                catch (JMSRuntimeException e) {
                    logger.log(Level.SEVERE, "Could not retrieve message from queue", e);
                }

                // Creates and sends a response depending on the message received.
                if (message != null) {
                    logger.info("Received message");
                    logger.info("Message received: " + message);
                    processMessage(message);
                }
            }
            logger.info("Stopped listening to responses");
            listeningToResponse = false;
        }
        else {
            logger.warning("Context was not created properly. Nothing to listen to");
        }
    }

    /**
     * Closes the connections made when listening for messages.
     * @param context <tt>Context</tt> to be closed down.
     * @param producer <tt>JMSProducer</tt> to be closed down.
     * @param consumer <tt>JMSConsumer</tt> to be closed down.
     */
    private void closeConnection() {
        try {
            if (consumer != null) {
                consumer.close();
            }
        }
        catch (JMSRuntimeException e) {
            logger.log(Level.SEVERE, "Could not close consumer down", e);
        }
        try {
            if (context != null) {
                context.close();
            }
        }
        catch (JMSRuntimeException e) {
            logger.log(Level.SEVERE, "Could not close context for cashier", e);
        }
        logger.finer("Connections for cashier have closed");
    }

    /**
     * Processes the message received and creates a response depending
     * on the request and abilities of the application.
     * @param context The current connection context.
     * @param producer The producer that will be used to send the request.
     * @param message The message received.
     */
    private void processMessage(Message message) {
        // Find the event that was requested for
        Integer eventID = Integer.parseInt(getValueFromMessage(message, "eventID"));

        // Find the number of tickets that were requested
        Integer numberRequested = Integer.parseInt(getValueFromMessage(message, "numberRequested"));

        /*
        * Saving the data to a file and editing memory if event manager is set.
        * Then create a response message and send to the "confirmation" queue.
        */
        TextMessage responseMessage = null;
        try {
            if (eventManager != null) {
                try {
                    this.eventManager.subtractTickets(eventID, numberRequested);
                    logger.info("Creating accepted response");
                    responseMessage = context.createTextMessage("Accepted");
                }
                catch (CouldNotDeductTicketsException e) {
                    logger.info(e.getMessage() + " - Creating rejection response");
                    responseMessage = context.createTextMessage("Rejected");
                }
            }
            else {
                logger.fine("No event manager was set for cashier so tickets could not be deducted");
                logger.info("Creating rejection response");
                responseMessage = context.createTextMessage("Rejected");
            }
        }
        catch (JMSRuntimeException e) {
            logger.warning("Could not create response message, check if connection was created for cashier.");
            e.printStackTrace();
        }

        // Sending response message
        if (sendTo != null) {
            try {
                responseMessage.setJMSCorrelationID(message.getJMSCorrelationID());
                producer.send(sendTo, responseMessage);
                logger.info("Sent response");
            }
            catch (JMSException | JMSRuntimeException e) {
                logger.warning("Could not send message. Encountered a JMS Exception");
                e.printStackTrace();
            }
        }
        else {
            logger.warning("There is no destination of where to send messages to. Not sending anything");
        }
    }

    /**
     * Gets a tag value from a message whilst handling exceptions.
     * @param xml The XML message
     * @param tagName The tag name that is requested.
     * @return A value that corresponds to the tag name requested.
     */
    private String getValueFromMessage(Message xml, String tagName) {
        String temp = null;
        try {
            temp = getTagValue(xml.getBody(String.class), tagName);
        }
        catch (ArrayIndexOutOfBoundsException | JMSRuntimeException | JMSException e) {
            logger.warning("Could not get value '" + tagName + "' from XML");
            if (e instanceof JMSRuntimeException) {
                logger.log(Level.SEVERE, "Encountered JMS exception", e);
            }
        }
        return temp;
    }

    /**
     * Gets the tag value from XML
     * @param xml The XML string.
     * @param tagName The tag name that is requested.
     * @return A value that corresponds to the tag name requested.
     */
    private String getTagValue(String xml, String tagName){
        return xml.split("<"+tagName+">")[1].split("</"+tagName+">")[0];
    }
}