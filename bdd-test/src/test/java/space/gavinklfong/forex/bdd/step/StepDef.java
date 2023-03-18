package space.gavinklfong.forex.bdd.step;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import space.gavinklfong.forex.bdd.dto.Customer;
import space.gavinklfong.forex.bdd.service.AppClient;
import space.gavinklfong.forex.bdd.service.CustomerRepo;
import space.gavinklfong.forex.bdd.setup.EnvSetup;
import space.gavinklfong.forex.bdd.setup.TestContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//@Slf4j
//@RequiredArgsConstructor
public class StepDef {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StepDef.class);

	private final TestContext testContext;

	private final CustomerRepo customerRepo;

	private final AppClient appClient;

	private final String apiServiceUrl;
	
	private HttpResponse<String> response;
	private String baseCurrency;
	private String counterCurrency;
	private JSONObject rateBooking;

	public StepDef(EnvSetup envSetup,
				   TestContext testContext,
				   AppClient appClient,
				   CustomerRepo customerRepo) {
		this.apiServiceUrl = envSetup.getAppUrl();
		this.testContext = testContext;
		this.appClient = appClient;
		this.customerRepo = customerRepo;
	}

	@Given("forex rate is available for base currency {string}")
	public void forex_rate_is_available_for_base_currency(String base) {
		stubFor(get(urlPathEqualTo(String.format("%s/%s", "/rates", base)))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.withBody(readContent(String.format("/wiremock-stub/%s-rates.json", base)))
				)
		);
	}

	@Given("Customer saved in database:")
	public void customerInDatabase(DataTable dataTable) {
		List<Map<String, String>> records = dataTable.asMaps(String.class, String.class);
		records.stream()
				.map(record -> Customer.builder()
						.id(Long.parseLong(record.get("id")))
						.name(record.get("name"))
						.tier(Integer.parseInt(record.get("tier")))
						.build())
				.forEach(customerRepo::save);
	}

	@Given("forex rate is available for base currency {string} and counter currency {string}")
	public void forex_rate_is_available_for_base_currency(String base, String counter) {
		stubFor(get(urlPathEqualTo(String.format("%s/%s-%s", "/rates", base, counter)))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.withBody(readContent("/wiremock-stub/GBP-rates.json"))));
	}

	private String readContent(String file) {
		try (InputStream in = StepDef.class.getResourceAsStream(file)) {
			return IOUtils.toString(in, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@When("I request for the latest rate with base currency {string}")
	public void i_request_for_the_latest_rate_with_base_currency(String base) throws URISyntaxException, IOException, InterruptedException {
		testContext.setBaseCurrency(base);
		testContext.setResponse(appClient.getForexRates(base));
	}
	
	@Then("I should receive list of currency rate")
	public void i_should_receive_list_of_currency_rate() {

		assertThat(testContext.getResponse()).extracting(HttpResponse::statusCode).isEqualTo(200);

		JSONArray jsonArray = new JSONArray(testContext.getResponse().body());
		assertThat(jsonArray).hasSizeGreaterThan(0);
		
		jsonArray.forEach(item -> {
			JSONObject json = (JSONObject) item;
			assertThat(json.getDouble("buyRate")).isPositive();
			assertThat(json.getDouble("sellRate")).isPositive();
			assertThat(json.getString("baseCurrency")).isEqualTo(testContext.getBaseCurrency());
			assertThat(json.getString("counterCurrency")).isNotEqualTo(testContext.getBaseCurrency());
		});
	}
	
	@When("I request for a rate booking with parameters: {string}, {string}, {string}, {long}, {long}")
	public void i_request_for_a_rate_booking_with_parameters(String base, String counter, String tradeAction, Long amount, Long customerId) throws URISyntaxException, IOException, InterruptedException {

		JSONObject rateBookingReq = new JSONObject();
		rateBookingReq.put("baseCurrency", base);
		rateBookingReq.put("counterCurrency", counter);
		rateBookingReq.put("tradeAction", tradeAction);
		rateBookingReq.put("baseCurrencyAmount", amount);
		rateBookingReq.put("customerId", customerId);

		testContext.setBaseCurrency(base);
		testContext.setCounterCurrency(counter);
		testContext.setResponse(appClient.bookForexRate(rateBookingReq));
	}
	
	@Then("I should receive a valid rate booking")
	public void i_should_receive_a_valid_rate_booking() {

		assertThat(testContext.getResponse()).extracting(HttpResponse::statusCode).isEqualTo(200);

		JSONObject json = new JSONObject(testContext.getResponse());

		assertThat(json.getDouble("rate")).isPositive();
		assertThat(json.getString("baseCurrency")).isEqualTo(testContext.getBaseCurrency());
		assertThat(json.getString("counterCurrency")).isEqualTo(testContext.getCounterCurrency());
		assertThat(json.getString("bookingRef")).isNotEmpty();

		Instant expiryTime = Instant.parse(json.getString("expiryTime"));
		assertThat(expiryTime).isAfter(Instant.now());

		testContext.setRateBooking(json);
	}	
	
	@When("I submit a forex trade deal with rate booking and parameters: {string}, {string}, {string}, {long}, {long}")
	public void i_submit_a_forex_trade_deal_with_rate_booking_and_parameters(String base, String counter, String tradeAction, Long amount, Long customerId) throws URISyntaxException, IOException, InterruptedException {
		
		JSONObject tradeDeal = new JSONObject();
		tradeDeal.put("baseCurrency", base);
		tradeDeal.put("counterCurrency", counter);
		tradeDeal.put("tradeAction", tradeAction);
		tradeDeal.put("baseCurrencyAmount", amount);
		tradeDeal.put("customerId", customerId);
		tradeDeal.put("rateBookingRef", rateBooking.getString("bookingRef"));
		tradeDeal.put("rate", rateBooking.getDouble("rate"));
		
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest
				.newBuilder(new URI(apiServiceUrl + "/deals"))
				.POST(HttpRequest.BodyPublishers.ofString(tradeDeal.toString()))
				.header("accept", "application/json")
				.header("Content-Type", "application/json")
				.build();	
		this.response = client.send(request, HttpResponse.BodyHandlers.ofString());
	}
	
	@Then("I should get the forex trade deal successfully posted")
	public void i_should_get_the_forex_trade_deal_successfully_posted() {
		
		assertEquals(200, response.statusCode());
		
		JSONObject json = new JSONObject(response.body());
		
		assertEquals(baseCurrency, json.getString("baseCurrency"));
		assertEquals(counterCurrency, json.getString("counterCurrency"));
		assertEquals(rateBooking.getDouble("rate"), json.getDouble("rate"));
		assertTrue(json.getString("dealRef").trim().length() > 0);
		assertTrue(json.getLong("id") > 0);		
	}
	
	@When("I request for forex trade deal by {long}")
	public void i_request_for_forex_trade_deal_by(Long customerId) throws URISyntaxException, IOException, InterruptedException {
		// Send request to get the latest rates
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest
				.newBuilder(new URI(apiServiceUrl + "/deals?customerId=" + customerId))
				.header("accept", "application/json").build();	
		this.response = client.send(request, HttpResponse.BodyHandlers.ofString());			
	}
	
	@Then("I should get a list of forex trade deal for {long}")
	public void i_should_get_a_list_of_forex_trade_deal_for(Long customer) {
		
		JSONArray jsonArray = new JSONArray(response.body());
		
		assertTrue(jsonArray.length() > 0);
		jsonArray.forEach(item -> {
			JSONObject json = (JSONObject) item;
			assertTrue(json.getString("dealRef").trim().length() > 0);
			assertNotNull(json.getString("baseCurrency"));
			assertNotNull(json.getString("counterCurrency"));
		});
		
	}

	
}
