@echo off
echo Packaging autohit

echo ... removing old
rmdir package /s/q

echo ... pack autohit.jar
mkdir .\package
jar cvf package\autohit.jar autohit creator master redux *.class

echo ... pack libraries
mkdir .\package\lib
rem jump into the dir, cause jar wont resolve a relative path right...
cd lib
jar cvf ..\package\lib\ahlib.jar HTTPClient
cd ..
copy .\lib\*.* package\lib

echo ... copy docs
mkdir .\package\docs
xcopy docs package\docs /e /q /i

echo ... copy tests and samples
mkdir .\package\test
xcopy test package\test /e /q /i

echo ... copy helpers
copy pack-setenv.bat package\setenv.bat

echo ... zip it
del .\autohit-v01.zip
rem zip-directory hell
cd package
pkzip ..\autohit-v01.zip -rec -dir=current *.*
cd ..

echo done packaging all...

