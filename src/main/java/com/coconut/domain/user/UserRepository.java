package com.coconut.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User,Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findUserById (Long id);

    Optional<ArrayList<User>> findUserByIdIn (List<Long> idList);

    @Query( "select u from User u where u.id in :ids" )
    Optional<ArrayList<User>> findByInventoryIds(@Param("ids") List<Long> inventoryIdList);
}
