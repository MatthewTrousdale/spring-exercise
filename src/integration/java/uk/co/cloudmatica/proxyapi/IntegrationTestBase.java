package uk.co.cloudmatica.proxyapi;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import uk.co.cloudmatica.proxyapi.config.AppConfig;
import uk.co.cloudmatica.proxyapi.config.WebConfig;

import java.io.File;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@EnableAutoConfiguration
@AutoConfigureWebTestClient
@ContextConfiguration(classes = {WebConfig.class, AppConfig.class})
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    @Container
    public static DockerComposeContainer dockerComposeContainer =
        new DockerComposeContainer(new File("src/integration/mock-server/docker-compose.yml"))
            .withExposedService("mockServer", 1080);

    @ServiceConnection
    final static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.0.10");

    public IntegrationTestBase() {
        dockerComposeContainer.start();
        mongoDBContainer.start();
    }
}
