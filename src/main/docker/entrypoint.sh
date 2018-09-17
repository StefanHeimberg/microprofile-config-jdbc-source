#!/usr/bin/env sh

set -e

exec java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 $PAYARA_MICRO_JAR \
     --sslport 8443 \
     --enablehealthcheck true \
     --disablephonehome \
     "$@"
