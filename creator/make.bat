@echo off
echo ...Making creator

rem javac -O %AUTOHIT_ROOT%\creator\*.java

@call %AUTOHIT_ROOT%\creator\compiler\make.bat

echo ...done making creator...

