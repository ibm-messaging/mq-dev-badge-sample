# © Copyright 2019, 2024 IBM Corporation
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at

#     http://www.apache.org/licenses/LICENSE-2.0

# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# Specify default base images 
# Can be overidden with different arguments for Mac and Linux in the build command
ARG baseImageBuildStage=docker.io/library/maven:3.6-openjdk-11
ARG baseImageRunStage=icr.io/ibm-messaging/mq:latest
# Specify platform architecture - either arm64 or amd64(default)
ARG platformArch=amd64
# Specify the Temurin OpenJDK 11 JRE version used for each architecture
ARG openjdkVersionAmd64=11.0.32.1+1
ARG openjdkVersionArm64=11.0.32.1+1
# Must match <version> in TicketGenerator/pom.xml
ARG appVersion=1.4

# stage 1
FROM $baseImageBuildStage AS build
# A global ARG is out of scope inside a stage, so re-declare it here.
ARG platformArch
ARG openjdkVersionAmd64
ARG openjdkVersionArm64
USER root

COPY ./TicketGenerator /data/TicketGenerator

# Compile the source code
RUN cd /data/TicketGenerator && \
    mvn -f /data/TicketGenerator/pom.xml clean package;


# get platform specific jdk libraries
# if architecture is not amd64 or arm64, then forcing abort of build
# Staged under /tmp/jdkroot so the versioned JRE folder name survives the copy into stage 2.
RUN set -eu; \
    mkdir -p /tmp/jdkroot; \
    if [ "$platformArch" = "amd64" ]; then \
        jdkVersion="$openjdkVersionAmd64"; jdkArch="x64"; \
    elif [ "$platformArch" = "arm64" ]; then \
        jdkVersion="$openjdkVersionArm64"; jdkArch="aarch64"; \
    else \
        echo "Error: Valid platform architectures are amd64 or arm64"; \
        exit 1; \
    fi; \
    releaseTag=$(echo "$jdkVersion" | sed 's/+/%2B/'); \
    fileVersion=$(echo "$jdkVersion" | sed 's/+/_/'); \
    curl -fLo /tmp/OpenJDK11U.tar.gz \
        "https://github.com/adoptium/temurin11-binaries/releases/download/jdk-${releaseTag}/OpenJDK11U-jre_${jdkArch}_linux_hotspot_${fileVersion}.tar.gz"; \
    tar -xzf /tmp/OpenJDK11U.tar.gz -C /tmp/jdkroot/

# stage 2
# Switch to the MQ image
FROM $baseImageRunStage
# A global ARG is out of scope inside a stage, so re-declare it here.
ARG platformArch
ARG appVersion
ENV APP_VERSION=${appVersion}

USER 0

# Copy over config and scripts
COPY ./startApp.sh /data/startApp.sh
COPY ./20-config.mqsc /etc/mqm/20-config.mqsc

# fix line ending issue on windows system
RUN if [ "$platformArch" = "amd64" ]; then \
    sed -i 's/\r$//' /data/startApp.sh && \
    sed -i 's/\r$//' /etc/mqm/20-config.mqsc; \
  fi

# Copy over compiled classes from previous stage
COPY --from=build /data/TicketGenerator /data/TicketGenerator
COPY --from=build /tmp/jdkroot/ /

# Stable, architecture independent path for startApp.sh
RUN ln -s "$(ls -d /jdk-*-jre)" /OpenJDK

# Fix permissions for compiled classes and app shell script
RUN chmod 776 -R /data/TicketGenerator
RUN chmod a+x /data/startApp.sh

# Run MQ and execute TicketGenerator
USER 1001
ENTRYPOINT [ "/data/startApp.sh" ]