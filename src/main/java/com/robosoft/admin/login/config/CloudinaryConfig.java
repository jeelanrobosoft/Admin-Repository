package com.robosoft.admin.login.config;


import com.cloudinary.Cloudinary;
import com.cloudinary.EagerTransformation;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
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

    public static  String uploadVideo(MultipartFile video) throws IOException {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "do52xyv54",
                "api_key", "815687279696268",
                "api_secret", "EBPsxcDsTwxLZxJ6jjtUaAKv1EU",
                "secure", "true"));
        cloudinary.config.secure = true;

        Map params1 = ObjectUtils.asMap(
                "use_filename", true,
                "unique_filename", true,
                "overwrite", false
        );
        Map url =cloudinary.uploader().upload(video.getBytes(),
                ObjectUtils.asMap("resource_type", "video",
                        "public_id", "myfolder/mysubfolder/dog_closeup",
                        "eager", Arrays.asList(
                                new EagerTransformation().width(300).height(300).crop("pad").audioCodec("none"),
                                new EagerTransformation().width(160).height(100).crop("crop").gravity("south").audioCodec("none")),
                        "eager_async", true,
                        "eager_notification_url", "https://mysite.example.com/notify_endpoint"));
        return url.get("secure_url").toString();
    }



}
