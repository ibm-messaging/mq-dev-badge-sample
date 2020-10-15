package com.ibm.mq.badge;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.JMSSecurityRuntimeException;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Deals with connections to MQ. This class is responsible for the config and
 * creation of connections to topics or queues. The default file requested by
 * this class must obey the following:
 * <ul>
 * <li>Follow the <b>JSON</b> format.
 * <li>Provide tags:
 *  <ul>
 *  <li><tt>HOST</tt>
 *  <li><tt>PORT</tt>
 *  <li><tt>CHANNEL</tt>
 *  <li><tt>QMGR</tt>
 *  <li><tt>USER</tt>
 *  <li><tt>PASSWORD</tt>
 *  <li><tt>SUBSCRIPTION_NAME</tt>
 *  </ul>
 * </li>
 * </ul>
 * @author Benjamin Brunyee
 * @version 1.0
 */

public class EnvSetter {
    private static final Logger logger = Logger.getLogger("com.ibm.mq.badge");

    /**
     * The JSON object that holds information from an <tt>mqConfig</tt> file
     * used to set the configuration for the MQ connection.
     */
    private JSONObject mqEnv = null;
    private String filename = null;

    /**
     * A list of all open connections created by this instance of
     * {@link EnvSetter}
     */
    private ArrayList<JMSContext> openContexts = new ArrayList<>();

    /**
     * Default details for the MQ connection.
     */
    private String HOST = "localhost"; // Host name or IP address
    private int PORT = 1414; // Listener port for your queue manager
    private String CHANNEL = "DEV.APP.SVRCONN"; // "SYSTEM.DEF.SVRCONN";
    private String QMGR = "QM1"; // Queue manager name
    private String USER = "app";
    private String PASSWORD = "pass";
    private String SUBSCRIPTION_NAME = "SampleSubscriber";

    /**
     * Creates the {@link EnvSetter} with a file that provides details for the
     * MQ connection in the format of <b>JSON</b>.
     * @param filename The filename which contains all the details for the
     * MQ connections.
     */
    public EnvSetter(String filename) {
        this.filename = filename;
    }

    /**
     * Creates a base {@link EnvSetter} which with no filename set, will create
     * an MQ connection using the default details provided in the source code.
     * These details will be wrong so providing a file with connection
     * configuration is the way to go.
     */
    public EnvSetter() {}

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    /**
     * Creates a MQ session with the details provided for this class. The
     * session created will be returned meaning that classes that will be
     * using queues and topics, for message transfer, can use this class to
     * create their connections.
     * @return A JMSContext that can be used in sending and receiving messages from
     * queues or topics.
     */
    public JMSContext connect() {
        if (filename != null) {
            setMQEnv(filename);
            setMQVariables();
        }
        else {
            logger.warning("Cannot set MQ environment for null filename, will be using default");
        }
        JmsConnectionFactory cf = createFactory();
        setJMSProperties(cf);
        JMSContext context = null;
        if (cf != null) {
            try {
                logger.finer("Creating connection session");
                context = cf.createContext(JMSContext.AUTO_ACKNOWLEDGE);
                context.start();
                openContexts.add(context);
                logger.finer("JMS session connection initialised successfully");
            }
            catch (JMSRuntimeException e) {
                if (e instanceof JMSSecurityRuntimeException) {
                    logger.log(Level.SEVERE, "Credentials are incorrect, could not create context", e);
                }
                logger.log(Level.SEVERE, "Couldn't create connection session", e);
            }
        }
        return context;
    }

    /**
     * Closes all the open connections that were created by this instance of
     * {@link EnvSetter}.
     */
    public void closeConnections() {
        logger.finer("Attempting to close all open connections for EnvSetter");
        for (JMSContext context : openContexts) {
            try {
                if (context != null) {
                    context.close();
                }
            }
            catch (Exception e) {
                logger.log(Level.SEVERE, "Could not close connection for EnvSetter", e);
            }
        }
    }

    /**
     * Creates a ConnectionFactory which will be used to create a connection to MQ.
     * @return A ConnectionFactory that encapsulates a set of connection configuration parameters
     */
    private JmsConnectionFactory createFactory() {
        JmsConnectionFactory cf = null;
        try {
            logger.fine("Creating connection factory");
            // Creating connection factory
            JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
            cf = ff.createConnectionFactory();
            logger.finer("Connection factory has been created");
        }
        catch (JMSException e) {
            logger.log(Level.SEVERE, "An error occurred when trying to create connection factory", e);
        }
        return cf;
    }

    /**
     * Sets the properties of the ConnectionFactory from default details or the details
     * provided.
     * @param cf The ConnectionFactory that will have it's properties changed.
     */
    private void setJMSProperties(JmsConnectionFactory cf) {
        try {
            // Setting properties
            logger.fine("Setting connection factory properties");
            cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, HOST);
            cf.setIntProperty(WMQConstants.WMQ_PORT, PORT);
            // cf.setIntProperty(WMQConstants.WMQ_CLIENT_RECONNECT_OPTIONS, WMQConstants.WMQ_CLIENT_RECONNECT);
            cf.setStringProperty(WMQConstants.WMQ_CHANNEL, CHANNEL);
            cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_BINDINGS);
            // cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
            // cf.setStringProperty(WMQConstants.USERID, USER);
            // cf.setStringProperty(WMQConstants.PASSWORD, PASSWORD);
            cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, QMGR);
            logger.finer("Connection factory properties have been set");
        }
        catch (JMSException e) {
            logger.log(Level.SEVERE, "An error occurred when trying to set connection factory properties", e);
        }
    }

    /**
     * Overrides the default values for the connection to the values
     * found in the file provided.
     */
    private void setMQVariables() {
        if (mqEnv != null) {
            try {
                this.HOST = getEnvKey("HOST");
                this.PORT = Integer.valueOf(getEnvKey("PORT"));
                this.CHANNEL = getEnvKey("CHANNEL");
                this.QMGR = getEnvKey("QMGR");
                this.USER = getEnvKey("APP_USER");
                this.PASSWORD = getEnvKey("APP_PASSWORD");
            }
            catch (JSONException e) {
                logger.log(Level.SEVERE, "Could not set MQ variables from file '" + filename + "'. Exiting...", e);
            }
        }
    }

    /**
     * Creates a JSON object that contains all the connection variables from the file specified.
     * @param filename The filename that contains the connection variables.
     */
    private void setMQEnv(String filename) {
        try {
            mqEnv = new JSONObject(new Scanner(new File(filename)).useDelimiter("\\Z").next());
        }
        catch (NoSuchElementException e) {
            logger.log(Level.SEVERE, "No data set in '" + filename + "'", e);
        }
        catch (JSONException e) {
            logger.log(Level.SEVERE, "Data in '" + filename + "' could not be parsed as a JSON object", e);
        }
        catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "File '" + filename + "' was not found", e);
        }
    }

    /**
     * Gets a value from the JSON Object {@code mqEnv} by providing a key.
     * @param key Key of the value to get.
     * @return A string containing the value corresponding to the key.
     * @throws JSONException Thrown when a key could not be retrieved from the
     * JSON Object {@code mqEnv}.
     */
    private String getEnvKey(String key) throws JSONException {
        String value = null;
        if (mqEnv != null) {
            try {
                value = String.valueOf(mqEnv.get(key));
            }
            catch (JSONException e) {
                logger.warning("Key '" + key + "' could not be retrieved from data");
                throw e;
            }
        }
        else {
            logger.warning("mqEnv not set. Provide a file to get mqEnv from.");
        }
        return value;
    }
}
