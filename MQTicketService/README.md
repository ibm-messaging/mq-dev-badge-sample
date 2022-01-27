# MQ Badge App (Ticket Generator)

## Application Usage

##### Purpose
The purpose of this application is to provide a server-side application that can be used to interact with when creating
a client application for the MQ Badge.

[Take a look at the IBM Developer Essentials course](https://developer.ibm.com/messaging/learn-mq/mq-tutorials/mq-dev-essentials/)

## Getting Started
### Prerequisites
* Download and Install [Maven](https://maven.apache.org/download.cgi)

Make sure you have these ```.jar``` files in your class path when compiling and executing code.

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
1. Build the `.jar` file required for by running the following command:
    * ```mvn clean package```
1. We need to create a Queue Manager for the application to work. We do this by running a container. You can follow the steps in the [Developer Essentials Course](https://developer.ibm.com/learningpaths/ibm-mq-badge/create-configure-queue-manager/) to create a container with the MQ image.
1. Execute the code.
    * Linux & Mac ```java -cp target/TicketGenerator-1.4.jar: com.ibm.mq.badge.Manager```
    * Windows ```java -cp target\TicketGenerator-1.4.jar; com.ibm.mq.badge.Manager```