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


Getting Started - your first *very simple* test
-----------------------------------------------

We are using here the JSONPlaceholder API at jsonplaceholder.typicode.com (Fake Online REST API for Testing and Prototyping).

We want to test the *users* resource.

For this resource, let's check that : 
* the http status code is *200*
* *ten* users are returned
* the name of the first user is *Leanne Graham*
* the address street name of the second user is *Victor Plains*

To create the test, we add a FitNesse test page with an Apifit dynamic decision table.  This table is based on the API Fixture. 

This fixture requires two parameters in its contructor : the *host* and the *path*. There is no other parameter to feed the table, only assertions (meaning colonnes with a ? at the end. Asstertions are based on 
'''
|ddt:API Fixture|jsonplaceholder.typicode.com|/users                             |
|status code?   |$.length()?                 |$.[0].name?  |$.[1].address.street?|
|200            |10                          |Leanne Graham|Victor Plains        |
'''


![very simple test](https://github.com/cyristo/apifit/blob/master/images/apifit%20very%20simple%20test.PNG)

Congratulations ! You've just created your first Apifit test...

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


