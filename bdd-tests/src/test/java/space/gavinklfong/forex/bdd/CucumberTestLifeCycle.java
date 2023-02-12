package space.gavinklfong.forex.bdd;


import io.cucumber.java.BeforeAll;

public class CucumberTestLifeCycle {

    @BeforeAll
    void setup() {
        MockApiSetup.start();
    }
}
