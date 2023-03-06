package vu.bakalauras.simulation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication // scan for components in current package and below
@ImportResource(locations = {"classpath:META-INF/beans.xml"})
@EnableAsync
public class SimulationApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimulationApplication.class, args);
    }
}
