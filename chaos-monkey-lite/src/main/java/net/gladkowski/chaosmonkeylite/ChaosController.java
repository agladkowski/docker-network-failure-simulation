package net.gladkowski.chaosmonkeylite;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ExecCreation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.SystemUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.time.Instant;
import java.util.*;

@Slf4j
@Controller
public class ChaosController {
    private final DockerClient docker;
    private final List<String> performedOperations = new ArrayList<>();
    private final Map<String, String> installedTooling = new HashMap<>();
    private final Map<String, Integer> delays = new HashMap<>();

    public ChaosController() throws DockerCertificateException {
        if (SystemUtils.IS_OS_WINDOWS) {
            log.info("Running on Windows OS");
            this.docker =  DefaultDockerClient.fromEnv().build();
        } else {
            log.info("Running on Linux/Unix OS");
            this.docker =  new DefaultDockerClient("unix:///var/run/docker.sock");
        }
    }

    @GetMapping("/")
    public ModelAndView index() {
        final ModelAndView mv = new ModelAndView("index");
        mv.addObject("delays", delays);
        try {
            final List<Container> containers = docker.listContainers(DockerClient.ListContainersParam.allContainers());
            //performedOperations.add(Instant.now().toString() + " # Listing all the containers\ndocker ps\n");
            mv.addObject("containers", containers);
            mv.addObject("operations", performedOperations);
            return mv;
        } catch (Exception e) {
            log.error("Error getting docker containers. " + e.getMessage());
            mv.addObject("containers", Collections.emptyList());
            mv.addObject("operations", Collections.emptyList());
        }
        return mv;
    }

    // ==================================

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.GET)
    public ModelAndView deleteContainer(@PathVariable("id") String id) {
        return execute(docker::removeContainer, id, "docker rm ", "# Deleting container");
    }

    @RequestMapping(value = "/pause/{id}", method = RequestMethod.GET)
    public ModelAndView pauseContainer(@PathVariable("id") String id) {
        return execute(docker::pauseContainer, id, "docker pause", "# Pausing container");
    }

    @RequestMapping(value = "/unpause/{id}", method = RequestMethod.GET)
    public ModelAndView unpauseContainer(@PathVariable("id") String id) {
        return execute(docker::unpauseContainer, id, "docker unpause", "# Resuming paused container");
    }

    @RequestMapping(value = "/start/{id}", method = RequestMethod.GET)
    public ModelAndView startContainer(@PathVariable("id") String id) {
        return execute(docker::startContainer, id, "docker start", "# Starting container");
    }


    @RequestMapping(value = "/stop/{id}", method = RequestMethod.GET)
    public ModelAndView stopContainer(@PathVariable("id") String id) {
        final StringBuilder operation = new StringBuilder();
        try {
            operation.append(Instant.now().toString())
                    .append(" # Stopping container ")
                    .append(docker.inspectContainer(id).name())
                    .append("\n")
                    .append("docker stop ")
                    .append(" ")
                    .append(id)
                    .append("\n");

            docker.stopContainer(id, 2);
        } catch (Exception e) {
            log.error("Error stopping container " + id, e);
            operation.append("\n").append(e.getMessage());
        }
        performedOperations.add(operation.toString());
        return new ModelAndView("redirect:/");
    }


    @RequestMapping(value = "/delay/{id}/{delayMs}", method = RequestMethod.GET)
    public ModelAndView delayContainer(@PathVariable("id") String id, @PathVariable("delayMs") String delayMs) {

        if (!installedTooling.containsKey(id)) {
            executeCmd(id, "apk add --update iproute2", "# Installing tc tool");
            installedTooling.put(id, "iproute2");
        }

        if (!delays.containsKey(id)) {
            delays.put(id, Integer.parseInt(delayMs));
            return executeCmd(id, "tc qdisc add dev eth0 root netem delay " + delayMs + "ms", "# Setting network delay to " + delayMs + "ms");
        }

        delays.put(id, Integer.parseInt(delayMs));
        return executeCmd(id, "tc qdisc change dev eth0 root netem delay " + delayMs + "ms", "# Updating network delay to " + delayMs + "ms");
    }

    // ====================================

    @FunctionalInterface
    interface ThrowableConsumer<T> {
        void accept(T argument) throws Exception;
    }


    private ModelAndView executeCmd(final String containerId, String cmd, final String description) {
        final StringBuilder operation = new StringBuilder();
        try {
            final String[] command = {"sh", "-c", cmd};
            final ExecCreation execCreation = docker.execCreate(
                    containerId, command, DockerClient.ExecCreateParam.attachStdout(),
                    DockerClient.ExecCreateParam.attachStderr());
            final LogStream output = docker.execStart(execCreation.id());
            final String execOutput = output.readFully();

            operation.append(Instant.now().toString())
                    .append(" ").append(description)
                    .append(" Container: ").append(docker.inspectContainer(containerId).name())
                    .append("\nCMD: ").append(cmd)
                    .append("\n").append(execOutput);
        } catch (Exception e) {
            log.error("Error executing cmd=" + cmd + " on containerId=" + containerId, e);
        }

        performedOperations.add(operation.toString());
        return new ModelAndView("redirect:/");
    }

    private ModelAndView execute(ThrowableConsumer<String> consumer, String containerId, String dockerCommand, String description) {
        final StringBuilder operation = new StringBuilder();
        try {
            operation.append(Instant.now().toString())
                    .append(" ")
                    .append(description).append(" ").append(docker.inspectContainer(containerId).name())
                    .append("\nCMD: ").append(dockerCommand)
                    .append(" ")
                    .append(containerId)
                    .append("\n");
            consumer.accept(containerId);
        } catch (Exception e) {
            log.error("Error executing " + dockerCommand + " on containerId=" + containerId, e);
            operation.append("\n").append(e.getMessage());
        }
        performedOperations.add(operation.toString());
        return new ModelAndView("redirect:/");
    }
}
