package com.example.demo.dto;

import lombok.Data;

@Data
public class AdminLoginResponse {
    private String token;
    private String adminId;
    private String organizationId;
}
