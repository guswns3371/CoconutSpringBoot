package com.coconut.service.auth;

import com.coconut.client.dto.req.UserSaveRequestDto;
import com.coconut.client.dto.res.UserSaveResponseDto;
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
        boolean isEmailOk = userRepository.findByEmail(email).isPresent();
        logger.warn("AuthService>emailCheck : " + email + ", isEmailOk : " + isEmailOk);
        return UserSaveResponseDto.builder()
                .isEmailOk(!isEmailOk)
                .build();
    }
}
