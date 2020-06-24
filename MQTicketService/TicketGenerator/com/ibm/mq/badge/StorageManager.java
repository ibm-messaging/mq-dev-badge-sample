package com.ibm.mq.badge;

import com.ibm.mq.exceptions.*;
import com.ibm.mq.events.*;
import java.io.*;
import java.nio.file.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import org.json.JSONObject;

/**
 * Responsible for writing data to a file in <b>JSON</b> and provides
 * a file to other classes to read data.
 * @author Benjamin Brunyee
 * @version 1.0
 */

public class StorageManager {
    private static final Logger logger = Logger.getLogger("com.ibm.mq.badge");

    /**
     * The filename that will be used in retrieving event information and
     * saving new event information to.
     */
    private String filename;

    /**
     * Initialisation of the storage manager with a specific filename.
     * This constructor will also check the environment to see if the
     * file specified actually exists. Will {@code System.exit(1)} if
     * the file doesn't exist.
     * @param dataFilename The name of the file of which will have data
     * read from and data saved to.
     */
    public StorageManager(String dataFilename) {
        this.filename = dataFilename;
        checkEnv();
    }

    /**
     * Initialisation of the storage manager with the default filename.
     * This constructor will set a default filename of
     * <b>StorageManagerData.json</b> and then will check the environment
     * to see if the file exists. Will {@code System.exit(1)} if the file
     * doesn't exist.
     */
    public StorageManager() {
        filename = "StorageManagerData.json";
        logger.fine("No constructor for storage manager so using files: " + filename);
        checkEnv();
    }

    /**
     * Sets the file to be used and then checks to see if the file exists.
     * Will {@code System.exit(1)} if the file doesn't exist.
     * @param filename Filename to be used.
     */
    public void setAndCheckFilename(String filename) {
        this.filename = filename;
        checkEnv();
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    /**
     * Saves <b>JSON</b> data to the file specified for the current storage manager.
     * @param dataToSave Data to be writen to the file set for the storage manager.
     * @throws DataDidNotSaveException Thrown if the data could not be saved.
     */
    public synchronized void saveNewData(JSONObject dataToSave) throws DataDidNotSaveException {
        logger.info("Saving data");
        PrintWriter pw;
        if (filename != null) {
            try {
                pw = new PrintWriter(filename);

                // Writing the data to the file with indents
                pw.write(dataToSave.toString(4));
                pw.flush();
                pw.close();
            }
            catch (FileNotFoundException e) {
                String errorMessage = "File was not found: " + filename;
                logger.warning(errorMessage);
                e.printStackTrace();
                throw new DataDidNotSaveException(errorMessage, e);
            }
            catch (SecurityException e) {
                String errorMessage = "Could not write to '" + filename + "'. Check permissions";
                logger.warning(errorMessage);
                e.printStackTrace();
                throw new DataDidNotSaveException(errorMessage, e);
            }
        }
        else {
            String errorMessage = "Could not use null filename";
            throw new DataDidNotSaveException(errorMessage);
        }
    }

    /**
     * Checks to see if the file specified for the storage manager exists.
     * Will exit with {@code System.exit(1)} if the file doesn't exist.
     */
    private void checkEnv() {
        if (!new File(filename).exists()) {
            logger.log(Level.SEVERE, "File: '" + filename + "' does not exist", new FileNotFoundException("File Not Found"));
        }
    }
}