# Prolog - Request and Response Logging Library
Prolog is a logging library designed for Spring Boot applications to log entry and exit points in service and controller methods. It provides functionality to log requests and responses in JSON format, including information such as class name, method name, URI, host, status code, and response body.

## Functionality
* Logs entry (Request In) and exit (Request Out) points in service and controller methods.
* Logs requests and responses in JSON format.
* Logs contain information such as class name, method name, URI, host, status code, and response body,etc.
* Mask sensetive data in logs, (mask attributes based on user input)
* slf4j log dependency is used for logging functionalities.
  
### Sample of Request Log 
```
{
  "logPointState": "REQUEST START HERE",
  "className": "UserController",
  "methodName": "getAllUsers",
  "arguments": [],
  "queryParam": null,
  "requestURI": "/user",
  "logLevel": "INFO",
  "serviceName": "USER_SERVICE",
  "traceId": "171266723775373834",
  "requestHeaders": {
    "postman-token": "5d2f1bdc-7ddd-4587-bd82-b5fc3bc62f10",
    "host": "example.com",
    "connection": "close",
    "accept-encoding": "deflate, gzip",
    "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36",
    "accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"
  }
}

```

### Sample of Response Log 
```
{
  "logPointState": "RESPONSE START HERE",
  "className": "UserController",
  "methodName": "getAllUsers",
  "arguments": [],
  "requestURI": "/hello",
  "logLevel": "INFO",
  "serviceName": "USER_SERVICE",
  "traceId": "9876543210987654321",
  "responseBody": [
    {"username": "user1", "email": "user1@example.com"},
    {"username": "user2", "email": "user2@example.com"}
  ],
  "responseStatusCode": 200,
  "responseError": null
}
```

## Using Prolog Locally
- Step 1: Clone the Dependency Project
  
  ```
  git clone <repository-url>
  ```
- Step 2: Navigate to the Cloned Project Directory
  
  ```
  cd <dependency-project-directory>
  ```
- Step 3: Install the Dependency Locally (Use Maven to install the dependency project locally by running the following command)
  
  ```
  mvn install
  ```
- Step 4: Use the Dependency in Your Project
  To use Prolog in your Spring Boot project, add the following dependency to your pom.xml file:

  ```
   <dependency>
    <groupId>com.proLog</groupId>
    <artifactId>ProLog</artifactId>
    <version>0.0.1-SNAPSHOT</version>
   </dependency>
  ```

## Configuration
Prolog requires certain attributes to be set in your application.properties file:

```
prolog.logging.requestIdentifier = REQUEST START HERE
prolog.logging.responseIdentifier = RESPONSE START HERE
prolog.logging.serviceName = TESTING_SERVICE
prolog.logging.environment-name=test
prolog.logging.version=1.0
prolog.logging.pretty-log-format=true
```
* **prolog.logging.requestIdentifier:** A user-defined string to identify the start of a request. (mandatory)
* **prolog.logging.responseIdentifier:** A user-defined string to identify the start of a response. (mandatory)
* **prolog.logging.serviceName:** The name of the microservice. (mandatory)
* **prolog.logging.pretty-log-format:** Set to true to format logs as beautify JSON.
* **prolog.logging.environment-name:** Set encironment name such as (test, prod, etc).
* **prolog.logging.version:** Set version of microservice deployement if needed.
```
Note: serviceName, responseIdentifier, and requestIdentifier are mandatory properties.
```
## Main Class Configuration
- You must add annotation **@ComponentScan(basePackages = {"com.prolog","YOUR.PROJECT.PACKAGE"})** in your main class.
```
@SpringBootApplication
@ComponentScan(basePackages = {"com.prolog","com.example"})
public class TestPrologApplication {
	public static void main(String[] args) {
		SpringApplication.run(TestPrologApplication.class, args);
	}
}
```

## Prolog Annotations Usage
- There are two annotation in current version **(ClassLogger, MethodLogger)**.
- ClassLogger used only on classes.
- MethodLogger used only on methods.

## Usage Example ClassLogger
- Just add annotation **@ClassLogger** on your class.
```
@RestController
@ClassLogger
public class ControllerClassExample {

  @GetMapping("/hello")
  public String printHello () {
      return "hello";
  }
}  
```
## Usage Example MethodLogger
- Just add annotation **@MethodLogger** method you want to log
```

@MethodLogger
 public String printHello () {
      return "hello";
  }
```
## Annotations attributes
- Both annotation (ClassLogger, MethodLogger) has 9 attributes (same functionality , the only different is one used for classes and one for methods only)
- LogOnEntry (has default value **true**), if you want to log response (exit) of methods only make this value false

	```
	@ClassLogger(logOnEntry = false)
	```
- LogOnExit (has default value **true**), if you want to log request (entry) of methods only make this value false

	```
	@ClassLogger(logOnExit = false)
	```
- level (has default value **INFO**), if you want to log DEBUG level you can set it to debug

	```
	@ClassLogger(level = LogLevel.DEBUG)
	```  
- hideHeadersSensitiveData (has default value of false), if you want to mask senstive data in header of requests make it true

	```
	@ClassLogger(hideHeadersSensitiveData = true)
	```
 - hideRequestBodySensitiveData (has default value of false), if you want to mask senstive data in rquest body make it true

	```
	@ClassLogger(hideRequestBodySensitiveData = true)
	```
 - hideResponseBodySensitiveData (has default value of false), if you want to mask senstive data in response body make it true

	```
	@ClassLogger(hideResponseBodySensitiveData = true)
	```
 - hideRequestBody (has default value of false), if you want to hide request body from logs make it true

	```
	@ClassLogger(hideRequestBody = true)
	```
 - hideResponseBody (has default value of false), if you want to hide response body from logs make it true

	```
	@ClassLogger(hideResponseBody = true)
	```
 
 - sensitiveAttributes (has default value of {"password", "token", "x-api-key"}), if you want to add sensitive attributes you want to mask, add it in array in annotation

	```
	@ClassLogger(sensitiveAttributes = {"password","x-api-key"})
	```
## Current Version (0.0.1-SNAPSHOT)

