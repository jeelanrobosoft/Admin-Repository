package com.robosoft.admin.login.controller;

import com.robosoft.admin.login.model.Admin;
import com.robosoft.admin.login.model.Register;
import com.robosoft.admin.login.service.SuperAdminRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/superAdmin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class SuperAdminApprovalController {

    @Autowired
    private SuperAdminRequestHandler superAdminRequestHandler;

    @PostMapping("/approve")
    public ResponseEntity<?> approveAdminRequest(@RequestBody Admin admin)
    {
        String response = superAdminRequestHandler.approveRequest(admin);
        if(response != null)
        {
            return ResponseEntity.of(Optional.of(Collections.singletonMap("message",response))).status(HttpStatus.ACCEPTED).build();
        }
        return ResponseEntity.of(Optional.of(Collections.singletonMap("message","Failed to approve. Try again....."))).status(HttpStatus.OK).build();
    }

    @DeleteMapping("/reject")
    public ResponseEntity<?> rejectAdminRequest(@RequestBody Admin admin)
    {
        String response = superAdminRequestHandler.rejectRequest(admin);
        if(response != null)
        {
            return ResponseEntity.of(Optional.of(Collections.singletonMap("message",response))).status(HttpStatus.OK).build();
        }
        return ResponseEntity.of(Optional.of(Collections.singletonMap("message","Failed. Try Again"))).status(HttpStatus.NOT_MODIFIED).build();
    }

    @GetMapping("/admins")
    public ResponseEntity<?> getApprovalPendingAdminDetails(){
       return new ResponseEntity<>(superAdminRequestHandler.getAdminDetails(),HttpStatus.OK);
    }


}

