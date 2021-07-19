@echo off
call mvn clean install
echo "Starting Application..."
call java -jar target\open-weather-map.jar