// RegisterRequest.java
package com.ecosmart.ecosmart_bin.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String telephone;
}