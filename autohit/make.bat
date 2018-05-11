@echo off
echo ...Making autohit

javac -O %AUTOHIT_ROOT%\autohit\*.java

echo ... ... Making autohit.utils
javac -O %AUTOHIT_ROOT%\autohit\utils\*.java
echo ... ... Making autohit.transport
javac -O %AUTOHIT_ROOT%\autohit\transport\*.java
echo ... ... Making autohit.verify
javac -O %AUTOHIT_ROOT%\autohit\verify\*.java

@call %AUTOHIT_ROOT%\autohit\vm\make.bat

echo ...done making autohit...

