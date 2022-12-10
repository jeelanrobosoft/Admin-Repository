package com.robosoft.admin.login.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.robosoft.admin.login.dao.RegisterDao;
import com.robosoft.admin.login.model.Register;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RegisterService {

    @Autowired
    private RegisterDao registerDao;


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
            String url = uploadResult.get("secure_url").toString();

            return url;
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
        if(!matcher.matches())
        {
            return "Invalid EmailId";
        }
        if(register.getProfilePhoto() != null)
        {
           uploadProfilePhoto(register.getProfilePhoto());


        }
        Pattern ptrn = Pattern.compile("(0/91)?[6-9][0-9]{9}");

        Matcher phMatcher = ptrn.matcher(register.getMobileNumber());
        if(!phMatcher.matches())
        {
            return "Invalid Mobile Number";
        }
        try {
            registerDao.adminRegister(register);
            return "Registration Request Sent You Will Receive the Mail ASAP.";
        }
        catch (Exception e)
        {
            return "Registration Failed";
        }
    }
}
