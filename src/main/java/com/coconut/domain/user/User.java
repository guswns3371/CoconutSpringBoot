package com.coconut.domain.user;

import com.coconut.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private String stateMessage;

    @Column
    private String profilePicture;

    @Column
    private String backgroundPicture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

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
}