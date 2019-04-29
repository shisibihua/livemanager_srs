@echo off
rem Add extra JVM options here

title HLiveManagerServices

cd /d %~dp0
set OPTS=-Xms256m -Xmx512m
"jre8_64\bin\java" -jar %OPTS% hht-livemanager-service-1.0.0.jar