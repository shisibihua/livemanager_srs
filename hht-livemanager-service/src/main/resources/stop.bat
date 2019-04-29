@echo off

cd /d %~dp0

For /f "tokens=1* delims=:" %%i in ('Type pid.txt^|Findstr /n ".*"') do (
 taskkill /f /PID  %%j
)

exit




