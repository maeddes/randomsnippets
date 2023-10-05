## Through Maven

```java
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8085

mvn spring-boot:run -Dserver.port=8085 will not work
```

## Through Java

```java
java -jar -Dserver.port=8085 target/hft-hello-0.0.1-SNAPSHOT.jar

java -jar -Dspring.profiles.active=dev target/hft-hello-0.0.1-SNAPSHOT.jar
```

## Through env variables

```bash
SERVER_PORT=8080 mvn spring-boot:run

SERVER_PORT=8080 SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
``` 

## Through Config Server
