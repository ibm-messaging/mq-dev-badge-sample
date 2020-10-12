package com.ibm.mq.badge;

import com.ibm.mq.events.*;
import java.util.HashMap;
import java.util.logging.*;
import java.io.IOException;
import java.util.Date;

/**
 * The purpose of this application is to demonstrate how MQ can be used in a
 * ticket seller scenario. Using connections to queues and topics to send/receive
 * messages to/from.
 *
 * @author Benjamin Brunyee
 * @see StorageManager
 * @see EventManager
 * @see Cashier
 * @version 1.0
 */
public class Manager {
    /**
     * Default level of logging - ALL
     */
    private static final Level LOGLEVEL = Level.ALL;

    /**
     * Creating a new logger.
     */
    private static final Logger logger = Logger.getLogger("com.ibm.mq.badge");

    /**
     * The {@code main} of the application.
     *
     * <p>The {@code main} of the application is where the creation
     * of {@link com.ibm.mq.badge.StorageManager}, {@link com.ibm.mq.badge.EventManager}
     * and {@link com.ibm.mq.badge.Cashier} resides.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        Thread.currentThread().setName("Main");
        initialiseLogging();

        // Setting storage manager properties and checking env
        StorageManager storageManager = new StorageManager("Data.json");

        // Setting up the event manager and creating initial events
        logger.fine("-----");
        logger.info("Creating Events");
        EventManager eventManager = new EventManager(storageManager);
        eventManager.setupAndSetEvents("Events");
        HashMap<Integer, Event> bookableEvents = eventManager.getBookableEvents();
        logger.info(("New total of " + bookableEvents.values().size() + " events"));
        logger.fine("-----");

        /*
        * Creating the MQ Env Setter for the cashier and advertiser as they will
        * be the classes making the mq connections.
        */
        EnvSetter envSetter = new EnvSetter("mqConfig.json");
        Cashier cashier = new Cashier(eventManager, envSetter);
        AdvertManager advertManager = new AdvertManager(envSetter);
        advertManager.connect("newTickets");

        /*
        * Start the cashier and wait for the cashier to listen to requests
        */
        new Thread(cashier).start();
        // Continuous loop for a constant check if the cashier is listening
        while (true) {
            if (cashier.isListeningToResponse()) {

                // For every event created, publish it in 30 second intervals
                for (Event bookableEvent : bookableEvents.values()) {
                    logger.info("-----");
                    logger.info("Publishing advert...");

                    advertManager.publishAdvert(bookableEvent);

                    logger.info("Advert for '" + bookableEvent.getTitle() + "' has been published");
                    logger.fine("Waiting for 30 seconds before publishing next advert");
                    logger.info("-----");
                    try {
                        // Sleep for 30 seconds before publishing the next event
                        Thread.sleep(30000);
                    }
                    catch (InterruptedException e) {
                        logger.log(Level.SEVERE, "Interrupted the 10 second wait inbetween publishing adverts", e);
                    }
                }
                // Break out of the while loop after all events have been published.
                break;
            }
            else {
                try {
                    // If the cashier is not ready yet then wait 1 second.
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    logger.info("Could not sleep for 1 second");
                    e.printStackTrace();
                }
            }
        }

        /*
        * Closing all the connections created by the classes that were initiated
        * here.
        */
        logger.info("Closing connections and stopping threads");
        advertManager.closeConnections();
        cashier.close();
        envSetter.closeConnections();
        logger.info("Closed all connections");
    }

    /**
     * Logging is initialised here with console
     * and file writing handlers. The handlers added are:
     * <>ul>
     * <li>Console handler
     * <li>File handler
     * <li>Severe handler
     * </ul>
     *
     * <p>Logging is preformatted as
     * [<i>DATE TIME</i>]-[<i>THREADNAME</i>]-[<i>LOGLEVEL</i>]-[<i>CLASS</i>]-[<i>METHOD</i>]:
     * <i>Message</i>
     */
    private static void initialiseLogging() {
        // Removing the initial handler for the logger
        Logger defaultLogger = Logger.getLogger("");
        Handler[] handlers = defaultLogger.getHandlers();
        defaultLogger.removeHandler(handlers[0]);

        /*
        * Setting the format in which logging will be printed to:
        * [DATE TIME]-[THREADNAME]-[LOGLEVEL]-[CLASS]-[METHOD]: Message
        */
        Formatter format = new SimpleFormatter() {
            private static final String FORMAT = "[%1$tF %1$tT]-[%2$-7s]-[%3$-7s]-[%4$-14s]-[%5$-19s]: %6$s %n";

            private boolean throwing = false; // The log is not throwing an exception by default.
            private int stackTraceElement = 8; // Default stack trace element.
            @Override
            public synchronized String format(LogRecord lr) {
                isThrowing(lr); // Is the log record including a throwable exception.
                return String.format(FORMAT,
                        new Date(lr.getMillis()),
                        Thread.currentThread().getName(),
                        lr.getLevel().getLocalizedName(),
                        Thread.currentThread().getStackTrace()[stackTraceElement].getClassName().toString().replace("com.ibm.mq.badge.", ""),
                        Thread.currentThread().getStackTrace()[stackTraceElement].getMethodName(),
                        getMessage(lr)
                );
            }

            // Is the log record including a throwable exception.
            private void isThrowing(LogRecord lr) {
                if (lr.getThrown() != null) {
                    throwing = true;
                    stackTraceElement = 7;
                }
            }

            // If log record is inlucding an exception then add stack trace to the message.
            private String getMessage(LogRecord lr) {
                String message = lr.getMessage();
                if (throwing) {
                    for (StackTraceElement str : lr.getThrown().getStackTrace()) {
                        message += "\n" + str;
                    }
                }
                return message;
            }
        };

        Formatter consoleFormatter = new SimpleFormatter() {
            private static final String FORMAT = "[%1$tF %1$tT]-[%2$-7s]: %3$s %n";

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(FORMAT,
                        new Date(lr.getMillis()),
                        Thread.currentThread().getName(),
                        getMessage(lr)
                );
            }

            // If log record is inlucding an exception then add stack trace to the message.
            private String getMessage(LogRecord lr) {
                String message = lr.getMessage();
                if (lr.getThrown() != null) {
                    for (StackTraceElement str : lr.getThrown().getStackTrace()) {
                        message += "\n" + str;
                    }
                }
                return message;
            }
        };

        // Writing the log to the console
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(consoleFormatter);
        consoleHandler.setLevel(LOGLEVEL);

        // Writing the log to a file
        try {
            FileHandler fileHandler = new FileHandler("ResellerLog.txt");
            fileHandler.setFormatter(format);
            fileHandler.setLevel(LOGLEVEL);
            logger.addHandler(fileHandler);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
        logger.addHandler(consoleHandler);
        logger.addHandler(new SevereHandler());

        logger.setLevel(LOGLEVEL);
        logger.finest("Logger initialised");
    }
}