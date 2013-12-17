#!/bin/bash
rm -rf restful* lib
wget "http://ondex.rothamsted.ac.uk/nexus/service/local/artifact/maven/redirect?r=snapshots&g=net.sourceforge.ondex.webservices&a=restful&v=0.5.0-SNAPSHOT&c=packaged-distro&p=zip"
unzip -o restful-*-packaged-distro.zip
nohup /usr/java/latest/bin/java -Xmx2048M -jar restful.jar graphs >> stdout.log 2>> stderr.log &
