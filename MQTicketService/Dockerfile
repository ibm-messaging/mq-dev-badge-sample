# © Copyright 2019, 2020 IBM Corporation
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at

#     http://www.apache.org/licenses/LICENSE-2.0

# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

FROM openjdk:8-alpine
USER root
RUN apk update && \
    apk add wget;
#
COPY ./TicketGenerator /data/TicketGenerator
#
# Setup JAVA_HOME -- useful for docker commandline
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
RUN export JAVA_HOME
#
# Compile the source code
RUN cd /data/TicketGenerator && \
    mkdir jarFiles && \
    cd jarFiles && \
    wget https://repo1.maven.org/maven2/com/ibm/mq/com.ibm.mq.allclient/9.2.0.0/com.ibm.mq.allclient-9.2.0.0.jar && \
    wget https://repo1.maven.org/maven2/javax/jms/javax.jms-api/2.0.1/javax.jms-api-2.0.1.jar && \
    wget https://repo1.maven.org/maven2/org/json/json/20190722/json-20190722.jar && \
    cd /data/TicketGenerator && \
    javac -cp /data/TicketGenerator/jarFiles/*: /data/TicketGenerator/com/ibm/mq/badge/*.java;
#
#
# Switch to the MQ image
# Tested with
#     9.2.0.0-r2
#     9.2.0.0-r1
#     9.1.5.0-r2
#     9.1.5.0-r1
#
FROM ibmcom/mq:latest
USER 0
# Copy over config and scripts
COPY ./startApp.sh /data/startApp.sh
COPY ./20-config.mqsc /etc/mqm/20-config.mqsc
#
# Copy over compiled classes from previous stage
COPY --from=0 /data/TicketGenerator /data/TicketGenerator
#
# Fix permissions for compiled classes and app shell script
RUN chmod 776 -R /data/TicketGenerator
RUN chmod a+x /data/startApp.sh
#
# Run MQ and execute TicketGenerator
USER 1001
ENTRYPOINT [ "/data/startApp.sh" ]
