@echo off
echo Setting the environments variables...
echo For this to work correctly, you must edit this file
echo and set AUTOHIT_ROOT to the root directory for your
echo installation.
set AUTOHIT_ROOT=c:\dev\autohit-build

set CLASSPATH=%AUTOHIT_ROOT%\;%AUTOHIT_ROOT%\lib;%AUTOHIT_ROOT%\lib\xml.jar;.;

rem # WARNING!!!!  WARNING!!!!  The first element of the classpath MUST be
rem # the root directory (%AUTOHIT_ROOT) or the compiler will not work!  This is
rem # because the compiler has to find the .dtd files located under
rem # %AUTOHIT_ROOT%\lib




