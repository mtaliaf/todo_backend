FROM jboss/wildfly:latest

USER root
RUN mkdir -p /usr/app
COPY ./ /usr/app

WORKDIR /usr/app
RUN ./gradlew war
RUN cp ./build/libs/todo_backend.war /opt/jboss/wildfly/standalone/deployments/
