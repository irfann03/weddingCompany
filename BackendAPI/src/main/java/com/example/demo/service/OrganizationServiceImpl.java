package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.bson.Document;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

	private final OrganizationRepository organizationRepository;
	private final AdminUserRepository adminUserRepository;
	private final MongoTemplate mongoTemplate;
	private final PasswordEncoder passwordEncoder;

	private static String normalizeName(String name) {
		if (name == null)
			return null;
		String nowhitespace = name.trim().replaceAll("\\s+", "_");
		String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		String clean = pattern.matcher(normalized).replaceAll("");
		return clean.replaceAll("[^a-zA-Z0-9_\\-]", "").toLowerCase(Locale.ROOT);
	}

	private AdminUser authenticateAdmin(String email, String password) {
		AdminUser admin = adminUserRepository.findByEmail(email.toLowerCase())
				.orElseThrow(() -> new RuntimeException("Admin user not found"));

		if (!passwordEncoder.matches(password, admin.getPassword())) {
			throw new RuntimeException("Invalid credentials");
		}
		return admin;
	}

	@Override
	public CreateOrgResponse createOrganization(CreateOrgRequest request) {
		String orgNameRaw = request.getOrganizationName();
		if (orgNameRaw == null || orgNameRaw.isBlank()) {
			throw new IllegalArgumentException("organizationName is required");
		}
		String orgName = normalizeName(orgNameRaw);

		if (organizationRepository.existsByOrganizationName(orgName)) {
			throw new DuplicateKeyException("Organization already exists: " + orgName);
		}

		AdminUser admin = new AdminUser();
		admin.setEmail(request.getEmail().toLowerCase());
		admin.setPassword(passwordEncoder.encode(request.getPassword()));

		Organization org = new Organization();
		org.setOrganizationName(orgName);
		String collectionName = "org_" + orgName;
		org.setCollectionName(collectionName);

		Organization savedOrg = organizationRepository.save(org);

		admin.setOrgId(savedOrg.getId());
		AdminUser savedAdmin = adminUserRepository.save(admin);

		savedOrg.setAdminUserId(savedAdmin.getId());
		savedOrg.setConnectionDetails("masterdb"); // placeholder
		organizationRepository.save(savedOrg);

		if (!mongoTemplate.collectionExists(collectionName)) {
			mongoTemplate.createCollection(collectionName);
		}

		CreateOrgResponse res = new CreateOrgResponse();
		res.setId(savedOrg.getId());
		res.setOrganizationName(savedOrg.getOrganizationName());
		res.setCollectionName(savedOrg.getCollectionName());
		res.setAdminEmail(savedAdmin.getEmail());
		return res;
	}

	@Override
	public Organization getOrganizationByName(String organizationName) {
		if (organizationName == null || organizationName.isBlank()) {
			throw new IllegalArgumentException("organizationName is required");
		}
		String orgName = normalizeName(organizationName);
		return organizationRepository.findByOrganizationName(orgName).orElse(null);
	}

	@Override
	public Organization updateOrganization(UpdateOrgRequest request) {
		if (request.getOrganizationName() == null || request.getOrganizationName().isBlank()) {
			throw new IllegalArgumentException("organizationName is required");
		}
		if (request.getEmail() == null || request.getEmail().isBlank() || request.getPassword() == null
				|| request.getPassword().isBlank()) {
			throw new IllegalArgumentException("Email and password are required for authentication");
		}

		String oldOrgName = normalizeName(request.getOrganizationName());
		Organization org = organizationRepository.findByOrganizationName(oldOrgName)
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		AdminUser admin = authenticateAdmin(request.getEmail(), request.getPassword());

		if (!admin.getOrgId().equals(org.getId())) {
			throw new RuntimeException("Unauthorized to update this organization");
		}

		String newOrgNameRaw = request.getNewOrganizationName();
		if (newOrgNameRaw != null && !newOrgNameRaw.isBlank()) {
			String newOrgName = normalizeName(newOrgNameRaw);

			if (!newOrgName.equals(oldOrgName) && organizationRepository.existsByOrganizationName(newOrgName)) {
				throw new RuntimeException("Organization name already exists: " + newOrgName);
			}

			String oldCollectionName = org.getCollectionName();
			String newCollectionName = "org_" + newOrgName;

			if (!mongoTemplate.collectionExists(newCollectionName)) {
				mongoTemplate.createCollection(newCollectionName);
			}

			List<Document> docs = mongoTemplate.findAll(Document.class, oldCollectionName);
			if (!docs.isEmpty()) {
				mongoTemplate.insert(docs, newCollectionName);
			}

			org.setOrganizationName(newOrgName);
			org.setCollectionName(newCollectionName);
			organizationRepository.save(org);
		}

		return org;
	}

	@Override
	public void deleteOrganization(DeleteOrgRequest request) {
		if (request.getOrganizationName() == null || request.getOrganizationName().isBlank()) {
			throw new IllegalArgumentException("organizationName is required");
		}
		if (request.getEmail() == null || request.getEmail().isBlank() || request.getPassword() == null
				|| request.getPassword().isBlank()) {
			throw new IllegalArgumentException("Email and password are required for authentication");
		}

		String orgName = normalizeName(request.getOrganizationName());
		Organization org = organizationRepository.findByOrganizationName(orgName)
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		AdminUser admin = authenticateAdmin(request.getEmail(), request.getPassword());

		if (!admin.getOrgId().equals(org.getId())) {
			throw new RuntimeException("Unauthorized to delete this organization");
		}

		if (mongoTemplate.collectionExists(org.getCollectionName())) {
			mongoTemplate.dropCollection(org.getCollectionName());
		}

		adminUserRepository.delete(admin);
		organizationRepository.delete(org);
	}
}