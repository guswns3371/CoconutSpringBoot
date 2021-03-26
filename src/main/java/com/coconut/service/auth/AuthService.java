package com.coconut.service.auth;

import com.coconut.client.dto.req.OAuthUserLoginRequestDto;
import com.coconut.client.dto.req.UserLoginRequestDto;
import com.coconut.client.dto.req.UserSaveRequestDto;
import com.coconut.client.dto.res.OAuthUserLoginResponseDto;
import com.coconut.client.dto.res.UserLoginResponseDto;
import com.coconut.client.dto.res.UserSaveResponseDto;
import com.coconut.domain.user.Role;
import com.coconut.domain.user.User;
import com.coconut.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;

    @Transactional
    public UserSaveResponseDto save(UserSaveRequestDto requestDto) {
        userRepository.save(requestDto.toEntity());
        return UserSaveResponseDto.builder()
                .isRegistered(true)
                .build();
    }

    @Transactional
    public UserSaveResponseDto emailCheck(String email) {
        boolean isPresent = userRepository.findByEmail(email).isPresent();
        return UserSaveResponseDto.builder()
                .isEmailOk(!isPresent)
                .build();
    }

    @Transactional
    public OAuthUserLoginResponseDto saveOAuthUser(OAuthUserLoginRequestDto requestDto) {

        // 먼저 이미 등록된 유저인지 확인한다. 존재하면 db에서 가져오고, 존재하지 않으면 req.toEntity()로 만든다
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElse(requestDto.toEntity());


        // JPA에서 save는 insert, update의 기능을 가진다.
        userRepository.save(user);

        return OAuthUserLoginResponseDto.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .build();
    }

    @Transactional
    public UserLoginResponseDto login(UserLoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElse(new User());

        boolean isCorrect = false;
        boolean isConfirmed = false;
        String id = null;

        if(user.getEmail() !=null){
            isCorrect = true;
            id = user.getId().toString();

            if (user.getRoleKey().equals(Role.USER.getKey())) { // 이메일 인증이 된 사용자
               isConfirmed = true;
            }
        }
        return UserLoginResponseDto.builder()
                .id(id)
                .isCorrect(isCorrect)
                .isConfirmed(isConfirmed)
                .build();
    }
}
