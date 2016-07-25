# API FIT
## Start securing your API through an automated user friendly test suite in 5 minutes, really !


Installation
------------
Download and install Java 8

Download and install FitNesse (www.fitnesse.org)

Download apifit-dist.zip

Create the /apifit/lib directory under FitNesseRoot

Extract apifit-dist.zip under FitNesseRoot/apifit/lib

Start FitNesse

Set the FitNesse test engine to SLIM (!define TEST_SYSTEM {slim})

Set the FitNesse classpath with the apifit distribution (!path <your_local_dir>/FitNesseRoot/apifit/lib/*.jar)


Getting Started - your first test in 5 minutes
----------------------------------------------



Functions
---------

Functions can be invoked at the tail end of a path - the input to a function is the output of the path expression.
The function output is dictated by the function itself.

| Function                  | Description                                                        | Output    |
| :------------------------ | :----------------------------------------------------------------- |-----------|
| min()                    | Provides the min value of an array of numbers                       | Double    |
| max()                    | Provides the max value of an array of numbers                       | Double    |
| avg()                    | Provides the average value of an array of numbers                   | Double    |
| stddev()                 | Provides the standard deviation value of an array of numbers        | Double    |
| length()                 | Provides the length of an array                                     | Integer   |


