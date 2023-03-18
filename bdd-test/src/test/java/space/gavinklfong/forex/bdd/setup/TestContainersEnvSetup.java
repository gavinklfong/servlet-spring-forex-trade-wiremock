package space.gavinklfong.forex.bdd.setup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@RequiredArgsConstructor
public class TestContainersEnvSetup implements EnvSetup {

    private static final String APP_IMAGE_NAME = "whalebig27/servlet-spring-forex-trade";

    private static final int APP_PORT = 9090;
    private static final int WIREMOCK_PORT = 8080;
    private static final int MYSQL_PORT = 3306;
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "passme";
    private static final String MYSQL_DATABASE = "forex";
    private static final String MYSQL_INIT_SCRIPT = "resources/mysql_init.sql";
    private static final String MYSQL_ALIAS = "mysql";
    private static final String WIREMOCK_ALIAS = "wiremock";

    private static final GenericContainer<?> WIREMOCK_CONTAINER = new GenericContainer<>(
            DockerImageName.parse("wiremock/wiremock").withTag("latest"))
            .withCommand("/docker-entry.sh", "--verbose");
    private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>(
            DockerImageName.parse("mysql").withTag("5.7"));
    private static final GenericContainer<?> APP_CONTAINER = new GenericContainer<>(
            DockerImageName.parse(APP_IMAGE_NAME).withTag("latest"));
    private static final Network NETWORK = Network.newNetwork();

    public void initialize() {
        initializeWireMockContainer();
        initializeMySQLContainer();
        initializeAppContainer();
    }

    private void initializeWireMockContainer() {
        WIREMOCK_CONTAINER
                .withNetwork(NETWORK)
                .withNetworkAliases(WIREMOCK_ALIAS)
                .withExposedPorts(WIREMOCK_PORT)
                .start();
        WIREMOCK_CONTAINER.followOutput(new Slf4jLogConsumer(LoggerFactory.getLogger(TestContainersEnvSetup.class)));
    }

    private void initializeMySQLContainer() {
        MYSQL_CONTAINER
                .withNetwork(NETWORK)
                .withNetworkAliases(MYSQL_ALIAS)
                .withExposedPorts(MYSQL_PORT)
                .withDatabaseName(MYSQL_DATABASE)
                .withUsername(MYSQL_USER)
                .withPassword(MYSQL_PASSWORD)
                .withInitScript(MYSQL_INIT_SCRIPT)
                .start();
        MYSQL_CONTAINER.followOutput(new Slf4jLogConsumer(LoggerFactory.getLogger(TestContainersEnvSetup.class)));
    }

    private void initializeAppContainer() {
        APP_CONTAINER
                .withNetwork(NETWORK)
                .withEnv("SPRING_DATASOURCE_URL", String.format("jdbc:mysql://%s:%d/%s", MYSQL_ALIAS, MYSQL_PORT, MYSQL_DATABASE))
                .withEnv("APP_FOREX_RATE_API_URL",
                        String.format("http://%s:%d", WIREMOCK_ALIAS, WIREMOCK_PORT))
                .withEnv("SERVER_PORT", String.valueOf(APP_PORT))
                .withExposedPorts(APP_PORT)
                .start();

        APP_CONTAINER.followOutput(new Slf4jLogConsumer(LoggerFactory.getLogger(TestContainersEnvSetup.class)));
    }


    @Override
    public String getWireMockUrl() {
        return String.format("http://%s:%d", WIREMOCK_CONTAINER.getHost(),
                WIREMOCK_CONTAINER.getMappedPort(WIREMOCK_PORT));
    }

    @Override
    public String getMySQLJdbcUrl() {
        return String.format("jdbc:mysql://%s:%d/%s", MYSQL_CONTAINER.getHost(),
                MYSQL_CONTAINER.getMappedPort(MYSQL_PORT), MYSQL_DATABASE);
    }

    @Override
    public String getMySQLUser() {
        return MYSQL_USER;
    }

    @Override
    public String getMySQLPassword() {
        return MYSQL_PASSWORD;
    }

    @Override
    public String getAppUrl() {
        return String.format("http://%s:%d", APP_CONTAINER.getHost(),
                APP_CONTAINER.getMappedPort(APP_PORT));
    }

}
