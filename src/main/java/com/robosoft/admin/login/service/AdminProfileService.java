package com.robosoft.admin.login.service;

import com.robosoft.admin.login.dao.AdminProfileDataAccess;
import com.robosoft.admin.login.model.Admin;
import com.robosoft.admin.login.model.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AdminProfileService {


    @Autowired
    private AdminProfileDataAccess adminProfileDataAccess;


    public Profile getProfile()
    {
        String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
        return adminProfileDataAccess.getProfile(adminId);
    }
}
