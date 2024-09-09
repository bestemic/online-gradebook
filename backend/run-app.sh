#!/bin/bash

APP_ARGS=""

if [ "$ADD_USER" == "true" ]; then
  APP_ARGS+=" --firstName=${FIRST_NAME}"
  APP_ARGS+=" --lastName=${LAST_NAME}"
  APP_ARGS+=" --email=${EMAIL}"
  APP_ARGS+=" --password=${PASSWORD}"
  APP_ARGS+=" --phoneNumber=${PHONE_NUMBER}"
  APP_ARGS+=" --birth=${BIRTH}"
  APP_ARGS+=" --roles=${ROLES}"
fi

exec java -jar /app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE} ${APP_ARGS}