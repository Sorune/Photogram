package com.sorune.photogram.Security.Repository;

import com.sorune.photogram.Security.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    long countByUsername(String username);
    long countByEmail(String email);

    User save(User user);
}
