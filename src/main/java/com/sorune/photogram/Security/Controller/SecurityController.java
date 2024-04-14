package com.sorune.photogram.Security.Controller;

import com.sorune.photogram.Security.Domain.UserVO;
import com.sorune.photogram.Security.Entity.User;
import com.sorune.photogram.Security.Repository.UserRepository;
import com.sorune.photogram.Security.Service.UserPrincipal;
import com.sorune.photogram.Security.Service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/")
@Log4j2
public class SecurityController {

    private final UserService userService;
    private final UserRepository userRepository;

    public SecurityController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "member/login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/";
    }

    @GetMapping("/register")
    public String register() { return "member/register"; }

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        log.info("register user : " + user);
        User savedUser = userService.save(user);
        log.info("저장된 유저 정보 : "+savedUser);
        return "redirect:/login";
    }

    @GetMapping("/api/userinfo")
    @ResponseBody
    public ResponseEntity<UserVO> getUserInfo(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) Optional.ofNullable(authentication)
                .map(Authentication::getPrincipal)
                .orElse(null);

        if (principal == null) {
            // Authentication 정보가 없을 경우 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info(principal.getUser());
        UserVO userInfoResponse = principal.getUser().entityToVO();
        return ResponseEntity.ok(userInfoResponse);
    }
}
