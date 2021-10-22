package com.coconut;

import com.coconut.domain.user.Role;
import com.coconut.domain.user.User;
import com.coconut.utils.encrypt.EncryptHelper;
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
            String[] names = {"baemin", "naver", "line", "seoultech", "kakao", "google", "spotify", "watcha"};
            String[] koreanNames = {"배민", "네이버", "라인", "과기대", "카카오", "구글", "스포티파이", "왓챠"};
            for (int i = 0; i < names.length; i++) {
                em.persist(User.builder()
                        .email(names[i] + "@gmail.com")
                        .name(koreanNames[i])
                        .usrId(names[i])
                        .stateMessage(names[i])
                        .profilePicture(names[i] + ".png")
                        .backgroundPicture(names[i] + ".png")
                        .role(Role.USER)
                        .password(encryptHelper.encrypt("1"))
                        .build());
            }

        }
    }
}
