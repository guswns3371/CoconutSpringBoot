package com.coconut.service;

import com.coconut.api.dto.res.UserDataResDto;
import com.coconut.domain.user.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    public void 유저목록_조회() throws Exception {
        // given
        User user1 = User.builder()
                .name("user1")
                .email("email1")
                .build();
        User user2 = User.builder()
                .name("user2")
                .email("email2")
                .build();
        // when
        userService.save(user1);
        userService.save(user2);

        List<UserDataResDto> all = userService.findAllUsersById(user1.getId());
        // then

        Assert.assertEquals(user1.getId(),all.get(0).getId());
    }
}