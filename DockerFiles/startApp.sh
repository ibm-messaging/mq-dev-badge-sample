#!/bin/bash
runmqserver&

sleep 30
. /opt/mqm/bin/setmqenv
/opt/mqm/java/jre64/jre/bin/java -cp /opt/mqm/java/lib/*:/data/GenerateEvents.jar -Djava.library.path=/opt/mqm/java/lib64 com.ibm.mq.demo.GenerateEvent
