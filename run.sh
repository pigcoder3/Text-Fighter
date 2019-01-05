#!/bin/sh
mode=0
compile=0
function run {
	CURRDIR="$( cd "$( dirname "$0" )" >/dev/null && pwd )";
	cd "$CURRDIR" || exit
	if [ -d bin ] && [ $1 = 1 ]; then
		rm -r bin;
	fi
	if [ ! -d bin/main/java/ ]; then
		mkdir -p bin/main/java;
	fi
	if [ $2 = 1 ]; then
		find src/main/java/org -name "*.java" > sourceFiles.txt
		javac -classpath ".:lib/json-simple-1.1.1.jar" -Xlint:unchecked -d bin/main/java @sourceFiles.txt
		rm sourceFiles.txt
		cp -r src/main/res bin/main/ || exit
	cd bin/main/java || exit
	if [ $2 = 1 ]; then
		java -classpath ".:../../../lib/json-simple-1.1.1.jar" org.textfighter.TextFighter testpacks
	else if [ $2 = 0 ]; then
		java -classpath ".:../../../lib/json-simple-1.1.1.jar" org.textfighter.TextFighter
	fi
}

function main {

	case $@ in
	"--compile")
		compile=1
		shift
		;;
	"--test")
		mode=1
		shift
		;;
	"--help")
		printf "usage run --compile\n
		              run --test\n
					  run --help"
		exit
		;;
	*)
		printf "usage run --compile\n
					  run --test\n
					  run --help"
		exit
		;;
	esac
	run $mode $compile

}

main $@
