package com.coconut.api;

import com.coconut.api.dto.req.UserProfileUpdateReqDto;
import com.coconut.api.dto.res.BaseResDto;
import com.coconut.api.dto.res.UserDataResDto;
import com.coconut.service.user.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/account")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @ApiOperation(value = "유저목록 조회", notes = "전체 유저목록을 조회한다.")
    public List<UserDataResDto> findAllUsers(@PathVariable String id) {
        log.warn("UserController> findAllUsers> id=" + id);
        return userService.findAllUsersById(Long.parseLong(id));
    }

    // https://blogs.perficient.com/2020/07/27/requestbody-and-multipart-on-spring-boot/
    @PostMapping(
            value = "/edit",
            consumes = {
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    // @RequestPart(value = "id", required = false) : required = false 해줘야 null 값이 들어와도 코드가 진행된다.
    public BaseResDto profileUpdate(
            @RequestPart(value = "id", required = false) String id,
            @RequestPart(value = "userId", required = false) String userId,
            @RequestPart(value = "name", required = false) String name,
            @RequestPart(value = "message", required = false) String message,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "backImage", required = false) MultipartFile backImage
    ) {
        return userService.profileUpdate(UserProfileUpdateReqDto.builder()
                .id(id)
                .userId(userId)
                .name(name)
                .message(message)
                .profileImage(profileImage)
                .backImage(backImage)
                .build());
    }

}
