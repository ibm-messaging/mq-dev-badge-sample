/*
* (c) Copyright IBM Corporation 2018
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.ibm.mq.demo;

import java.io.StringWriter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RequestTickets
{
	private int eventID;
	private String title;
	private String time;
	private String location;
	private int numberRequested;
	public int getEventID()
	{
		return eventID;
	}
	@XmlElement
	public void setEventID(int eventID)
	{
		this.eventID = eventID;
	}
	public String getTitle()
	{
		return title;
	}
	@XmlElement
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getTime()
	{
		return time;
	}
	@XmlElement
	public void setTime(String time)
	{
		this.time = time;
	}
	public String getLocation()
	{
		return location;
	}
	@XmlElement
	public void setLocation(String location)
	{
		this.location = location;
	}
	public int getNumberRequested()
	{
		return numberRequested;
	}
	@XmlElement
	public void setNumberRequested(int numberRequested)
	{
		this.numberRequested = numberRequested;
	}
	public RequestTickets(Event event, int numberRequested)
	{
		super();
		this.eventID = event.getEventID();
		this.title = event.getTitle();
		this.time = event.getTime();
		this.location = event.getLocation();
		this.numberRequested = numberRequested;
	}

	public RequestTickets()
	{
		super();
		this.eventID = 0;
		this.title = "";
		this.time = "";
		this.location = "";
		this.numberRequested = 0;
	}

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
