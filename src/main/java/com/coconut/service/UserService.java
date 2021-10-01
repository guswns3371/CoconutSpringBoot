package com.coconut.service;

import com.coconut.domain.user.User;
import com.coconut.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<ArrayList<User>> findUsersByIds(List<Long> ids) {
        return userRepository.findUserByIdIn(ids);
    }

    public boolean checkByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public Long save(User user) {
        validateDuplicateUser(user);
        return userRepository.save(user).getId();
    }

    @Transactional
    public void update(Long id, User entity) {
        User getUser = getUserById(id);
        getUser.update(entity);
    }

    @Transactional
    public void updateProfile(Long id, User entity) {
        User user = getUserById(id);
        user.update(entity);
    }

    @Transactional
    public void approveUser(Long id) {
        getUserById(id).approveUser();
    }

    @Transactional
    public void updateFcmToken(Long id, String token) {
        getUserById(id).updateFcmToken(token);
    }

    @Transactional
    public String updateConfirmToken(Long id) {
        return getUserById(id).updateConfirmToken();
    }

    private void validateDuplicateUser(User user) {
        Optional<User> findUsers = userRepository.findByEmail(user.getEmail());
        if (findUsers.isPresent()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    private User getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new IllegalStateException("없는 유저 " + id);
        }
        return optionalUser.get();
    }

}
