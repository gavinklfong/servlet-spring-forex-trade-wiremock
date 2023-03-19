package space.gavinklfong.forex.bdd.setup;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LocalEnvSetup implements EnvSetup {

    private static final String HOST = "localhost";

    private static final int APP_PORT = 9090;
    private static final int WIREMOCK_PORT = 8080;
    private static final int MYSQL_PORT = 3306;
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "passme";
    private static final String MYSQL_DATABASE = "forex";

    @Override
    public String getWireMockUrl() {
        return String.format("http://%s:%d", HOST, WIREMOCK_PORT);
    }

    @Override
    public String getMySQLJdbcUrl() {
        return String.format("jdbc:mysql://%s:%d/%s", HOST, MYSQL_PORT, MYSQL_DATABASE);
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
        return String.format("http://%s:%d", HOST, APP_PORT);
    }

}
