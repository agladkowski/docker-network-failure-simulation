# Network Failure/Chaos testing with docker

The aim of this project is to demonstrate how to simulate different types of network failure using docker.
There is many tools that provide abstraction over basic linux tools like iptables and tc:
- Pumba (http://blog.terranillius.com/post/pumba_docker_netem/)
- Comcast (https://github.com/tylertreat/Comcast)
- Blockade (https://github.com/worstcase/blockade)
- Gremlins (https://github.com/qualimente/gremlins)

The problem with those tools is that they are an abstraction and you don't really know how things work under the hood.

I believe in the power of simple one liners, demonstrating the concept of  

## Delay Simulation

### Run the target test server in the first terminal. 
In our case it's a simple ping server that will show when network delay is added/removed.
 
```
docker run -it --name test-server --cap-add NET_ADMIN --rm alpine sh -c "apk add --update iproute2 && ping www.wp.pl"
```

Let's decipher some of the options
_docker run -it_        - starts new docker container in interactive mode
_--name test-server_    - defines name for newly started container
_--cap-add NET_ADMIN_   - new container can manage network, important, it's needed by traffic shaping tool
_--rm_                  - delete container on exit
_alpine_                - it's the name of a lightweight linux container
_sh -c "apk add --update iproute2 && ping www.wp.pl"_ - this is the command that will be executed when the container starts up. It installs iproute2 tools and runs ping command. 

Here is expected output:
```
D:\dev\git.repo\docker-network-failure-simulations>docker run -it --name test-server --cap-add NET_ADMIN --rm alpine sh -c "apk add --update iproute2 && ping www.wp.pl"
fetch http://dl-cdn.alpinelinux.org/alpine/v3.5/main/x86_64/APKINDEX.tar.gz
fetch http://dl-cdn.alpinelinux.org/alpine/v3.5/community/x86_64/APKINDEX.tar.gz
(1/5) Installing libelf (0.8.13-r2)
(2/5) Installing libmnl (1.0.4-r0)
(3/5) Installing libnftnl-libs (1.0.7-r0)
(4/5) Installing iptables (1.6.0-r0)
(5/5) Installing iproute2 (4.7.0-r0)
Executing iproute2-4.7.0-r0.post-install
Executing busybox-1.25.1-r0.trigger
OK: 7 MiB in 16 packages
PING www.wp.pl (212.77.98.9): 56 data bytes
64 bytes from 212.77.98.9: seq=0 ttl=37 time=0.762 ms
64 bytes from 212.77.98.9: seq=1 ttl=37 time=0.710 ms
64 bytes from 212.77.98.9: seq=2 ttl=37 time=0.651 ms
...
```

### Add delay of 100ms (use separate terminal for that)
```
docker exec -it test-server sh -c "tc qdisc add dev eth0 root netem delay 100ms"
```

Here is the test-server console output
```
64 bytes from 212.77.98.9: seq=7 ttl=37 time=0.638 ms
64 bytes from 212.77.98.9: seq=8 ttl=37 time=0.683 ms
64 bytes from 212.77.98.9: seq=9 ttl=37 time=0.825 ms
64 bytes from 212.77.98.9: seq=10 ttl=37 time=0.546 ms
64 bytes from 212.77.98.9: seq=11 ttl=37 time=100.503 ms <-- Delay is added
64 bytes from 212.77.98.9: seq=12 ttl=37 time=100.531 ms
64 bytes from 212.77.98.9: seq=13 ttl=37 time=100.666 ms
64 bytes from 212.77.98.9: seq=14 ttl=37 time=100.896 ms
```

### Add delay of 100ms (use separate terminal for that)
```
docker exec -it test-server sh -c "tc qdisc add dev eth0 root netem delay 100ms"
```

Here is the test-server console output after setting delay back to 0ms

```
64 bytes from 212.77.98.9: seq=8 ttl=37 time=0.683 ms
64 bytes from 212.77.98.9: seq=9 ttl=37 time=0.825 ms
64 bytes from 212.77.98.9: seq=10 ttl=37 time=0.546 ms
64 bytes from 212.77.98.9: seq=11 ttl=37 time=100.503 ms
64 bytes from 212.77.98.9: seq=12 ttl=37 time=100.531 ms
64 bytes from 212.77.98.9: seq=13 ttl=37 time=100.666 ms
64 bytes from 212.77.98.9: seq=14 ttl=37 time=100.896 ms
64 bytes from 212.77.98.9: seq=15 ttl=37 time=100.675 ms
64 bytes from 212.77.98.9: seq=19 ttl=37 time=100.699 ms
64 bytes from 212.77.98.9: seq=20 ttl=37 time=100.523 ms
64 bytes from 212.77.98.9: seq=21 ttl=37 time=100.494 ms
64 bytes from 212.77.98.9: seq=22 ttl=37 time=0.661 ms <-- Delay is removed
64 bytes from 212.77.98.9: seq=23 ttl=37 time=0.665 ms
64 bytes from 212.77.98.9: seq=24 ttl=37 time=0.652 ms
```

## Java - Docker Client 

Reference: https://github.com/spotify/docker-client

### Sample java code NetworkDelayTestingWithDocker.java 

The above class demonstrates how the same delay can be applied using java. 
This may be useful if you want to start your environment with docker-compose and then apply 
different types of network failures to test resilience of your microservices.