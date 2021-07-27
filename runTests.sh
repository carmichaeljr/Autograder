#!/bin/bash
echo "Running StringHashCode tests:"
./runAutoGrader.sh tests/StringHashCode_HWData.json tests/StringHashCode_CodeTestCases.json > tests/StringHashCode_ProgOutput.txt
if [[ $? -eq 0 ]]
then
	if cmp -s "tests/StringHashCode_ExpectedResults.csv" "tests/StringHashCode_Results.csv"
	then
		echo "Results Test: PASSED"
	else
		echo "Results Test: FAILED"
	fi

	if cmp -s "tests/StringHashCode_ExpectedProgOutput.txt" "tests/StringHashCode_ProgOutput.txt"
	then
		echo "Output Test: PASSED"
	else
		echo "Output Test: FAILED"
	fi
else
	echo "Tests not run, program encountered an error."
fi

echo "Running BinarySearch tests:"
./runAutoGrader.sh tests/BinarySearch_HWData.json tests/BinarySearch_CodeTestCases.json > tests/BinarySearch_ProgOutput.txt
if [[ $? -eq 0 ]]
then
	if cmp -s "tests/BinarySearch_ExpectedResults.csv" "tests/BinarySearch_Results.csv"
	then
		echo "Results Test: PASSED"
	else
		echo "Results Test: FAILED"
	fi

	if cmp -s "tests/BinarySearch_ExpectedProgOutput.txt" "tests/BinarySearch_ProgOutput.txt"
	then
		echo "Output Test: PASSED"
	else
		echo "Output Test: FAILED"
	fi
else
	echo "Tests not run, program encountered an error."
fi
