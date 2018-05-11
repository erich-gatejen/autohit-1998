@echo off
echo Making all

@call %AUTOHIT_ROOT%\autohit\make.bat
@call %AUTOHIT_ROOT%\creator\make.bat

javac -O %AUTOHIT_ROOT%\*.java

echo done making all...

