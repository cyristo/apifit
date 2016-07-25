# Apifit
## Start securing your API layer through an automated user friendly test suite, in 5 minutes !


Installation
------------
* Prerequisite : have Java 8 and FitNesse installed (www.fitnesse.org)
* Download apifit-dist.zip
* Extract apifit-dist.zip under FitNesseRoot
* Start FitNesse
* Set the FitNesse test engine to SLIM (!define TEST_SYSTEM {slim})
* Set the FitNesse classpath with the Apifit distribution (!path <your_local_dir>/FitNesseRoot/apifit/lib/*.jar)
* You are ready to use Apifit !


Getting Started - your first test
---------------------------------

We will use the JSONPlaceholder API at jsonplaceholder.typicode.com (Fake Online REST API for Testing and Prototyping).

We will test the 'users' resource.

For this resource, we will check that : 
* the http status code is **200**
* 'ten' users are returned
* the name of the first user is 'Leanne Graham'
* the address street name of the second user is 'Victor Plains'

First, we create 2 

Available features
------------------

Functions can be invoked at the tail end of a path - the input to a function is the output of the path expression.
The function output is dictated by the function itself.

| Function                  | Description                                                        | Output    |
| :------------------------ | :----------------------------------------------------------------- |-----------|
| min()                    | Provides the min value of an array of numbers                       | Double    |
| max()                    | Provides the max value of an array of numbers                       | Double    |
| avg()                    | Provides the average value of an array of numbers                   | Double    |
| stddev()                 | Provides the standard deviation value of an array of numbers        | Double    |
| length()                 | Provides the length of an array                                     | Integer   |


