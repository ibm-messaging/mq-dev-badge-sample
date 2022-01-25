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
import java.util.StringTokenizer;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Event
{
  private int eventID;
  private String title;
  private String time;
  private String location;
  private int capacity;


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

  public int getCapacity()
  {
    return capacity;
  }

  @XmlElement
  public void setCapacity(int capacity)
  {
    this.capacity = capacity;
  }

  public int getEventID()
  {
    return eventID;
  }

  @XmlElement
  public void setEventID(int eventID)
  {
    this.eventID = eventID;
  }


  public Event(int eventID, String title, String time, String location, int capacity)
  {
    super();
    this.eventID = eventID;
    this.title = title;
    this.time = time;
    this.location = location;
    this.capacity = capacity;
  }

  public Event(int eventID,  String data)
  {
    StringTokenizer token = new StringTokenizer(data, ",");
    if(token.countTokens()==4)
    {
      this.title = token.nextToken();
      this.time = token.nextToken();
      this.location = token.nextToken();
      this.capacity = Integer.parseInt(token.nextToken());

    }
    this.eventID = eventID;
  }

  public Event()
  {
    this.title = "";
    this.time = "";
    this.location = "";
    this.capacity = 0;
    this.eventID=0;
  }


  @Override
  public String toString()
  {
    return "Event [eventID="+eventID+", title=" + title + ", time=" + time + ", location=" + location + ", capacity=" + capacity + "]";
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
