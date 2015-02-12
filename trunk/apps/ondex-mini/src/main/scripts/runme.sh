#!/bin/bash
WORKFLOW=$1
shift

PLUGIN_ARGS=""
until [ -z $1 ]
do
  PLUGIN_ARGS="$PLUGIN_ARGS -P$1"
  shift
done

java -Xmx2G -Dondex.dir=./data -jar lib/ondex-mini-0.5.0-SNAPSHOT.jar -ubla -ptest -w$WORKFLOW $PLUGIN_ARGS
