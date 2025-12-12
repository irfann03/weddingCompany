package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.AdminUser;
import com.example.demo.service.AdminService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AdminLoginRequest request) {
		try {
			AdminLoginResponse resp = adminService.login(request);
			return ResponseEntity.ok(resp);
		} catch (RuntimeException ex) {
			return ResponseEntity.status(401).body("Unauthorized: " + ex.getMessage());
		}
	}

	@PostMapping("/add")
	public ResponseEntity<?> addAdmin(@RequestBody AdminAddRequest request) {
		System.out.println("in controller");
		try {
			AdminUser adminUser = adminService.addAdmin(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(adminUser);
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
		}
	}
}
