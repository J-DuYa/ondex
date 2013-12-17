#!/bin/sh -e
kill `ps -ef | grep restful.jar | grep -v grep | awk '{ print $2 }'`
