package Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ValidationTest {

	static String ExpectedPopulation;
	static String userYear = "2019";
	
    @BeforeMethod
	public static String getPopulation() throws IOException {
		String jsonContent = FileUtils.readFileToString(
				new File(System.getProperty("user.dir") + "//src//test//java//Files//Test.json"),
				StandardCharsets.UTF_8);

		JsonPath js = new JsonPath(jsonContent);
		int count = js.getInt("data.size()");
		System.out.println(count);
		for (int i = 0; i < count; i++) {
			String year = js.get("data[" + i + "].Year").toString();
			if (userYear.equals(year)) {
				String UserYearPopulation = js.get("data[" + i + "].Population").toString();
				System.out.println("The population of " + userYear + " is " + UserYearPopulation);
				ExpectedPopulation = UserYearPopulation;
			}
		}
		return ExpectedPopulation;

	}

	
	@org.testng.annotations.Test
	public void Test() throws IOException {

		RestAssured.baseURI = "https://datausa.io/api/data";
		RequestSpecification req = RestAssured.given().queryParam("drilldowns", "Nation")
				.queryParam("measures", "Population").log().all();// .queryParam("Year", "2020")
		Response response = req.request(Method.GET, "");

		System.out.println(response.prettyPrint());
		System.out.println(response.statusCode());
		JsonPath js = response.jsonPath();
		int count = js.getInt("data.size()");
		System.out.println(count);
		for (int i = 0; i < count; i++) {

			String year = js.get("data[" + i + "].Year").toString();
			
			if (userYear.equals(year)) {

				String actualPopulation = js.get("data[" + i + "].Population").toString();
				System.out.println(actualPopulation);
				Assert.assertEquals(actualPopulation, ExpectedPopulation);
			}

		}

	}

}
