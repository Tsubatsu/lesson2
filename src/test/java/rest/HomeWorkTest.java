package rest;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Json;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.apache.http.HttpStatus;
import org.hamcrest.Matcher;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.CoreMatchers.equalTo;


public class HomeWorkTest {

    /**
     *  {
     *         "id": 1,
     *         "name": "lol",
     *         "email": "helloooo"
     *     }
     *
     *     the same thing
     */
    @BeforeTest
    public static  void start() {
        WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(8082));
        wireMockServer.start();

        wireMockServer.stubFor(post(urlPathEqualTo("/hometask")).
                withCookie("auth-token", matching("SDJDJFFJKasd12312asdasdsa")).
                withHeader("Accept", containing("json")).
                withRequestBody(equalToJson("{\n" +//
                        "        \"id\": 1,\n" +
                        "        \"name\": \"John\",\n" +
                        "        \"email\": \"Smith\"\n" + // look above
                        "    }")).
                willReturn(aResponse().
                        withBody("{\n" +
                                "\t\"result\": true,\n" + //check it
                                "\t\"description\": \"We have it!\"\n" + //check it
                                "}").
                        withStatus(201)));  //don't touch me

        requestSpecification = new RequestSpecBuilder().
                setBaseUri("http://127.0.0.1").setPort(8082).
                log(LogDetail.ALL).
                addFilter(new ResponseLoggingFilter()).
                build();

    }


    @Test
    public void homeWorkTest()
    {
        given().basePath("/hometask")
                .cookie("auth-token", "SDJDJFFJKasd12312asdasdsa")
                .header("Accept", "json")
                .body(" {" +
                "        \"id\": 1," +
                "        \"name\": \"John\"," +
                "        \"email\": \"Smith\"" +
                "    }")
                .when().post()
                .then()
                .statusCode(201)
                .body("result", equalTo(true),
                        "description", equalTo("We have it!"));
    }

}
