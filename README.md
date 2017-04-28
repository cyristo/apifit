# Apifit
## Secure your API layer the agile way, with an automated and collaborative testing tool !

Apifit is a framework that extends FitNesse with a full range of Rest API testing capabilities. If you don't know about FitNesse, start here : www.fitnesse.org.

Installation
------------
Prerequisite : have Java 8 installed.

* Download Apifit x.x.zip
* Extract the zip
* Start FitNesse : java -jar fitnesse-standalone.jar
* Start up a browser and go to localhost/8001

You're ready to go !

You can execute all examples below directly from the distribution. Go to the localhost:8001/ApiFitDemoPage and click Test !

Getting Started - your first *very simple* test
-----------------------------------------------

We will test here the *users* resource of the JSONPlaceholder API at https://jsonplaceholder.typicode.com (Fake Online REST API for Testing and Prototyping).

For this resource, let's check that : 
* the http status code is *200*
* *ten* users are returned by the API call
* the name of the first user is *Leanne Graham*
* the address street name of the second user is *Victor Plains*
* the response contains at least one time the word *innovative* 
* the response does not contain the word *Trump* (fortunatly not) 

To create the test, we add a FitNesse test page with a FitNesse dynamic decision table.  This table uses the **API Fixture** of Apifit. 
```
|ddt:API Fixture         |GET          |jsonplaceholder.typicode.com|/users                                    |
|status code?|$.length()?|$.[0].name?  |$.[1].address.street?|APIFIT:CONTAINS(innovative)?|APIFIT:COUNT(Trump)?|
|200         |10         |Leanne Graham|Victor Plains        |TRUE                        |0                   |
```

The first row is the table header. We have here the fixture name, and 3 constructor parameters: 
* the *http verb* to execute (GET)
* the *host* of the service (jsonplaceholder.typicode.com) 
* the *path* to the ressource (/users)

The second row is the column headers. This test doesn't requiere any input data, only output assertions (header column names with an ?). 

The third row contains the test data. 

Assertions done on returned data can use :
* Apifit sentences (like *status code* or *execution time*)
* Apifit patterns (like COUNT or TODAY+X)
* JsonPath expression language, dedicated to assert on Json responses (https://github.com/jayway/JsonPath/blob/master/README.md)
* GPath expression language, to assert on Json, XML or HTML responses (http://groovy-lang.org/processing-xml.html)
* Numerical or regular expressions comparisons  (http://www.fitnesse.org/FitNesse.UserGuide.WritingAcceptanceTests.SliM.ValueComparisons) 

You - What ? Asserting on Html result pages within a simple test table ? 
Me - Yes, no more heavy-and-hard-to-maintain Selenium tests !!!

Now let's run the test. No, wait, just a little step more. Indeed, we need to provide FitNesse with our Apifit Fixtures. Just a simple import directive, to be placed in a suite setup page for example. 
```
|import                                |
|apifit.fixture                        |
```
Now let's run the test.
Here is the result :
![very simple test](https://github.com/cyristo/apifit/blob/master/images/apifit%20very%20simple%20test.PNG)

Congratulations ! You've just run your first Apifit test...and it's green !

Data Driven test style
----------------------
Apifit inherits of FitNesse data driven testing capabilities. A table can contain multiple rows, testing the same API with different inputs and outputs. 

An interesting Apifit feature is that data used as test inputs can be some parts of the API URI (like host name or path to the API), as well as any GET parameters or POST payloads. For example, this capability enables you to check the same API on different environments, or different version of the service, or different outputs with different inputs, all of that within the same test table.

To demonstrate this, let's consider that the *typicode* part of the *jsonplaceholder.typicode.com* host is variable. Let's consider that the full path is variable as well. 

First we create two FitNesse variables (API_HOST, API_PATH) for the *host* and the *path* (this is optional, but cleaner). These FitNesse variables contains the Apitfit variable parts (named *host_var* and *path_var*] put into brackets [].

```
!define API_HOST {jsonplaceholder.[host_var].com}
!define API_PATH {/[path_var]}
```
These variables can now used in any test tables of any test pages. 

We will test here different *id* of the *users* resource, which is a GET parameter of this API.   
Here is the test table description in FitNesse wiki :
```
|ddt:API Fixture|GET       |${API_HOST}|${API_PATH}                  |
|[host_var]     |[path_var]|id         |status code?|$.[0].name?     |
|typicode       |users     |1          |200         |Leanne Graham   |
|typicode       |users     |2          |200         |Ervin Howell    |
|typicode       |users     |3          |200         |Clementine Bauch|
```

Now let's run the test. Here is the result (green as usual) :
![data driven test](https://github.com/cyristo/apifit/blob/master/images/apifit%20data%20driven%20test.PNG)

BDD test style
--------------

Another interesting Apifit feature is that you can design a BDD test scenario involving different API calls within the same test page. The result of a step can be used for input or assertions in the next test step. 

To do so, you need to start your BDD scenario by opening an Apifit test session. This is possible through the Apifit **Session Fixture**. 

This fixture generates a unique test session id. This id is used by Apifit to share objects in the background of a test scenario execution. 

To share the session context between test steps, the test session id needs to be put in a FitNesse variable and used as a contructor parameter of every Apifit fixtures involved in the scenario. 

At the end of the scenario, you need to close the session in order to clear out the session memory. 

Typicaly, this test session management is done through the setup and teardown capability of FitNesse. 
```
|Session Fixture          |
|set up?|test session id? |
|OK     |$TEST_SESSION_ID=|

...your BDD scenario...

|Session Fixture|$TEST_SESSION_ID|
|tear down?                      |
|OK                              |
```
Now let's consider this simple BDD scenario:

**GIVEN** I am authentified

**WHEN** I ask for the description of user number 1

**THEN** the response time is less than 1 second

**AND** the name of the user is Leanne Graham 

**AND** the city of the user is an alphanumeric string

Below the related scenario implementation with Apifit :
```
'''GIVEN I am authentified'''
'''WHEN I ask for the description of user number 1'''
'''THEN the response time is less than 1 second'''
|ddt:API Fixture|GET       |${API_HOST}|${API_PATH} |$TEST_SESSION_ID                        |
|[host_var]     |[path_var]|id         |status code?|execution error message?|execution time?|
|typicode       |users     |1          |200         |                        |< 1000         |
'''AND the name of the user is Leanne Graham'''
'''AND the city of the user is an alphanumeric string'''
|ddt: Data Result Parsing Fixture|$TEST_SESSION_ID   |
|$.[0].name?                     |$.[0].address.city?|
|Leanne Graham                   |=~/^[a-zA-Z]*$/    |
```

And the test execution result : 
![bdd test](https://github.com/cyristo/apifit/blob/master/images/apifit%20bdd%20test.PNG)


We took this Apifit BDD testing style presentation as an opportunity to introduce some new Apifit features:
* the *execution error message* sentence, which show the error message if any
* the *execution time* sentence, which is asserted here to be less than 1 second
* the *Data Result Parsing Fixture*, which enables to navigate and assert the result message of the previous step

Post example
-------------

Until now, we saw how to assert on GET requests with different test styles. Let's have a look at how we can do the same with POST requests. 

First we need to add the request payload template into the test session, so that each test (or table row) can update it before the test. Let's consider here that this payload comes from a FitNesse variable.
```
!define PAYLOAD {
!-{-!
    "id": 1,
    "name": "Leanne Graham",
    "username": "Bret",
    "email": "Sincere@april.biz",
    "address": !-{-!
      "street": "Kulas Light",
      "suite": "Apt. 556",
      "city": "Gwenborough",
      "zipcode": "92998-3874",
      "geo": !-{-!
        "lat": "-37.3159",
        "lng": "81.1496"
      !-}-!
    !-}-!,
    "phone": "1-770-736-8031 x56442",
    "website": "hildegard.org",
    "company": !-{-!
      "name": "Romaguera-Crona",
      "catchPhrase": "Multi-layered client-server neural-net",
      "bs": "harness real-time e-markets"
    !-}-!
  !-}-!
}

|Session Fixture|$TEST_SESSION_ID|
|payload                         |
|${PAYLOAD}                      |
```

Then, simply call the *API Fixture* like we did before, but with a *POST* parameter in its constructor. The test table below will execute 2 tests (2 POST requests) by updating the *id*, the *name* and the *city* of template payload, and asserting the same on the responses. 
```
|ddt:API Fixture|POST           |jsonplaceholder.typicode.com|/users      |$TEST_SESSION_ID               |
|$.id           |$.name         |$.address.city              |status code?|$.name?        |$.address.city?|
|12             |Jack the Ripper|London                      |201         |Jack the Ripper|London         |
|13             |Marilyn Manson |Los Angeles                 |201         |Marilyn Manson |Los Angeles    |
```

Let's test. And it's green again !
![post test](https://github.com/cyristo/apifit/blob/master/images/apifit%20post%20test.PNG)

Apifit features
-------------------

This is the list of what you can do with Apifit. 

Available Feature | Description                                                                
------------------|------------
Configurable request method | Set a GET, POST, PUT or DELETE method for an API test (default is GET) - Can be configured localy (test table) or globaly (test suite)
Configurable content type | Set any request content type (default is *application/json*) - Can be configured localy (test table) or globaly (test suite). Must be set to *application/xml* to assert on Xml results, and to *text/html* to assert on html page result.
Configurable scheme | Set the URL scheme (HTTP or HTTPS, default is HTTP) - Can be configured localy (test table) or globaly (test suite)
Configurable port | Set the URL port (default is 80) - Can be configured localy (test table) or globaly (test suite)
Configurable host | Set the API host - Can be configured localy (test table) or globaly (test suite)
Configurable path | Set the API path - Can be configured localy (test table) or globaly (test suite)
Configurable proxy | Set a proxy configuration if needed
Variable host parts | Set variable parts of a host string as test inputs
Variable path parts | Set variable parts of a path string as test inputs
URL parameters | Add any paramters to an URL as test inputs
Date pattern input | Set input date with embeded time resilient pattern (TODAY, TODAY+X, TODAY-X)
String pattern output | Assert output string with embeded string manipulation pattern (COUNT(xxx), CONTAINS(xxx)) 
Status code | Set an expected HTTP return status and direct the returned data in a success or error message (default is 200)
Body visualization | Show the content of a returned message (either succes or error) 
Json Body validation | Validation of a retruned Json body against a schema
Json navigation | Assert any element of a Json returned message (either succes or error) - based on JsonPath expression language
Xml navigation | Assert any element of a Xml returned message (either succes or error) - based on GPath expression language
Html navigation | Assert any element of a Html returned page (either succes or error) - based on GPath expression language
Data assertion | Assert any returned data with equality, numerical or regular expression comparisons - based on Java Regex language
Execution log | Get the full trace of a test scenario execution (including URLs, headers, payloads, status code, returned bodies)
Request time | Get and assert the time taken by a request
Test session | Share and reuse data between test steps (bodies, data values, cookies)
Cookies | Automatic cookies setting between API calls
Header | Add http header to a test session
Redirections | Compatible with HTTP redirections
Authentification | Compatible with HTTP authentification

This is the list of what you will be able to do with Apifit in a near futur.

//TODO Feature | Description                                                                
---------------|------------
Configurable timeout | Set a request and/or connection timeout - Can be configured localy (test table) or globaly (test suite)
Data externalisation | Get test data from external sources
SQL fixture | Set an SQL step in a test scenario to check data stored by an API 
Xml Body validation | Validation of a retruned Xml body against a schema

Apifit configuration
--------------------
work in progress

Apifit architecture
-------------------
work in progress
