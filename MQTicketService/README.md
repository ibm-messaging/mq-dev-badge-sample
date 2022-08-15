# MQ Badge App (Ticket Generator)

## Application Usage

##### Purpose
The purpose of this application is to provide a server-side application that can be used to interact with when creating
a client application for the MQ Badge.

[Take a look at the IBM Developer Essentials course](https://developer.ibm.com/messaging/learn-mq/mq-tutorials/mq-dev-essentials/)

## Getting Started
### Prerequisites
* Download and Install [Maven](https://maven.apache.org/download.cgi)

### Step-by-step
Steps for running the application on your local machine - <b>Do not follows these steps for the MQ badge</b>
1. Create a directory.
    * ```mkdir mq-badge-app```
1. Change into that directory.
    * ```cd mq-badge-app```
1. Clone this repository.
    * ```git clone "https://github.com/ibm-messaging/mq-dev-badge-sample.git"```
1. Change directory into the TicketGenerator directory.
    * ```cd mq-dev-badge-sample/MQTicketService/TicketGenerator```
1. Build the application by running the following command:
    * ```mvn clean package```
1. We need to create a Queue Manager for the application to work. We do this by running a container. You will need to first build the container image:
    * ```cd ..```
    * ```docker build . -t mqbadge:latest```

   Following this you will create and run a container in the TicketGenerator directory.
    * ```cd TicketGenerator```
    * ```docker run -e LICENSE=accept -e MQ_QMGR_NAME=QM1 -e LOG_FORMAT=json -e MQ_APP_PASSWORD=passw0rd -p 1414:1414 -p 9443:9443 --detach -ti --name mqebs mqbadge:latest```

1. Execute the code.
    * Linux & Mac ```java -cp target/TicketGenerator-1.4.jar: com.ibm.mq.badge.Manager```
    * Windows ```java -cp target\TicketGenerator-1.4.jar; com.ibm.mq.badge.Manager```
