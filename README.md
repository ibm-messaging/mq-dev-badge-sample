# MQ Badge developer challenge
This is the Java template with code stubs for the MQ Badge developer challenge. You can use this code as a starting point as you build a messaging application that integrates a ticket reseller with a an event booking system.

_add link to challenge here_

## Reseller.java
The main entry point for the reseller application is in Reseller.java

## Challenge Code Changes
For the challenge you will make code updates to `SessionBuilder.java`,
`TicketSubscriber.java` and `TicketRequester.java`.

## Compilation
To compile run

````
javac -cp ./com.ibm.mq.allclient-9.0.4.0.jar:./javax.jms-api-2.0.1.jar com/ibm/mq/demo/*.java
````

## Application
The application will run as a code stub. When run the application logs will
indicate where code changes need to be made. 

To run the application

````
java -cp ./com.ibm.mq.allclient-9.0.4.0.jar:./javax.jms-api-2.0.1.jar:. com.ibm.mq.demo.Reseller
````
