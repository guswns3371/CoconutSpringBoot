package com.coconut.client;

import com.coconut.client.dto.req.UserSaveRequestDto;
import com.coconut.client.dto.res.UserSaveResponseDto;
import com.coconut.config.auth.LoginUser;
import com.coconut.config.auth.dto.SessionUser;
import com.coconut.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class AuthController {

    /**
     *  logger.trace("Trace Level 테스트");
     *  logger.debug("DEBUG Level 테스트");
     *  logger.info("INFO Level 테스트");
     *  logger.warn("Warn Level 테스트");
     *  logger.error("ERROR Level 테스트");
     */

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AuthService authService;

    @PostMapping("/register")
    public UserSaveResponseDto save(@RequestBody UserSaveRequestDto requestDto) {

        logger.error("@PostMapping(\"/register\") : "+requestDto.toString());
        return authService.save(requestDto);
    }

    @PostMapping("/register/{email}")
    public UserSaveResponseDto emailCheck(@PathVariable String email) {
        logger.error("@PostMapping(\"/register/{email}\") : "+email);
        return authService.emailCheck(email);
    }

    @GetMapping("/")
    public SessionUser sessionCheck(@LoginUser SessionUser user){
        logger.error("get /");
        return user;
    }
}
