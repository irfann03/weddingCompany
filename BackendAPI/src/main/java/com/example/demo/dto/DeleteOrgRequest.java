package com.example.demo.dto;

import lombok.Data;

@Data
public class DeleteOrgRequest {
    private String organizationName;
    private String email; // admin email for authentication
    private String password; // admin password for authentication
}
