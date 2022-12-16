package com.robosoft.admin.login.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.robosoft.admin.login.dao.AdminDao;
import com.robosoft.admin.login.model.Register;
import com.robosoft.admin.login.model.ResetPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RegisterService {

    @Autowired
    AdminDao adminDao;


    public String uploadProfilePhoto(MultipartFile profilePhoto)
    {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dbmgzhnzv",
                "api_key", "517396485856626",
                "api_secret", "iJJQWYkddrRz8DA_MRg01ZYXXbk",
                "secure", "true"));
        cloudinary.config.secure = true;
        try
        {
            // Upload the image
            Map params1 = ObjectUtils.asMap(
                    "use_filename", true,
                    "unique_filename", true,
                    "overwrite", false
            );
            Map uploadResult = cloudinary.uploader().upload(profilePhoto.getBytes(), params1);
            //String publicId = uploadResult.get("public_id").toString();

            return uploadResult.get("secure_url").toString();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public String adminRegister(Register register) {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(register.getEmailId());
        String url = null;
        if(!matcher.matches())
        {
            return "Invalid EmailId";
        }

//        if(register.getProfilePhoto() != null)
//        {
////           url = uploadProfilePhoto(register.getProfilePhoto());
//        }
        Pattern ptrn = Pattern.compile("^\\+(?:[0-9] ?){6,14}[0-9]$");


        Matcher phMatcher = ptrn.matcher(register.getMobileNumber());
        if(!phMatcher.matches())
        {
            return "Invalid Mobile Number";
        }
        try {
            register.getEmailId().matches(adminDao.getAdminEmailId(register.getEmailId()));
            if(!adminDao.getAdminApprovalStatus(register.getEmailId())) {
                adminDao.updateRegistration(register,url);
            }
            return "Email Id already Exists";
        }
        catch (Exception exception) {
            try {
                adminDao.adminRegister(register, url);
                return "Registration Request Sent You Will Receive the Mail ASAP.";
            } catch (Exception e) {
                return "Registration Failed";
            }
        }
    }

    public String resetPassword(ResetPassword resetPassword) {
        try {
            String emailId = adminDao.getAdminEmailId(resetPassword.getEmailId());
            if(emailId != null)
                adminDao.resetPassword(emailId,new BCryptPasswordEncoder().encode(resetPassword.getPassword()));
            return "Password Updated Successfully.";
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            return "Invalid Email Id";
        }
    }
}
