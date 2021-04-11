# Network chaos monkey

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

# Building docker image
```
mvn clean compile jib:dockerBuild
```

# Running sample locally
Run:
```
docker-compose up
```
Navigate to http://localhost:8080

# References
- https://netbeez.net/blog/how-to-use-the-linux-traffic-control/