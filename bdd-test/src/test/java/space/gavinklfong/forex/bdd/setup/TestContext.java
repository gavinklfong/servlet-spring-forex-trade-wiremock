package space.gavinklfong.forex.bdd.setup;

import io.cucumber.spring.ScenarioScope;
import lombok.Data;
import org.json.JSONObject;
import org.springframework.boot.test.context.TestComponent;

import java.net.http.HttpResponse;

@Data
@TestComponent
@ScenarioScope
public class TestContext {
    private HttpResponse<String> response;
    private String baseCurrency;
    private String counterCurrency;
    private JSONObject rateBooking;
}
