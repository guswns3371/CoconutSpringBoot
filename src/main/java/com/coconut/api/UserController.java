package com.coconut.api;

import com.coconut.api.dto.MailDto;
import com.coconut.api.dto.req.*;
import com.coconut.api.dto.res.*;
import com.coconut.domain.user.Role;
import com.coconut.domain.user.User;
import com.coconut.service.UserService;
import com.coconut.service.utils.encrypt.EncryptHelper;
import com.coconut.service.utils.file.FilesStorageService;
import com.coconut.service.utils.mail.MailService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/account")
public class UserController {

    private final UserService userService;
    private final EncryptHelper encryptHelper;
    private final FilesStorageService storageService;
    private final MailService mailService;

    @GetMapping("/{id}")
    @ApiOperation(value = "유저목록 조회", notes = "전체 유저목록을 조회한다.")
    public List<UserDataResDto> findUsers(@PathVariable Long id) {
        log.warn("findAllUsers> : id =" + id);

        List<User> all = userService.findAll();
        int userIndex = 0;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(id)) {
                userIndex = i;
                break;
            }
        }
        Collections.swap(all, 0, userIndex);

        return all.stream()
                .map(UserDataResDto::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/register/{email}")
    public UserSaveResDto emailCheck(@PathVariable String email) {
        boolean emailCheck = userService.checkByEmail(email);
        return UserSaveResDto.builder()
                .isEmailOk(!emailCheck)
                .build();
    }

    @PostMapping("/register")
    public UserSaveResDto saveUser(@RequestBody UserSaveReqDto requestDto) {

        User user = User.builder()
                .email(requestDto.getEmail())
                .name(requestDto.getName())
                .uId(requestDto.getUserId())
                .role(Role.GUEST)
                .password(encryptHelper.encrypt(requestDto.getPassword()))
                .build();

        try {
            userService.save(user);
            return UserSaveResDto.builder()
                    .isRegistered(true)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return UserSaveResDto.builder()
                    .isRegistered(false)
                    .build();
        }

    }

    @PostMapping("/user/info")
    public OAuthUserLoginResDto saveOAuthUser(@RequestBody OAuthUserLoginReqDto requestDto) {
        Optional<User> optionalUser = userService.findByEmail(requestDto.getEmail());
        if (optionalUser.isEmpty()) {
            log.error("존재하지 않는 유저 email = " + requestDto.getEmail());
            User entity = requestDto.toEntity();
            userService.save(entity);
            return OAuthUserLoginResDto.builder()
                    .userId(entity.getId())
                    .name(entity.getName())
                    .email(entity.getEmail())
                    .profilePicture(entity.getProfilePicture())
                    .build();
        }
        User user = optionalUser.get();
        userService.update(user.getId(), user);
        return OAuthUserLoginResDto.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .build();
    }
    // OAuth가 아닌 ID,PW로 로그인한 경우
    @PostMapping("/login")
    public UserLoginResDto login(@RequestBody UserLoginReqDto requestDto) {
        Optional<User> optionalUser = userService.findByEmail(requestDto.getEmail());
        if (optionalUser.isEmpty()) {
            log.error("존재하지 않는 유저 email = " + requestDto.getEmail());
            return UserLoginResDto.builder()
                    .isCorrect(false)
                    .isConfirmed(false)
                    .build();
        }

        User user = optionalUser.get();
        boolean isCorrect = encryptHelper.isMatch(requestDto.getPassword(), user.getPassword());
        boolean isConfirmed = user.getRoleKey().equals(Role.USER.getKey());

        if (!isConfirmed) {
            String token = userService.updateConfirmToken(user.getId());
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
        return UserLoginResDto.builder()
                .id(String.valueOf(user.getId()))
                .isCorrect(isCorrect)
                .isConfirmed(isConfirmed)
                .build();
    }

    @PostMapping("/login/verify")
    public UserLoginResDto emailVerify(@RequestBody UserEmailVerifyReqDto requestDto) {
        Optional<User> optionalUser = userService.findByEmail(requestDto.getEmail());
        if (optionalUser.isEmpty()) {
            log.error("존재하지 않는 유저 email = " + requestDto.getEmail());
            return UserLoginResDto.builder()
                    .isCorrect(false)
                    .isConfirmed(false)
                    .build();
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

        return UserLoginResDto.builder()
                .id(user.getId().toString())
                .isCorrect(isCorrect)
                .isConfirmed(isConfirmed)
                .build();
    }

    @PostMapping(
            value = "/edit",
            consumes = {
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    // https://blogs.perficient.com/2020/07/27/requestbody-and-multipart-on-spring-boot/
    // @RequestPart(value = "id", required = false) : required = false 해줘야 null 값이 들어와도 코드가 진행된다.
    public BaseResDto updateProfile(
            @RequestPart(value = "id", required = false) String id,
            @RequestPart(value = "userId", required = false) String userId,
            @RequestPart(value = "name", required = false) String name,
            @RequestPart(value = "message", required = false) String message,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "backImage", required = false) MultipartFile backImage
    ) {

        UserProfileUpdateReqDto reqDto = UserProfileUpdateReqDto.builder()
                .id(id)
                .userId(userId)
                .name(name)
                .message(message)
                .profileImage(profileImage)
                .backImage(backImage)
                .build();

        User entity = reqDto.toEntity();

        log.error("image " +profileImage.getOriginalFilename());

        try {
            storageService.save(profileImage, entity.getProfilePicture());
            storageService.save(backImage, entity.getBackgroundPicture());
            userService.updateProfile(Long.valueOf(id), entity);
            return BaseResDto.builder()
                    .success(true)
                    .message("프로필 업데이트 성공")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResDto.builder()
                    .success(false)
                    .message("프로필 업데이트 실패")
                    .build();
        }

    }

    @PostMapping("/user/fcm")
    public BaseResDto updateFcmToken(@RequestBody UserFcmUpdateReqDto reqDto) {
        Optional<User> optionalUser = userService.findById(Long.valueOf(reqDto.getUserId()));
        if (optionalUser.isEmpty()) {
            return BaseResDto.builder()
                    .success(false)
                    .message("존재하지 않은 유저입니다.")
                    .build();
        }
        userService.updateFcmToken(Long.valueOf(reqDto.getUserId()), reqDto.getFcmToken());

        return BaseResDto.builder()
                .success(true)
                .message("FCM Token 업데이트 성공")
                .build();
    }

}
