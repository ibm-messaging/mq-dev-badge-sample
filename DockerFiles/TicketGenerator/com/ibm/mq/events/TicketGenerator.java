package com.ibm.mq.events;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * NOT BEING USED
 */
public class TicketGenerator {
    private static final Logger logger = Logger.getLogger("com.ibm.mq.badge");
    public ArrayList<Ticket> createTickets(String type, float price, Integer quantity) {
        ArrayList<Ticket> ticketList = new ArrayList<>();
        logger.finest("Creating '" + quantity + "' tickets of type '" + type + "' at price '" + price + "'");
        for (Integer i = 0; i < quantity; i++) {
            ticketList.add(new Ticket(type, price));
        }
        return ticketList;
    }
}