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

import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
public class ChaosController {
    final DockerClient docker;

    public ChaosController() throws DockerCertificateException {
        this.docker =  DefaultDockerClient.fromEnv().build();
    }

    @GetMapping("/")
    public ModelAndView index() throws DockerException, InterruptedException {
        try {
            List<Container> containers = docker.listContainers(DockerClient.ListContainersParam.allContainers());
            return new ModelAndView("index", "containers", containers);
        } catch (Exception e) {
            log.error("Error getting docker containers", e);
            return new ModelAndView("index", "containers", Collections.emptyList());
        }
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.GET)
    public ModelAndView deleteContainer(@PathVariable("id") String id) throws DockerException, InterruptedException {
        try {
            docker.removeContainer(id);
        } catch (Exception e) {
            log.error("Error deleting container " + id, e);
        }
        return new ModelAndView("redirect:/");
    }
}
