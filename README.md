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
javac -cp ./com.ibm.mq.allclient-9.2.0.0.jar:./javax.jms-api-2.0.1.jar com/ibm/mq/demo/*.java
````

## Application
The application will run as a code stub. When run the application logs will
indicate where code changes need to be made.

To run the application

````
java -cp ./com.ibm.mq.allclient-9.2.0.0.jar:./javax.jms-api-2.0.1.jar:. com.ibm.mq.demo.Reseller
````

## Environment variables
You can override the default MQ connection settings using environment variables on the system where you run your Reseller application code.

* **MQ_BADGE_QM_HOSTNAME** - Specify the Host name or IP address of your queue manager
* **MQ_BADGE_QM_NAME** - Set the queue manager name
* **MQ_BADGE_QM_PORT** - Listener port for your queue manager
* **MQ_BADGE_CHANNEL** - MQ Channel name
* **MQ_BADGE_USER** - User name that application uses to connect to MQ
* **MQ_BADGE_PASSWORD** - Password that the application uses to connect to MQ


## Ticket Service
For convenience the Ticket Service is provided as MQ docker image. When started it will start
both MQ server and the Ticket service. The MQ Server is configured to create and configure the
queues and topics that the Ticket Service requires.

## Docker FROM
The Dockerfile bases its image on ibmcom/mq:latest
The image is pulled from [docker hub](https://hub.docker.com/r/ibmcom/mq/)
the ticket service has been tested with the ibmcom/mq images

* 9.2.0.0-r2
* 9.2.0.0-r1
* 9.1.5.0-r2
* 9.1.5.0-r1

The ticker reseller docker file will not run with
* 9.1.4.0-r1
or earlier.

Running the following command will determine which version you are using.

````
docker image inspect <your image id> --format '{{.ContainerConfig.Labels.version}}'
````

### Docker Build
To build the docker image, open a terminal, navigate to the MQTicketService/TicketGenerator
directory, then run

````
docker build . -t mqbadge:latest
````     

### Docker Run
To start the container, run

````
docker run -e LICENSE=accept -e MQ_QMGR_NAME=QM1 -e LOG_FORMAT=json -e MQ_APP_PASSWORD=passw0rd   -p 1414:1414 -p 9443:9443 -ti --name mqebs mqbadge:latest
````

This will configure and start the MQ Server, then will start the Ticket Service application.

### Docker Restart
The Ticket Service will time it self out. To restart a stopped container run

````
docker restart mqebs
````
