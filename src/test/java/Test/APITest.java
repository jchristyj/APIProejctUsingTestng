package Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

//FOR EACH LOOP//READ URL FROM PROPERTY FILE//TRY CATCH BLOCK//USING LOGGER

public class APITest {

	private static final Logger LOGGER = Logger.getLogger(APITest.class.getName());
	static int ExpectedPopulation = 324697795;
	static String ExpectedYear = "2019";

	@Test
	public void apiCall() throws IOException {

		try {

			// READING THE URL FROM FILE

			Properties prop = new Properties();
			FileInputStream fis = new FileInputStream(
					System.getProperty("user.dir") + "//src//test//java//Files//url.properties");
			prop.load(fis);
			String urlName = prop.getProperty("url");
			RestAssured.baseURI = urlName;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Cannot read URL from File/File Missing");
			// System.out.println(e);

		}

		try {

			// API CALL
			RequestSpecification req = RestAssured.given().queryParam("drilldowns", "Nation")
					.queryParam("measures", "Population").log().all();// .queryParam("Year", "2020")
			Response response = req.request(Method.GET, "");

			// System.out.println(response.prettyPrint());
			LOGGER.log(Level.INFO, "Printing the full response and the response is  " + response.prettyPrint());

			// System.out.println(response.statusCode());
			LOGGER.log(Level.INFO, "Printing the response code and the response code is  " + response.statusCode());
			JsonPath js = response.jsonPath();
			ArrayList<String> listOfYear = js.get("data.Year");

			for (String actualYear : listOfYear) {
				if (ExpectedYear.equals(actualYear)) {

					// System.out.println(actualYear + " is present");
					LOGGER.log(Level.INFO, actualYear + "  is present");
				}

			}
			ArrayList<Integer> listOfPopulation = js.get("data.Population");
			for (int actualPopulation : listOfPopulation) {

				if (ExpectedPopulation == actualPopulation) {
					// System.out.println(actualPopulation + " is present");
					LOGGER.log(Level.INFO, actualPopulation + "  is present");
					Assert.assertEquals(actualPopulation, ExpectedPopulation);
				}

			}

		}

		catch (Exception e) {
			LOGGER.log(Level.SEVERE, "bad request from API");
			// System.out.println(e);

		}

	}
}
