#!/bin/bash
echo "Compiling RarsProc..."
javac -cp ".;dependencies/*" src/RarsProc/*.java src/Common/*.java -d build
#java -cp ".;build/;dependencies/*" RarsProc
if [[ $? -eq 0 ]]
then
	cd build
	echo "Creating RarsProc JAR..."
	jar cfm ../dependencies/RarsProc.jar ../RarsProcManifest.mf *
fi

if [[ $? -eq 0 ]]
then
	cd ..
	rm -r build/*
	echo "Compiling AutoGrader..."
	javac -cp ".;dependencies/*" src/AutoGrader/*.java src/Common/*.java -d build
	#java -cp ".;build/;dependencies/*" AutoGrader HWData.json CodeTestCases.json
fi
if [[ $? -eq 0 ]]
then
	cd build
	echo "Creating AutoGrader JAR..."
	jar cfm ../dependencies/AutoGrader.jar ../AutograderManifest.mf *
	cd ..
	rm -r build/*
fi
