package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.model.Organization;

public interface OrganizationService {
	CreateOrgResponse createOrganization(CreateOrgRequest request);

	Organization getOrganizationByName(String organizationName);

	Organization updateOrganization(UpdateOrgRequest request);

	void deleteOrganization(DeleteOrgRequest request);
}
