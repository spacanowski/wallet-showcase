# wallet-showcase

Simple application using Java 11, dropwizard, lombok and gradle to showcase thread safe transfer between accounts.

## Running test

To run tests run:
```
./gradlew clean test
```

## Running locally

To run locally application on port `8080` run:
```
./gradlew clean shadowJar
java -jar  ./build/libs/simple-wallet-0.1.0-all.jar server
```
