![image](https://user-images.githubusercontent.com/1870012/114316276-c0147600-9afa-11eb-8624-53d52b45fad6.png)

# Docker Network Failure Simulator

The main purpose of this project is to ease the process of network failure simulation, failure being:
- Delayed packets,
- Dropped packets,
- Corrupted packets,
- Re-ordered packets,
- Bandwidth limits.

# Prerequisites
- Java 11
- Maven 3.x
- Docker

# Updating pom before release
```
mvn versions:set -DnewVersion=0.1
```

# Building docker image
```
cd docker-network-failure-simulator
mvn clean compile jib:dockerBuild
```

# Publishing to docker hub (before push configure repo in docker-hub)
```
docker push agladkowski/docker-network-failure-simulator:<tag>
e.g
docker push agladkowski/docker-network-failure-simulator:0.1
```

# Running sample locally
Run:
```
cd docker-network-failure-simulator
to start: docker-compose up
to stop: docker-compose down or ctrl+c
```
Navigate to http://localhost:8080

# References
- https://netbeez.net/blog/how-to-use-the-linux-traffic-control/