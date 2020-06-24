package com.ibm.mq.events;

import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * The class that implements {@link Advertisable} and is used in
 * creating <b>XML</b> strings that a publishable to a queue or
 * topic.
 * @author Benjamin Brunyee
 * @version 1.0
 * @see Advertisable
 */
@XmlRootElement
public class Event implements Advertisable {
    private Integer eventID = null;
    private String title = null;
    private Integer capacity = null;
    private String time = null;
    private String date = null;
    private String location = null;

    @XmlElement
    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getEventID() {
        return eventID;
    }

    @XmlElement
    public void setEventID(Integer eventID) {
        this.eventID = eventID;
    }

    public String getTitle() {
        return this.title;
    }

    @XmlElement
    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return this.location;
    }

    @XmlElement
    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return this.time;
    }

    @XmlElement
    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return this.date;
    }

    @XmlElement
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getCapacity() {
        return this.capacity;
    }

    /**
     * Writes this class into a <b>XML</b> string that is ready
     * to be published or sent to a queue or topic.
     * @return A String of <b>XML</b> with
     * variables as <b>XML</b> elements.
     * @throws JAXBException Thrown when there was an error in
     * converting the {@link Event} class into <b>XML</b>.
     */
    @Override
    public String toXML() throws JAXBException
	{
        StringWriter writer = new StringWriter();
        JAXBContext jaxbContext = JAXBContext.newInstance(this.getClass());
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        jaxbMarshaller.marshal(this, writer);
        return writer.toString();
	}
}