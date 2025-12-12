package com.example.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "organizations")
public class Organization {
    @Id
    private String id;

    private String organizationName;     
    private String collectionName;     
    private String adminUserId;   
    private String connectionDetails;
}
