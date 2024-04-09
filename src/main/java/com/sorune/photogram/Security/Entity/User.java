package com.sorune.photogram.Security.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

@Builder
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String username;    //아이디
    private String password;    //비밀번호

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private String roles = "ROLE_MEMBER";

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    private String name;
    private String nickName;
    private String permissions = "";
    private String email;
    private String zoneCode;
    private String roadAddress;
    private String buildingName;
    private String address;
    private String phone;
    private String instaURL;

    public User(String subject /*OAuth2의 경우 email로 전달받음*/, String s, Collection<? extends GrantedAuthority> authorities) {
        this.username = subject;
        this.roles = authorities.stream().toString();
    }

    public void addRole(UserRole role) {
        userRoles.add(role);
    }

    public List<String> getRoleList(){
        if(!this.roles.isEmpty()){
            return Arrays.asList(this.roles.split(","));
        }
        return new ArrayList<>();
    }

    public List<String> getPermissionList() {
        if (!this.permissions.isEmpty()) {
            return Arrays.asList(this.permissions.split(","));
        }
        return new ArrayList<>();
    }
}
