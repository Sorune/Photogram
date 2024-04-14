package com.sorune.photogram.Security.Configuration;

import com.sorune.photogram.Security.Handler.APILoginSuccessHandler;
import com.sorune.photogram.Security.JWT.JwtAccessDeniedHandler;
import com.sorune.photogram.Security.JWT.JwtAuthenticationEntryPoint;
import com.sorune.photogram.Security.JWT.JwtFilter;
import com.sorune.photogram.Security.JWT.TokenProvider;
import com.sorune.photogram.Security.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Log4j2
public class SecurityConfiguration {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private static final String[] URL_TO_PERMIT = {
            "/member/login",
            "/member/signup",
            "/auth/**",
            "/photos/**",

    };
    @Bean
    public WebSecurityCustomizer configure(){
        //리소스 시큐리티 예외 처리
        return (web) -> web.ignoring()
                .requestMatchers(new AntPathRequestMatcher("/css/**"))
                .requestMatchers(new AntPathRequestMatcher("/vender/**"))
                .requestMatchers(new AntPathRequestMatcher("/favicon.ico"));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((auth)-> {
                    AntPathRequestMatcher.antMatcher("/static/**");
                    AntPathRequestMatcher.antMatcher("/");
                    auth.requestMatchers(URL_TO_PERMIT).permitAll()
                            .requestMatchers(AntPathRequestMatcher.antMatcher("/**")).permitAll()
                            .anyRequest().authenticated();
                }
            )
            //커스텀 로그인 처리
            .formLogin((formLogin)->formLogin.loginPage("/login")
                    .successHandler(new APILoginSuccessHandler(tokenProvider, userRepository))
                    //로그인 성공시 기본 리다이렉트 동작. defaultSuccessUrl이 설정되어 있으면 SuccessHandler동작 안함
                    /*.defaultSuccessUrl("/home")*/
            )
            //로그아웃 처리
            .logout((logout)->logout.logoutSuccessUrl("/home")
                    .invalidateHttpSession(true).deleteCookies())

            .csrf(AbstractHttpConfigurer::disable)//csrf설정 끔
            .sessionManagement((sessionManagement)->sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS).disable())//jwt를 사용하는 STATELESS방식이므로 session 사용하지 않는다고 명시
            //예외처리 핸들러 설정
            .exceptionHandling((exception)->exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    .accessDeniedHandler(jwtAccessDeniedHandler)
            ).anonymous((anonymous) ->
                    anonymous
                            .authorities("ROLE_ANONYMOUS") // 익명 사용자에게 부여할 권한
            );
        //OAuth2 인증 정보 추가
/*      http.oauth2Login(oauth2->oauth2.successHandler(oAuth2SuccessHandler).userInfoEndpoint().userService(oAuth2UserService));*/
        //jwt필터를 usernamepassword인증 이전에 실행
        http.addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

        log.info("securityConfig");
        return http.build();
    }
}
