package com.sorune.photogram.Security.Domain;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVO{
    private Long id;
    private String username;
    private String password;
    private boolean active;
    private String roles;
    private String name;
    private String nickName;
    private String email;
    private String zoneCode;
    private String roadAddress;
    private String buildingName;
    private String address;
    private String phone;
    private String instaURL;

}
