package net.gladkowski.chaosmonkeylite;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
public class ChaosController {
    final DockerClient docker;
    final List<String> performedOperations = new ArrayList<>();

    public ChaosController() throws DockerCertificateException {
        this.docker =  DefaultDockerClient.fromEnv().build();
    }

    @GetMapping("/")
    public ModelAndView index() throws DockerException, InterruptedException {
        try {
            List<Container> containers = docker.listContainers(DockerClient.ListContainersParam.allContainers());
            //performedOperations.add(Instant.now().toString() + " # Listing all the containers\ndocker ps\n");
            ModelAndView mv = new ModelAndView("index");
            mv.addObject("containers", containers);
            mv.addObject("operations", performedOperations);
            return mv;
        } catch (Exception e) {
            log.error("Error getting docker containers", e);
            return new ModelAndView("index", "containers", Collections.emptyList());
        }
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
        StringBuilder operation = new StringBuilder()
                .append(Instant.now().toString())
                .append(" # Stopping container ")
                .append("\n").append("docker stop ")
                .append(" ")
                .append(id)
                .append("\n");
        try {
            docker.stopContainer(id, 2);
        } catch (Exception e) {
            log.error("Error stopping container " + id, e);
            operation.append("\n").append(e.getMessage());
        }
        performedOperations.add(operation.toString());
        return new ModelAndView("redirect:/");
    }

    // ====================================

    @FunctionalInterface
    interface ThrowableConsumer<T> {
        void accept(T argument) throws Exception;
    }

    private ModelAndView execute(ThrowableConsumer<String> consumer, String containerId, String dockerCommand, String description) {
        StringBuilder operation = new StringBuilder()
                .append(Instant.now().toString())
                .append(" ")
                .append(description)
                .append("\n")
                .append(dockerCommand)
                .append(" ")
                .append(containerId)
                .append("\n");
        try {
            consumer.accept(containerId);
        } catch (Exception e) {
            log.error("Error pausing container " + containerId, e);
            operation.append("\n").append(e.getMessage());
        }
        performedOperations.add(operation.toString());
        return new ModelAndView("redirect:/");
    }
}
