package com.hcx.hcxPayor.controller;


import com.hcx.hcxPayor.model.PreAuthResponse;
import com.hcx.hcxPayor.service.PreAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/vhi/response")
public class VhiResponseController {

    @Autowired
    private PreAuthService preAuthResponseService;

    @PostMapping("/savePreAuthResponse")
    public String savePreAuthResponse(@RequestBody PreAuthResponse preAuthResponse) throws Exception {
        log.info("Entered Save PreAuth Response Controller");
        return preAuthResponseService.storePreAuthResponse(preAuthResponse);
    }
}
