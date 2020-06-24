package com.ibm.mq.badge;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.JMSRuntimeException;
import javax.jms.TextMessage;
import javax.jms.Destination;
import javax.xml.bind.JAXBException;

import com.ibm.mq.events.Advertisable;
import com.ibm.mq.events.Event;

/**
 * Used to connect to topics to publish adverts of an {@link Advertisable}
 * type. The main purpose of this class is to publish {@link Event} to a
 * topic for the client to recognise and respond to.
 *
 * <p>The {@link AdvertManager} uses a {@link EnvSetter} to set properties
 * for the MQ connection and then further store that connetion in a
 * private variable for later use.
 * @author Benjamin Brunyee
 * @version 1.0
 * @see EnvSetter
 * @see Advertisable
 * @see Event
 */

public class AdvertManager {
    private static final Logger logger = Logger.getLogger("com.ibm.mq.badge");

    /**
     * {@link EnvSetter} is responsible for settin the correct configuration
     * for the connection to MQ topics and queues.
     */
    private EnvSetter envSetter = null;

    /**
     * The <tt>JMSContext</tt> is the current MQ context. This is set when
     * the connection is created for the {@link AdvertManager}.
     */
    private JMSContext context = null;

    /**
     * A JMSProducer is what is required when wanting to send messages
     * to a queue or topic.
     */
    private JMSProducer producer = null;

    /**
     * The destination of where messages will be sent to.
     */
    private Destination destination = null;

    /**
     * Creates a {@link AdvertManager} with connection properties needed
     * for creating a {@code Context} that is connected to MQ.
     * @param envSetter The {@link EnvSetter} that contains the MQ
     * configuration.
     */
    public AdvertManager(EnvSetter envSetter) {
        this.envSetter = envSetter;
    }

    public EnvSetter getEnvSetter() {
        return envSetter;
    }

    public void setEnvSetter(EnvSetter envSetter) {
        this.envSetter = envSetter;
    }

    /**
     * Creates the connection and saves to local variables for later use.
     * This method uses the configured {@link EnvSetter} set for the
     * current instance of {@link AdvertManager}.
     * @param topicName Destination topic for where a message will be sent.
     */
    public void connect(String topicName) {
        context = envSetter.connect();
        if (context != null) {
            try {
                destination = context.createTopic(topicName);
                producer = context.createProducer();
                logger.finer("Connection for advert manager has been created");
            }
            catch (JMSRuntimeException e) {
                logger.log(Level.SEVERE, "An error occurred when trying to create producer/destination", e);
            }
        }
        else {
            logger.severe("Session for advert manager was not created properly. Exiting...");
        }
    }

    /**
     * Publishes a message to the topic specified when creating the
     * connection. Uses the connection most recently saved. The object
     * passed in should have a valid implementation of {@code toXML()}
     * and provide tags such as the ones set for {@link Event}.
     * @param event An object of type {@link Advertisable} to provide
     * the {@code toXml()} function.
     */
    public void publishAdvert(Advertisable event) {
        if (context != null && producer != null) {
            try {
                TextMessage message = context.createTextMessage(event.toXML());
                producer.send(destination, message);
            }
            catch (JMSRuntimeException e) {
                logger.log(Level.SEVERE, "A JMS exception occurred when trying to publish advert to topic", e);
            }
            catch (JAXBException e) {
                logger.log(Level.SEVERE, "A JAX exception occurred when trying to publish advert to topic", e);
            }
        }
        else {
            logger.warning("Connection details not yet set for AdvertManager");
            logger.warning("Could publish message");
        }
    }

    /**
     * Closes the current <tt>Session</tt> and <tt>JMSProducer</tt>
     * if not null.
     */
    public void closeConnections() {
        try {
            if (context != null) {
                context.close();
            }
        }
        catch (JMSRuntimeException e) {
            logger.log(Level.SEVERE, "Could not close session for AdvertManager", e);
        }
    }
}