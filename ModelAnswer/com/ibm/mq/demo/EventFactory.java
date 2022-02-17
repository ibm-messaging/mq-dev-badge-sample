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

import java.io.StringReader;
import java.util.logging.*;

import javax.jms.JMSException;
import javax.jms.Message;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public class EventFactory
{
  private static final Logger logger = Logger.getLogger("com.ibm.mq.demo");

  public static Event newEventFromMessage(Message message) {
    Event event = null;
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(Event.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      event = (Event) jaxbUnmarshaller.unmarshal(new StringReader(message.getBody(String.class)));
    }
    catch (JAXBException e) {
      logger.warning("XML Errors detected parsing Event Message");
      e.printStackTrace();
    }
    catch (JMSException e)
    {
      logger.warning("JMS Errors detected parsing Event Message");
      e.printStackTrace();
    }
    return event;
  }

}
