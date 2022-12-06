package com.hcx.hcxPayor.apiTest;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HcxRequestControllerTest {

    @Test
    public void TestsavePreAuthRespons() throws IOException {
        RestAssured.baseURI = "http://localhost:9080";
        File file = new ClassPathResource("input/preAuthReqPayload").getFile();
        String preAuth = FileUtils.readFileToString(file);
        Response response= given().header("Content-Type", "application/json").body(preAuth).when().post("/hcxPayor/request/preauth/submit");
        String result= response.getBody().asString();
        Assertions.assertEquals(result,"PreAuth request pushed to Queue");
    }
}
