package com.hcx.hcxPayor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String responseId;
    private String senderCode;
    private String insurerCode;
    private String responseType;
}
