#!/bin/bash
# © Copyright IBM Corporation 2018
#
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

runmqserver&

sleep 30
. /opt/mqm/bin/setmqenv
/opt/mqm/java/jre64/jre/bin/java -cp /opt/mqm/java/lib/*:/data/GenerateEvents.jar -Djava.library.path=/opt/mqm/java/lib64 com.ibm.mq.demo.GenerateEvent
