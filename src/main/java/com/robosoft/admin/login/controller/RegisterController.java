package com.robosoft.admin.login.controller;

import com.robosoft.admin.login.model.Register;
import com.robosoft.admin.login.model.ResetPassword;
import com.robosoft.admin.login.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PostMapping("/register")
    public ResponseEntity<?> adminRegister(@ModelAttribute Register register){
        String s = registerService.adminRegister(register);
        if(s!= null)
            return new ResponseEntity<>(Collections.singletonMap("message",s), HttpStatus.OK);
        return new ResponseEntity<>(Collections.singletonMap("message","Something Went Wrong"),HttpStatus.OK);
    }
    @PutMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPassword resetPassword){
        String s = registerService.resetPassword(resetPassword);
        if(s!= null)
            return new ResponseEntity<>(Collections.singletonMap("message",s), HttpStatus.OK);
        return new ResponseEntity<>(Collections.singletonMap("message","Something Went Wrong"),HttpStatus.OK);
    }


}
