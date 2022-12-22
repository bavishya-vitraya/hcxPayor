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
    public String savePreAuthResponse(HttpServletRequest request) throws Exception {
        log.info("authtype{}",request.getAuthType());
        log.info("conetextpath{}",request.getContextPath());
        log.info("servletpath",request.getServletPath());
        List<String> headers= new ArrayList<>();
        request.getHeaderNames().asIterator().forEachRemaining(headers::add);
        log.info("headers{}",headers.toString());
        log.info("pathinfo{}",request.getPathInfo());
        log.info("url{}",request.getRequestURL());
        log.info("userprincipal", request.getUserPrincipal().getName());
        return null;
        //log.info("Entered Save PreAuth Request Controller");
       // return preAuthService.storePreAuthRequest(hcxResponseDTO.getPayload());
    }
}
