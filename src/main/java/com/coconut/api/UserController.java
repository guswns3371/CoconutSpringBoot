package com.coconut.api;

import com.coconut.api.dto.req.UserProfileUpdateReqDto;
import com.coconut.api.dto.res.BaseResDto;
import com.coconut.api.dto.res.UserDataResDto;
import com.coconut.domain.user.User;
import com.coconut.service.UserService;
import com.coconut.utils.file.FilesStorageService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {


    private final UserService userService;
    private final FilesStorageService storageService;


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
                .map(UserDataResDto::new)
                .collect(Collectors.toList());
    }

    // https://blogs.perficient.com/2020/07/27/requestbody-and-multipart-on-spring-boot/
    // @RequestPart(value = "id", required = false) : required = false 해줘야 null 값이 들어와도 코드가 진행된다.
    @PostMapping(
            value = "",
            consumes = {
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
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


}
