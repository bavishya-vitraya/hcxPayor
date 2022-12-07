package com.hcx.hcxPayor.repository;

import com.hcx.hcxPayor.model.PreAuthRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PreAuthRequestRepo extends MongoRepository<PreAuthRequest, String> {
    PreAuthRequest findPreAuthRequestByPreAuthRequestId(String preAuthRequestId);
}
