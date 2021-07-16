#!/bin/bash
./runAutoGrader.sh tests/StringHashCode_HWData.json tests/StringHashCode_CodeTestCases.json
if [[ $? -eq 0 ]]
then
	echo Test Complete
fi
