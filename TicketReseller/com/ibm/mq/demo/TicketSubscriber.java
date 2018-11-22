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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;


/**
  A <code>TicketSubscriber</code> uses a MQ connection to create a
  subscription to a topic.
 */
public class TicketSubscriber
{
    private static final Logger logger = Logger.getLogger("com.ibm.mq.demo");
    private MessageConsumer subscriber = null;
    private int concurrentErrorCounter = 0;
    private Session session;

    /**
      * Uses the JMS Client classes to establish a connection to the Queue Manager
      * running on the MQ Server
      *
      * Challenge : Subscribes to topic
      *
      * @param session A pre-established connection to a MQ Server
      * @param destinationName The topic to be subscribed to
      *
      * @return A constructed TicketSubscriber containing a MessageConsumer
      * subscription if the subscription was successful.
      */

    public TicketSubscriber(Session session, String destinationName) {
      logger.fine("Building Message Consumer");
      System.out.println("Challenge : Subscribes to topic");
      System.out.println("Your code to create a subscription will go here");

      //try {
        // Create the Topic and Subscription to it.
        // The following code needs to be added here
        logger.finest("Challenge Add code to : Create a topic");
        logger.finest("Challenge Add code to : Create a Consumer, save in class variable subscriber");

        logger.fine("Subscription to ticket queue established");
      //} catch (JMSException e) {
      //  logger.severe("Unable to establish subscription to ticket queue");
      //  e.printStackTrace();
      //}
    }

    /**
      * Verifies that the subscription was successfully created
      *
      * @param None
      *
      * @return boolean indicating whether the connection was successfull.
      */
    public boolean isGood() {
      return (subscriber != null);
    }


    /**
      * Polls the subscription waiting for a message to be published.
      *
      * Challenge : Receives a publication
      *
      * @param None
      *
      * @return Message that has been received, once a message has been
      * received.
      */
    public Message waitForPublish () throws PublishWaitException {
      Message message = null;
      try {
        logger.finest("Waiting for a message");
        System.out.println("Challenge : Receives a publication");
        System.out.println("Your code to receive a message will go here");
        // The following code needs to be added here
        logger.finest("Challenge Add code to : Receive a message from the MessageConsumer");

        if (message != null)
        {
          System.out.println("************************************");
          System.out.println("Received Event Opportunity");
          System.out.println(message.getBody(String.class));
          System.out.println("");
        }

        concurrentErrorCounter = 0;

        // Once you have your message you can delete this line
        System.out.println("You can remove this thrown exception when you have your message code");
        throw new PublishWaitException("Temp Exit from an endless loop in shell code");
      }
      catch (JMSException e) {
        logger.warning("Error waiting for ticket message to be published");
        e.printStackTrace();
        if (3 > concurrentErrorCounter++) {
          throw new PublishWaitException(String.format("JMS Exception seen %d times", concurrentErrorCounter));
        }
      }

      return message;
    }
}

class PublishWaitException extends Exception {
  private static final long serialVersionUID = 42l;

  public PublishWaitException() {}
  public PublishWaitException(String error) {
    super(error);
  }
}
