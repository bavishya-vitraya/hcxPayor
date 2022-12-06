package com.hcx.hcxPayor.apiTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcx.hcxPayor.dto.UserRequestDTO;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VhiResponseControllerTest {
    String token="";

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        UserRequestDTO userRequestDTO= new UserRequestDTO();
        userRequestDTO.setUserName("Joshi");
        userRequestDTO.setPassword("Joshi1999");

        ObjectMapper objectMapper= new ObjectMapper();
        String json=objectMapper.writeValueAsString(userRequestDTO);
        RestAssured.baseURI = "http://localhost:9080";
        token=  given().header("Content-Type", "application/json").body(json).when().post("/payor/user/login").jsonPath().get("token");


    }

    @Test
    public void testSavePreAuthResponse() throws IOException {

        Map<String,String> headers= new HashMap<>();
        headers.put("Authorization",token);
        headers.put("Content-Type","application/json");


        File preAuthResponse = new ClassPathResource("input/preAuthResInput").getFile();
        String json = FileUtils.readFileToString(preAuthResponse);

        RequestSpecification request = given();
        request.headers(headers);
        request.body(json);
        Response response= request.post("/hcxPayor/response/savePreAuthResponse");

        String result= response.getBody().asString();
        System.out.println(result);
        Assertions.assertEquals(result,"Pre Auth Response From VHI pushed to Queue");
    }


}
