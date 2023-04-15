package com.coconut.auth.presentation;

import com.coconut.crawl.presentation.dto.MailDto;
import com.coconut.base.presentation.dto.BaseResDto;
import com.coconut.auth.presentation.dto.OAuthUserLoginResDto;
import com.coconut.auth.presentation.dto.UserLoginResDto;
import com.coconut.auth.presentation.dto.UserSaveResDto;
import com.coconut.auth.presentation.dto.*;
import com.coconut.user.domain.constant.Role;
import com.coconut.user.domain.entity.User;
import com.coconut.user.application.UserService;
import com.coconut.user.utils.encrypt.EncryptHelper;
import com.coconut.base.utils.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final EncryptHelper encryptHelper;
    private final MailService mailService;


    // OAuth가 아닌 ID,PW로 로그인한 경우
    @PostMapping("/login")
    public ResponseEntity<UserLoginResDto> login(@RequestBody UserLoginReqDto requestDto) {
        Optional<User> optionalUser = userService.findByEmail(requestDto.getEmail());
        if (optionalUser.isEmpty()) {
            log.error("존재하지 않는 유저 email = " + requestDto.getEmail());
            return ResponseEntity.ok(UserLoginResDto.builder()
                    .isCorrect(false)
                    .isConfirmed(false)
                    .build());
        }

        User user = optionalUser.get();
        boolean isCorrect = encryptHelper.isMatch(requestDto.getPassword(), user.getPassword());
        boolean isConfirmed = user.getRoleKey().equals(Role.USER.getKey());

        if (!isConfirmed) {
            String token = userService.updateConfirmToken(user.getId());
            new Thread(() -> {
                try {
                    mailService.sendEmail(MailDto.builder()
                            .address(user.getEmail())
                            .title("Coconut 이메일 인증")
                            .token(token)
                            .build());
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        return ResponseEntity.ok(UserLoginResDto.builder()
                .id(String.valueOf(user.getId()))
                .isCorrect(isCorrect)
                .isConfirmed(isConfirmed)
                .build());
    }

    @PostMapping("/oauth")
    public ResponseEntity<OAuthUserLoginResDto> saveOAuthUser(@RequestBody OAuthUserLoginReqDto requestDto) {
        Optional<User> optionalUser = userService.findByEmail(requestDto.getEmail());
        if (optionalUser.isEmpty()) {
            log.error("존재하지 않는 유저 email = " + requestDto.getEmail());
            User entity = requestDto.toEntity();
            userService.save(entity);
            return ResponseEntity.ok(OAuthUserLoginResDto.builder()
                    .userId(entity.getId())
                    .name(entity.getName())
                    .email(entity.getEmail())
                    .profilePicture(entity.getProfilePicture())
                    .build());
        }
        User user = optionalUser.get();
        userService.update(user.getId(), user);
        return ResponseEntity.ok(OAuthUserLoginResDto.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .build());
    }

    @PostMapping()
    public ResponseEntity<UserSaveResDto> saveUser(@RequestBody UserSaveReqDto requestDto) {

        User user = User.builder()
                .email(requestDto.getEmail())
                .name(requestDto.getName())
                .usrId(requestDto.getUserId())
                .role(Role.GUEST)
                .password(encryptHelper.encrypt(requestDto.getPassword()))
                .build();

        try {
            userService.save(user);
            return ResponseEntity.ok(UserSaveResDto.builder()
                    .isRegistered(true)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(UserSaveResDto.builder()
                    .isRegistered(false)
                    .build());
        }

    }

    @PostMapping("/email-check")
    public ResponseEntity<UserSaveResDto> emailCheck(@RequestBody UserEmailCheckReqDto reqDto) {
        boolean emailCheck = userService.checkByEmail(reqDto.getEmail());
        return ResponseEntity.ok(UserSaveResDto.builder()
                .isEmailOk(!emailCheck)
                .build());
    }

    @PostMapping("/email-verify")
    public ResponseEntity<UserLoginResDto> emailVerify(@RequestBody UserEmailVerifyReqDto requestDto) {
        Optional<User> optionalUser = userService.findByEmail(requestDto.getEmail());
        if (optionalUser.isEmpty()) {
            log.error("존재하지 않는 유저 email = " + requestDto.getEmail());
            return ResponseEntity.ok(UserLoginResDto.builder()
                    .isCorrect(false)
                    .isConfirmed(false)
                    .build());
        }

        User user = optionalUser.get();
        boolean isConfirmed = false;
        boolean isCorrect = false;

        if (user.getConfirmToken() != null) {
            isConfirmed = user.getConfirmToken().equals(requestDto.getSecretToken());
            isCorrect = user.getEmail().equals(requestDto.getEmail());
        }

        if (isConfirmed) {
            userService.approveUser(user.getId()); // Role.GUEST 에서 Role.USER 로 변경
        }

        return ResponseEntity.ok(UserLoginResDto.builder()
                .id(user.getId().toString())
                .isCorrect(isCorrect)
                .isConfirmed(isConfirmed)
                .build());
    }

    @PostMapping("/fcm")
    public ResponseEntity<BaseResDto> updateFcmToken(@RequestBody UserFcmUpdateReqDto reqDto) {
        Optional<User> optionalUser = userService.findById(Long.valueOf(reqDto.getUserId()));
        if (optionalUser.isEmpty()) {
            return ResponseEntity.ok(BaseResDto.builder()
                    .success(false)
                    .message("존재하지 않은 유저입니다.")
                    .build());
        }
        userService.updateFcmToken(Long.valueOf(reqDto.getUserId()), reqDto.getFcmToken());

        return ResponseEntity.ok(BaseResDto.builder()
                .success(true)
                .message("FCM Token 업데이트 성공")
                .build());
    }


}
