<pre>
                    ##        .            
              ## ## ##       ==            
           ## ## ## ##      ===            
       /""""""""""""""""\___/ ===        
  ~~~ {~~ ~~~~ ~~~ ~~~~ ~~ ~ /  ===- ~~~   
       \______ o          __/            
         \    \        __/             
          \____\______/                
 
          |          |
       __ |  __   __ | _  __   _
      /  \| /  \ /   |/  / _\ | 
      \__/| \__/ \__ |\_ \__  |
</pre>

# Docker in Java development

## Krystian Nowak


###### Ascii art by https://github.com/dhrp

---

# Agenda:

* Why Docker?

* Docker setup

* Hello Guinea Pig!

* Docker Maven plugin

* Marathon Maven plugin

* TestContainers

---

# Why Docker
## in Java development

* should we really care?

 * c'mon! (I am) dev != DevOps ?

* everyone (rly?) uses it nowadays

 * what do I care? (buzzwords come and go...)

* but it needs to be secured properly (we have already learned it the hard way)

 * but maybe not in development yet?

  * we do Java apps, not Docker apps, that's the contract


---

# Why Docker
## in Java development
### no buzzwords - in reality

* separation/sandboxing for possibly unwanted or temporary tools

 * I just need to try this thing - not to bloat my machine

* getting up to speed with Docker technology

 * maybe I just want to evaluate if my app should be _dockerized_

* reproducing issues in an environment as close to production setup as possible

 * in case my production already is _dockerized_

 * write once, debug everywhere

---

# Docker setup

* runs as a daemon

 * _native_ on Linux platform

 * on Windows and Mac uses various forms of virtualization to _proxy_ the commands

  * Boot2Docker, Docker Machine, Docker Toolbox, Docker for Windows/Mac

 * more advanced setup - out of scope

* exposes API via HTTP(S) or UNIX socket

 * mostly REST - https://docs.docker.com/engine/reference/api/docker_remote_api/

---

# Docker setup

* direct access from Java
 * multiple libraries exist e.g.
```xml
<dependency>
 <groupId>com.spotify</groupId>
 <artifactId>docker-client</artifactId>
 <version>6.0.0</version>
</dependency>
``` 
 
* most of them properly detect Docker daemon

 * on Linux _unix:///var/run/docker.sock_ existence check is obvious

 * on Windows/Mac `DOCKER_HOST` env variable setting might be handy

 * recently on Mac also Unix socket is used (detected properly)

---

# Hello Guinea Pig
## Let's meet our simple application

* simple behaviour

```shell
curl -i http://localhost:8080/application/hello-world
HTTP/1.1 200 OK
Date: Wed, 12 Oct 2016 17:51:42 GMT
Content-Type: text/plain
Vary: Accept-Encoding
Content-Length: 30

Hello World! Counter value = 1
```

* simple deployment

```shell
java -jar myapp.jar
```

---

# Inside our Guinea Pig

* Using http://dropwizard.io/ to make it simple.

 * one [JAX-RS GET resource](https://github.com/krystiannowak/Docker-in-Java-development/blob/master/src/main/java/krystiannowak/helloworld/HelloWorldResource.java)
 
 * one `java.util.concurrent.atomic.AtomicLong` counter
 
 * one [simple config file](https://github.com/krystiannowak/Docker-in-Java-development/blob/master/helloworld.yml)

* Run by a similar command:
```shell
java -jar target/helloworld-1.0-SNAPSHOT.jar server helloworld.yml
```

---

# Docker Maven plugin
## Maven project + Docker Maven plugin -> _dockerized_

* https://github.com/spotify/docker-maven-plugin

* the plugin in use - [project's POM](https://github.com/krystiannowak/Docker-in-Java-development/blob/master/pom.xml)

* let's see what it does:
```shell
mvn clean package docker:build -DskipTests
```

 * and what we have just created:
```shell
docker images | grep helloworld
helloworld     latest         sha256:5b86d   2 minutes ago  124.8 MB
```

---

# Docker Maven plugin
## Let's run it!

* using command line
```shell
docker run -p 8080:8080 helloworld server helloworld.yml
```

* does Docker confirm it is running?
```shell
docker ps | grep helloworld
238252aee8c1   helloworld   "java -jar /helloworl"   2 minutes ago 
Up 51 seconds   0.0.0.0:8080->8080/tcp   condescending_easley
```

---

# Docker Maven plugin
## Let's run it!

* is it accessible again?

 * with a small help of `hosts` file in some cases:

```shell
curl -i http://docker:8080/application/hello-world
HTTP/1.1 200 OK
Date: Wed, 12 Oct 2016 18:57:30 GMT
Content-Type: text/plain
Vary: Accept-Encoding
Content-Length: 30

Hello World! Counter value = 1
```

---

# Marathon Maven plugin

* https://github.com/holidaycheck/marathon-maven-plugin

* why to use?

 * orchestration, [blue-green deployments](http://martinfowler.com/bliki/BlueGreenDeployment.html) and other buzzwords...

* but why in development already?

 * prepare your application for being used from within Marathon and Mesos ecosystem

 * reproduce issues hard to investigate without distributed environment

* why Maven plugin?

 * integrates well with Docker Maven plugin e.g. tags for versioning 

---

# Marathon Maven plugin
## How does it work?

* uses [Marathon REST API](http://mesosphere.github.io/marathon/docs/rest-api.html)

* processes a template of Marathon config file before using it in Deployment
 
 * possibility to change Docker image tag used by previously run Docker Maven plugin

* see http://techblog.holidaycheck.com/2015/04/ for more examples

---

# Marathon Maven plugin
## Healthcheck

* remember about healthcheck URL for Marathon in your app e.g.

```shell
curl -i http://docker:8080/admin/healthcheck
HTTP/1.1 200 OK
Date: Wed, 12 Oct 2016 21:43:48 GMT
Content-Type: application/json
Cache-Control: must-revalidate,no-cache,no-store
Vary: Accept-Encoding
Content-Length: 61

{"Hello World":{"healthy":true},"deadlocks":{"healthy":true}}
```

* example naive implementation in [HelloWorldHealthCheck](https://github.com/krystiannowak/Docker-in-Java-development/blob/master/src/main/java/krystiannowak/helloworld/HelloWorldHealthCheck.java)
 
 * real check should probe something meaningful for availability of the application e.g. accessibility of external resources
 
---

# TestContainers
## JUnit + Docker -> throwaway instances of dependencies

* https://github.com/testcontainers/testcontainers-java

* when is it needed?

 * real instances e.g. of DBs, servers (when Java in-memory implementations or similar API lightweight implementations are not enough)
 
 * isolated testing - available only for given test, no unwanted concurrent modifications (no external instance, tests should be self-contained) 
 
 * throwaway instances to be recreated from scratch by other tests (so not provided e.g. by Maven plugins pre and post test phases hooks)

---

# TestContainers
## How does it work?

* JUnit's `org.junit.Rule` (and the family)

* comes with already predefined test containers e.g. PostgreSQL, MySQL, Nginx or Selenium
exposing specific properties to be used in tests e.g. `java.sql.Connection`
 * an example of testing PostgreSQL built-in procedures - [PostgresqlDBTest](https://github.com/krystiannowak/Docker-in-Java-development/blob/master/src/test/java/krystiannowak/helloworld/db/PostgresqlDBTest.java)

* a generic test container can be used (or extended) to support e.g. MongoDB
 * an example of testing MongoDB server metadata - [MongoDBTest](https://github.com/krystiannowak/Docker-in-Java-development/blob/master/src/test/java/krystiannowak/helloworld/db/MongoDBTest.java)

* multiple test containers depending on each other -> use `org.junit.rules.RuleChain` to chain them together

---

# Q&A

---

# Thank you!
## Krystian Nowak
### Krystian.Nowak@gmail.com
