import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;

import java.util.List;

/**
 * Created by andrzej on 2017-05-13.
 */
public class NetworkDelayTestingWithDocker {

    public static final String CONTAINER_ID = "test-server";

    public static void main(String[] args) throws DockerCertificateException, DockerException, InterruptedException {
        final DockerClient docker = DefaultDockerClient.fromEnv().build();

        // Run docker-compose up from the root of the project
        // this should start the test-server with the ping command
        List<Container> containers = docker.listContainers(DockerClient.ListContainersParam.filter("name", "test-server"));
        containers.forEach(c -> System.out.println(c.names()));
        if (containers.size() == 0) {
            System.err.println("test-server not running! Please run 'docker-compose up' in the root of the project.");
            docker.close();
            System.exit(0);
            return;
        }

        try {
            simulateNetworkDelay(docker, CONTAINER_ID);
            //pauseContainerExample(docker, CONTAINER_ID);
            //deleteContainerExample(docker, CONTAINER_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        docker.close();
    }

    private static void simulateNetworkDelay(DockerClient docker, String containerId) throws DockerException, InterruptedException {
        // Delay network
        // https://en.wikipedia.org/wiki/Example.com
        int exitCode = execute(docker, containerId, "tc qdisc add dev eth0 root netem delay 100ms");
        if (exitCode == 2) {
            execute(docker, containerId, "tc qdisc change dev eth0 root netem delay 100ms");
        }

        // wait 5 seconds
        Thread.sleep(5000);

        // Un-do network delay
        execute(docker, containerId, "tc qdisc change dev eth0 root netem delay 0ms");
    }

    private static int execute(DockerClient docker, String containerId, String command) throws DockerException, InterruptedException {
        ExecCreation exec = docker.execCreate(containerId, new String[]{"sh", "-c", command}, DockerClient.ExecCreateParam.attachStdout());
        try (final LogStream stream = docker.execStart(exec.id())) {
            System.out.println(stream.readFully());
        }

        int exitCode = docker.execInspect(exec.id()).exitCode();
        System.out.println("exitCode " + exitCode);

        return exitCode;
    }

    private static void pauseContainerExample(DockerClient docker, String containerId) throws DockerException, InterruptedException {
        docker.pauseContainer(containerId);
        Thread.sleep(3000);
        docker.unpauseContainer(containerId);
    }

    private static void deleteContainerExample(DockerClient docker, String containerId) {
        try {
            docker.removeContainer(containerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
