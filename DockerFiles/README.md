# MQ Badge App (Ticket Generator)

## Application Usage

##### Purpose
The purpose of this application is to provide a server-side application that can be used to interact with when creating
a client application for the MQ Badge.

[Take a look at the IBM Developer Essentials course](https://developer.ibm.com/messaging/learn-mq/mq-tutorials/mq-dev-essentials/)

## Getting Started
### Prerequisites
* [com.ibm.mq.allclient-9.1.3.0.jar](https://repo1.maven.org/maven2/com/ibm/mq/com.ibm.mq.allclient/9.1.3.0/com.ibm.mq.allclient-9.1.3.0.jar) => [Info](https://mvnrepository.com/artifact/com.ibm.mq/com.ibm.mq.allclient/9.1.3.0)
* [javax.jms-api-2.0.1.jar](https://repo1.maven.org/maven2/javax/jms/javax.jms-api/2.0.1/javax.jms-api-2.0.1.jar) => [Info](https://mvnrepository.com/artifact/javax.jms/javax.jms-api/2.0.1)
* [json-20190722.jar](https://repo1.maven.org/maven2/org/json/json/20190722/json-20190722.jar) => [Info](https://mvnrepository.com/artifact/org.json/json/20190722)

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
    * ```cd mq-dev-badge-sample/DockerFiles/TicketGenerator```
1. Create a new directory inside.
    * ```mkdir jarFiles```
1. Download all the ```.jar``` files above ^^^.
1. Once the files have downloaded, throw them into the ```jarFiles``` directory.
1. From the ```TicketGenerator``` folder, compile the source code with relevant class paths.
    * Linux & Mac ```javac -cp ./jarFiles/*: com/ibm/mq/badge/*.java```
    * Windows ```javac -cp .\jarFiles\*; com\ibm\mq\badge\*.java```
1. Execute the code.
    * Linux & Mac ```java -cp ./jarFiles/*: com.ibm.mq.badge.Manager```
    * Windows ```java -cp .\jarFiles\*; com.ibm.mq.badge.Manager```