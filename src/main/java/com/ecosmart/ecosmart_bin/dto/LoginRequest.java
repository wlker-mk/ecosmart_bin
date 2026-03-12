// LoginRequest.java
package com.ecosmart.ecosmart_bin.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}