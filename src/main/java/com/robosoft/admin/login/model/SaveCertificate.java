package com.robosoft.admin.login.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveCertificate {
    private Integer courseId;
    private MultipartFile certificate;
    private String userName;
}
