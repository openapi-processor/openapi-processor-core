# contributing to openapi-processor

## feature & bug reports

In case some feature is missing, or the generated code is not 100% what you would expect please create an issue with test case. Providing a test case will help significantly :-) 

A test case is a single folder with an openapi.yaml file, and the expected Java files the processor should create. The structure looks like this:

    resources/tests/my-test
     +--- inputs.yaml
     \--- inputs
          +--- mapping.yaml
          \--- openapi.yaml
    
    the expected files:
    
     resources/tests/my-test
     +--- generated.yaml
     \--- generated
          +--- api
          |    \--- EndpointApi.java
          \--- model
               \--- Foo.java
    
    the `inputs.yaml` and `generated.yaml` use the same simple format:
    
     items:
        - inputs/openapi.yaml
        - inputs/mapping.yaml
    
    or
    
     items:
        - generated/api/EndpointApi.java
        - generated/model/Foo.java
    
The `mapping.yaml` contains the mapping information if the specific processor (e.g. Spring) requires it.

See the [existing integration tests][oap-int-resources] for a couple of examples. Note that most of them only contain the expected files. The inputs files are in the openapi-processor-test project to simplify re-use. 

## working on the code

### jdk

the minimum jdk is currently JDK 8

### ide setup

openapi-processor can be imported into IntelliJ IDEA by opening the `build.gradle` file.
 
### running the tests

To run the tests use `./gradlew check`. 

`check` runs the unit tests, and the integration tests  

### documentation

The documentation is in `docs`. See the `README` in `docs` for the setup and viewing it locally.

[oap-int-resources]: https://github.com/hauner/openapi-processor-spring/tree/master/src/testInt/resources/tests
