package com.ibm.mq.people;

/**
 * NOT BEING USED
 */
public class Customer extends Person {
    private Float pricePaid = null;

    public Customer(String name) {
        super(name);
    }

    public Float getPricePaid() {
        return pricePaid;
    }

    public void setPricePaid(Float pricePaid) {
        this.pricePaid = pricePaid;
    }
}