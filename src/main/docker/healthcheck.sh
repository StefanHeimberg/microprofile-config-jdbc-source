#!/bin/bash

healthcheckUrl='https://localhost:8443/health'

function healthy {
    echo "healthcheck: ok"
    exit 0
}

function unhealthy {
    local reason=$1
    (>&2 echo "healthcheck: nok. ${reason}")
    exit 1
}

function do_healthcheck {
    result=$(curl --write-out "\n%{http_code}" --insecure --connect-timeout 5 --max-time 5 --silent --fail ${healthcheckUrl})
    httpStatus="${result##*$'\n'}"

    # HealthCheck API liefert HTTP 200 wenn alles "UP" ist. Ansonsten HTTP 503
    if [[ "${httpStatus}" -ne 200 ]] ; then
        unhealthy "Status Code: ${httpStatus}"
    fi

    httpContent="${result%$'\n'*}"

    # Leider ist ein "UP" der HealthCheck API nicht eine bestätigung dass auch
    # die Webapplikation deployed werden konnte. Daher müssen wir noch prüfen
    # dass der eigene konto-service-check ausgeführt wurde und ebenfalls "UP"
    # liefert. Sollte zu diesem Zeitpunkt immer "UP" sein da ja bereits HTTP 200
    # von der HealthCheck API geliefert wurde.
    myprojectServicePingCheckState=$(echo $httpContent | jq '.checks[] | select(.name=="myproject-service-ping-check") | .state')

    if [[ ! "${myprojectServicePingCheckState}" =~ ^\"UP\" ]]; then
        unhealthy "Body: ${httpContent}"
    fi

    healthy
}

do_healthcheck
