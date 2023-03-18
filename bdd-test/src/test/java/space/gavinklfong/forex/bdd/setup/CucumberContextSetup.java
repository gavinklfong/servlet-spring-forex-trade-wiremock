package space.gavinklfong.forex.bdd.setup;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@ContextConfiguration(classes = {SpringConfig.class})
public class CucumberContextSetup {
}
