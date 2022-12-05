package com.hcx.hcxPayor.service.impl;

import com.hcx.hcxPayor.dto.PreAuthReqDTO;
import com.hcx.hcxPayor.dto.PreAuthResponseDTO;
import com.hcx.hcxPayor.model.PreAuthRequest;
import com.hcx.hcxPayor.model.PreAuthResponse;
import com.hcx.hcxPayor.repository.PreAuthRequestRepo;
import com.hcx.hcxPayor.repository.PreAuthResponseRepo;
import com.hcx.hcxPayor.service.PreAuthService;
import com.hcx.hcxPayor.utils.Constants;
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
        preAuthResponseDTO.setReferenceId(preAuthResponse.getResponseId());
        preAuthResponseDTO.setMessageType(preAuthResponse.getResponseType());
        preAuthResponseDTO.setSenderCode(preAuthResponse.getSenderCode());
        preAuthResponseDTO.setInsurerCode(preAuthResponse.getInsurerCode());
        log.info("{}",preAuthResponseDTO);
        rabbitTemplate.convertAndSend(exchange,resroutingKey,preAuthResponseDTO);
        return "Pre Auth Response From VHI pushed to Queue";
    }

    @Override
    public String storePreAuthRequest(String request) {
        PreAuthRequest preAuthRequest=new PreAuthRequest();
        preAuthRequest.setPreAuthRequest(request);
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
