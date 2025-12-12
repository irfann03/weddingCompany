package com.example.demo.dto;

import lombok.Data;

@Data
public class AdminAddRequest {
    private String email;
    private String password;
    private String organizationId;
}