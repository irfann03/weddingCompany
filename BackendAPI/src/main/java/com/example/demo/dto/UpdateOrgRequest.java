package com.example.demo.dto;

import lombok.Data;

@Data
public class UpdateOrgRequest {
    private String organizationName;
    private String newOrganizationName;
    private String email;
    private String password;
}

