package com.coconut;

import com.coconut.domain.user.Role;
import com.coconut.domain.user.User;
import com.coconut.service.utils.encrypt.EncryptHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class InitDb {
    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;
        private final EncryptHelper encryptHelper;

        public void dbInit1() {
            for (int i = 1; i < 10; i++) {
                User user = User.builder()
                        .email("test" + i + "@gmail.com")
                        .name("테스트" + i)
                        .usrId("test" + i)
                        .role(Role.USER)
                        .password(encryptHelper.encrypt("1"))
                        .build();

                em.persist(user);
            }

        }
    }
}
