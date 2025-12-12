package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.model.AdminUser;

public interface AdminService {
	AdminLoginResponse login(AdminLoginRequest request);

	AdminUser addAdmin(AdminAddRequest request);
}
