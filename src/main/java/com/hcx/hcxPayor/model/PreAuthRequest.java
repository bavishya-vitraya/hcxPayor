package com.hcx.hcxPayor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "hcx_preAuthRequests")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PreAuthRequest {
    @Id
    private String id;
    private String requestObject; // hcx response from provider
    private String fhirPayload;// decrypted fhir payload
    private String preAuthRequest; //request to be sent to vas
    private String preAuthRequestId; // claim id
    private String correlationId; // mapping between request and response
}
