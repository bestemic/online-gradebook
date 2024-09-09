#!/bin/bash

echo "ADD_USER=${ADD_USER}"
echo "FIRST_NAME=${FIRST_NAME}"
echo "LAST_NAME=${LAST_NAME}"
echo "EMAIL=${EMAIL}"
echo "PASSWORD=${PASSWORD}"
echo "ROLES=${ROLES}"
echo "PHONE_NUMBER=${PHONE_NUMBER}"
echo "BIRTH=${BIRTH}"

APP_ARGS=""

if [ "$ADD_USER" == "true" ]; then
  APP_ARGS+=" --firstName=${FIRST_NAME}"
  APP_ARGS+=" --lastName=${LAST_NAME}"
  APP_ARGS+=" --email=${EMAIL}"
  APP_ARGS+=" --password=${PASSWORD}"
  APP_ARGS+=" --roles=${ROLES}"
  APP_ARGS+=" --phoneNumber=${PHONE_NUMBER}"
  APP_ARGS+=" --birth=${BIRTH}"
fi

exec java -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE} ${APP_ARGS}