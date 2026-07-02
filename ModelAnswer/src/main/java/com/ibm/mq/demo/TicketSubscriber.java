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

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Session;


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
      try {
        // Create the Topic and Subscription to it.
        Destination topic = session.createTopic(destinationName);
        subscriber = session.createConsumer(topic);
        this.session = session;
        logger.fine("Subscription to ticket queue established");
      } catch (JMSException e) {
        logger.severe("Unable to establish subscription to ticket queue");
        e.printStackTrace();
      }
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
        message = subscriber.receive();
        if (message != null)
        {
          System.out.println("************************************");
          System.out.println("Received Event Opportunity");
          System.out.println(message.getBody(String.class));
          System.out.println("");
        }
        concurrentErrorCounter = 0;
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
