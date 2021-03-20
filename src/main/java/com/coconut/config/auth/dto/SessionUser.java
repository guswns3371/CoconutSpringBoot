package com.coconut.config.auth.dto;

// SessionUser 에는 인증된 사용자 정보만 필요하다.
// 그 외의 정보는 필요 없으니 name, email, picture만 필드로 선언한다.

import com.coconut.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/****
 * SessionUser 대신 User 클래스를 사용하면 직렬화 관련 에러가 발생한다.
 * 그렇다고 해서 User 클래스에 Serializable를 implements해주면 안된다
 * User 클래스는 엔티티 클래스이므로 다른 엔티티와 관계가 형성될 수 있다.
 * User 클래스가 자식 엔티티를 갖고 있다면 직렬화 대상에 자식들까지 포함된다. 이는 성능 이슈, 부수 효과가 발생한다.
 * 이러한 이유로 인해 직렬화 기능을 갖은 세션 Dto를 하나 추가로 만드는게 유지보수에 도움이 된다.
 */
@Getter
public class SessionUser implements Serializable {
    private boolean isExited;
    private String name;
    private String email;
    private String profilePicture;

    @Builder
    public SessionUser(User user) {
        this.isExited = true;
        this.name = user.getName();
        this.email = user.getEmail();
        this.profilePicture = user.getProfilePicture();
    }

    @Override
    public String toString() {
        return "SessionUser{" +
                "isExited=" + isExited +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                '}';
    }
}
