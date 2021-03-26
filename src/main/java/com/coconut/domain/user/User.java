package com.coconut.domain.user;

import com.coconut.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column
    private String password;

    @Column
    private String stateMessage;

    @Column
    private String profilePicture;

    @Column
    private String backgroundPicture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.GUEST;

//    // 부모 정의 (셀프 참조)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "SUPER_USER_ID")
//    private User superUser;
//
//    // 자식 정의
//    @OneToMany(fetch = FetchType.LAZY,mappedBy = "superUser", cascade = CascadeType.ALL)
//    private List<User> subUsers;

    @Builder
    public User(String userId, String name, String email, String password, String stateMessage, String profilePicture, String backgroundPicture, Role role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.stateMessage = stateMessage;
        this.profilePicture = profilePicture;
        this.backgroundPicture = backgroundPicture;
        this.role = role;
    }

    public User update(String name, String profilePicture, String backgroundPicture, String stateMessage) {
        this.name = name;
        this.profilePicture = profilePicture;
        this.backgroundPicture = backgroundPicture;
        this.stateMessage = stateMessage;

        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", stateMessage='" + stateMessage + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", backgroundPicture='" + backgroundPicture + '\'' +
                ", role=" + role +
                '}';
    }
}
