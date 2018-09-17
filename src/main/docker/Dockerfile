FROM payara/micro:${payara.version}

USER root

RUN apk update \
    && apk add --no-cache bash curl jq

ADD /entrypoint.sh $PAYARA_PATH/

RUN chmod 755 $PAYARA_PATH \
    && chmod 755 $PAYARA_PATH/entrypoint.sh \
    && mkdir -p -m 755 $PAYARA_PATH/deployments \
    && mkdir -p -m 755 $PAYARA_PATH/conf \
    && chown -R payara:payara $PAYARA_PATH

USER payara

EXPOSE 8443

ADD /maven/ROOT.war /opt/payara/deployments/

ENTRYPOINT ["/opt/payara/entrypoint.sh"]
CMD ["--deploy", "/opt/payara/deployments/ROOT.war"]
