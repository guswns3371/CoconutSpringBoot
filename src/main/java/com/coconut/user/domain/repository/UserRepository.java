package com.coconut.user.domain.repository;

import com.coconut.user.domain.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);

  Optional<User> findUserById(Long id);

  Optional<ArrayList<User>> findUserByIdIn(List<Long> idList);

  @Query("select u from User u where u.id in :ids")
  Optional<ArrayList<User>> findByInventoryIds(@Param("ids") List<Long> inventoryIdList);
}
