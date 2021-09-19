package com.coconut.service.auth;

import com.coconut.api.dto.MailDto;
import com.coconut.api.dto.req.*;
import com.coconut.api.dto.res.OAuthUserLoginResDto;
import com.coconut.api.dto.res.UserLoginResDto;
import com.coconut.api.dto.res.UserSaveResDto;
import com.coconut.domain.user.Role;
import com.coconut.domain.user.User;
import com.coconut.domain.user.UserRepository;
import com.coconut.service.utils.encrypt.EncryptHelper;
import com.coconut.service.utils.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final EncryptHelper encryptHelper;

    @Transactional
    public UserSaveResDto saveUser(UserSaveReqDto requestDto) {

        userRepository.save(requestDto.toEntity(
                encryptHelper.encrypt(requestDto.getPassword())));
        return UserSaveResDto.builder()
                .isRegistered(true)
                .build();
    }

    @Transactional
    public UserSaveResDto emailCheck(String email) {
        boolean isPresent = userRepository.findByEmail(email).isPresent();
        return UserSaveResDto.builder()
                .isEmailOk(!isPresent)
                .build();
    }

    @Transactional
    public OAuthUserLoginResDto saveOAuthUser(OAuthUserLoginReqDto requestDto) {

        // 먼저 이미 등록된 유저인지 확인한다. 존재하면 db에서 가져오고, 존재하지 않으면 req.toEntity()로 만든다
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElse(requestDto.toEntity());


        // JPA에서 save는 insert, update의 기능을 가진다.
        userRepository.save(user);

        return OAuthUserLoginResDto.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .build();
    }

    // OAuth가 아닌 ID,PW로 로그인한 경우
    @Transactional
    public UserLoginResDto login(UserLoginReqDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElse(new User());

        boolean isCorrect = false;
        boolean isConfirmed = false;
        String id = null;

        if (user.getEmail() != null) {
            isCorrect = encryptHelper.isMatch(requestDto.getPassword(), user.getPassword());
            id = user.getId().toString();

            if (user.getRoleKey().equals(Role.USER.getKey())) { // 이메일 인증이 된 사용자
                isConfirmed = true;
            } else {
                isConfirmed = false;
                String token = user.updateConfirmToken();
                new Thread(() -> {
                    try {
                        mailService.sendEmail(MailDto.builder()
                                .address("gkguswns3371@gmail.com")
                                //.address(user.getEmail()) : 테스트가 끝나면 이걸로 바꿔야 유저가 등록한 이메일로 메일이 간다.
                                .title("Coconut 이메일 인증")
                                .token(token)
                                .build());
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
        return UserLoginResDto.builder()
                .id(id)
                .isCorrect(isCorrect)
                .isConfirmed(isConfirmed)
                .build();
    }

    @Transactional
    public UserLoginResDto emailVerify(UserEmailVerifyReqDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElse(new User());

        boolean isConfirmed = false;
        boolean isCorrect = false;
        if (user.getConfirmToken() != null) {
            isConfirmed = user.getConfirmToken().equals(requestDto.getSecretToken());
            isCorrect = user.getEmail().equals(requestDto.getEmail());
        }

        if (isConfirmed) {
            user.approveUser(); // Role.GUEST 에서 Role.USER 로 변경
        }

        return UserLoginResDto.builder()
                .id(user.getId().toString())
                .isCorrect(isCorrect)
                .isConfirmed(isConfirmed)
                .build();
    }
}
