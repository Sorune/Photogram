package com.sorune.photogram.Security.JWT;

import com.sorune.photogram.Security.Domain.TokenVO;
import com.sorune.photogram.Security.Entity.User;
import com.sorune.photogram.Security.Service.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Log4j2
public class TokenProvider {
    private static final String BEARER_TYPE = "Bearer";

    private final Long accessTokenValidationTime;   //30분
    private final Long refreshTokenValidationTime;  //7일

    private final Key key;

    public TokenProvider(@Value("${jwt.accessTokenValidationTime}") long accessTokenValidationTime, @Value("${jwt.refreshTokenValidationTime}") long refreshTokenValidationTime){
        this.accessTokenValidationTime = accessTokenValidationTime;
        this.refreshTokenValidationTime = refreshTokenValidationTime;
        // 새로운 키 생성
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    /**
     * accessToken과 refreshToken을 생성함
     * @param subject
     * @return TokenDTO
     * subject는 Form Login방식의 경우 userId, Social Login방식의 경우 email
     */
    public TokenVO createTokenVO(String subject, String authority) {

        //토큰 생성시간
        Instant now = Instant.from(OffsetDateTime.now());
        //accessToken 만료시간
        Instant refreshTokenExpirationDate = now.plusMillis(refreshTokenValidationTime);

        //accessToken 생성
        String accessToken = Jwts.builder()
                .setSubject(subject)
                .claim("roles", authority)
                .setExpiration(Date.from(now.plusMillis(accessTokenValidationTime)))
                .signWith(key)
                .compact();

        //refreshToken 생성
        String refreshToken = Jwts.builder()
                .setExpiration(Date.from(now.plusMillis(refreshTokenValidationTime)))
                .signWith(key)
                .compact();

        // refreshTokenValidationTime을 밀리초에서 분으로 변환
        long refreshTokenValidationTimeInMinutes = refreshTokenValidationTime / (1000 * 60);

        //TokenVO에 두 토큰을 담아서 반환
        return TokenVO.builder()
                .tokenType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .duration(Duration.ofMinutes(refreshTokenValidationTimeInMinutes))
                .build();
    }

    public Authentication getAuthentication(String token){
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

            if (claims != null && claims.get("roles") != null) {
                Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("roles").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UserPrincipal principal = new UserPrincipal(new User(claims.getSubject(), "", authorities));

                return new UsernamePasswordAuthenticationToken(principal, null, authorities);
            } else {
                log.error("Invalid claims in JWT token");
                return null;
            }
        } catch (Exception e) {
            log.error("Error parsing JWT token", e);
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰입니다");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (MalformedJwtException |SignatureException e) {
            log.info("잘못된 JWT 서명입니다");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
