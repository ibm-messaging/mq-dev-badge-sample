package com.ibm.mq.events;

/**
 * NOT BEING USED
 */
public class Ticket {
    private String type = null;
    private float price = 0.0f;

    public Ticket(String type, float price) {
        if (type != null && !type.isEmpty()) {
            this.type = type;
        }
        this.price = price;
    }

    public float getPrice() {
        return this.price;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}