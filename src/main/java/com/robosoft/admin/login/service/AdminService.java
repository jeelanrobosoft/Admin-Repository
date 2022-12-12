package com.robosoft.admin.login.service;

import com.robosoft.admin.login.dao.AdminDao;
import com.robosoft.admin.login.dto.StudentList;
import com.robosoft.admin.login.model.ChangePassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    AdminDao adminDao;

    @Value("${page.data.count}")
    private int perPageDataCount;
    public String changePassword(ChangePassword changePassword) {
        String emailId = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            String oldPassword = adminDao.checkPassword(emailId);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if(encoder.matches(changePassword.getOldPassword(),oldPassword)) {
                if(encoder.matches(changePassword.getNewPassword(),oldPassword))
                    return "Password Should not be same as Old Password";
                adminDao.resetPassword(emailId, new BCryptPasswordEncoder().encode(changePassword.getNewPassword()));
                return "Password Changed Successfully";
            }
            return "Incorrect Password";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Incorrect Password.";
        }
    }

    public List<Long> getOffsetUsingCustomLimit(int pageNumber, long limit) {
        List<Long> list = new ArrayList<>();

        if (pageNumber < 1)
            pageNumber = 1;

        if (limit < 1)
            limit = perPageDataCount;

        list.add(limit);
        list.add(limit * (pageNumber - 1));
        return list;
    }


    public List<StudentList> getStudentList(int pageNumber, int pageLimit) {
        String emailId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Long> list = this.getOffsetUsingCustomLimit(pageNumber, pageLimit);
        long limit = list.get(0);
        long offset = list.get(1);
        return adminDao.getStudentList(emailId,offset,limit);
    }
}
