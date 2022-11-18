package com.hcx.hcxPayor.service.impl;

import com.hcx.hcxPayor.dto.Message;
import com.hcx.hcxPayor.dto.PreAuthResponseDTO;
import com.hcx.hcxPayor.model.PreAuthResponse;
import com.hcx.hcxPayor.repository.PreAuthResponseRepo;
import com.hcx.hcxPayor.service.PreAuthService;
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
public class PreAuthResponseServiceImplementation implements PreAuthService {

    @Value("${queue.exchange.name}")
    private String exchange;

    @Value("${queue.reqrouting.key}")
    private String reqroutingKey;

    @Value("${queue.resrouting.key}")
    private String resroutingKey;

    @Autowired
    private PreAuthResponseRepo preAuthResponseRepo;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${hcx.protocolBasePath}")
    String protocolBasePath;

    @Value("${hcx.authBasePath}")
    String authBasePath;

    @Value("${hcx.participantCode}")
    String participantCode;

    @Value("${hcx.recipientCode}")
    String recipientCode;

    @Value("${hcx.username}")
    String username;

    @Value("${hcx.password}")
    String password;

    @Value("${hcx.igUrl}")
    String igUrl;

    public Map<String, Object> setPayorConfig() throws IOException {
        Map<String, Object> config = new HashMap<>();
        File file = new ClassPathResource("keys/vitraya-mock-payor-private-key.pem").getFile();
        String privateKey= FileUtils.readFileToString(file);
        config.put("protocolBasePath", protocolBasePath);
        config.put("authBasePath", authBasePath);
        config.put("participantCode","1-434d79f6-aad8-48bc-b408-980a4dbd90e2");
        config.put("username", "vitrayahcxpayor1@vitrayatech.com");
        config.put("password","BkYJHwm64EEn8B8");
        config.put("encryptionPrivateKey", privateKey);
        config.put("igUrl", igUrl);
        return config;
    }
    @Override
    public String storePreAuthResponse(PreAuthResponse preAuthResponse) {
        preAuthResponseRepo.save(preAuthResponse);
        log.info("PreAuth Response from VHI is saved");
        PreAuthResponseDTO preAuthResponseDTO = new PreAuthResponseDTO();
        preAuthResponseDTO.setRefernceId(preAuthResponse.getResponseId());
        preAuthResponseDTO.setMessageType(preAuthResponse.getResponseType());
        preAuthResponseDTO.setSenderCode(preAuthResponse.getSenderCode());
        preAuthResponseDTO.setInsurerCode(preAuthResponse.getInsurerCode());
        log.info("{}",preAuthResponseDTO);
        rabbitTemplate.convertAndSend(exchange,resroutingKey,preAuthResponseDTO);
        return "Pre Auth Response From VHI pushed to Queue";
    }

    @Override
    public String storePreAuthRequest(String preAuthRequestObject) {
        return null;
    }
}
