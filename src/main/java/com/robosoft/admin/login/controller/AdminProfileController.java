package com.robosoft.admin.login.controller;

import com.robosoft.admin.login.model.Admin;
import com.robosoft.admin.login.model.ProfileDetails;
import com.robosoft.admin.login.service.AdminProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class AdminProfileController {

    @Autowired
    private AdminProfileService adminProfileService;

    @GetMapping("/getProfile")
    public ResponseEntity<?> getProfile()
    {
        Admin admin = adminProfileService.getProfile();
        if(admin != null)
        {
            return ResponseEntity.of(Optional.of(admin));
        }
        return new ResponseEntity<>(Collections.singletonMap("message", "Invalid Credentials"), HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveProfileDetails(@ModelAttribute ProfileDetails details){
        String status = adminProfileService.saveProfileDetails(details);
        if(status == null)
            return  new ResponseEntity<>(Collections.singletonMap("message","Profile updated successfully"),HttpStatus.OK);
        return  new ResponseEntity<>(Collections.singletonMap("Error",status),HttpStatus.NOT_ACCEPTABLE);
    }

}
