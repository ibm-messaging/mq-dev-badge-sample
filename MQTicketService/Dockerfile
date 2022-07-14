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

FROM docker.io/library/maven:3.6-openjdk-11 AS build
USER root
#
COPY ./TicketGenerator /data/TicketGenerator
#
#
# Compile the source code
RUN cd /data/TicketGenerator && \
    mvn -f /data/TicketGenerator/pom.xml clean package;
#
#
# Switch to the MQ image
# Tested with
#     9.2.5.0-r2
#
FROM icr.io/ibm-messaging/mq:latest
USER 0

RUN curl -Lo OpenJDK11U-jre_x64_linux_hotspot_11.0.13_8.tar.gz https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.13%2B8/OpenJDK11U-jre_x64_linux_hotspot_11.0.13_8.tar.gz && \
    ls -l OpenJDK11U-jre_x64_linux_hotspot_11.0.13_8.tar.gz && file OpenJDK11U-jre_x64_linux_hotspot_11.0.13_8.tar.gz && \
    tar -xvzf OpenJDK11U-jre_x64_linux_hotspot_11.0.13_8.tar.gz

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
