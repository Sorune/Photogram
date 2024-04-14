package com.sorune.photogram.Security.Handler;

import com.nimbusds.jose.shaded.gson.Gson;
import com.sorune.photogram.Security.Domain.TokenVO;
import com.sorune.photogram.Security.Entity.User;
import com.sorune.photogram.Security.JWT.TokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Log4j2
public class APILoginSUccessHandler implements AuthenticationSuccessHandler {
    @Setter(onMethod_ = @Autowired)
    private TokenProvider tokenProvider;
    //로그인 성공시 실행할 로직
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        TokenVO tokenVO = tokenProvider.createTokenVO(user.getUsername(), user.getPassword());
        log.info(tokenVO.toString());
        Gson gson = new Gson();

        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(gson.toJson(tokenVO));

    }
}
