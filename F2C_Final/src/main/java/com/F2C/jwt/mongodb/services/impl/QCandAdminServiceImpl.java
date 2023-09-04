package com.F2C.jwt.mongodb.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.F2C.jwt.mongodb.config.NotificationHandler;
import com.F2C.jwt.mongodb.models.CCAdminResponse;
import com.F2C.jwt.mongodb.models.CCToQCReq;
import com.F2C.jwt.mongodb.models.ERole;
import com.F2C.jwt.mongodb.models.Role;
import com.F2C.jwt.mongodb.models.User;
import com.F2C.jwt.mongodb.repository.RoleRepository;
import com.F2C.jwt.mongodb.repository.UserRepository;
import com.F2C.jwt.mongodb.services.QCandAdminService;

@Service
public class QCandAdminServiceImpl implements QCandAdminService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;
	public User changeCCAvailable(String ccId,String status) {
		Optional<User> list = userRepository.findById(ccId);
		User user =list.get();
		
		user.setCcAvailable(status);
		user = userRepository.save(user);
		return user;
	}
	public List<User> availableEmployees(){
		Optional<Role> adminRole = roleRepository.findByName(ERole.ROLE_ADMIN);
		Role role = adminRole.get();
		List<User> list2 = new ArrayList();
		List<User> list = userRepository.findByRoles(role);
		for(User user : list) {
			String status = user.getCcAvailable();
			if(status.equals("free")) {
				list2.add(user);
			}
			
		}
		
		
		return list2;
	}
	
	
	public User setEmptyRequestFieldCCQC(String userId) {
		Optional<User> list = userRepository.findById(userId);
		User user = list.get();
		//CCToQCReq request = new CCToQCReq();
		//request.setRequestId(UUID.randomUUID().toString());
		//request.setFarmerId(userId);
		//request.setCropId(cropId);
		//request.setCcAvailable("free");  
		user.setCcAvailable("free");
		//List <CCToQCReq> requestList = new ArrayList();
		//requestList.add(request);
		//user.setRequestList(requestList);
		user = userRepository.save(user);
		return user;
		
	}
	
	
	public User assignCCEmployee(String userId,String requestId) {
		Optional<User> list = userRepository.findById(userId);
		User farmer = list.get();
		CCToQCReq request = farmer.getRequestList()
                .stream()
                .filter(req -> req.getRequestId().equals(requestId))
                .findFirst()
                .orElseThrow(() -> new CustomEntityNotFoundException("Request not found"));
		
		 List<User> availableEmployee = availableEmployees();

		//User admin=null;
		
		if (!availableEmployee.isEmpty()) {
            // Implement round-robin logic
            int currentIndex = -1;
            if (request.getAssignedCCId() != null) {
                // Find the index of the currently assigned employee
                for (int i = 0; i < availableEmployee.size(); i++) {
                    if (availableEmployee.get(i).getId().equals(request.getAssignedCCId())) {
                        
                    	//admin = availableEmployee.get(i);
                    			currentIndex = i;
                    			//admin.setCcAvailable("busy");
                               // request.setHandledCC("processing");
                        break;
                    }
                }
            }
            
            int nextIndex = (currentIndex + 1) % availableEmployee.size();
            User nextEmployee = availableEmployee.get(nextIndex);
            System.out.println(nextEmployee.getCcAvailable());
            
            nextEmployee.setCcAvailable("busy");
            System.out.println(nextEmployee.getCcAvailable());
            userRepository.save(nextEmployee);
           // request.setHandledCC("processing");
            // Assign the request to the next available employee
            request.setAssignedCCId(nextEmployee.getId());
           request.setHandledCC("processing");
           request.setIsHandledByCC(true);
            userRepository.save(farmer);  
            
           NotificationHandler.sendNotificationToEmployee(nextEmployee.getId(), request);
           //System.out.println(notification);
		}
		return farmer;
		}
	
	
	public List<CCAdminResponse> currentAllEmployeeStatus() {
		 List<CCAdminResponse> responseList = new ArrayList<>();
		    Optional<Role> farmerRole = roleRepository.findByName(ERole.ROLE_FARMER);
		    Role role = farmerRole.get();
		    List<User> list = userRepository.findByRoles(role);

		    for (User user : list) {
		        if (user.getRequestCreated()) {
		            List<CCToQCReq> requestList = user.getRequestList();

		            for (CCToQCReq req : requestList) {
		                if (req.getIsHandledByCC()) {
		                    CCAdminResponse response = new CCAdminResponse(); 
		                    
		                    Optional<User> farmerList = userRepository.findById(req.getFarmerId());  
		                    User farmer = farmerList.get();
		                    String name = farmer.getfirstName() + " " + farmer.getlastName();
		                    response.setFarmerName(name);

		                    Optional<User> ccList1 = userRepository.findById(req.getAssignedCCId());  
		                    User ccAdmin1 = ccList1.get();
		                    response.setCcAvailable(ccAdmin1.getCcAvailable());
		                    String ccname = ccAdmin1.getfirstName() + " " + ccAdmin1.getlastName();
		                    response.setCCEmployeeName(ccname);

		                    response.setHandledCC(req.getHandledCC());
		                    
		                    responseList.add(response);
		                }
		            }
		        }
		    }

		    return responseList;
	}
	
}
