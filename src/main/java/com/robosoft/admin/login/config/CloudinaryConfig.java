package com.robosoft.admin.login.config;


import com.cloudinary.utils.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public class CloudinaryConfig {

    public static String uploadProfilePhoto(MultipartFile profilePhoto)
    {
        com.cloudinary.Cloudinary cloudinary = new com.cloudinary.Cloudinary(ObjectUtils.asMap(
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
            System.out.println(url);

            return url;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }


}
