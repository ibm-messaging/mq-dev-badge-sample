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
import java.util.Scanner;

import javax.jms.Message;
import javax.jms.Session;

public class Reseller
{
  private static final Level LOGLEVEL = Level.ALL;
  private static final Logger logger = Logger.getLogger("com.ibm.mq.demo");

  private static String DESTINATION_NAME = "newTickets";

  /**
   * Main method
   *
   * @param args
   */
  public static void main(String[] args)
  {
    initialiseLogging();
    logger.info("Reseller Application is starting");

    // Challenge : Connect to a queue manager
    Session session = SessionBuilder.connect();

    if (session != null) {
      // Challenge : Subscribes to topic
      TicketSubscriber ticketSubscriber = new TicketSubscriber(session, DESTINATION_NAME);
      TicketRequester ticketRequester = new TicketRequester(session);
      if (ticketSubscriber.isGood()) {
        logger.fine("Entering wait loop for event tickets");
        while(true) {
          // Challenge : Receives a publication
          try {
            Message message = ticketSubscriber.waitForPublish();
            if (message != null) {
              logger.fine("Tickets have been made available");

              // Avoids an illegal reflective access operation caused by jaxb dependencies
              final String key = "org.glassfish.jaxb.runtime.v2.bytecode.ClassTailor.noOptimize";
              System.setProperty(key, "true");

              // Challenge : Processes a publication
              int numToReserve = howMany(message);

              logger.fine("Sending request to purchase tickets over peer to peer");

              // Challenge : Receiving a publication triggers a put
              // then requests to purchase a batch of tickets
              String correlationID = ticketRequester.put(message, numToReserve);
              if (correlationID != null) {
                logger.fine("Request has been sent, waiting response from Event Booking System");
                // Challenge : Our reseller application does a get from this queue
                if (ticketRequester.get(correlationID)) {
                  logger.info("Tickets secured!");
                } else {
                  logger.info("No tickets reserved!");
                }
              }
            }
          }
          catch(PublishWaitException e) {
            logger.warning("Repeated Exceptions thrown while waiting for response");
            e.printStackTrace();
            break; // The while true loop
          }
        }
      }

      SessionBuilder.close(session);
    } else {
      logger.severe("Was unable to connect to MQ");
    }
    logger.info("Reseller Application is closing");
  }

  /**
   * Initilise the logging by switching off default logging and
   * setting the log level to the desired level. The Default logger
   * is first removed to prevent duplication of INFO and above logs.
   *
   * @param None
   * @return None
   */
  private static void initialiseLogging()
  {
    Logger defaultLogger = Logger.getLogger("");
    Handler[] handlers = defaultLogger.getHandlers();
    if (handlers != null && handlers.length > 0) {
      defaultLogger.removeHandler(handlers[0]);
    }

    Handler consoleHandler = new ConsoleHandler();
    consoleHandler.setLevel(LOGLEVEL);
    logger.addHandler(consoleHandler);

    logger.setLevel(LOGLEVEL);
    logger.finest("Logging initialised");
  }

  /**
   * Processes the publication, by extracting the details from the
   * received Message and determining how many tickets to request.
   *
   * Challenge : Processes a publication
   *
   * @param Message the Message that was received by the subcription
   * @return int the quantity of tickets to be requested.
   */
  private static int howMany(Message message) {
    int iWant = -1;
    Scanner in = new Scanner(System.in);
    Event event = EventFactory.newEventFromMessage(message);

    // getTitle
    // getTime
    // getLocation
    // getCapacity
    // getEventID

    String title = event.getTitle();
    int capacity = event.getCapacity();

    System.out.printf("There are %d tickets available for %s \n", capacity, title);
    while (-1 == iWant) {
      System.out.println("How many do you want to secure?");
      if (in.hasNextInt())
        iWant = in.nextInt();
      else {
        System.out.println("I am expecting a quantity expressed in digits from you?");
        in.next();
      }
    }

    return iWant;
  }

}
