package com.hcx.hcxPayor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String refernceId;
    private String senderCode;
    private String insurerCode;
    private String messageType;
}
