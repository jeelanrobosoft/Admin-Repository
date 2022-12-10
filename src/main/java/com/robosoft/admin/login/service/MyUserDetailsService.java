package com.robosoft.admin.login.service;

import com.robosoft.admin.login.model.UserAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            UserAuth auth = jdbcTemplate.queryForObject("select * from authenticate where emailId='" + username + "'", new BeanPropertyRowMapper<>(UserAuth.class));
            return new MyUserDetailsImpl(auth);
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found");
        }

    }
}