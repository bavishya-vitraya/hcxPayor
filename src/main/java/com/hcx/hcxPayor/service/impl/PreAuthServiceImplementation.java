package com.hcx.hcxPayor.service.impl;

import com.hcx.hcxPayor.dto.PreAuthReqDTO;
import com.hcx.hcxPayor.dto.PreAuthResponseDTO;
import com.hcx.hcxPayor.model.PreAuthRequest;
import com.hcx.hcxPayor.model.PreAuthResponse;
import com.hcx.hcxPayor.repository.PreAuthRequestRepo;
import com.hcx.hcxPayor.repository.PreAuthResponseRepo;
import com.hcx.hcxPayor.service.PreAuthService;
import com.hcx.hcxPayor.utils.Constants;
import io.hcxprotocol.impl.HCXIncomingRequest;
import io.hcxprotocol.init.HCXIntegrator;
import io.hcxprotocol.utils.JSONUtils;
import io.hcxprotocol.utils.Operations;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PreAuthServiceImplementation implements PreAuthService {

    @Value("${queue.exchange.name}")
    private String exchange;

    @Value("${queue.reqrouting.key}")
    private String reqroutingKey;

    @Value("${queue.resrouting.key}")
    private String resroutingKey;

    @Autowired
    private PreAuthResponseRepo preAuthResponseRepo;

    @Autowired
    private PreAuthRequestRepo  preAuthRequestRepo;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public String storePreAuthResponse(PreAuthResponse preAuthResponse) {
        preAuthResponseRepo.save(preAuthResponse);
        log.info("PreAuth Response from VHI is saved");
        PreAuthResponseDTO preAuthResponseDTO = new PreAuthResponseDTO();
        preAuthResponseDTO.setReferenceId(preAuthResponse.getId());
        preAuthResponseDTO.setMessageType(preAuthResponse.getResponseType());
        preAuthResponseDTO.setSenderCode(preAuthResponse.getSenderCode());
        preAuthResponseDTO.setInsurerCode(preAuthResponse.getInsurerCode());
        log.info("{}",preAuthResponseDTO);
        rabbitTemplate.convertAndSend(exchange,resroutingKey,preAuthResponseDTO);
        return "Pre Auth Response From VHI pushed to Queue";
    }

    public  Map<String, Object> setPayorConfig() throws IOException {
        Map<String, Object> config = new HashMap<>();
        File file = new ClassPathResource("keys/vitraya-mock-payor-private-key.pem").getFile();;
        String privateKey= FileUtils.readFileToString(file);
        config.put("protocolBasePath", "http://staging-hcx.swasth.app/api/v0.7");
        config.put("authBasePath","http://a9dd63de91ee94d59847a1225da8b111-273954130.ap-south-1.elb.amazonaws.com:8080/auth/realms/swasth-health-claim-exchange/protocol/openid-connect/token");
        config.put("participantCode","1-434d79f6-aad8-48bc-b408-980a4dbd90e2");
        config.put("username", "vitrayahcxpayor1@vitrayatech.com");
        config.put("password","BkYJHwm64EEn8B8");
        config.put("encryptionPrivateKey", privateKey);
        config.put("igUrl", "https://ig.hcxprotocol.io/v0.7");
        return config;
    }

    @Override
    public String storePreAuthRequest(String request) throws Exception {
        Operations operation = Operations.PRE_AUTH_SUBMIT;
        HCXIntegrator.init(setPayorConfig());
        Map<String,Object> output = new HashMap<>();
        Map<String,Object> input = new HashMap<>();
        input.put("payload",request);
        HCXIncomingRequest hcxIncomingRequest = new HCXIncomingRequest();
        hcxIncomingRequest.process(JSONUtils.serialize(input),operation,output);
        log.info("Incoming Request: {}",output);
        String fhirPayload = (String) output.get("fhirPayload");

        PreAuthRequest preAuthRequest=new PreAuthRequest();
        preAuthRequest.setPreAuthRequest(fhirPayload);
        preAuthRequestRepo.save(preAuthRequest);
        log.info("preAuth  req saved");
        PreAuthReqDTO preAuthReqDTO = new PreAuthReqDTO();
        preAuthReqDTO.setMessageType(Constants.PRE_AUTH);
        preAuthReqDTO.setReferenceId(preAuthRequest.getId());
        log.info("preAuthReqDTO {} ",preAuthReqDTO);
        rabbitTemplate.convertAndSend(exchange,reqroutingKey,preAuthReqDTO);
        return "PreAuth request pushed to Queue";
    }
}
