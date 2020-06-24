package com.ibm.mq.events;

import javax.xml.bind.JAXBException;

import com.ibm.mq.badge.AdvertManager;

/**
 * An interface which provides the method to transform a class into
 * XML. This can be used in conjunction with other classes such as
 * the {@link AdvertManager} to publish to a topic. In this package,
 * {@link Event} class implements this as it can then be published to
 * a topic by the {@link AdvertManager}. This is implemented by the
 * {@link Event} class to allow it to be published by the
 * {@link AdvertManager} to a topic.
 * @author Benjamin Brunyee
 * @version 1.0
 * @see AdvertManager
 * @see Event
 */
public interface Advertisable {
    /**
     * Provides the ability to turn the class into <b>XML</b>. The class must
     * provide <tt>XML annotations</tt>.
     * @return Returns a string in the <b>XML</b> format.
     * @throws JAXBException Thrown when there was an error in trying to
     * convert to XML.
     */
    abstract public String toXML() throws JAXBException;
}