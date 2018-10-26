package rest;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

public class MockTest {

    @BeforeTest
    public static  void start() {
        WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(8082));
        wireMockServer.start();

        wireMockServer.stubFor(get(urlPathEqualTo("/something")).
                willReturn(aResponse().
                        withHeader("Content-Type", "json").
                        withStatus(HttpStatus.SC_OK)));

        wireMockServer.stubFor(post(urlPathEqualTo("/something")).
                withHeader("Accept", containing("xml")).
                withRequestBody(equalToJson(" {\n" +
                        "        \"id\": 1,\n" +
                        "        \"name\": \"lol\",\n" +
                        "        \"email\": \"helloooo\"\n" +
                        "    }")).
                willReturn(aResponse().
                        withStatus(HttpStatus.SC_ACCEPTED)));


        requestSpecification = new RequestSpecBuilder().
                setBaseUri("http://127.0.0.1").setPort(8082).
                log(LogDetail.ALL).
                addFilter(new ResponseLoggingFilter()).
                build();
    }

    @Test(groups = "courier api")
    public void mockGetTest() {
        given().basePath("/something").
                when().get().
                then().statusCode(HttpStatus.SC_OK);

    }

    @Test
    public void mockPostTest()
    {
        given().basePath("/something")
                .accept(ContentType.XML)
                .body(" {" +
                "        \"id\": 1," +
                "        \"name\": \"lol\"," +
                "        \"email\": \"helloooo\"" +
                "    }").
                when().post().
                then().statusCode(HttpStatus.SC_ACCEPTED);
    }
}
