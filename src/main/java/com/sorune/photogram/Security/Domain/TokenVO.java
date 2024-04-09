package com.sorune.photogram.Security.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenVO {
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private Duration duration;
}
