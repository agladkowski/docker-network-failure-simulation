![image](https://user-images.githubusercontent.com/1870012/114316276-c0147600-9afa-11eb-8624-53d52b45fad6.png)

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