package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.Organization;
import com.example.demo.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/org")
@RequiredArgsConstructor
public class OrganizationController {

	private final OrganizationService organizationService;

	@PostMapping("/create")
	public ResponseEntity<?> createOrganization(@RequestBody CreateOrgRequest request) {
		try {
			CreateOrgResponse response = organizationService.createOrganization(request);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (Exception ex) {
			return ResponseEntity.status(409).body(ex.getMessage());
		}
	}
	

	@GetMapping("/get")
	public ResponseEntity<?> getOrganization(@RequestParam("organization_name") String organizationName) {
		Organization org = organizationService.getOrganizationByName(organizationName);
		if (org == null) {
			return ResponseEntity.status(404).body("Organization not found");
		}
		return ResponseEntity.ok(org);
	}
	

	@PutMapping("/update")
	public ResponseEntity<?> updateOrganization(@RequestBody UpdateOrgRequest request) {
		try {
			Organization updatedOrg = organizationService.updateOrganization(request);
			return ResponseEntity.ok(updatedOrg);
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (RuntimeException ex) {
			return ResponseEntity.status(403).body(ex.getMessage());
		} catch (Exception ex) {
			return ResponseEntity.status(500).body("Internal server error: " + ex.getMessage());
		}
	}
	

	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteOrganization(@RequestBody DeleteOrgRequest request) {
		try {
			organizationService.deleteOrganization(request);
			return ResponseEntity.ok("Organization deleted successfully.");
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (RuntimeException ex) {
			return ResponseEntity.status(403).body(ex.getMessage());
		} catch (Exception ex) {
			return ResponseEntity.status(500).body("Internal server error: " + ex.getMessage());
		}
	}
}