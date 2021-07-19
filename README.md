# Task Chosen

No. 2 (Front the Open Weather Map service).

## Design & Implementation

* A spring-boot application, based on other Spring modules (web, webflux, data etc.)
* A REST service with a single API: http://[host]:[port]/open-weather-map/[country 2-letter code]/[city]. An http request header named api-key holds the API key, default port is 8080, and the method is GET.
* There are three tiers: User (model, controller, validation and filter packages), Service (service package) and data (jpa package).
* Input validation is implemented using the Spring and Java validation capabilities (JSR 303).
* Persistency is implemented using a Spring Data repository.
* Enforcement of requests rate and API keys is done using Spring webflux's filters.
* The API is synchronous and so is the Open Weather Map API, however I used the reactive WebClient for calling the Open Weather Map service, because it provides a very convenient interface.
* Error information is handled and prepared for the client in an exception handling class. 
* API Keys and rate limit parameters are provided as properties. No hard-coded values.
* For providing flexibility of the data returned to the user and avoiding related database schema changes, the whole Open Weather Map response is saved, and the information
  required by the client is extracted using Json paths; the paths are provided as properties.
* I used Lombok to save writing boilerplate code.
* JUnit is used for unit testing; I omitted unit test classes for POJOs, exceptions and config classes.
* JUnit is also used for an integration test, i.e. staring the application and testing it by making Web calls.


## Solution Limitations

* In case of multiple places with the same name on a federal country (e.g. Richmond in Australia), the result is unpredictable because the API does not accept a state.
* Place names in languages other than English are not supported.
* Rate limit tracking is reset when the service restarts.
* The structure of the response returned to the client must be flat; however, this task requires only one field, so this is a non-issue.


## Changes I would do on a "real" production-level solution

* Add security (e.g. TLS support).
* Persist rate tracking data, or store them on a central cache (like Memcached) so they survive service restart.
* Provide the API Keys using an external repository.
* No need to save the weather data in a database and they query it, result can be returned immediately and data can be saved asynchronously.
* Add checkstyle and test code coverage checks.
* Create the database using an external script, not Hibernate's DDL update feature.


## How to Build and Run

  It is assumed that the target machine has the following:
* Java 8 (or later; I used Java 11) installed and JAVA_HOME environment variable is set.
* Maven is installed and its bin directory is in the path.
* Git client is installed (optional; source can be downloaded as a zip file).


###Instructions:
1. In the same directory, run *open-weather-map.sh* or *open-weather-map.bat*, according to your OS. This will build the code and start the Spring Boot application using Tomcat.
2. Use your favourite REST client (Postman, Curl...) to call the service. For example, using Curl:
   *curl -H "api-key: [API Key]" http://localhost:8080/open-weather-map/au/Melbourne* .
3. Service can be stopped using Ctrl-C.

