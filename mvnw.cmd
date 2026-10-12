@echo off
setlocal
set MAVEN_VERSION=3.9.11
set MAVEN_HOME=%~dp0.maven\apache-maven-%MAVEN_VERSION%
if exist "%MAVEN_HOME%\bin\mvn.cmd" goto run
set ZIP=%TEMP%\apache-maven-%MAVEN_VERSION%-bin.zip
set URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip
echo Maven is not installed. Downloading Apache Maven %MAVEN_VERSION%...
powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -UseBasicParsing -Uri '%URL%' -OutFile '%ZIP%'" || exit /b 1
if not exist "%~dp0.maven" mkdir "%~dp0.maven"
powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Force '%ZIP%' '%~dp0.maven'" || exit /b 1
del /q "%ZIP%" >nul 2>&1
:run
call "%MAVEN_HOME%\bin\mvn.cmd" %*
endlocal
