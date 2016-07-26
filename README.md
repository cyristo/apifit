# Apifit
## Start securing your REST API layer through an automated user friendly test suite, in 5 minutes !


Installation
------------
Prerequisite : have Java 8 and FitNesse installed (www.fitnesse.org).

* Download apifit-dist.zip
* Extract apifit-dist.zip under FitNesseRoot
* Start FitNesse
* Set the FitNesse test engine to SLIM 
```!define TEST_SYSTEM {slim}```
* Set the FitNesse classpath with the Apifit distribution 
```!path <your_local_dir>/FitNesseRoot/apifit/lib/*.jar```

You are ready to use Apifit !


Getting Started - your first *very simple* test
-----------------------------------------------

We will test here the *users* resource of the JSONPlaceholder API at https://jsonplaceholder.typicode.com (Fake Online REST API for Testing and Prototyping).

For this resource, let's check that : 
* the http status code is *200*
* *ten* users are returned by the API call
* the name of the first user is *Leanne Graham*
* the address street name of the second user is *Victor Plains*

To create the test, we add a FitNesse test page with an Apifit dynamic decision table.  This table is based on the **API Fixture**. 

The first row is the table header. We have here the fixture name, and its constructor parameters. This test needs two parameters:
* the *host* (jsonplaceholder.typicode.com) 
* the *path* (/users)

The second row is the column headers. This test doesn't requieres input data, only output assertions (columns with an ?). 

The third row contains the test data, which are only assertions for this simple test. Apitfit assertions are based on two patterns:
* Apifit key words (like *status code*)
* Jsonpath expression language (http://goessner.net/articles/JsonPath/)

Here is the test table :
```
|ddt:API Fixture|jsonplaceholder.typicode.com|/users                             |
|status code?   |$.length()?                 |$.[0].name?  |$.[1].address.street?|
|200            |10                          |Leanne Graham|Victor Plains        |
```

Now let's run the test. Here is the result :
![very simple test](https://github.com/cyristo/apifit/blob/master/images/apifit%20very%20simple%20test.PNG)

Congratulations ! You've just created your first Apifit test...


Data Driven test style
----------------------


BDD test style
--------------



Available features
------------------

Apifit includes these capabilities. To go further with it, please refer to the user guide.

| Capability              | Description                                                               | 
| :----------------------- | :----------------------------------------------------------------------- |
| min()                    | Provides the min value of an array of numbers                            | 
| max()                    | Provides the max value of an array of numbers                            | 
| avg()                    | Provides the average value of an array of numbers                        | 
| stddev()                 | Provides the standard deviation value of an array of numbers             | 
| length()                 | Provides the length of an array                                          | 


