#!/bin/sh

mvn clean install
echo "Starting Application..."
java -jar target/open-weather-map.jar
#