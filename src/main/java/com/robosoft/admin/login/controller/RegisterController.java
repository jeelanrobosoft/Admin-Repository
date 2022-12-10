package com.robosoft.admin.login.controller;

import com.robosoft.admin.login.model.Register;
import com.robosoft.admin.login.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PostMapping("/register")
    public String adminRegister(@ModelAttribute Register register){
        return registerService.adminRegister(register);
    }
}
