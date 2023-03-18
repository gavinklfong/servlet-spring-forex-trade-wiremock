package space.gavinklfong.forex.bdd.setup;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan("space.gavinklfong.forex.bdd")
@Configuration
@EnableJpaRepositories
@PropertySource("classpath:application.properties")
public class SpringConfig {

//    @Bean
//    public Datasource datasource() {
//
//    }

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
