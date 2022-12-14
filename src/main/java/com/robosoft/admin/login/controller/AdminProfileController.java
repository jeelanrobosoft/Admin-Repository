package com.robosoft.admin.login.controller;

import com.robosoft.admin.login.model.Admin;
import com.robosoft.admin.login.model.Profile;
import com.robosoft.admin.login.service.AdminProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminProfileController {

    @Autowired
    private AdminProfileService adminProfileService;

    @GetMapping("/getProfile")
    public ResponseEntity<?> getProfile()
    {
        Profile profile = adminProfileService.getProfile();
        if(profile != null)
        {
            return ResponseEntity.of(Optional.of(profile));
        }
        return new ResponseEntity<>(Collections.singletonMap("message", "Invalid Credentials"), HttpStatus.OK);
    }

}
