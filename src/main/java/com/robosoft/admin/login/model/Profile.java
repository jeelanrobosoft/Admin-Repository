
package com.robosoft.admin.login.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    private String fullName;
    private String emailId;
    private String mobileNumber;
    private String profilePhoto;
}
