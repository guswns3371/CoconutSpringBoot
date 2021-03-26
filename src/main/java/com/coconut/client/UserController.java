package com.coconut.client;

import com.coconut.client.dto.res.UserDataResponseDto;
import com.coconut.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserService userService;

    @GetMapping("/api/user/{id}")
    public List<UserDataResponseDto> findAllUsers(@PathVariable String id) {
        logger.warn("UserController> findAllUsers> id="+id);
        return userService.findAllUsers(Long.parseLong(id));
    }
}
