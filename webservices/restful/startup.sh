#!/bin/sh -e
nohup /usr/java/latest/bin/java -Xmx2048M -jar restful.jar graphs >> stdout.log 2>> stderr.log &
