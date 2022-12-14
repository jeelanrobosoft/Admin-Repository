package com.robosoft.admin.login.controller;


import com.robosoft.admin.login.model.*;
import com.robosoft.admin.login.service.LoginService;
import com.robosoft.admin.login.service.MyUserDetailsService;
import com.robosoft.admin.login.service.RegisterService;
import com.robosoft.admin.login.utility.JwtUtility;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"},exposedHeaders = {"jwt-token","refreshToken"})
public class LoginController {


    @Autowired
    private JwtUtility jwtUtility;
    @Autowired
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private MyUserDetailsService myUserDetailsService;
    @Autowired
    private LoginService loginService;
    @Autowired
    RegisterService registerService;

    private String otpSendEmailId;

    @PutMapping("/login")
    public ResponseEntity<?> login(@RequestBody Authenticate adminAuth) throws Exception {
        try {
            authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(adminAuth.getUserName(), adminAuth.getPassword()));
        } catch (DisabledException e) {
            throw new Exception("User Disabled");
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid Credentials");
        }
        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(adminAuth.getUserName());
        final String token = jwtUtility.generateToken(userDetails);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
        String authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        LoginStatus loginStatus = new LoginStatus();
        loginStatus.setStatus("login successfully");
        loginStatus.setRole(authorities);
        HttpHeaders headers = new HttpHeaders();
        headers.set("jwt-token",token);
        return ResponseEntity.ok().headers(headers).body(loginStatus);
    }


    @PostMapping("/send")
    public ResponseEntity<?> sendEmailOtp(@RequestBody EmailId emailId) {
        String status = loginService.checkForEmail(emailId);
        if (status != null)
            return new ResponseEntity<>(Collections.singletonMap("message", status), HttpStatus.NOT_FOUND);
        loginService.deletePreviousOtp(emailId);
        long otpStatus = loginService.sendOtp(emailId);
        otpSendEmailId = emailId.getEmailId();
        return new ResponseEntity<>(Collections.singletonMap("message", "OTP Valid for " + otpStatus + " Mins"), HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerification verification) {
        if (!(verification.getEmailId().equalsIgnoreCase(otpSendEmailId)))
            return new ResponseEntity<>(Collections.singletonMap("Error", "Enter a valid email id"), HttpStatus.NOT_ACCEPTABLE);
        String verificationStatus = loginService.verifyOtp(verification);
        if (verificationStatus.equals("Verified"))
            return new ResponseEntity<>(Collections.singletonMap("status", verificationStatus), HttpStatus.OK);
        return new ResponseEntity<>(Collections.singletonMap("status", verificationStatus), HttpStatus.NOT_ACCEPTABLE);
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        DefaultClaims claims = (io.jsonwebtoken.impl.DefaultClaims) request.getAttribute("claims");
        Map<String, Object> expectedMap = getMapFromIoJsonwebtokenClaims(claims);
        if(expectedMap == null)
            return new ResponseEntity<>(Collections.singletonMap("Error" , "Token Not Expired"), HttpStatus.NOT_ACCEPTABLE);
        String token = jwtUtility.doGenerateRefreshToken(expectedMap, expectedMap.get("sub").toString());
        HttpHeaders headers = new HttpHeaders();
        headers.set("refreshToken",token);
        return ResponseEntity.ok().headers(headers).body(Collections.singletonMap("status","Refresh Token Generated"));
    }

    public Map<String, Object> getMapFromIoJsonwebtokenClaims(DefaultClaims claims) {
        Map<String, Object> expectedMap = new HashMap<>();
        try{
            for (Map.Entry<String, Object> entry : claims.entrySet()) {
                expectedMap.put(entry.getKey(), entry.getValue());
            }
            return expectedMap;
        } catch (NullPointerException e){
            return null;
        }
    }


    @PostMapping("/resend")
    public ResponseEntity<?> resendOtp(@RequestBody EmailId emailId){
        String status = loginService.checkForEmail(emailId);
        if (status != null)
            return new ResponseEntity<>(Collections.singletonMap("message", status), HttpStatus.NOT_FOUND);
        loginService.deletePreviousOtp(emailId);
        long otpStatus = loginService.sendOtp(emailId);
        otpSendEmailId = emailId.getEmailId();
        return new ResponseEntity<>(Collections.singletonMap("message", "OTP Valid for " + otpStatus + " Mins"), HttpStatus.OK);
    }

    @PutMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPassword resetPassword){
        if (!(resetPassword.getEmailId().equalsIgnoreCase(otpSendEmailId)))
            return new ResponseEntity<>(Collections.singletonMap("Error", "Enter a valid email id"), HttpStatus.OK);
        String s = registerService.resetPassword(resetPassword);
        if(s != null)
            return new ResponseEntity<>(Collections.singletonMap("message",s), HttpStatus.OK);
        return new ResponseEntity<>(Collections.singletonMap("message","Something Went Wrong"),HttpStatus.OK);
    }



    @Scheduled(fixedRate = 3600000)
    public void eventScheduler() {
        otpSendEmailId = null;
    }

//    @GetMapping("/dummy")
//    public String get() {
//        jdbcTemplate.update("insert into admin(adminUserName,fullName,mobileNumber,emailId) values('SuperAdmin','Sharath','+918970802687','chandanakgowda33@gmail.com')");
//        jdbcTemplate.update("insert into authenticate values('SuperAdmin','" + new BCryptPasswordEncoder().encode("SuperAdmin@123") + "','ROLE_SUPER_ADMIN')");
//        return "iam dummy";
//    }


}