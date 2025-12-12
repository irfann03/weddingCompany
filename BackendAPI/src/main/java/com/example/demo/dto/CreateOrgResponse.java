package com.example.demo.dto;

import lombok.Data;

@Data
public class CreateOrgResponse {
    private String id;
    private String organizationName;
    private String collectionName;
    private String adminEmail;
}
