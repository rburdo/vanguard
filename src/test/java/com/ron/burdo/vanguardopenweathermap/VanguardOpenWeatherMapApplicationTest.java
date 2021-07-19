package com.ron.burdo.vanguardopenweathermap;

import com.ron.burdo.vanguardopenweathermap.config.OpenWeatherMapProperties;
import com.ron.burdo.vanguardopenweathermap.model.ErrorResponse;
import com.ron.burdo.vanguardopenweathermap.model.WeatherResponse;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class VanguardOpenWeatherMapApplicationTest {

	@Autowired
	private WebTestClient testClient;

	@Autowired
	private OpenWeatherMapProperties properties;

	@Test
	public void testHappyPathDifferentCases_succeeds() throws Exception {
		callWebTestClient("fr", "Paris", properties.getApiKeys().get(0), HttpStatus.OK);
		callWebTestClient("FR", "Paris", properties.getApiKeys().get(0), HttpStatus.OK);
	}

	@Test
	public void testInvalidInput_fails() throws Exception {
		callWebTestClient("FR", "Paris", null, HttpStatus.BAD_REQUEST);
		callWebTestClient("FR", "Paris", " ", HttpStatus.BAD_REQUEST);
		callWebTestClient("XY", "Paris", properties.getApiKeys().get(0), HttpStatus.BAD_REQUEST);
		callWebTestClient("France", "Paris", properties.getApiKeys().get(0), HttpStatus.BAD_REQUEST);
		callWebTestClient("FR", "Paris", "12345", HttpStatus.BAD_REQUEST);
		callWebTestClient(" ", "Paris", properties.getApiKeys().get(1), HttpStatus.BAD_REQUEST);
		callWebTestClient("FR", " ", properties.getApiKeys().get(2), HttpStatus.BAD_REQUEST);
		callWebTestClient("FR", "Paris", " ", HttpStatus.BAD_REQUEST);
		callWebTestClient("FR", "Sydney", properties.getApiKeys().get(3), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Test
	public void testConcurrentCallsForDifferentApiKeys_succeed() throws Exception {
		testMultiThreaded(true,
				() -> callWebTestClient("fr", "Paris", properties.getApiKeys().get(0), HttpStatus.OK),
				() -> callWebTestClient("fr", "Paris", properties.getApiKeys().get(1), HttpStatus.OK),
				() -> callWebTestClient("fr", "Paris", properties.getApiKeys().get(2), HttpStatus.OK),
				() -> callWebTestClient("fr", "Paris", properties.getApiKeys().get(3), HttpStatus.OK),
				() -> callWebTestClient("fr", "Paris", properties.getApiKeys().get(4), HttpStatus.OK)
			);
	}

	@Test
	public void testConcurrentCallsForSameApiKey_fails() throws Exception {
		List<Throwable> errors = testMultiThreaded(false,
				() -> callWebTestClient("fr", "Paris", properties.getApiKeys().get(0), HttpStatus.OK),
				() -> callWebTestClient("fr", "Paris", properties.getApiKeys().get(0), HttpStatus.OK),
				() -> callWebTestClient("fr", "Paris", properties.getApiKeys().get(0), HttpStatus.OK),
				() -> callWebTestClient("fr", "Paris", properties.getApiKeys().get(0), HttpStatus.OK),
				() -> callWebTestClient("fr", "Paris", properties.getApiKeys().get(0), HttpStatus.OK)
		);
		assertTrue(errors.stream().anyMatch(
				th -> th instanceof ResponseStatusException
						&& ((ResponseStatusException) th).getStatus() == HttpStatus.TOO_MANY_REQUESTS));
	}


	private void callWebTestClient(String country, String city, String apiKey, HttpStatus expectedStatus) {
		WebTestClient.ResponseSpec responseSpec = testClient
			.get()
			.uri("/" + country + "/" + city)
			.headers(headers -> {
				if (apiKey != null) {
					headers.add("api-key", apiKey);
				}
			})
			.exchange()
			.expectStatus().value(rawStatus ->  {
				if (expectedStatus.value() != rawStatus) {
					throw new ResponseStatusException(HttpStatus.valueOf(rawStatus));
				}
			});

		if (expectedStatus.is2xxSuccessful()) {
			responseSpec.expectBody(WeatherResponse.class)
				.value(wr -> IsNull.notNullValue().matches(wr.getWeatherDescription()));
		} else if (expectedStatus.isError()) {
			responseSpec.expectBody(ErrorResponse.class)
				.value(er -> Is.is(er.getHttpStatus()).matches(expectedStatus))
				.value(er -> IsNull.notNullValue().matches(er.getMessage()));
		}
	}


	private List<Throwable> testMultiThreaded(boolean successExpected, Runnable... runnables) throws Exception {
		List<Throwable> errors = Collections.synchronizedList(new LinkedList<>());

		List<Thread> threads = new ArrayList<>();
		for (Runnable runnable : runnables) {
			Thread thread = new Thread(runnable);
			thread.setUncaughtExceptionHandler((thrd, throwable) -> {
				errors.add(throwable);
			});
			threads.add(thread);
		}
		for (Thread thread : threads) {
			thread.start();
		}
		for (Thread thread : threads) {
			thread.join();
		}

		assertFalse(successExpected ^ errors.isEmpty());
		return errors;
	}
}
