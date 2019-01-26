#!/usr/bin/env bash
mode=0
compile=0
cpresources=0
run() {
	CURRDIR="$( cd "$( dirname "$0" )" >/dev/null && pwd )";
	cd "$CURRDIR" || exit
	if [ -d bin ] && [ "$2" = 1 ]; then
		rm -r bin;
	fi
	if [ ! -d bin/main/java/ ]; then
		mkdir -p bin/main/java;
	fi
	if [ "$2" = 1 ]; then
		find src/main/java/org -name "*.java" > sourceFiles.txt
		javac -g -classpath ".:lib/json-simple-1.1.1.jar" -Xlint:unchecked -d bin/main/java @sourceFiles.txt
		rm sourceFiles.txt
	fi
	if [ "$4" = 1 ] || [ "$2" = 1 ]; then
		cp -r src/main/res bin/main/ || exit
	fi
	cd bin/main/java || exit
	if [ "$1" = 1 ]; then
		java -classpath ".:../../../lib/json-simple-1.1.1.jar" org.textfighter.TextFighter test
	elif [ "$1" = 0 ]; then
		java -classpath ".:../../../lib/json-simple-1.1.1.jar" org.textfighter.TextFighter
	fi
}

main() {

	if [ "$#" = 0 ]; then
		run 0 0
		exit
	fi

	while [ $# != 0 ]; do
		case $1 in
			"--compile")
				compile=1
				shift
				;;
			"--test")
				mode=1
				shift
				;;
			"--cpres")
				cpresources=1
				shift
				;;
			"--help")
				printf "usage run --compile  compiles the game\\n          --test  tests packs for errors\\n          --defaultpackmsgs  Displays default pack messages if you are testing packs\\n          --cpres  copies the resources without needingt to compile\\n          --help\\nall can be used at once"
				exit
				;;
			*)
				printf "usage run --compile\\n          --test\\n          --defaultpackmsgs\\n          --cpres\\n      --help\\nall can be used at once"
				exit
				;;
		esac
	done
	
	run $mode $compile $defaultpackmsgs $cpresources
}

main "$@"
