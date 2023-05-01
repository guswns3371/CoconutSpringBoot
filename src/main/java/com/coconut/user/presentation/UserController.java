package com.coconut.user.presentation;

import com.coconut.auth.presentation.dto.UserDataResDto;
import com.coconut.base.presentation.dto.BaseResDto;
import com.coconut.common.utils.file.FilesStorageService;
import com.coconut.user.application.UserService;
import com.coconut.user.domain.entity.User;
import com.coconut.user.presentation.dto.UserProfileUpdateReqDto;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final FilesStorageService storageService;

  @GetMapping("")
  public ResponseEntity<List<UserDataResDto>> findAllUsers() {
    return ResponseEntity.ok(userService.findAll().stream().map(UserDataResDto::new).collect(Collectors.toList()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<List<UserDataResDto>> findUsers(@PathVariable Long id) {
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

    return ResponseEntity.ok(all.stream()
                                .map(UserDataResDto::new)
                                .collect(Collectors.toList()));
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
  public ResponseEntity<BaseResDto> updateProfile(
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
      return ResponseEntity.ok(BaseResDto.builder()
                                         .success(true)
                                         .message("프로필 업데이트 성공")
                                         .build());
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.ok(BaseResDto.builder()
                                         .success(false)
                                         .message("프로필 업데이트 실패")
                                         .build());
    }

  }

}
