package com.hcx.hcxPayor.controller;

import com.hcx.hcxPayor.dto.HCXResponseDTO;
import com.hcx.hcxPayor.service.PreAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class HcxRequestController {

    @Autowired
    private PreAuthService preAuthService;

    @PostMapping("/preauth/submit")
    public String savePreAuthResponse(@RequestBody HCXResponseDTO hcxResponseDTO) throws Exception {
        log.info("Entered Save PreAuth Request Controller");
        log.info("response{}",hcxResponseDTO.getPayload());
        return preAuthService.storePreAuthRequest(hcxResponseDTO.getPayload());
    }
}
