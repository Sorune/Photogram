package com.sorune.photogram.Security.Handler;


import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.sorune.photogram.Security.Domain.TokenVO;
import com.sorune.photogram.Security.Entity.User;
import com.sorune.photogram.Security.JWT.TokenProvider;
import com.sorune.photogram.Security.Repository.UserRepository;
import com.sorune.photogram.Security.Service.UserPrincipal;
import com.sorune.photogram.Security.Util.DurationAdapter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.Duration;


@Log4j2
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    public APILoginSuccessHandler(TokenProvider tokenProvider, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        log.info("authorites : " + userPrincipal.getAuthorities());
        User user = userRepository.findByUsername(userPrincipal.getUsername());
        user.setPassword("");
        log.info("user : " + user.toString());
        userPrincipal.setUser(user);
        log.info("user principal : " + userPrincipal);
        log.info("principal user : " + userPrincipal.getUser());
        TokenVO tokenVO = tokenProvider.createTokenVO(user.getUsername(), userPrincipal.getUser().getRoles());
        log.info(tokenVO);
        Gson gson = new GsonBuilder().registerTypeAdapter(Duration.class, new DurationAdapter()).create();

        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(gson.toJson(tokenVO));
    }
}
