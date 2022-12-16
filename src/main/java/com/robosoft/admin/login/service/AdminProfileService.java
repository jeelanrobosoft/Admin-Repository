package com.robosoft.admin.login.service;

import com.robosoft.admin.login.dao.AdminProfileDataAccess;
import com.robosoft.admin.login.model.Admin;
import com.robosoft.admin.login.model.Profile;
import com.robosoft.admin.login.model.ProfileDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static com.robosoft.admin.login.config.CloudinaryConfig.uploadProfilePhoto;
import static com.robosoft.admin.login.service.CourseService.checkStringContainsNumberOrNot;

@Service
public class AdminProfileService {


    @Autowired
    private AdminProfileDataAccess adminProfileDataAccess;


    public Profile getProfile()
    {
        String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
        return adminProfileDataAccess.getProfile(adminId);
    }
    public String saveProfileDetails(ProfileDetails details) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        String profilePhoto = null;
        String fullName = null;
        String mobileNumber = null;
        Integer status = checkStringContainsNumberOrNot(details.getFullName());
        if (status == -1)
            return "FullName name cannot be empty";

        if (status == 1)
            return "FullName should not contain digits";
        if(!(details.getFullName().length()>=5))
            return "FullName should more than 5 chars";
        fullName = details.getFullName();
        try{
            details.getProfilePhoto().isEmpty();
            profilePhoto = uploadProfilePhoto(details.getProfilePhoto());
        } catch (Exception e){}
        if(details.getMobileNumber() == null)
            return  "Mobile Number cannot be empty";
        if(details.getMobileNumber().isEmpty())
            return  "Mobile Number cannot be empty";
        if(!(details.getMobileNumber().startsWith("+91")))
            return  "Invalid Mobile Number";
        mobileNumber = details.getMobileNumber();
        adminProfileDataAccess.saveDetails(userName,fullName,profilePhoto,mobileNumber);
        return null;
    }
}
