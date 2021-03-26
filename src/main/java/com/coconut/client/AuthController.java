package com.coconut.client;

import com.coconut.client.dto.req.OAuthUserLoginRequestDto;
import com.coconut.client.dto.req.UserLoginRequestDto;
import com.coconut.client.dto.req.UserSaveRequestDto;
import com.coconut.client.dto.res.OAuthUserLoginResponseDto;
import com.coconut.client.dto.res.UserLoginResponseDto;
import com.coconut.client.dto.res.UserSaveResponseDto;
import com.coconut.config.auth.LoginUser;
import com.coconut.config.auth.dto.SessionUser;
import com.coconut.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 *  logger.trace("Trace Level 테스트");
 *  logger.debug("DEBUG Level 테스트");
 *  logger.info("INFO Level 테스트");
 *  logger.warn("Warn Level 테스트");
 *  logger.error("ERROR Level 테스트");
 */

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AuthService authService;

    @PostMapping("/api/auth/register")
    public UserSaveResponseDto save(@RequestBody UserSaveRequestDto requestDto) {
        return authService.save(requestDto);
    }

    @PostMapping("/api/auth/register/{email}")
    public UserSaveResponseDto emailCheck(@PathVariable String email) {
        return authService.emailCheck(email);
    }

    @PostMapping("/api/auth/user/info")
    public OAuthUserLoginResponseDto saveOAuthUser(@RequestBody OAuthUserLoginRequestDto requestDto) {
        return authService.saveOAuthUser(requestDto);
    }

    @PostMapping("/api/auth/login")
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto requestDto) {
        return authService.login(requestDto);
    }
}
