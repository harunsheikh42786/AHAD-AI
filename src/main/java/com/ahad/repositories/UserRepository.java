package com.ahad.repositories;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ahad.entities.User;

public interface UserRepository extends JpaRepository<User, String> {
    User findUserById(String id);

    User findUserByEmail(String email);

    User findUserByUsername(String username);

    Set<User> findFriendsById(String id);

    List<User> findByNameContaining(String query);

}
