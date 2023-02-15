package Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

//JSON PARSING// READING URL FROM PROPERTY FILE//
public class ValidationTest {

	private static final Logger LOGGER = Logger.getLogger(ValidationTest.class.getName());

	static String ExpectedPopulation;
	static String userYear = "2019";
	
    @BeforeMethod
	public static String getPopulation()  {
    	try {
    	//READING JSON FROM THE BELOW PATH
		String jsonContent = FileUtils.readFileToString(
				new File(System.getProperty("user.dir") + "//src//test//java//Files//Test.json"),
				StandardCharsets.UTF_8);
		
    	JsonPath js = new JsonPath(jsonContent);
		int count = js.getInt("data.size()");
		System.out.println(count);
		for (int i = 0; i < count; i++) {
			String year = js.get("data[" + i + "].Year").toString();//list of year
			if (userYear.equals(year)) {
				String UserYearPopulation = js.get("data[" + i + "].Population").toString();//corresponding population
				LOGGER.log(Level.INFO,"The population of " + userYear + " is " + UserYearPopulation);
				ExpectedPopulation = UserYearPopulation;
			}
		}
		
		
    	}
    	

    	catch (Exception e) {
			LOGGER.log(Level.SEVERE, "File missing/Cannot read json file");
			// System.out.println(e);

		}
		return ExpectedPopulation;


	}

	
	@org.testng.annotations.Test
	public void Test()  {
		
		try {
		
		Properties prop=new Properties();
		FileInputStream fis=new FileInputStream(System.getProperty("user.dir")+"//src//test//java//Files//url.properties");
		prop.load(fis);
		String urlName=prop.getProperty("url");
		RestAssured.baseURI = urlName;		
		
		RequestSpecification req = RestAssured.given().queryParam("drilldowns", "Nation")
				.queryParam("measures", "Population").log().all();// .queryParam("Year", "2020")
        
		Response response = req.request(Method.GET, "");
		LOGGER.log(Level.INFO,"The response is  "+response.prettyPrint());
		LOGGER.log(Level.INFO,"The response code is  "+ response.statusCode());
		JsonPath js1 = response.jsonPath();

		int count = js1.getInt("data.size()");
		LOGGER.log(Level.INFO,"List of Year  "+count);
		for (int i = 0; i < count; i++) {

			String year = js1.get("data[" + i + "].Year").toString();
			
			if (userYear.equals(year)) {

				String actualPopulation = js1.get("data[" + i + "].Population").toString();
				LOGGER.log(Level.INFO,"The actual population from the API call is  "+actualPopulation);
				Assert.assertEquals(actualPopulation, ExpectedPopulation);
			}

		}

	}
		
		catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Bad request from API");
			// System.out.println(e);

		}



}

}
