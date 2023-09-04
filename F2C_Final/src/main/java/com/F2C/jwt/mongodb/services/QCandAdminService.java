package com.F2C.jwt.mongodb.services;

import java.util.List;
//import java.util.Optional;

import org.springframework.stereotype.Service;

import com.F2C.jwt.mongodb.models.CCAdminResponse;
import com.F2C.jwt.mongodb.models.User;

@Service
public interface QCandAdminService {
	User changeCCAvailable(String ccId,String status);
   List<User> availableEmployees();
   User setEmptyRequestFieldCCQC(String userId);
   User assignCCEmployee(String userId,String requestId);
   List<CCAdminResponse> currentAllEmployeeStatus();
}
