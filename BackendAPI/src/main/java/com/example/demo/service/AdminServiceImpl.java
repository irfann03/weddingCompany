package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.*;
import com.example.demo.model.AdminUser;
import com.example.demo.repository.AdminUserRepository;
import com.example.demo.util.JwtUtil;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final AdminUserRepository adminUserRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Override
	public AdminLoginResponse login(AdminLoginRequest request) {
		AdminUser user = adminUserRepository.findByEmail(request.getEmail().toLowerCase())
				.orElseThrow(() -> new RuntimeException("Invalid credentials"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new RuntimeException("Invalid credentialssss");
		}

		String token = jwtUtil.generateToken(user.getId(), user.getOrgId());

		AdminLoginResponse resp = new AdminLoginResponse();
		resp.setToken(token);
		resp.setAdminId(user.getId());
		resp.setOrganizationId(user.getOrgId());
		return resp;
	}

	@Override
	public AdminUser addAdmin(AdminAddRequest request) {
		System.out.println("in serviceee");
		if (request.getEmail() == null || request.getEmail().isBlank()) {
			throw new IllegalArgumentException("Email is required");
		}
		if (request.getPassword() == null || request.getPassword().isBlank()) {
			throw new IllegalArgumentException("Password is required");
		}
		if (request.getOrganizationId() == null || request.getOrganizationId().isBlank()) {
			throw new IllegalArgumentException("Organization ID is required");
		}

		if (adminUserRepository.findByEmail(request.getEmail().toLowerCase()).isPresent()) {
			throw new IllegalArgumentException("Admin with this email already exists");
		}

		AdminUser adminUser = new AdminUser();
		adminUser.setEmail(request.getEmail().toLowerCase());
		adminUser.setPassword(passwordEncoder.encode(request.getPassword()));
		adminUser.setOrgId(request.getOrganizationId());

		return adminUserRepository.save(adminUser);
	}

}
