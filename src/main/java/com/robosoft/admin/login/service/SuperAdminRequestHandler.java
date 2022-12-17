package com.robosoft.admin.login.service;

import com.robosoft.admin.login.dao.SuperAdminApproval;
import com.robosoft.admin.login.model.Admin;
import com.robosoft.admin.login.model.AdminDashBoardDetails;
import com.robosoft.admin.login.model.Register;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuperAdminRequestHandler {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    private SuperAdminApproval superAdminApproval;
    private static Integer randomNumber = 100;
    public String approveRequest(Admin admin)
    {
        randomNumber++;
        String[] fullName= admin.getFullName().split(" ");
        String password = "Vqm"+fullName[0]+"lR"+randomNumber;
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("virtuallearn2022@gmail.com");
        email.setTo(admin.getEmailId());
        email.setSubject("Virtual Learn");
        email.setText("Hello "+admin.getFullName()+".\n"+" Your request for Virtual Learn admin role has been APPROVED...\n Login to dashboard using the password\n password: "+password);
        javaMailSender.send(email);
        superAdminApproval.addToAuthenticate(admin,password);
        superAdminApproval.updateStatus(admin.getEmailId());
        return "Approved SuccessFully";
    }

    public String rejectRequest(Admin admin)
    {
       Integer response = superAdminApproval.deleteFromAuthenticate(admin);
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("virtuallearn2022@gmail.com");
        email.setTo(admin.getEmailId());
        email.setSubject("Virtual Learn");
        email.setText("Hello "+admin.getFullName()+".\n"+" Your request for Virtual Learn admin role has been REJECTED...");
       if(response == 1)
       {
//           SimpleMailMessage emailId = new SimpleMailMessage();
//           email.setFrom("virtuallearn2022@gmail.com");
//           email.setTo(admin.getEmailId());
//           email.setSubject("Virtual Learn");
//           email.setText("Hello "+admin.getFullName()+".\n"+" Your request for Virtual Learn admin role has been REJECTED...\n Try again......");
           javaMailSender.send(email);
           return "Rejected";
       }
       return null;
    }

    public AdminDashBoardDetails getAdminDetails() {
        AdminDashBoardDetails details = superAdminApproval.getAdminCountAndCoursesAdded();
        List<Register> adminDetails = superAdminApproval.getDetails();
        if(adminDetails != null){
            details.setListOfAdmins(adminDetails);
            return details;
        }
        return details;
    }

    public List<Register> getDeletedAdminDetails() {
        return superAdminApproval.deletedAdminList();
    }
}

