package com.sorune.photogram.Security.Controller;

import com.sorune.photogram.Security.Entity.User;
import com.sorune.photogram.Security.Service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@Log4j2
public class SecurityController {

    private final UserService userService;

    public SecurityController(UserService userService) {
        this.userService = userService;
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

}
