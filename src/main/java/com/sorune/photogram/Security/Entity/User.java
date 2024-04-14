package com.sorune.photogram.Security.Entity;

import com.sorune.photogram.Security.Domain.UserVO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

@Builder
@Entity
@Getter
@Setter
@ToString(exclude = "userRoles")
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

    public UserVO entityToVO(){
        return UserVO.builder()
                .id(getId())
                .username(getUsername())
                .password(getPassword())
                .active(isActive())
                .roles(getRoles())
                .name(getName())
                .nickName(getNickName())
                .zoneCode(getZoneCode())
                .roadAddress(getRoadAddress())
                .buildingName(getBuildingName())
                .address(getAddress())
                .phone(getPhone())
                .instaURL(getInstaURL())
                .build();
    }

    public User voToEntity(UserVO userVO){
        return User.builder()
                .id(userVO.getId())
                .username(userVO.getUsername())
                .password(userVO.getPassword())
                .active(userVO.isActive())
                .roles(userVO.getRoles())
                .name(userVO.getName())
                .nickName(userVO.getNickName())
                .phone(userVO.getPhone())
                .email(userVO.getEmail())
                .zoneCode(userVO.getZoneCode())
                .roadAddress(userVO.getRoadAddress())
                .buildingName(userVO.getBuildingName())
                .address(userVO.getAddress())
                .instaURL(userVO.getInstaURL())
                .build();
    }
}
