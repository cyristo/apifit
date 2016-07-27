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

Here is the test table description in FitNesse wiki :
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
As Apifit is based on FitNesse, it inherits of its data driven testing capabilities. A table can contain multiple rows, testing the same API with different inputs and outputs. 

An interesting Apifit feature is that test inputs data can be some parts of the host name or path to the API, as well as any GET parameters or POST payloads. For example, this capability enables you to check the same endpoint on different environments, or different version of the service within the same test table.

To demonstrate this, let's consider that the **typicode** part of the **jsonplaceholder.typicode.com** host is variable. Let's consider that the full path is variable as well. 

First we create two FitNesse variables (API_HOST, API_PATH) for the **host** and the **path**. These FitNesse variables contains the Apitfit variable parts (named host_var and path_vat] into brackets [].

```
!define API_HOST {jsonplaceholder.[host_var].com}
!define API_PATH {/[path_var]}
```
These variables can now used any the test tables of any test pages. 

We will test different **id** of the **users** resource, which is a GET parameter of this API.   
Here is the test table description in FitNesse wiki :
```
|ddt:API Fixture|${API_HOST}|${API_PATH}                     |
|[host_var]     |[path_var] |id|status code?|$.[0].name?     |
|typicode       |users      |1 |200         |Leanne Graham   |
|typicode       |users      |2 |200         |Ervin Howell    |
|typicode       |users      |3 |200         |Clementine Bauch|
```

Now let's run the test. Here is the result :
![very simple test](https://github.com/cyristo/apifit/blob/master/images/apifit%20data%20driven%20test.PNG)

BDD test style
--------------

Another interesting Apifit feature is that you can design a test scenario involving different API endpoints within the same test page. The result of a step can be used for input or assertions in the next test step. 

This is possible through the Apifit Session Fixture. You first  


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


