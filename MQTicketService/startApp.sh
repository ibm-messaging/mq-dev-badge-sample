#!/bin/bash
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

cd /data/TicketGenerator

# Setup MQ
runmqdevserver &
sleep 30
# Set environment
. /opt/mqm/bin/setmqenv -s
# Execute TicketGenerator


if [ "$platformArch" == "amd64" ]
then
/jdk-11.0.13+8-jre/bin/java -cp /data/TicketGenerator/target/TicketGenerator-1.4.jar com.ibm.mq.badge.Manager 
fi
  
if [ "$platformArch" == "arm64" ] 
then
/jdk-11.0.22+7-jre/bin/java -cp /data/TicketGenerator/target/TicketGenerator-1.4.jar com.ibm.mq.badge.Manager 
fi