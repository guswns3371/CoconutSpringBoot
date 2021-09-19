package com.coconut.api;

import com.coconut.api.dto.req.*;
import com.coconut.api.dto.res.BaseResDto;
import com.coconut.api.dto.res.OAuthUserLoginResDto;
import com.coconut.api.dto.res.UserLoginResDto;
import com.coconut.api.dto.res.UserSaveResDto;
import com.coconut.service.auth.AuthService;
import com.coconut.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public UserSaveResDto saveUser(@RequestBody UserSaveReqDto requestDto) {
        return authService.saveUser(requestDto);
    }

    @PostMapping("/register/{email}")
    public UserSaveResDto emailCheck(@PathVariable String email) {
        return authService.emailCheck(email);
    }

    @PostMapping("/user/info")
    public OAuthUserLoginResDto saveOAuthUser(@RequestBody OAuthUserLoginReqDto requestDto) {
        return authService.saveOAuthUser(requestDto);
    }

    @PostMapping("/login")
    public UserLoginResDto login(@RequestBody UserLoginReqDto requestDto) {
        return authService.login(requestDto);
    }

    @PostMapping("/login/verify")
    public UserLoginResDto emailVerify(@RequestBody UserEmailVerifyReqDto requestDto) {
        return authService.emailVerify(requestDto);
    }

    @PostMapping("/user/fcm")
    public BaseResDto updateFcmToken(@RequestBody UserFcmUpdateReqDto reqDto) {
        return userService.updateFcmToken(reqDto);
    }
}
