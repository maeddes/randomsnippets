# Code

Run mvn command for hello-world project

```
mvn io.quarkus:quarkus-maven-plugin:create \
    -DprojectGroupId=de.maeddes \
    -DprojectArtifactId=quarkus-hello \
    -DclassName="de.maeddes.GreetingResource" \
    -Dpath="/"
```

edit the source code to have 2 endpoints

```java
@Path("/")
public class GreetingResource {

    @GET
    @Path("hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello World!";
    }

    @GET
    @Path("fail")
    @Produces(MediaType.TEXT_PLAIN)
    public String fail() {
        System.exit(1);
        return "Hello World!";
    }
}
```

Run the code with `mvn compile quarkus:dev`

Edit the test class

```java
    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/hello")
          .then()
             .statusCode(200)
             .body(is("Hello World!"));
    }
```

# Docker

Pre-build
`mvn package -Pnative -Dnative-image.docker-build=true` (takes a bit)

Use dockerfile
`docker build -f src/main/docker/Dockerfile.native -t maeddes/quarkus-hello .`

Dockerfile details

```docker
FROM registry.access.redhat.com/ubi8/ubi-minimal
WORKDIR /work/
COPY target/*-runner /work/application
RUN chmod 775 /work
EXPOSE 8080
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
```

Check images `docker image ls | grep minutes`

```bash
docker image ls
REPOSITORY                                                         TAG                   IMAGE ID            CREATED             SIZE
maeddes/quarkus-hello                                              latest                aa904fae38fd        5 minutes ago       113MB
```

Run it `docker run -i --rm -p 8080:8080 maeddes/quarkus-hello`

```
2019-10-29 14:33:30,329 INFO  [io.quarkus] (main) knative-hello 1.0-SNAPSHOT (running on Quarkus 0.26.1) started in 0.041s. Listening on: http://0.0.0.0:8080
2019-10-29 14:33:30,329 INFO  [io.quarkus] (main) Profile prod activated.
2019-10-29 14:33:30,329 INFO  [io.quarkus] (main) Installed features: [cdi, resteasy]
2019-10-29 14:33:53,250 INFO  [io.quarkus] (main) knative-hello stopped in 0.010s
```

Push it `docker push maeddes/quarkus-knative`

```
The push refers to repository [docker.io/maeddes/quarkus-hello]
3c6b2440eea8: Pushed
19172b632b9c: Pushing   17.2MB/22.78MB
534b795a7ddf: Mounted from maeddes/quarkus-knative
b6f081e4b2b6: Mounted from maeddes/quarkus-knative
d8e1f35641ac: Mounted from maeddes/quarkus-knative
```
