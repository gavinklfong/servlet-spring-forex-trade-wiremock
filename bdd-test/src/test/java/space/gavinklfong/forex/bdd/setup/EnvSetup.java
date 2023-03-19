package space.gavinklfong.forex.bdd.setup;

public interface EnvSetup {
    String getWireMockUrl();

    String getMySQLJdbcUrl();

    String getMySQLUser();

    String getMySQLPassword();

    String getAppUrl();
}
