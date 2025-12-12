package com.example.demo.dto;

import lombok.Data;

@Data
public class CreateOrgRequest {
    private String organizationName;
    private String email;
    private String password;
}
