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
2. Change into that directory.
    * ```cd mq-badge-app```
3. Clone this repository.
    * ```git clone "https://github.com/ibm-messaging/mq-dev-badge-sample.git"```
4. Change directory into the TicketService directory.
    * ```cd mq-dev-badge-sample/MQTicketService```

5. The ticket generator application needs a Queue Manager to work. We do this by running a container with both MQ and the ticket generator application.
You will need to first build the container image:
    * ```docker build . -t mqbadge:latest```

6. Create and run a container.
     * ```docker run -e LICENSE=accept -e MQ_QMGR_NAME=QM1 -e LOG_FORMAT=json -e MQ_APP_PASSWORD=passw0rd -p 1414:1414 -p 9443:9443 --detach -ti --name mqebs mqbadge:latest```


### Run Ticket Generator app locally
If you want you can run the ticket generator application outside docker, then
you still need a Queue Manager. If you comment out the

````
/jdk-11.0.13+8-jre/bin/java -cp /data/TicketGenerator/target/TicketGenerator-1.4.jar com.ibm.mq.badge.Manager
````

from the `startApp.sh` file, then the ticket generator app will not be started by the container, but MQ will. You will need to rebuild the image. After that `cd` to the `TicketGenerator` directory and run
  * ```mvn clean package```

Run the ticket generator app by executing

  * Linux & Mac ```java -cp target/TicketGenerator-1.4.jar: com.ibm.mq.badge.Manager```
  
  * Windows ```java -cp target\TicketGenerator-1.4.jar; com.ibm.mq.badge.Manager```
