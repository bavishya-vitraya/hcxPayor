package com.hcx.hcxPayor.service;

import com.hcx.hcxPayor.model.PreAuthResponse;

public interface PreAuthService {
    String storePreAuthResponse(PreAuthResponse preAuthResponse);
    String storePreAuthRequest(String preAuthRequestObject);
}

