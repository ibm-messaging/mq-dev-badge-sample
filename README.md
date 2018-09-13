# MQ Badge developer challenge
This is the Java template with code stubs for the MQ Badge developer challenge. You can use this code as a starting point as you build a messaging application that integrates a ticket reseller with a an event booking system.

[Take a look at the IBM Developer Essentials course](https://ibm.biz/mq-badge)

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

## Environment variables
You can override the default MQ connection settings using environment variables on the system where you run your Reseller application code.

* **MQ_BADGE_QM_HOSTNAME** - Specify the Host name or IP address of your queue manager 
* **MQ_BADGE_QM_NAME** - Set the queue manager name
* **MQ_BADGE_QM_PORT** - Listener port for your queue manager
* **MQ_BADGE_CHANNEL** - MQ Channel name
* **MQ_BADGE_USER** - User name that application uses to connect to MQ
* **MQ_BADGE_PASSWORD** - Password that the application uses to connect to MQ
     
