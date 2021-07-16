#!/bin/bash
java -cp ".;dependencies/AutoGrader.jar;dependencies/gson-2.8.7.jar;dependencies/RarsProc.jar;dependencies/zip4j-2.8.0.jar" AutoGrader "$@"
