package com.hcx.hcxPayor.service.impl;

import com.hcx.hcxPayor.dto.PreAuthReqDTO;
import com.hcx.hcxPayor.dto.PreAuthResponseDTO;
import com.hcx.hcxPayor.dto.PreAuthVhiResponse;
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


    @Value("${hcx.protocolBasePath}")
    String protocolBasePath;

    @Value("${hcx.authBasePath}")
    String authBasePath;

    @Value("${hcx.participantCode}")
    String participantCode;

    @Value("${hcx.username}")
    String username;

    @Value("${hcx.password}")
    String password;

    @Value("${hcx.igUrl}")
    String igUrl;

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
        PreAuthVhiResponse vhiResponse = preAuthResponse.getPreAuthVhiResponse();
        PreAuthRequest preAuthRequest = preAuthRequestRepo.findPreAuthRequestByPreAuthRequestId(String.valueOf(vhiResponse.getHospitalReferenceId()));
        preAuthResponse.setCorrelationId(preAuthRequest.getCorrelationId());
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
        config.put("protocolBasePath", protocolBasePath);
        config.put("authBasePath", authBasePath);
        config.put("participantCode",participantCode);
        config.put("username", username);
        config.put("password",password);
        config.put("encryptionPrivateKey", privateKey);
        config.put("igUrl", igUrl);
        return config;
    }

    @Override
    public String storePreAuthRequest(String request) throws Exception {
        Operations operation = Operations.PRE_AUTH_SUBMIT;
        HCXIntegrator.init(setPayorConfig());
        Map<String,Object> output = new HashMap<>();
        Map<String,Object> input = new HashMap<>();
        Map<String,Object> headers;
        input.put("payload",request);
        HCXIncomingRequest hcxIncomingRequest = new HCXIncomingRequest();
        hcxIncomingRequest.process(JSONUtils.serialize(input),operation,output);
        headers = (Map<String, Object>) output.get("headers");
        log.info("headers {}",headers);
        String correlationId = (String) headers.get("x-hcx-correlation_id");
        log.info("Incoming Request: {}",output);
        String fhirPayload = (String) output.get("fhirPayload");

        PreAuthRequest preAuthRequest=new PreAuthRequest();
        preAuthRequest.setRequestObject(request);
        preAuthRequest.setFhirPayload(fhirPayload);
        preAuthRequest.setCorrelationId(correlationId);
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
