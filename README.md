# AutoGrader

A multi-thread, multi-process Autograder for RISC-V programs built for direct use with raw Canvas submission downloads. Designed to be flexible, all settings are parsed in through two supplied json files. (Explained in detail below.) To run the AutoGrader, simply run the bash script ```runAutoGrader.sh``` and supply it two arguments for the json files.

```./runAutoGrader.sh HWData.json TestCases.json```

This is a java application, so it is assumed that java is in the ```PATH``` environment variable.

## HWData Settings File

The first settings file supplies general settings for the entire applicaiton. An example is shown below. More examples are shown in the tests folder.

```
{
	"gradingScript": "",
	"zippedSubmissions": "tests/StringHashCode.zip",
	"cleanedSubmissionsDir": "TEMP",
	"gradebook": "tests/StringHashCode_Results.csv",
	"numThreads": "2",
	"students": [
		"notInSubmissionStudent"
	],
	"acceptedCodeFiles": [
		"s",
		"asm"
	],
	"acceptedReadmeFiles": [
		"txt",
		"md"
	]
}
```

Not Optional:
Setting | Description
--- | ---
```zippedSubmissions``` | This defines the zip file where the students submissions are. This should be a direct canvas download, with no need to unzip or modify anything yourself.
```cleanedSubmissionsDir``` | This defines the temporary directory to place students unzipped submissions when running the application. This folder will be removed once the application finishes running tests.
```gradebook``` | This defines the file where the results of the AutoGrader will be placed. This file will be formated as a CSV file.
```numThreads``` | This defines how many threads will be used when grading the students submissions. The accepted range is [1-10].
```acceptedCodeFiles``` | A list of extensions that are allowed as submissions for code files. The list is in order of precidence.
```acceptedReadmeFiles``` | A list of extensions that are allowed as submissions for README files. The list is in order of precidence.

Optional:
Setting | Description
--- | ---
```gradingScript``` | This defines the assembly file that will be run as the main file when simulating the students RISC-V submission, often usefull for giving students starter code that does not change. If left blank, the students submission is assumed to be the main file.
```students``` | A list of students to grade, usefull when only a handfull of students need to be graded from the zip file.

## TestCases Settings File

The second settings file supplies the tests to be performed on both the students code and README submission. The order of the tests in the settings file has no effect on how the tests are run, and does not guarintee the order they will run in. There can be any number of each type of test. An example is shown below, shortened for brevity. More, complete, examples are shown in the tests folder.

```
{
	"tests": [
		{
			"commentTest": {
				...
			}
		},
		{
			"keywordTest": {
				...
			}
		},
		{
			"executeTest": {
				...
			}
		},
		{
			"lengthTest":{
				...
			}
		}
	]
}
```

There are several types of tests that can be performed: a comment test, a keyword test, a length test, and an execute test.

### Length Test
The length test checks that a file contains enough lines.

```
"lengthTest":{
	"applyTo": "readme",
	"points": "5",
	"comment": "README was not sufficient.",
	"minLines": "7"
}
```
All of the settings are not optional.
Setting | Description
--- | ---
```applyTo``` | Either "code" or "readme", to specify which file to perform the test on, given the file types defined for each in the general settings json file.
```points``` | How many points the test is worth.
```comment``` | The comment that will be placed in the gradebook should the student's submission fail the test.
```minLines``` | The minimum number of lines that is required.


### Comment Test
The comment test checks for adequate comment coverage. 

```
{
	"commentTest": {
		"applyTo": "code",
		"points": "5",
		"comment": "Not enough comment coverage",
		"percentage": "25",
		"symbol": "#"
	}
}
```
All of the settings are not optional.
Setting | Description
--- | ---
```applyTo``` | Either "code" or "readme", to specify which file to perform the test on, given the file types defined for each in the general settings json file.
```points``` | How many points the test is worth.
```comment``` | The comment that will be placed in the gradebook should the student's submission fail the test.
```percentage``` | The percentage of the lines in the file that needs to have comments.
```symbol``` | The symbol that defines what a comment is.

### Keyword Test
The keyword test can check for both the presence or absence of the supplied keywords. All keyword searches are always converted to lowercase, *so all keywords should be lowercase*.

```
"keywordTest": {
	"applyTo": "code",
	"points": "5",
	"comment": "Pseudo instructions are not allowed",
	"takeAwayPoints": "true",
	"onePointPer": "true",
	"minNumOccurances": 0,
	"words": [
		"beqz",
		"bgez",
		...
		"tail"
	]
}
```
All of the settings are not optional.
Settings | Description
--- | ---
```applyTo``` | Either "code" or "readme", to specify which file to perform the test on, given the file types defined for each in the general settings json file.
```points``` | How many points the test is worth.
```comment``` | The comment that will be placed in the gradebook should the student's submission fail the test.
```takeAwayPoints``` | Determines if points will be taken away for having the keywords rather than given.
```minNumOccurances``` | Determines the minimum number of occurances required to give/take away points. Set to 0 if none are expected.
```onePointPer``` | Determines if one point will be given/taken away per keyword rather than giving/taking away all points when ```minNumOccurances``` is met.
```words``` | The list of kwywords to check for.

### Execute Test
The execute test runs a students submission with certian inputs and checks for certian outputs.

```
"executeTest": {
	"applyTo": "code",
	"points": "5",
	"removeWhitespace": "true",
	"matchCase": "false",
	"outputConditional": "or",
	"input": [
		"hello",
		"\n"
	],
	"output": [
		"56568"
	],
	"regVals": {
		"s0": "60"
	}
}
```

All of the settings are not optional.
Settings | Description
--- | ---
```applyTo``` | Either "code" or "readme", to specify which file to perform the test on, given the file types defined for each in the general settings json file.
```points``` | How many points the test is worth.
```removeWhiteSpace``` | Weather or not to remove the whitespace in the programs output before checking it against the expected outputs.
```matchCase``` | Weather of not to match the case of the expected outputs when checking them against the programs output.
```outputConditional``` | Either "and" or "or", defining weather or not to check that all of the outputs are present in the programs output ("and"), or only that at least one output in the list is present ("or").
```input``` | The list of inputs that are given to the students program.
```output``` | The list of outputs that are used to check agains the students program output.
```regVals``` | A map of registers and there expected values. Only works on standard registers and not FPR's at the moment.


## TODO

1. Add register value checking
1. Remove lowercase keyword dependence
1. Add FPR checking
