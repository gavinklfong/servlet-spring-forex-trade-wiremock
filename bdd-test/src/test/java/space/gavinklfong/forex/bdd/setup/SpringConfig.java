package space.gavinklfong.forex.bdd.setup;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@ComponentScan("space.gavinklfong.forex.bdd")
@Configuration
@PropertySource("classpath:application.properties")
public class SpringConfig {

    @Bean
    public DataSource datasource(EnvSetup envSetup) {
        DataSourceBuilder builder = DataSourceBuilder.create();
        builder.driverClassName("com.mysql.cj.jdbc.Driver");
        builder.url(envSetup.getMySQLJdbcUrl());
        builder.username(envSetup.getMySQLUser());
        builder.password(envSetup.getMySQLPassword());
        return builder.build();
    }

    @Bean
    @ConditionalOnProperty(name = "env.local", havingValue = "false")
    public EnvSetup testContainersEnvSetup() {
        TestContainersEnvSetup testContainersEnvSetup = new TestContainersEnvSetup();
        testContainersEnvSetup.initialize();
        return testContainersEnvSetup;
    }

    @Bean
    @ConditionalOnProperty(name = "env.local", havingValue = "true")
    public EnvSetup localEnvSetup() {
        return new LocalEnvSetup();
    }
}
