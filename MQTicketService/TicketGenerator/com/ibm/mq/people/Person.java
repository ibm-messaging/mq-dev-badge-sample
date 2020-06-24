package com.ibm.mq.people;

/**
 * NOT BEING USED
 */
public abstract class Person {
    public String name = null;

    Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}