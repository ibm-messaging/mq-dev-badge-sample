package com.ibm.mq.people;

/**
 * NOT BEING USED
 */
public class Performer extends Person {
    private String category = null;

    public Performer(String name) {
        super(name);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}