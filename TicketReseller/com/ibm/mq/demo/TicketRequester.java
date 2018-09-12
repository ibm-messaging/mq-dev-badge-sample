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

import java.util.logging.*;
import java.util.UUID;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import javax.xml.bind.JAXBException;

/**
  A <code>TicketRequester</code> uses peer to peer messaging to put and get
  messages to and from a queue.
 */
public class TicketRequester
{
    private static final Logger logger = Logger.getLogger("com.ibm.mq.demo");

    // session needs to be a static else createTextMessage generates a
    // compilation error
    private static Session session = null;
    private static String PURCHASE_QUEUE = "purchase";
    private static String CONFIRMATION_QUEUE = "confirmation";
    private static String ACCEPTED = "Accepted";


    /**
     * Constructs a TicketRequester with the Session representing
     * the connection to MQ.
     *
     * @param Session the estalished connection to MQ
     */
    public TicketRequester(Session s) {
      session = s;
    }

    /**
     * puts a message on the purchase queue, by merging the
     * number of tickets desired with the message recieved from
     * the subscription into a new Request.
     *
     * Challenge : Receiving a publication triggers a put
     * then requests to purchase a batch of tickets
     *
     * @param Message the message that was recieved
     * @param int the number of tickets to request
     * @return None
     */
    public static String put(Message message, int numTickets)
    {
      String correlationID = null;

      try {
        logger.finest("Building message to request tickets");

        Event event = EventFactory.newEventFromMessage(message);
        RequestTickets request = new RequestTickets(event, numTickets);

        TextMessage requestMessage = session.createTextMessage(request.toXML());
        correlationID = UUID.randomUUID().toString();

        System.out.println("Challenge : Receiving a publication triggers a put then requests to purchase a batch of tickets");
        System.out.println("Your code to put a message onto the purchase queue will go here");
        // The following code needs to be added here
        logger.finest("Challenge Add code to : Set the JMS Correlation ID");
        logger.finest("Challenge Add code to : Set the JMS Expiration");

        logger.finest("Sending request to purchase tickets");

        // Create the purchase Queue
        // Create a MessageProducer
        // Send the Request

        logger.finest("Sent request for tickets");
       }
       catch (JAXBException e) {
         correlationID = null;
         logger.warning("XML Errors detected");
         e.printStackTrace();
       }
       catch (JMSException e)
       {
         correlationID = null;
         e.printStackTrace();
       }

      return correlationID;
    }


    /**
     * gets a message from the confirmation queue,
     *
     * Challenge : our reseller application does a get from this queue
     *
     * @param String the correlation id that was sent with the put
     * @return boolean indicating if the ticket request was successful.
     */
    public boolean get(String correlationID) {
      boolean success = false;
      Message responseMsg = null;
      //try {
        logger.finest("Performing receive on confirmation queue");
        System.out.println("Challenge : our reseller application does a get from this queue");
        System.out.println("Your code to receive a message from the confirmation queue will go here");
        // The following code needs to be added here
        logger.finest("Challenge Add code to : Create Confirmation Queue");
        logger.finest("Challenge Add code to : Create a Consumer");
        logger.finest("Challenge Add code to : Receive a Message");

        if (responseMsg != null) {
          success = isAccepted(responseMsg);
        }
      //}
      //catch (JMSException e) {
      //  logger.warning("Error connecting to confirmation queue");
      //  e.printStackTrace();
      //}
      return success;
    }

    private boolean isAccepted(Message responseMsg) {
      boolean accepted = false;
      try {
        String msgBody = responseMsg.getBody(String.class);
        accepted = msgBody.equals(ACCEPTED);

        logger.info("*************COMPLETED*********");
        logger.info("Received response of....");
        logger.info(msgBody);
      }
      catch (JMSException e)
      {
        logger.warning("Error parsing the response from Event Booking System");
        e.printStackTrace();
      }

      return accepted;
    }

}
