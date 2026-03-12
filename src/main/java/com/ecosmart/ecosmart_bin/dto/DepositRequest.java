// DepositRequest.java
package com.ecosmart.ecosmart_bin.dto;

import lombok.Data;

@Data
public class DepositRequest {
    private Long userId;
    private Long binId;
    private double poids; // en grammes
}