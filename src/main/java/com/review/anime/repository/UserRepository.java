package com.review.anime.repository;

import com.review.anime.entites.Role;
import com.review.anime.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.email =:email")
    User findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.role =:role")
    List<User> getAdmins(@Param("role") Role role);

    boolean existsByEmail(String email);
}
