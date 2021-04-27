package com.coconut.service.user;

import com.coconut.client.dto.req.UserProfileUpdateReqDto;
import com.coconut.client.dto.res.BaseResDto;
import com.coconut.client.dto.res.UserDataResDto;
import com.coconut.domain.user.User;
import com.coconut.domain.user.UserRepository;
import com.coconut.service.utils.file.FilesStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final FilesStorageService storageService;

    @Transactional
    public List<UserDataResDto> findAllUsers(Long id) {
        log.warn("findAllUsers> : id =" + id.toString());
        List<User> userFindAll = userRepository.findAll();

        // 자기 자신
        User me = userFindAll.stream()
                .filter(user -> user.getId().equals(id))
                .collect(Collectors.toList())
                .get(0);

        // 자기 자신을 제외한 나머지 유저정보 리스트
        List<User> userList = userFindAll.stream()
                .filter(user -> !user.getId().equals(id))
                .collect(Collectors.toList());

        // index 0에 자기 자신을 삽입한다.
        userList.add(0, me);

        // List<User> 를 List<UserDataResponseDto>로 변환한다.
        return userList.stream()
                .map(UserDataResDto::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BaseResDto profileUpdate(UserProfileUpdateReqDto requestDto) {
        log.warn(requestDto.toString());
        User user = userRepository.findUserById(Long.parseLong(requestDto.getId())).orElse(new User());
        boolean success;
        String message;

        if (!String.valueOf(user.getId()).equals(requestDto.getId())) {
            success = false;
            message = "존재하지 않은 유저입니다. id=" + requestDto.getId();
        } else {
            try {
                User entity = requestDto.toEntity();
                storageService.save(requestDto.getProfileImage(),
                        entity.getProfilePicture());
                storageService.save(requestDto.getBackImage(),
                        entity.getBackgroundPicture());

                user.update(entity);

                success = true;
                message = "프로필 업데이트 성공";
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
                message = "프로필 업데이트 실패";
            }

        }
        return BaseResDto.builder()
                .success(success)
                .message(message)
                .build();
    }
}
