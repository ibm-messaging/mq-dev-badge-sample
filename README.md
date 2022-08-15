# MQ Badge developer challenge
This is the Java template with code stubs for the MQ Badge developer challenge. You can use this code as a starting point as you build a messaging application that integrates a ticket reseller with a an event booking system.

[Take a look at the IBM Developer Essentials course](https://ibm.biz/mq-badge)

## Prerequisites
You will need to download and install [Maven](https://maven.apache.org/download.cgi) for this application to work.

## Ticket Service
For convenience the Ticket Service is provided as MQ docker image. When started it will start
both MQ server and the Ticket service. The MQ Server is configured to create and configure the
queues and topics that the Ticket Service requires.

## Docker FROM
The Dockerfile bases its image on icr.io/ibm-messaging/mq:latest
The image is pulled from the IBM Container Registry.

* 9.2.4.0-r1

The ticker reseller docker file will not run with
* 9.1.4.0-r1
or earlier.

Running the following command will determine which version you are using.

````
docker image inspect <your image id> --format '{{.ContainerConfig.Labels.version}}'
````

### Docker Build
To build the docker image, open a terminal, navigate to the `MQTicketService`
directory, then run

````
docker build . -t mqbadge:latest
````     

### Docker Run
To start the container, run

````
docker run -e LICENSE=accept -e MQ_QMGR_NAME=QM1 -e LOG_FORMAT=json -e MQ_APP_PASSWORD=passw0rd -p 1414:1414 -p 9443:9443 -ti --name mqebs mqbadge:latest
````

This will configure and start the MQ Server, then will start the Ticket Service application.

### Docker Restart
The Ticket Service will time it self out. To restart a stopped container run

````
docker restart mqebs
````

### ModelAnswer
Open another terminal alongside the TicketService.

If you have installed Maven, change your working directory to `ModelAnswer`.

To create the `.jar` file with the dependencies required for the application, run the following command:

````
mvn clean package
````

## Reseller.java
The main entry point for the reseller application is in Reseller.java

## Challenge Code Changes
For the challenge you will make code updates to `SessionBuilder.java`,
`TicketSubscriber.java` and `TicketRequester.java`.

## Application
The application will run as a code stub. When run the application logs will
indicate where code changes need to be made.

To run the application

````
java -cp target/ModelAnswer-1.4.jar:. com.ibm.mq.demo.Reseller
````

## Environment variables
You can override the default MQ connection settings using environment variables on the system where you run your Reseller application code.

* **MQ_BADGE_QM_HOSTNAME** - Specify the Host name or IP address of your queue manager
* **MQ_BADGE_QM_NAME** - Set the queue manager name
* **MQ_BADGE_QM_PORT** - Listener port for your queue manager
* **MQ_BADGE_CHANNEL** - MQ Channel name
* **MQ_BADGE_USER** - User name that application uses to connect to MQ
* **MQ_BADGE_PASSWORD** - Password that the application uses to connect to MQ

This is the default configuration. Run the following in your command terminal.
```
export MQ_BADGE_QM_HOSTNAME = "localhost"
export MQ_BADGE_QM_NAME = "QM1"
export MQ_BADGE_QM_PORT = 1414
export MQ_BADGE_CHANNEL = "DEV.APP.SVRCONN"
export MQ_BADGE_USER = "app"
export MQ_BADGE_PASSWORD = "passw0rd"
```
