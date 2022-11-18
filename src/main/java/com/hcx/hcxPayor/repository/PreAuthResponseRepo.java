package com.hcx.hcxPayor.repository;

import com.hcx.hcxPayor.model.PreAuthResponse;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PreAuthResponseRepo extends MongoRepository<PreAuthResponse, String> {
}
