package com.coconut.client;

import com.coconut.client.dto.req.OAuthUserLoginReqDto;
import com.coconut.client.dto.req.UserEmailVerifyReqDto;
import com.coconut.client.dto.req.UserLoginReqDto;
import com.coconut.client.dto.req.UserSaveReqDto;
import com.coconut.client.dto.res.OAuthUserLoginResDto;
import com.coconut.client.dto.res.UserLoginResDto;
import com.coconut.client.dto.res.UserSaveResDto;
import com.coconut.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *  logger.trace("Trace Level 테스트");
 *  logger.debug("DEBUG Level 테스트");
 *  logger.info("INFO Level 테스트");
 *  logger.warn("Warn Level 테스트");
 *  logger.error("ERROR Level 테스트");
 */

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/api/auth/register")
    public UserSaveResDto saveUser(@RequestBody UserSaveReqDto requestDto) {
        return authService.saveUser(requestDto);
    }

    @PostMapping("/api/auth/register/{email}")
    public UserSaveResDto emailCheck(@PathVariable String email) {
        return authService.emailCheck(email);
    }

    @PostMapping("/api/auth/user/info")
    public OAuthUserLoginResDto saveOAuthUser(@RequestBody OAuthUserLoginReqDto requestDto) {
        return authService.saveOAuthUser(requestDto);
    }

    @PostMapping("/api/auth/login")
    public UserLoginResDto login(@RequestBody UserLoginReqDto requestDto) {
        return authService.login(requestDto);
    }

    @PostMapping("/api/auth/login/verify")
    public UserLoginResDto emailVerify(@RequestBody UserEmailVerifyReqDto requestDto) {
        return authService.emailVerify(requestDto);
    }
}
