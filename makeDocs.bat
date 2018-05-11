@echo off

echo Making all documentation.
echo ...this could take a while.

cd %AUTOHIT_ROOT%
javadoc -d %AUTOHIT_ROOT%\docs\api autohit autohit.utils autohit.vm autohit.transport autohit.verify creator.compiler

echo Done making all documentation

