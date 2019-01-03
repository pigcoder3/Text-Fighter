#!/bin/sh
CURRDIR="$( cd "$( dirname "$0" )" >/dev/null && pwd )";
cd "$CURRDIR" || exit

if [ -d bin ]; then
	rm -r bin;
fi
if [ ! -d bin/main/java/ ]; then
	mkdir -p bin/main/java;
fi

find src/main/java/org -name "*.java" > sourceFiles.txt
javac -classpath ".:lib/json-simple-1.1.1.jar" -Xlint:unchecked -d bin/main/java @sourceFiles.txt
cp -r src/main/res bin/main/ || exit
cd bin/main/java || exit
java -classpath ".:../../../lib/json-simple-1.1.1.jar" org.textfighter.TextFighter
cd ../../../ || exit
pwd
rm sourceFiles.txt
