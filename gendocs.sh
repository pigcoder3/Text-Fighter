#!/usr/bin/env bash
echo lib/*
javadoc -private -d docs -sourcepath "src/main/java" -classpath lib/json-simple-1.1.1.jar com.seanjohnson.textfighter
